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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 开户事项补录
 *
 * @auther zhangyuanyuan
 * @create 2018/6/27
 */

public class OpenCompleteService {

    /**
     * 开户事项补录待办列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> findOpenCompleteToPage(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("aoc.findOpenCompleteToPage", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 开户事项补录已办列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> findOpenCompleteDonePage(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("aoc.findOpenCompleteDonePage", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 开户信息补录新增
     *
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record add(final Record record, final UserInfo userInfo) throws BusinessException {

        //查询锁定表是否有补录信息
        Record lockRec = Db.findById("acc_open_intention_process_lock", "source_id", TypeUtils.castToLong(record.get("relation_id")));
        if (lockRec != null) {
            throw new ReqDataException("此申请单已有人操作，不可重复补录!");
        }


        //申请单补录人id非分发范围内用户，不允许进行操作
        Record issueRec = Db.findById("acc_open_intention_apply_issue", "bill_id,user_id", TypeUtils.castToLong(record.get("relation_id")), userInfo.getUsr_id());
        if (issueRec == null) {
            throw new ReqDataException("无权进行此操作！");
        }

        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ACC_OPEN_COM);

        //根据账户号查询该账户是否已存在
        Record accountRec = Db.findById("account", "acc_id", TypeUtils.castToLong(record.get("acc_id")));
        if (accountRec != null) {
            throw new ReqDataException("该账户号已存在!");
        }

        //通过relationid查询开户事项信息
        final Record intRec = Db.findById("acc_open_intention_apply", "id", TypeUtils.castToLong(record.get("relation_id")));
        if (intRec == null) {
            throw new ReqDataException("未找到有效的开户事项信息!");
        }

        record.set("org_id", intRec.get("org_id"));
        record.set("apply_on", TypeUtils.castToDate(intRec.get("apply_on")));

        record.set("bank_type", TypeUtils.castToString(intRec.get("bank_type")));
        record.set("bank_cnaps_code", TypeUtils.castToString(intRec.get("bank_cnaps_code")));
        record.set("lawfull_man", TypeUtils.castToString(intRec.get("lawfull_man")));
        record.set("curr_id", TypeUtils.castToLong(intRec.get("curr_id")));
        record.set("interactive_mode", TypeUtils.castToInt(intRec.get("interactive_mode")));
        record.set("acc_attr", TypeUtils.castToString(intRec.get("acc_attr")));
        record.set("acc_purpose", TypeUtils.castToString(intRec.get("acc_purpose")));
        record.set("area_code", TypeUtils.castToString(intRec.get("area_code")));
        record.set("deposits_mode", TypeUtils.castToString(intRec.get("deposits_mode")));


        record.set("create_on", new Date());
        record.set("create_by", userInfo.getUsr_id());
        record.set("service_serial_number", serviceSerialNumber);
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("persist_version", 0);

        final List<Object> list = record.get("files");

        record.remove("files");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //保存补录信息
                boolean flag = Db.save("acc_open_complete_apply", "id", record);
                if (flag) {
                    //修改开户申请状态为已完结
                    int old_version = TypeUtils.castToInt(intRec.get("persist_version"));

                    flag = CommonService.update("acc_open_intention_apply",
                            new Record()
                                    .set("persist_version", old_version + 1)
                                    .set("service_status", WebConstant.BillStatus.COMPLETION.getKey()),
                            new Record()
                                    .set("id", TypeUtils.castToLong(record.get("relation_id")))
                                    .set("persist_version", old_version));
                    if (flag) {
                        //插入锁定表信息

                        flag = Db.save("acc_open_intention_process_lock", "source_id",
                                new Record().set("source_id", TypeUtils.castToLong(record.get("relation_id")))
                                        .set("target_id", TypeUtils.castToLong(record.get("id")))
                                        .set("user_id", userInfo.getUsr_id())
                                        .set("user_name", userInfo.getName())
                        );

                        if (flag) {
                            if (list != null && list.size() > 0) {
                                //保存附件
                                return CommonService.saveFileRef(WebConstant.MajorBizType.ACC_OPEN_COM.getKey(),
                                        TypeUtils.castToLong(record.get("id")), list);
                            }
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
        });

        if (flag) {
            return Db.findById("acc_open_complete_apply", "id", record.get("id"));
        }

        throw new DbProcessException("新增开户补录信息失败!");
    }

    /**
     * 开户信息补录修改
     *
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record chg(final Record record, final UserInfo userInfo) throws BusinessException {

        //修改补录信息
        final long id = TypeUtils.castToLong(record.get("id"));

        Record comRec = Db.findById("acc_open_complete_apply", "id", id);

        if (comRec == null) {
            throw new ReqDataException("未找到有效的开户补录信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(comRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        //根据账户号查询该账户是否已存在
        Record accountRec = Db.findById("account", "acc_id", TypeUtils.castToLong(record.get("acc_id")));
        if (accountRec != null) {
            throw new ReqDataException("该账户号已存在!");
        }


        final List<Object> list = record.get("files");
        record.remove("files");

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("persist_version", old_version + 1);
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("update_on", new Date());
        record.set("update_by", userInfo.getUsr_id());
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());

        //移除ID，兼容sqlserver
        record.remove("id");
//        //要更新的列

        boolean flag = Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {
                boolean flag = CommonService.update("acc_open_complete_apply",
                        record,
                        new Record().set("id", id).set("persist_version", old_version));
                //Db.update(Db.getSqlPara("aoc.chgOpenCompleteByIdAndVersion", Kv.by("map", kv)));
                if (flag) {
                    //删除锁定表信息
                    Db.deleteById("acc_open_intention_process_lock", "source_id", TypeUtils.castToLong(record.get("relation_id")));
                    //重新插入锁定表信息
                    flag = Db.save("acc_open_intention_process_lock", "source_id",
                            new Record().set("source_id", TypeUtils.castToLong(record.get("relation_id")))
                                    .set("target_id", id)
                                    .set("user_id", userInfo.getUsr_id())
                                    .set("user_name", userInfo.getName()));
                    if (flag) {
                        //删除附件
                        CommonService.delFileRef(WebConstant.MajorBizType.ACC_OPEN_COM.getKey(), id);
                        if (list != null && list.size() > 0) {
                            //保存附件
                            return CommonService.saveFileRef(WebConstant.MajorBizType.ACC_OPEN_COM.getKey(),
                                    id, list);
                        }
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });

        if (flag) {
            return Db.findById("acc_open_complete_apply", "id", id);
        }

        throw new DbProcessException("修改开户补录信息失败!");
    }

    /**
     * 开户信息补录查看
     *
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //根据id查询补录信息
        long id = TypeUtils.castToLong(record.get("id"));
        Record comRec = Db.findById("acc_open_complete_apply", "id", id);

        if (comRec == null) {
            throw new ReqDataException("未找到有效的开户补录信息!");
        }


        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), comRec.getLong("org_id"));
        }

        Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(comRec.get("bank_cnaps_code")));
        if (bankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }
        comRec.set("bank_name", TypeUtils.castToString(bankRec.get("name")));
        comRec.set("province", TypeUtils.castToString(bankRec.get("province")));
        comRec.set("city", TypeUtils.castToString(bankRec.get("city")));
        comRec.set("open_date", format.format(TypeUtils.castToDate(comRec.get("open_date"))));
        comRec.set("apply_on", format.format(TypeUtils.castToDate(comRec.get("apply_on"))));

        //根据币种id查询币种名称
        Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(comRec.get("curr_id")));
        if (currRec == null) {
            throw new ReqDataException("未找到有效的币种信息!");
        }
        comRec.set("curr_name", TypeUtils.castToString(currRec.get("name")));
        //根据机构id查询机构名称

        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(comRec.get("org_id")));
        if (orgRec == null) {
            throw new ReqDataException("未找到有效的机构信息!");
        }
        comRec.set("org_name", TypeUtils.castToString(orgRec.get("name")));

        //查询账户属性
        Record attrRec = Db.findById("category_value", "cat_code,key", "acc_attr", TypeUtils.castToString(comRec.get("acc_attr")));
        if (attrRec == null) {
            throw new ReqDataException("未找到有效的账户属性!");
        }
        comRec.set("acc_attr_name", TypeUtils.castToString(attrRec.get("value")));

        //查询账户用途
        Record purRec = Db.findById("category_value", "cat_code,key", "acc_purpose", TypeUtils.castToString(comRec.get("acc_purpose")));
        if (purRec == null) {
            throw new ReqDataException("未找到有效的账户用途!");
        }
        comRec.set("acc_purpose_name", TypeUtils.castToString(purRec.get("value")));
        return comRec;
    }

    /**
     * 开户信息补录删除
     *
     * @param record
     * @param userInfo
     * @throws BusinessException
     */
    public void del(final Record record, UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));

        Record comRec = Db.findById("acc_open_complete_apply", "id", id);

        if (comRec == null) {
            throw new ReqDataException("未找到有效的开户补录信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(comRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }


        final Record intRec = Db.findById("acc_open_intention_apply", "id", TypeUtils.castToLong(comRec.get("relation_id")));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //删除补录信息
                int old_version = TypeUtils.castToInt(record.get("persist_version"));
                boolean flag = CommonService.update("acc_open_complete_apply",
                        new Record()
                                .set("persist_version", old_version + 1)
                                .set("delete_flag", 1),
                        new Record()
                                .set("id", id)
                                .set("persist_version", old_version));
                if (flag) {
                    //开户申请改为审批通过
                    old_version = TypeUtils.castToInt(intRec.get("persist_version"));

                    //修改开户申请状态为已通过
                    boolean result = CommonService.update("acc_open_intention_apply",
                            new Record()
                                    .set("persist_version", old_version + 1)
                                    .set("service_status", WebConstant.BillStatus.PASS.getKey()),
                            new Record()
                                    .set("id", TypeUtils.castToLong(intRec.get("id")))
                                    .set("persist_version", old_version));
                    if (result) {
                        //删除锁定表信息
                        flag = Db.deleteById("acc_open_intention_process_lock", "source_id", TypeUtils.castToLong(intRec.get("id")));
                        if (flag) {
                            //删除附件
                            CommonService.delFileRef(WebConstant.MajorBizType.ACC_OPEN_COM.getKey(), TypeUtils.castToLong(record.get("id")));
                            return true;
                        }
                        return false;
                    }
                }
                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("删除开户补录信息失败!");
        }
    }

    public boolean pass(final Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        //插入账户表
        //根据补录id 查询
        final Record comRec = Db.findById("acc_open_complete_apply", "id", id);

        final Record accountRec = new Record();
        accountRec.set("acc_no", TypeUtils.castToString(comRec.get("acc_no")));
        accountRec.set("acc_name", TypeUtils.castToString(comRec.get("acc_name")));
        accountRec.set("acc_attr", TypeUtils.castToString(comRec.get("acc_attr")));
        accountRec.set("acc_purpose", TypeUtils.castToString(comRec.get("acc_purpose")));
        accountRec.set("open_date", TypeUtils.castToDate(comRec.get("open_date")));
        accountRec.set("lawfull_man", TypeUtils.castToString(comRec.get("lawfull_man")));
        accountRec.set("curr_id", TypeUtils.castToLong(comRec.get("curr_id")));
        accountRec.set("bank_cnaps_code", TypeUtils.castToString(comRec.get("bank_cnaps_code")));
        accountRec.set("org_id", TypeUtils.castToLong(comRec.get("org_id")));
        accountRec.set("interactive_mode", TypeUtils.castToInt(comRec.get("interactive_mode")));
        accountRec.set("is_activity", 0);
        accountRec.set("status", WebConstant.SetAccAndMerchStatus.NORMAL.getKey());
        accountRec.set("memo", TypeUtils.castToString(comRec.get("memo")));
        accountRec.set("deposits_mode", TypeUtils.castToInt(comRec.get("deposits_mode")));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean flag = Db.save("account", "acc_id", accountRec);
                if (flag) {
                    List<Record> extAccList = new ArrayList<>();

                    //查询账户拓展信息类型
                    List<Record> extTypeRec = Db.find(Db.getSql("acc.findAccountExtType"));
                    for (Record ext : extTypeRec) {
                        String value = comRec.get(TypeUtils.castToString(ext.get("code"))); //增加判断 ，如果扩展信息为空，不进行插入
                        if (value != null && !"".equals(value)) {
                            Record extRec = new Record();
                            extRec.set("acc_id", TypeUtils.castToLong(accountRec.get("acc_id")));
                            extRec.set("type_code", TypeUtils.castToString(ext.get("code")));
                            extRec.set("value", value);
                            extAccList.add(extRec);
                        }
                    }

                    //账户拓展信息插入
                    if (extAccList != null && extAccList.size() > 0) {
                        int[] result = Db.batchSave("acc_extra_info", extAccList, extAccList.size());
                    }


                    //补录信息帐号回写
                    return Db.update("acc_open_complete_apply", "id", new Record().set("acc_id", TypeUtils.castToLong(accountRec.get("acc_id"))).set("id", id));
                }
                return false;
            }
        });
        if (!flag) {
            throw new DbProcessException("补录新增账户保存失败!");
        }
        return true;
    }

}
