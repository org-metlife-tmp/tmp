package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.utils.CommonService;
import org.apache.commons.collections.CollectionUtils;
import org.bouncycastle.ocsp.Req;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangsq on 2018/6/26.
 */
public class CloseAccIntentionService {

    private static final String CAI = "cai.";
    private static final String TABLE_NAME = "acc_close_intertion_apply";
    private static final String ID = "id";
    private static final String STATUS = "service_status";
    private static final String VERSION = "persist_version";
    private static final String ISSUE_TABLE_NAME = "acc_close_intention_apply_issue";
    private static final String ISSUE_KEYS = "bill_id,user_id";

    private static String installKey(String key) {
        return CAI + key;
    }

    public Page<Record> getTodoPage(int page_num, int page_size, Record record) {
        AccCommonService.setTodoListStatus(record, STATUS);
        SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
        return page;
    }

    public Record todoadd(final Record record) throws BusinessException {
        //提取文件
        final List<Object> list = record.get("files");
        record.remove("files");
        record.set(STATUS, WebConstant.BillStatus.SAVED.getKey());
        record.set("create_on", new Date());
        record.set("apply_on", new Date());
        record.set("persist_version", 0);
        record.set("service_serial_number", BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ACC_CLOSE_INT));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save(TABLE_NAME, ID, record);
                if (save && list != null && !list.isEmpty()) {
                    return CommonService.saveFileRef(WebConstant.MajorBizType.ACC_CLOSE_INT.getKey(),
                            record.getLong(ID), list);
                }
                return save;
            }
        });
        if (!flag) {
            throw new DbProcessException("新增销户事项失败");
        }
        record.set("create_on", AccCommonService.formatDate(record.get("create_on")));
        record.set("apply_on", AccCommonService.formatDate(record.get("apply_on")));
        return record;
    }

    public Record todochg(final Record record, final UserInfo userInfo) throws BusinessException {
        long id = record.getLong(ID);
        check(id, userInfo);
        final List<Object> list = record.get("files");
        record.remove("files");
        record.set(STATUS, WebConstant.BillStatus.SAVED.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean update = Db.update(TABLE_NAME, ID, record);
                CommonService.delFileRef(WebConstant.MajorBizType.ACC_CLOSE_INT.getKey(), record.getLong(ID));
                if (update && list != null && !list.isEmpty()) {
                    boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.ACC_CLOSE_INT.getKey(),
                            record.getLong(ID), list);
                    return saveFileRef;
                }
                return update;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改销户事项失败！");
        }
        record.set("create_on", AccCommonService.formatDate(record.get("create_on")));
        record.set("apply_on", AccCommonService.formatDate(record.get("apply_on")));
        record.set("update_on", AccCommonService.formatDate(record.get("update_on")));
        return Db.findById("acc_close_intertion_apply", "id", id);
    }

    private Record check(long id, UserInfo userInfo) throws BusinessException {
        Record data = Db.findById(TABLE_NAME, ID, id);
        if (null == data
                || data.getInt("delete_flag").intValue() == WebConstant.YesOrNo.YES.getKey()) {
            throw new ReqDataException("不存在，请刷新重试！");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(data.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        return data;
    }

    public void tododel(final Record record, final UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get(ID));
        check(id, userInfo);
        record.set("delete_flag", WebConstant.YesOrNo.YES.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                CommonService.delFileRef(WebConstant.MajorBizType.ACC_CLOSE_INT.getKey(), record.getLong(ID));
                boolean del = Db.update(TABLE_NAME, "id", record);
                return del;
            }
        });
        if (!flag) {
            throw new DbProcessException("删除销户事项失败！");
        }
    }

    public Page<Record> getDonePage(int page_num, int page_size, Record record) {
        AccCommonService.setDoneListStatus(record, STATUS);
        SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
        return page;
    }

    public String doneissue(Record record, final UserInfo userInfo) throws BusinessException {
        long bill_id = record.getLong("id");

        List<Long> user_ids = record.get("user_ids");
        Long[] userIds = new Long[user_ids.size()];

        for (int i = 0; i < user_ids.size(); i++) {
            userIds[i] = TypeUtils.castToLong(user_ids.get(i));
        }

        final Long[] finalUserIds = userIds;
        Record data = check(bill_id, userInfo);
        if (data.getInt(STATUS).intValue() != WebConstant.BillStatus.PASS.getKey()) {
            throw new ReqDataException("单据未审批通过，请刷新重试！");
        }
        if (user_ids == null || user_ids.size() == 0) {
            throw new ReqDataException("请选择分发人！");
        }
        boolean flag = issueOp(record, finalUserIds);
        if (!flag) {
            throw new DbProcessException("销户事项分布失败！");
        }
        return Db.queryStr(Db.getSql(installKey("usernames")), bill_id);
    }

    public void doneend(final Record record, final UserInfo userInfo) throws BusinessException {

        Record billRec = Db.findById(TABLE_NAME, ID, TypeUtils.castToLong(record.get("id")));

        if (billRec == null) {
            throw new ReqDataException("未找到有效的单据信息！");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(billRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        record.set("service_status", WebConstant.BillStatus.COMPLETION.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update(TABLE_NAME, ID, record);
            }
        });
        if (!flag) {
            throw new DbProcessException("销户事项办结失败！");
        }
    }

    /**
     * @param record
     * @param userInfo
     * @return
     * @throws DbProcessException
     */
    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        //根据单据id查询申请信息
        Record closeRec = Db.findById("acc_close_intertion_apply", "id", id);
        if (closeRec == null) {
            throw new DbProcessException("未找到有效的单据!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), closeRec.getLong("org_id"));
        }


        //根据acc_id查询账户信息
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(closeRec.get("acc_id")));


        //账户属性
        Record attrRec = Db.findById("category_value", "cat_code,key", "acc_attr", TypeUtils.castToString(accRec.get("acc_attr")));
        accRec.set("acc_attr_name", attrRec == null ? "" : TypeUtils.castToString(attrRec.get("value")));

        //账户用途
        Record poseRec = Db.findById("category_value", "cat_code,key", "acc_purpose", TypeUtils.castToString(accRec.get("acc_purpose")));
        accRec.set("acc_purpose_name", poseRec == null ? "" : TypeUtils.castToString(poseRec.get("value")));

        //币种
        Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(accRec.get("curr_id")));
        accRec.set("curr_name", currRec == null ? "" : TypeUtils.castToString(currRec.get("name")));

        //所属公司
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));
        accRec.set("org_name", TypeUtils.castToString(orgRec.get("name")));

        //开户行
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(accRec.get("bank_cnaps_code")));
        accRec.set("bank_name", bankRec == null ? "" : TypeUtils.castToString(bankRec.get("name")));

        closeRec.set("account_info", accRec);

        return closeRec;
    }

    public boolean pass(final Record record, UserInfo userInfo) {
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
        long bill_id = TypeUtils.castToLong(record.get("id"));
        final List<Record> add = new ArrayList<>();
        final List<Record> chg = new ArrayList<>();

        for (Long user_id : userIds) {
            Record issue = Db.findById(ISSUE_TABLE_NAME, ISSUE_KEYS, bill_id, user_id);
            if (issue == null) {
                add.add(new Record().set("bill_id", bill_id).set("user_id", user_id).set("counts", 1));
            } else {
                chg.add(issue.set("counts", issue.getLong("counts") + 1));
            }
        }
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (CollectionUtils.isNotEmpty(add) && CollectionUtils.isNotEmpty(chg)) {
                    int[] save = Db.batchSave(ISSUE_TABLE_NAME, add, 1000);
                    int[] update = Db.batchUpdate(ISSUE_TABLE_NAME, ISSUE_KEYS, chg, 1000);
                    return ArrayUtil.checkDbResult(save) && ArrayUtil.checkDbResult(update);
                } else if (CollectionUtils.isNotEmpty(add) && CollectionUtils.isEmpty(chg)) {
                    int[] save = Db.batchSave(ISSUE_TABLE_NAME, add, 1000);
                    return ArrayUtil.checkDbResult(save);
                } else if (CollectionUtils.isEmpty(add) && CollectionUtils.isNotEmpty(chg)) {
                    int[] update = Db.batchUpdate(ISSUE_TABLE_NAME, ISSUE_KEYS, chg, 1000);
                    return ArrayUtil.checkDbResult(update);
                }
                return true;
            }
        });
    }
}
