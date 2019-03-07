package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import org.apache.commons.collections.CollectionUtils;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 开户事项申请
 *
 * @auther zhangyuanyuan
 * @create 2018/6/26
 */

public class OpenIntentionService {

    private static final String TABLE_NAME = "acc_open_intention_apply";
    private static final String ID = "id";
    private static final String STATUS = "service_status";
    private static final String VERSION = "persist_version";

    /**
     * 获取开户事项列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> findOpenIntentionToPage(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("aoi.findOpenIntentionToPage", Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(pageNum, pageSize, sqlPara);
        return page;
    }

    /**
     * 添加开户事项
     *
     * @param record
     * @param userInfo
     * @throws DbProcessException
     * @returnopenRec
     */
    public Record add(final Record record, UserInfo userInfo, UodpInfo uodp) throws DbProcessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        record.set("org_id", TypeUtils.castToLong(uodp.getOrg_id()));
        record.set("dept_id", TypeUtils.castToLong(uodp.getDept_id()));
        record.set("create_by", TypeUtils.castToLong(userInfo.getUsr_id()));
        record.set("create_on", new Date());
        record.set("apply_on", TypeUtils.castToDate(record.get("create_on")));
        record.set("service_serial_number", BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ACC_OPEN_INT));
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("persist_version", 1);

        final List<Object> list = record.get("files");
        record.remove("files");
        record.set("attachment_count", list == null ? 0 : list.size());

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                boolean falg = Db.save(TABLE_NAME, ID, record);
                if (falg) {
                    //保存附件
                    if (list != null && list.size() > 0) {
                        return CommonService.saveFileRef(WebConstant.MajorBizType.ACC_OPEN_INT.getKey(),
                                TypeUtils.castToLong(record.get(ID)), list);
                    }
                    return true;
                }
                return false;
            }
        });

        if (flag) {
            record.set("apply_on", format.format(TypeUtils.castToDate(record.get("apply_on"))));
            record.set("org_name", uodp.getOrg_name());
            record.set("dept_name", uodp.getDept_name());
            return record;
        }

        throw new DbProcessException("添加开户事项失败!");
    }

    /**
     * 修改开户事项信息
     *
     * @param record
     * @param uodp
     * @return
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record chg(final Record record, UserInfo userInfo, UodpInfo uodp) throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final long id = TypeUtils.castToLong(record.get(ID));

        record.set("update_on", new Date());
        record.set("update_by", userInfo.getUsr_id());

        final List<Object> list = record.get("files");
        record.remove("files");
        record.set("attachment_count", list == null ? 0 : list.size());

        Record intentionRec = getOpenIntentionById(id);
        if (intentionRec == null) {
            throw new ReqDataException("未找到有效的开户信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(intentionRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        record.set("service_serial_number", TypeUtils.castToString(intentionRec.get("service_serial_number")));

        if (TypeUtils.castToInt(intentionRec.get("delete_flag")) != WebConstant.YesOrNo.NO.getKey()) {
            throw new ReqDataException("该单据状态不正确!");
        }

        final int old_version = TypeUtils.castToInt(record.get(VERSION));
        //移除id 兼容sqlserver
        record.remove("id");

        boolean flag = Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {
                boolean flag = CommonService.update("acc_open_intention_apply",
                        record.set(VERSION, old_version + 1)
                                .set(STATUS, WebConstant.BillStatus.SAVED.getKey()),
                        new Record()
                                .set(ID, id)
                                .set(VERSION, old_version));
                if (flag) {
                	//删除附件
                	CommonService.delFileRef(WebConstant.MajorBizType.ACC_OPEN_INT.getKey(), id);
                    if (list != null && list.size() > 0) {
                        //保存附件
                        return CommonService.saveFileRef(WebConstant.MajorBizType.ACC_OPEN_INT.getKey(),
                                id, list);
                    }
                }
                return flag;
            }
        });

        if (flag) {
            record.set("apply_on", format.format(TypeUtils.castToDate(record.get("apply_on"))));
            record.set("org_name", uodp.getOrg_name());
            record.set("dept_name", uodp.getDept_name());
            record.set("id", id);
            return record;
        }

        throw new DbProcessException("修改开户事项失败!");
    }

    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        //根据单据id查询开户申请信息
        Record openRec = Db.findById("acc_open_intention_apply", "id", id);
        if (openRec == null) {
            throw new DbProcessException("未找到有效的单据!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if(record.get("wf_inst_id") == null || record.get("biz_type") == null){
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(),openRec.getLong("org_id"));
        }

        //开户行
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(openRec.get("bank_cnaps_code")));
        openRec.set("bank_name", bankRec == null ? "" : TypeUtils.castToString(bankRec.get("name")));
        openRec.set("province", bankRec == null ? "" : TypeUtils.castToString(bankRec.get("province")));
        openRec.set("city", bankRec == null ? "" : TypeUtils.castToString(bankRec.get("city")));

        //账户属性
        Record attrRec = Db.findById("category_value", "cat_code,key", "acc_attr", TypeUtils.castToString(openRec.get("acc_attr")));
        openRec.set("acc_attr_name", attrRec == null ? "" : TypeUtils.castToString(attrRec.get("value")));

        //账户用途
        Record poseRec = Db.findById("category_value", "cat_code,key", "acc_purpose", TypeUtils.castToString(openRec.get("acc_purpose")));
        openRec.set("acc_attr_purpose", poseRec == null ? "" : TypeUtils.castToString(poseRec.get("value")));

        //币种
        Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(openRec.get("curr_id")));
        openRec.set("curr_name", currRec == null ? "" : TypeUtils.castToString(currRec.get("name")));

        //申请部门
        Record depRec = Db.findById("department", "dept_id", TypeUtils.castToLong(openRec.get("dept_id")));
        openRec.set("dept_name", TypeUtils.castToString(depRec.get("name")));

        //所属公司
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(openRec.get("org_id")));
        openRec.set("org_name", TypeUtils.castToString(orgRec.get("name")));
        return openRec;
    }


    /**
     * 删除开户事项
     *
     * @param record
     * @param userInfo
     * @return
     * @throws DbProcessException
     */
    public boolean del(final Record record, UserInfo userInfo) throws BusinessException {

        final long id = TypeUtils.castToLong(record.get(ID));

        record.set("delete_flag", WebConstant.YesOrNo.YES.getKey());

        Record intentionRec = getOpenIntentionById(id);
        if (intentionRec == null) {
            throw new ReqDataException("未找到有效的开户信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(intentionRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }


        if (TypeUtils.castToInt(intentionRec.get("delete_flag")) != WebConstant.YesOrNo.NO.getKey()) {
            throw new ReqDataException("该单据状态不正确!");
        }

        final int old_version = TypeUtils.castToInt(record.get(VERSION));

        //移除ID 兼容sqlserver
        record.remove("id");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean flag = CommonService.update("acc_open_intention_apply",
                        record.set(VERSION, old_version + 1),
                        new Record().set(ID, id).set(VERSION, old_version));
//                        Db.update(Db.getSqlPara("aoi.chgOpenIntentionByIdAndVersion", Kv.by("map", kv))) > 0;
                if (flag) {
                    //删除附件
                    CommonService.delFileRef(WebConstant.MajorBizType.ACC_OPEN_INT.getKey(), id);
                    return true;
                }
                return false;
            }
        });
        if (flag) {
            return true;
        }
        throw new DbProcessException("删除开户事项信息失败!");
    }

    /**
     * 开户事项分发
     *
     * @param record
     * @throws BusinessException
     */
    public Record issue(final Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));

        Record intentionRec = getOpenIntentionById(id);
        if (intentionRec == null) {
            throw new ReqDataException("未找到有效的开户信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(intentionRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        if (TypeUtils.castToInt(intentionRec.get(STATUS)) != WebConstant.BillStatus.PASS.getKey()) {
            throw new ReqDataException("该单据状态不正确!");
        }


        //获取用户id
        final List<Long> issList = record.get("iss_users");
        Long[] userIds = new Long[issList.size()];

        for (int i = 0; i < issList.size(); i++) {
            userIds[i] = TypeUtils.castToLong(issList.get(i));
        }

        final Long[] finalUserIds = userIds;

        boolean flag = issueOp(record, finalUserIds);

        if (flag) {
            //查询分发人
            Record issueRec = Db.findFirst(Db.getSql("aoi.findIssueName"), id);
            record.remove("iss_users");
            record.set("issue", TypeUtils.castToString(issueRec.get("name")));
            return record;
        }

        throw new DbProcessException("分发失败!");

    }

    public boolean finish(final Record record, UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get(ID));

        Record intentionRec = getOpenIntentionById(id);
        if (intentionRec == null) {
            throw new ReqDataException("未找到有效的开户信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(intentionRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        if (TypeUtils.castToInt(intentionRec.get(STATUS)) != WebConstant.BillStatus.PASS.getKey()) {
            throw new ReqDataException("该单据状态不正确!");
        }

        final int old_version = TypeUtils.castToInt(record.get(VERSION));
        //兼容sqlserver
        record.remove(ID);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() {

                return CommonService.update("acc_open_intention_apply",
                        record.set(VERSION, old_version + 1)
                                .set("service_status", WebConstant.BillStatus.COMPLETION.getKey()),
                        new Record().set(ID, id).set(VERSION, old_version)
                );
                //Db.update(Db.getSqlPara("aoi.chgOpenIntentionByIdAndVersion", Kv.by("map", kv))) > 0;
            }
        });

        if (flag) {
            return true;
        }
        throw new DbProcessException("办结失败!");

    }

    /**
     * 审批通过后分发给用户
     *
     * @param record   审批记录
     * @param userInfo 用户信息
     * @return
     */
    public boolean pass(final Record record, final UserInfo userInfo) {
        final List<Long> issList = new ArrayList<>();
        /** ↓↓↓ 审批通过后默认分发给自己 ↓↓↓ */
        issList.add(userInfo.getUsr_id());
        /** ↑↑↑ 审批通过后默认分发给自己 ↑↑↑ */
        final Long[] userIds = new Long[issList.size()];

        for (int i = 0; i < issList.size(); i++) {
            userIds[i] = TypeUtils.castToLong(issList.get(i));
        }

        return issueOp(record, userIds);
    }

    public boolean issueOp(final Record record, final Long[] userIds) {
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() {
                List<Record> recordAddList = new ArrayList<>();
                List<Record> recordChgList = new ArrayList<>();
                long bill_id = TypeUtils.castToLong(record.get("id"));
                long counts = 0;

                for (long userId : userIds) {
                    //根据billid  userid 查询该用户是否已分发
                    Record issue = Db.findById("acc_open_intention_apply_issue", "bill_id,user_id", bill_id, userId);
                    if (issue != null) {
                        //修改该用户分发次数
                        counts = TypeUtils.castToLong(issue.get("counts"));
                        issue.set("counts", counts + 1);
                        recordChgList.add(issue);
                    } else {
                        issue = new Record();
                        issue.set("bill_id", bill_id);
                        issue.set("user_id", userId);
                        issue.set("counts", 1);
                        recordAddList.add(issue);
                    }

                }

                if (CollectionUtils.isNotEmpty(recordAddList) && CollectionUtils.isNotEmpty(recordChgList)) {
                    int[] addRes = Db.batchSave("acc_open_intention_apply_issue", recordAddList, 1000);
                    int[] chgRes = Db.batchUpdate("acc_open_intention_apply_issue", "bill_id,user_id", recordChgList, 1000);
                    return ArrayUtil.checkDbResult(addRes) && ArrayUtil.checkDbResult(chgRes);
                } else if (CollectionUtils.isNotEmpty(recordAddList) && CollectionUtils.isEmpty(recordChgList)) {
                    int[] addRes = Db.batchSave("acc_open_intention_apply_issue", recordAddList, 1000);
                    return ArrayUtil.checkDbResult(addRes);
                } else if (CollectionUtils.isEmpty(recordAddList) && CollectionUtils.isNotEmpty(recordChgList)) {
                    int[] chgRes = Db.batchUpdate("acc_open_intention_apply_issue", "bill_id,user_id", recordChgList, 1000);
                    return ArrayUtil.checkDbResult(chgRes);
                }
                return true;
            }
        });
        return flag;
    }


    public Record getOpenIntentionById(Long id) {
        String sql = Db.getSql("aoi.getOpenIntentionById");
        return Db.findFirst(sql, id, WebConstant.YesOrNo.NO.getKey());
    }

}
