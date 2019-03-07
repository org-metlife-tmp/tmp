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
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by zhangsq on 2018/6/26.
 */
public class CloseAccCompleteService {

    private static final String CAF = "caf.";
    private static final String STATUS = "service_status";

    private static Set<String> tableColumns = new HashSet<String>() {{
        add("id");
        add("relation_id");
        add("acc_id");
        add("close_date");
        add("memo");
        add("apply_on");
        add("create_on");
        add("create_by");
        add("update_on");
        add("update_by");
        add("service_serial_number");
        add("service_status");
        add("delete_flag");
        add("persist_version");
        add("attachment_count");
        add("feedback");
    }};

    private static String installKey(String key) {
        return CAF + key;
    }

    public Page<Record> getTodoPage(int page_num, int page_size, Record record) {
        AccCommonService.setTodoListStatus(record, STATUS);
        SqlPara sqlPara = Db.getSqlPara(installKey("getTodoPage"), Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(page_num, page_size, sqlPara);

        for (Record rec : page.getList()) {
            //账户属性
            Record attrRec = Db.findById("category_value", "cat_code,key", "acc_attr", TypeUtils.castToString(rec.get("acc_attr")));
            rec.set("acc_attr_name", attrRec == null ? "" : TypeUtils.castToString(attrRec.get("value")));

            //账户用途
            Record poseRec = Db.findById("category_value", "cat_code,key", "acc_purpose", TypeUtils.castToString(rec.get("acc_purpose")));
            rec.set("acc_purpose_name", poseRec == null ? "" : TypeUtils.castToString(poseRec.get("value")));

            //币种
            Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(rec.get("curr_id")));
            rec.set("curr_name", currRec == null ? "" : TypeUtils.castToString(currRec.get("name")));

            //所属公司
            Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(rec.get("org_id")));
            rec.set("org_name", TypeUtils.castToString(orgRec.get("name")));

            //开户行
            Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(rec.get("bank_cnaps_code")));
            rec.set("bank_name", bankRec == null ? "" : TypeUtils.castToString(bankRec.get("name")));
        }
        return page;
    }

    public Record todoadd(Record record,final UserInfo userInfo) throws BusinessException {
        //当前登录用户
        final long usr_id = TypeUtils.castToLong(record.get("create_by"));
        final String name = TypeUtils.castToString(record.get("user_name"));
        //账户ID
        final long acc_id = TypeUtils.castToLong(record.get("acc_id"));
        //申请单ID
        final long source_id = TypeUtils.castToLong(record.get("relation_id"));


        //申请单补录人id非分发范围内用户，不允许进行操作
        Record issueRec = Db.findById("acc_close_intention_apply_issue", "bill_id,user_id", TypeUtils.castToLong(record.get("relation_id")), userInfo.getUsr_id());
        if (issueRec == null) {
            throw new ReqDataException("无权进行此操作！");
        }

        //销户交易
        final List<Record> list = record.get("additionals");
        //检查是否有人操作过
        AccCommonService.checkAccProcessLock(acc_id);
        final List<Object> fileIdList = record.get("files");
        record.remove("files");
        final Record save = new Record();
        for (Map.Entry<String, Object> entry : record.getColumns().entrySet()) {
            if (tableColumns.contains(entry.getKey())) {
                save.set(entry.getKey(), entry.getValue());
            }
        }
        save.set("persist_version", 0)
                .set("relation_id", source_id)
                .set("acc_id", acc_id)
                .set("create_by", usr_id)
                .set("create_on", new Date())
                .set("update_by", usr_id)
                .set("attachment_count", (null == fileIdList || fileIdList.isEmpty()) ? 0 : fileIdList.size())
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("service_serial_number",
                        BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ACC_CLOSE_COM));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean s = Db.save("acc_close_complete_apply", "id", save);
                if (s) {
                    boolean f = true;
                    if (null != list && list.size() > 0) {
                        for (Record _r : list) {
                            _r.set("apply_id", save.get("id"));
                        }

                        int[] additionals = Db.batchSave("acc_close_complete_apply_additional", list, list.size());
                        f = ArrayUtil.checkDbResult(additionals);
                    }
                    Record lock = new Record()
                            .set("source_id", source_id)
                            .set("type", 3)
                            .set("acc_id", acc_id)
                            .set("user_id", usr_id)
                            .set("user_name", name);
                    boolean b = Db.save("acc_process_lock", lock);
                    //更新申请的单为已完结
                    boolean update = Db.update("acc_close_intertion_apply", "id",
                            new Record()
                                    .set("id", source_id)
                                    .set("service_status", WebConstant.BillStatus.COMPLETION.getKey()));
                    CommonService.saveFileRef(WebConstant.MajorBizType.ACC_CLOSE_COM.getKey(), save.getLong("id"), fileIdList);
                    return f && b && update;
                }
                return s;
            }
        });
        if (!flag) {
            throw new DbProcessException("保存销户事项补录信息失败！");
        }
        save.set("create_on", AccCommonService.formatDate(record.get("create_on")));
        save.set("apply_on", AccCommonService.formatDate(record.get("apply_on")));
        return save;
    }

    public Record todochg(Record record, final UserInfo userInfo) throws BusinessException {
        //获取销户交易
        final List<Record> list = record.get("additionals");
        final long id = record.getLong("id");
        final Record update = Db.findById("acc_close_complete_apply", "id", id);
        for (Map.Entry<String, Object> entry : record.getColumns().entrySet()) {
            if (tableColumns.contains(entry.getKey())) {
                update.set(entry.getKey(), entry.getValue());
            }
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(update.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        update.set("update_on", new Date());
        update.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        final List<Object> fileIdList = record.get("files");
        record.remove("files");
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean b = Db.update("acc_close_complete_apply", "id", update);
                if (b) {
                    //清空原有销户交易记录
                    Db.deleteById("acc_close_complete_apply_additional",
                            "apply_id", id);

                    boolean f = true;
                    if (null != list && list.size() > 0) {
                        //保存新关系
                        for (Record _r : list) {
                            _r.set("apply_id", update.get("id"));
                        }
                        int[] additionals = Db.batchSave("acc_close_complete_apply_additional", list, 1000);
                        f = ArrayUtil.checkDbResult(additionals);
                    }
                    //保存文件关联关系
                    CommonService.delFileRef(WebConstant.MajorBizType.ACC_CLOSE_COM.getKey(), id);
                    CommonService.saveFileRef(WebConstant.MajorBizType.ACC_CLOSE_COM.getKey(), id, fileIdList);
                    return f;
                }
                return b;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改销户事项补录信息失败！");
        }
        update.set("create_on", AccCommonService.formatDate(record.get("create_on")));
        update.set("apply_on", AccCommonService.formatDate(record.get("apply_on")));
        update.set("update_on", AccCommonService.formatDate(record.get("update_on")));
        return Db.findById("acc_close_complete_apply", "id", id);
    }

    public void tododel(Record record, final UserInfo userInfo) throws BusinessException {
        final long id = record.getLong("id");
        final Record old = Db.findById("acc_close_complete_apply", "id", id);
        if (null == old
                || old.getInt("delete_flag").intValue() == WebConstant.YesOrNo.YES.getKey()) {
            throw new ReqDataException("销户信息补录不存在！请刷新重试！");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(old.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        //修改销户申请单状态为审批通过###
        final Record oldCai = new Record()
                .set("id", old.get("relation_id"))
                .set("service_status", WebConstant.BillStatus.PASS.getKey());
        final Record oldAcc = new Record()
                .set("id", id)
                .set("delete_flag", WebConstant.YesOrNo.YES.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //删除锁定表信息
                Db.deleteById("acc_process_lock", "acc_id", old.get("acc_id"));
                //删除补录信息
                boolean acc = Db.update("acc_close_complete_apply", "id", oldAcc);
                //重置申请单状态
                boolean aci = Db.update("acc_close_intertion_apply", "id", oldCai);
                //删除文件关联关系
                CommonService.delFileRef(WebConstant.MajorBizType.ACC_CLOSE_COM.getKey(), id);
                return acc && aci;
            }
        });
        if (!flag) {
            throw new DbProcessException("删除销户事项补录信息失败！");
        }
    }

    public Page<Record> getDonePage(int page_num, int page_size, Record record) {
        AccCommonService.setDoneListStatus(record, STATUS);
        SqlPara sqlPara = Db.getSqlPara(installKey("getDonePage"), Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
        for (Record rec : page.getList()) {
            //账户属性
            Record attrRec = Db.findById("category_value", "cat_code,key", "acc_attr", TypeUtils.castToString(rec.get("acc_attr")));
            rec.set("acc_attr_name", attrRec == null ? "" : TypeUtils.castToString(attrRec.get("value")));

            //账户用途
            Record poseRec = Db.findById("category_value", "cat_code,key", "acc_purpose", TypeUtils.castToString(rec.get("acc_purpose")));
            rec.set("acc_attr_purpose", poseRec == null ? "" : TypeUtils.castToString(poseRec.get("value")));

            //币种
            Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(rec.get("curr_id")));
            rec.set("curr_name", currRec == null ? "" : TypeUtils.castToString(currRec.get("name")));

            //所属公司
            Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(rec.get("org_id")));
            rec.set("org_name", TypeUtils.castToString(orgRec.get("name")));

            //开户行
            Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(rec.get("bank_cnaps_code")));
            rec.set("bank_name", bankRec == null ? "" : TypeUtils.castToString(bankRec.get("name")));
        }
        return page;
    }

    public Record detail(Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        Record info = Db.findById("acc_close_complete_apply", "id", id);
        if (null == info) {
            throw new ReqDataException("未找到有效的补录信息!");
        }


        //账户信息
        Record r = Db.findFirst(Db.getSql("acc.getAccByAccId"), info.get("acc_id"));
        if (r != null) {
            //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
            //否则进行机构权限的校验
            if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
                CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), r.getLong("org_id"));
            }


            r.set("additionals", Db.find(Db.getSql(installKey("getAdditional")), id));
        } else {
            r = new Record().set("additionals", new ArrayList<>());
        }

        //单据信息
        r.set("service_serial_number", TypeUtils.castToString(info.get("service_serial_number")));
        r.set("memo", TypeUtils.castToString(info.get("memo")));
        r.set("service_status", TypeUtils.castToInt(info.get("service_status")));
        r.set("persist_version", TypeUtils.castToInt(info.get("persist_version")));

        //根据acc_id查询银行地址
        List<Record> accExtInfo = Db.find(Db.getSql("acc.findAccountExtInfo"), TypeUtils.castToLong(info.get("acc_id")));
        for (Record ext : accExtInfo) {
            r.set(TypeUtils.castToString(ext.get("type_code")), TypeUtils.castToString(ext.get("value")));
        }
        return r;
    }
}
