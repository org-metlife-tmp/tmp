package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangsq on 2018/6/28.
 */
public class AccDefreezeService {
    private static final String ADF = "afd.";

    private static String installKey(String key) {
        return ADF + key;
    }

    public Page<Record> getTodoPage(int page_num, int page_size, Record record) {
        AccCommonService.setTodoListStatus(record, "service_status");
        record.set("status", new int[]{
                WebConstant.AccountStatus.FREEZE.getKey()
        });
        record.set("type", 2);
        SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record todoadd(final Record record, final String name) throws BusinessException {
        //校验账户是否存在其他操作。
        long acc_id = TypeUtils.castToLong(record.get("acc_id"));
        AccCommonService.checkAccProcessLock(acc_id);
        record.set("type", 2);
        record.set("apply_on", new Date());
        record.set("update_by", record.get("create_by"));
        record.set("service_serial_number",
                BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ACC_DEFREEZE_APL));
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("persist_version", 0);
        final List<Object> list = record.get("files");
        record.remove("files");
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("acc_freeze_and_defreeze_apply", "id", record);
                if (save) {
                    Record lock = new Record()
                            .set("source_id", record.get("id"))
                            .set("type", 2)
                            .set("acc_id", record.get("acc_id"))
                            .set("user_id", record.get("create_by"))
                            .set("user_name", name);
                    CommonService.saveFileRef(WebConstant.MajorBizType.ACC_DEFREEZE_APL.getKey(), record.getLong("id"), list);
                    return Db.save("acc_process_lock", lock);
                }
                return save;
            }
        });
        if (!flag) {
            throw new DbProcessException("新增账户解冻申请单失败！");
        }
        return record;
    }

    public Record todochg(final Record record, final UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        //校验新账户ID是否已用
        long newAccId = TypeUtils.castToLong(record.get("acc_id"));
        Record old = Db.findById("acc_freeze_and_defreeze_apply", "id", id);
        final long oldAccId = TypeUtils.castToLong(old.get("acc_id"));
        if (newAccId != oldAccId) {
            AccCommonService.checkAccProcessLock(newAccId);
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(old.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        record.set("update_on", new Date());
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        final List<Object> list = record.get("files");
        record.remove("files");
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean del = Db.deleteById("acc_process_lock", "acc_id", oldAccId);
                boolean update = Db.update("acc_freeze_and_defreeze_apply", "id", record);
                if (update && del) {
                    Record lock = new Record()
                            .set("source_id", record.get("id"))
                            .set("type", 2)
                            .set("acc_id", record.get("acc_id"))
                            .set("user_id", record.get("create_by"))
                            .set("user_name", userInfo.getName());
                    CommonService.delFileRef(WebConstant.MajorBizType.ACC_DEFREEZE_APL.getKey(), record.getLong("id"));
                    CommonService.saveFileRef(WebConstant.MajorBizType.ACC_DEFREEZE_APL.getKey(), record.getLong("id"), list);
                    return Db.save("acc_process_lock", lock);
                }
                return update && del;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改账户解冻申请单失败！");
        }
        Record de = new Record();
        de.set("id", id);
        de.set("type", 2);
        SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", de.getColumns()));
        return Db.findFirst(sqlPara);
    }

    public void tododel(final Record record,final UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        Record defreezeRec = Db.findById("acc_freeze_and_defreeze_apply", "id", id);

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(defreezeRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        record.set("acc_id", TypeUtils.castToLong(defreezeRec.get("acc_id")));
        record.set("delete_flag", WebConstant.YesOrNo.YES.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空锁定表信息
                Db.deleteById("acc_process_lock", "acc_id", record.get("acc_id"));
                boolean update = Db.update("acc_freeze_and_defreeze_apply", "id", record);
                CommonService.delFileRef(WebConstant.MajorBizType.ACC_DEFREEZE_APL.getKey(), record.getLong("id"));
                return update;
            }
        });
        if (!flag) {
            throw new DbProcessException("删除账户解冻申请单失败！");
        }
    }

    public Page<Record> getDonePage(int page_num, int page_size, Record record) {
        AccCommonService.setDoneListStatus(record, "service_status");
        record.set("status", new int[]{
                WebConstant.AccountStatus.NORAMAL.getKey(),
                WebConstant.AccountStatus.FREEZE.getKey()
        });
        record.set("type", 2);
        SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record detail(final Record record, UserInfo userInfo) throws  BusinessException {
        Record de = new Record();
        de.set("id", TypeUtils.castToString(record.get("id")));
        de.set("type", 2);
        SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", de.getColumns()));
        de = Db.findFirst(sqlPara);

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if(record.get("wf_inst_id") == null || record.get("biz_type") == null){
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(),de.getLong("org_id"));
        }

        //账户属性
        Record accpur = Db.findById("category_value", "cat_code,key", "acc_purpose", TypeUtils.castToString(de.get("acc_attr")));
        de.set("acc_purpose_name", accpur == null ? null : TypeUtils.castToString(accpur.get("value")));
        return de;
    }
}
