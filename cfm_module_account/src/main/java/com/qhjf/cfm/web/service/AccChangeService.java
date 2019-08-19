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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 账户变更申请
 *
 * @auther zhangyuanyuan
 * @create 2018/6/29
 */

public class AccChangeService {

    /**
     * 账户变更列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("chg.findChangeToList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public Record add(final Record record, final UserInfo userInfo) throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final long accId = TypeUtils.castToLong(record.get("acc_id"));
        AccCommonService.checkAccProcessLock(accId);

        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ACC_CHG_APL);
        final List<Record> list = record.get("change_content");

        record.set("create_on", new Date());
        record.set("create_by", userInfo.getUsr_id());
        record.set("apply_on", new Date());
        record.set("service_serial_number", serviceSerialNumber);
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("persist_version", 0);

        final List<Object> filesList = record.get("files");
        record.remove("files");

        //根据账户id查询原账户信息
        final Record account = Db.findById("account", "acc_id", accId);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //保存账户信息变更申请单
                record.remove("change_content");
                boolean flag = Db.save("acc_change_apply", "id", record);
                if (flag) {
                    //保存变更详情
                    if (saveChangeDetail(record, list, account, userInfo)) {

                        Record accRec = Db.findById("account", "acc_id", accId);
                        record.set("acc_no", TypeUtils.castToString(accRec.get("acc_no")));
                        record.set("acc_name", TypeUtils.castToString(accRec.get("acc_name")));

                        if (filesList != null && filesList.size() > 0) {
                            //保存附件
                            return CommonService.saveFileRef(WebConstant.MajorBizType.ACC_CHG_APL.getKey(),
                                    TypeUtils.castToLong(record.get("id")), filesList);
                        }
                        return true;
                    }
                    return false;
                }

                return false;
            }
        });

        if (flag) {
            record.set("apply_on", format.format(TypeUtils.castToDate(record.get("apply_on"))));
            return record;
        }

        throw new DbProcessException("新增账户变更申请失败!");
    }

    public Record chg(final Record record, final UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));

        Record change_apply = Db.findById("acc_change_apply", "id", id);

        if (change_apply == null) {
            throw new ReqDataException("未找到有效的开户补录信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(change_apply.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }


        final long accId = TypeUtils.castToLong(record.get("acc_id"));
        final List<Record> list = record.get("change_content");
        final List<Object> filesList = record.get("files");
        record.remove("files");
        record.remove("change_content");
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        //根据账户id查询原账户信息
        final Record account = Db.findById("account", "acc_id", accId);
        if (account == null) {
            throw new ReqDataException("未找到有效的账户信息!");
        }

        Record accRec = Db.findById("account", "acc_id", accId);
        if (accRec == null) {
            throw new ReqDataException("未找到有效的账户信息!");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //修改主单据信息
                final int old_version = TypeUtils.castToInt(record.get("persist_version"));
                record.remove("id");

                boolean flag = CommonService.update("acc_change_apply",
                        record.set("persist_version", old_version + 1),
                        new Record().set("id", id).set("persist_version", old_version)
                );
                if (flag) {
                    //删除详细内容
                    int result = Db.delete(Db.getSql("chg.delChangeDetailByApplyId"), id);

                    if (result > 0) {
                        //兼容sqlserver
                        record.set("id", id);
                        //插入变更详情信息
                        if (saveChangeDetail(record, list, account, userInfo)) {
                            //删除附件
                            CommonService.delFileRef(WebConstant.MajorBizType.ACC_CHG_APL.getKey(), id);
                            if (filesList != null && filesList.size() > 0) {
                                //保存附件
                                return CommonService.saveFileRef(WebConstant.MajorBizType.ACC_CHG_APL.getKey(),
                                        id, filesList);
                            }
                            return true;
                        }
                        return false;
                    }
                }
                return false;
            }
        });
        if (flag) {
            return Db.findById("acc_change_apply", "id", id);
        }


        throw new DbProcessException("修改账户变更申请失败!");
    }

    public void del(final Record record, final UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));

        final Record account = Db.findById("acc_change_apply", "id", record.get("id"));

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(account.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        record.set("delete_flag", 1);

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        //要更新的列
        record.remove("id");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean flag = CommonService.update("acc_change_apply",
                        record.set("persist_version", old_version + 1),
                        new Record().set("id", id).set("persist_version", old_version)
                );
                if (flag) {
                    //删除锁定表信息
                    Long oldAccId = TypeUtils.castToLong(account.get("acc_id"));
                    boolean del = Db.deleteById("acc_process_lock", "acc_id", oldAccId);

                    //删除附件
                    CommonService.delFileRef(WebConstant.MajorBizType.ACC_CHG_APL.getKey(), id);
                    return true;
                }
                return false;
            }
        });

        if (flag) {
            return;
        }

        throw new DbProcessException("删除账户变更申请失败!");
    }

    /**
     * 账户变更申请查看
     *
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        long id = TypeUtils.castToLong(record.get("id"));

        //根据id查询主表信息
        Record changeRec = Db.findById("acc_change_apply", "id", id);
        if (changeRec == null) {
            throw new ReqDataException("未找到有效的账户变更申请信息!");
        }

        //根据accid查询账户信息
        Record accRec = Db.findFirst(Db.getSql("acc.getAccByAccId"), TypeUtils.castToLong(changeRec.get("acc_id")));
        if (accRec != null) {

            //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
            //否则进行机构权限的校验
            if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
                CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), accRec.getLong("org_id"));
            }

            changeRec.set("acc_no", TypeUtils.castToString(accRec.get("acc_no")));
            changeRec.set("acc_name", TypeUtils.castToString(accRec.get("acc_name")));
            changeRec.set("org_name", TypeUtils.castToString(accRec.get("org_name")));
            changeRec.set("lawfull_man", TypeUtils.castToString(accRec.get("lawfull_man")));
            changeRec.set("curr_name", TypeUtils.castToString(accRec.get("curr_name")));
            changeRec.set("bank_name", TypeUtils.castToString(accRec.get("bank_name")));
            changeRec.set("interactive_mode", TypeUtils.castToInt(accRec.get("interactive_mode")));
            changeRec.set("acc_purpose", TypeUtils.castToString(accRec.get("acc_purpose")));
            changeRec.set("acc_purpose_name", TypeUtils.castToString(accRec.get("acc_purpose_name")));
            changeRec.set("acc_attr", TypeUtils.castToString(accRec.get("acc_attr")));
            changeRec.set("acc_attr_name", TypeUtils.castToString(accRec.get("acc_attr_name")));
            changeRec.set("open_date", format.format(TypeUtils.castToDate(accRec.get("open_date"))));
            changeRec.set("apply_on", format.format(TypeUtils.castToDate(changeRec.get("apply_on"))));
        } else {
            throw new ReqDataException("账户信息不存在！");
        }

        //查询变更详细
        List<Record> detalRec = Db.find(Db.getSql("chg.findChangeDetailByApplyId"), id);
        changeRec.set("change_content", detalRec);
        return changeRec;

    }

    public boolean saveChangeDetail(final Record record, final List<Record> list, final Record accRec, UserInfo userInfo) {
        boolean flag = false;
        List<Record> detailList = new ArrayList<>();
        String old_id = "";
        String old_value = "";
        String newValueStr = "";
        Long newValueLong = 0L;

        Record oldChgRec = Db.findById("acc_change_apply", "id", record.get("id"));

        for (Record rec : list) {
            Record detailRec = new Record();
            detailRec.set("apply_id", TypeUtils.castToLong(record.get("id")));

            int type = TypeUtils.castToInt(rec.get("type"));
            detailRec.set("type", type);

            WebConstant.AccountChangeField field = WebConstant.AccountChangeField.get(type);

            if (rec.get("value") == null || "".equals(rec.get("value"))) {
                continue;
            }

            assert field != null;
            switch (field) {
                case ACCNAME:
                    old_id = "";
                    old_value = TypeUtils.castToString(accRec.get("acc_name"));

                    newValueStr = TypeUtils.castToString(rec.get("value"));

                    detailRec.set("new_id", old_id);
                    detailRec.set("new_value", newValueStr);
                    break;
                case ORG:
                    old_id = TypeUtils.castToString(accRec.get("org_id"));
                    //根据orgid查询机构名称
                    Record orgRec = Db.findById("organization", "org_id", old_id);
                    if (orgRec == null) {
                        return false;
                    }
                    old_value = TypeUtils.castToString(orgRec.get("name"));

                    newValueLong = TypeUtils.castToLong(rec.get("value"));
                    orgRec = Db.findById("organization", "org_id", newValueLong);
                    if (orgRec == null) {
                        return false;
                    }

                    detailRec.set("new_id", newValueLong);
                    detailRec.set("new_value", TypeUtils.castToString(orgRec.get("name")));
                    break;
                case BANK:
                    old_id = TypeUtils.castToString(accRec.get("bank_cnaps_code"));
                    //根据cnaps号查询银行信息
                    Record bankRec = Db.findById("all_bank_info", "cnaps_code", old_id);
                    if (bankRec == null) {
                        return false;
                    }
                    old_value = TypeUtils.castToString(bankRec.get("name"));

                    newValueStr = TypeUtils.castToString(rec.get("value"));
                    bankRec = Db.findById("all_bank_info", "cnaps_code", newValueStr);
                    if (bankRec == null) {
                        return false;
                    }

                    detailRec.set("new_id", newValueStr);
                    detailRec.set("new_value", TypeUtils.castToString(bankRec.get("name")));

                    break;
                case ACCATTR:
                    old_id = TypeUtils.castToString(accRec.get("acc_attr"));
                    Record attrRec = Db.findFirst(Db.getSql("acc.findCategory"), "acc_attr", old_id);
                    if (attrRec == null) {
                        return false;
                    }
                    old_value = TypeUtils.castToString(attrRec.get("value"));

                    newValueStr = TypeUtils.castToString(rec.get("value"));
                    attrRec = Db.findFirst(Db.getSql("acc.findCategory"), "acc_attr", newValueStr);
                    if (attrRec == null) {
                        return false;
                    }

                    detailRec.set("new_id", newValueStr);
                    detailRec.set("new_value", TypeUtils.castToString(attrRec.get("value")));

                    break;
                case CURRENCY:
                    old_id = TypeUtils.castToString(accRec.get("curr_id"));
                    //币种
                    Record currRec = Db.findById("currency", "id", old_id);
                    if (currRec == null) {
                        return false;
                    }
                    old_value = TypeUtils.castToString(currRec.get("name"));

                    newValueLong = TypeUtils.castToLong(rec.get("value"));
                    currRec = Db.findById("currency", "id", newValueLong);
                    if (currRec == null) {
                        return false;
                    }

                    detailRec.set("new_id", newValueLong);
                    detailRec.set("new_value", TypeUtils.castToString(currRec.get("name")));

                    break;
                case LAWFULLMAN:
                    //根据accid查询法人信息
                    Record accInfoRec = Db.findById("account", "acc_id", TypeUtils.castToLong(record.get("acc_id")));
                    old_id = "";
                    old_value = TypeUtils.castToString(accInfoRec.get("lawfull_man"));

                    newValueStr = TypeUtils.castToString(rec.get("value"));
                    detailRec.set("new_id", "");
                    detailRec.set("new_value", newValueStr);
                    break;
                case INACTIVEMODE:
                    old_id = TypeUtils.castToString(accRec.get("interactive_mode"));
                    old_value = TypeUtils.castToInt(old_id) == 1 ? "直联" : "人工";

                    newValueLong = TypeUtils.castToLong(rec.get("value"));
                    detailRec.set("new_id", newValueLong);
                    detailRec.set("new_value", TypeUtils.castToInt(newValueLong) == 1 ? "直联" : "人工");
                    break;
                case ACCPURPOSE:
                    old_id = TypeUtils.castToString(accRec.get("acc_purpose"));
                    Record purRec = Db.findFirst(Db.getSql("acc.findCategory"), "acc_purpose", old_id);
                    if (purRec == null) {
                        return false;
                    }
                    old_value = TypeUtils.castToString(purRec.get("value"));

                    newValueStr = TypeUtils.castToString(rec.get("value"));
                    purRec = Db.findFirst(Db.getSql("acc.findCategory"), "acc_purpose", newValueStr);
                    if (purRec == null) {
                        return false;
                    }

                    detailRec.set("new_id", newValueStr);
                    detailRec.set("new_value", TypeUtils.castToString(purRec.get("value")));
                    break;
                default:
                    return false;
            }
            detailRec.set("old_id", old_id);
            detailRec.set("old_value", old_value);


            detailList.add(detailRec);
        }
        int[] result = Db.batchSave("acc_change_apply_detail", detailList, 1000);
        if (ArrayUtil.checkDbResult(result)) {

            //删除锁定表信息
            Long oldAccId = TypeUtils.castToLong(oldChgRec.get("acc_id"));
            boolean del = Db.deleteById("acc_process_lock", "acc_id", oldAccId);
            //添加锁定表信息
            Record lockRec = new Record();
            lockRec.set("source_id", TypeUtils.castToLong(record.get("id")));
            lockRec.set("type", 1);
            lockRec.set("acc_id", TypeUtils.castToLong(record.get("acc_id")));
            lockRec.set("user_id", userInfo.getUsr_id());
            lockRec.set("user_name", userInfo.getName());
            flag = Db.save("acc_process_lock", "acc_id", lockRec);
            return flag;
        }
        return false;
    }

    /**
     * 审批通过修改account信息
     *
     * @param record
     * @throws BusinessException
     */
    public boolean pass(final Record record) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        final Record changeRec = Db.findById("acc_change_apply", "id", id);

        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(changeRec.get("acc_id")));

        List<Record> changeContent = Db.find(Db.getSql("chg.findChangeDetailByApplyId"), id);

        String newValueStr = "";
        Long newValueLong = 0L;

        final Record accountRec = new Record();
        for (Record rec : changeContent) {
            int type = TypeUtils.castToInt(rec.get("type"));

            WebConstant.AccountChangeField field = WebConstant.AccountChangeField.get(type);

            if (rec.get("new_value") == null || "".equals(rec.get("new_value"))) {
                continue;
            }
            accountRec.set("acc_id", TypeUtils.castToLong(changeRec.get("acc_id")));
            accountRec.set("acc_no", TypeUtils.castToString(accRec.get("acc_no")));

            assert field != null;
            switch (field) {
                case ACCNAME:
                    newValueStr = TypeUtils.castToString(rec.get("new_value"));
                    accountRec.set("acc_name", newValueStr);
                    break;
                case ORG:
                    newValueLong = TypeUtils.castToLong(rec.get("new_id"));
                    accountRec.set("org_id", newValueLong);
                    break;
                case BANK:
                    newValueLong = TypeUtils.castToLong(rec.get("new_id"));
                    accountRec.set("bank_cnaps_code", newValueLong);
                    break;
                case ACCATTR:
                    newValueStr = TypeUtils.castToString(rec.get("new_id"));
                    accountRec.set("acc_attr", newValueStr);
                    break;
                case CURRENCY:
                    newValueLong = TypeUtils.castToLong(rec.get("new_id"));
                    accountRec.set("curr_id", newValueLong);
                    break;
                case LAWFULLMAN:
                    newValueStr = TypeUtils.castToString(rec.get("new_value"));
                    accountRec.set("lawfull_man", newValueStr);
                    break;
                case INACTIVEMODE:
                    newValueLong = TypeUtils.castToLong(rec.get("new_id"));
                    accountRec.set("interactive_mode", newValueLong);
                    break;
                case ACCPURPOSE:
                    newValueStr = TypeUtils.castToString(rec.get("new_id"));
                    accountRec.set("acc_purpose", newValueStr);
                    break;
                default:
                    break;
            }
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //删除锁定表信息
                boolean del = Db.deleteById("acc_process_lock", "acc_id", TypeUtils.castToLong(changeRec.get("acc_id")));

                boolean upd = Db.update("account", "acc_id", accountRec);
                return (del && upd);
            }
        });

        if (flag) {
            return true;
        }

        throw new DbProcessException("变更信息失败!");
    }

}
