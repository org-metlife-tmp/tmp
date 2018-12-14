package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.QuartzCronFormatException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.*;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/17
 * @Description: 自动下拨设置
 */
public class AllocationSetService {

    /**
     * 资金下拨 - 更多列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param userInfo
     * @return
     */
    public Page<Record> morelist(int pageNum, int pageSize, final Record record, final UserInfo userInfo) {
        Kv kv = Kv.create();
        kv.set("create_by", userInfo.getUsr_id());
        kv.set("delete_flag", 0);
        SqlPara sqlPara = getListParam(record, "allocation.findMoreList", kv);
        Page<Record> page = Db.paginate(pageNum, pageSize, sqlPara);
        for (Record rec : page.getList()) {
            //根据topicid查询下拨频率
            List<Record> timesetList = Db.find(Db.getSql("allocation.findTimesettingByAlloId"), TypeUtils.castToLong(rec.get("id")));
            rec.set("frequency_detail", timesetList);
        }
        return page;
    }

    /**
     * 主账户列表
     *
     * @param record
     * @param uodpInfo
     * @return
     */
    public List<Record> mainacclist(final Record record, final UodpInfo uodpInfo) {
        Record rec = new Record()
                .set("interactive_mode", WebConstant.InactiveMode.DIRECTCONN.getKey())
                .set("org_id", uodpInfo.getOrg_id());

        List<Long> notin = record.get("excludeInstIds");
        if (notin != null && notin.size() > 0) {
            Object[] excludeInstIds = notin.toArray();
            rec.set("notin", excludeInstIds);

        }
        //查询所有账户信息(当前机构下的直连账户)
        SqlPara sqlPara = Db.getSqlPara("allocation.findAccount", Ret.by("map", rec.getColumns()));
        return Db.find(sqlPara);
    }

    /**
     * 子账户列表
     *
     * @param record
     * @param uodpInfo
     * @return
     */
    public List<Record> childacclist(final Record record, final UodpInfo uodpInfo) throws BusinessException {
        Record rec = new Record();
//                .set("interactive_mode", WebConstant.InactiveMode.DIRECTCONN.getKey());
        List<Long> notin = record.get("excludeInstIds");
        if (notin != null && notin.size() > 0) {
            Object[] excludeInstIds = notin.toArray();
            rec.set("notin", excludeInstIds);

        }

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());
        if (orgRec == null) {
            throw new ReqDataException("机构信息不存在!");
        }

        rec.set("level_code", orgRec.get("level_code"));

        String querykey = TypeUtils.castToString(record.get("query_key"));
        if (querykey != null && !"".equals(querykey)) {
            //是否包含中文
            boolean payFlag = StringKit.isContainChina(querykey);
            if (payFlag) {
                //名称
                rec.set("acc_name", querykey);
            } else {
                //帐号
                rec.set("acc_no", querykey);
            }
        }

        String accAttr = TypeUtils.castToString(record.get("acc_attr"));
        if (accAttr != null && !"".equals(accAttr)) {
            rec.set("acc_attr", accAttr);
        }

        String bankType = TypeUtils.castToString(record.get("bank_type"));
        if (bankType != null && !"".equals(bankType)) {
            rec.set("bank_type", bankType);
        }

        rec.set("not_org_num", TypeUtils.castToInt(orgRec.get("level_num")));

        HashMap<Long, Record> tempMap = new HashMap<>();

        //查询所有账户信息(当前机构下的直连账户)
        SqlPara sqlPara = Db.getSqlPara("allocation.findChildAccInfo", Ret.by("map", rec.getColumns()));
        List<Record> list = Db.find(sqlPara);
        if (list != null && list.size() > 0) {
            for (Record acc : list) {
                //返回报文组装
                long currOrgId = TypeUtils.castToLong(acc.get("org_id"));
                String currOrgName = TypeUtils.castToString(acc.get("org_name"));
                Record childRec = tempMap.get(currOrgId);
                List<Record> childAccountInfos = null;
                if (childRec == null) {
                    childAccountInfos = new ArrayList<>();
                    childRec = new Record().set("org_id", currOrgId).set("org_name", currOrgName);
                    childAccountInfos.add(acc);
                    childRec.set("accounts", childAccountInfos);
                    tempMap.put(currOrgId, childRec);
                } else {
                    childAccountInfos = childRec.get("accounts");
                    childRec.set("org_id", currOrgId).set("org_name", currOrgName);
                    childAccountInfos.add(acc);
                }
            }
        }
        list = new ArrayList<>(tempMap.values());
        return list;
    }

    /**
     * 资金下拨新增
     *
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     * @throws DbProcessException
     */
    public Record add(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws DbProcessException {
        // 获取单据编号
        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ALLOCATION);

        final List<Record> mainAccounts = record.get("main_accounts");
        final List<Object> attachmentList = record.get("files");
        record.remove("files");

        int mainAccountCount = mainAccounts.size();
        int childAccountCount = 0;
        for (Record mainAccount : mainAccounts) {
            List<Record> childAccouns = mainAccount.get("child_accounts");
            childAccountCount += childAccouns.size();
        }

        final Record topic = new Record()
                .set("service_serial_number", serviceSerialNumber)
                .set("topic", record.get("topic"))
                .set("allocation_type", record.get("allocation_type"))
                .set("allocation_amount", record.get("allocation_amount"))
                .set("allocation_frequency", record.get("allocation_frequency"))
                .set("allocation_main_account_count", mainAccountCount)
                .set("allocation_child_account_count", childAccountCount)
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("summary", record.get("summary"))
                .set("is_activity", 0)
                .set("execute_id", null)
                .set("create_by", userInfo.getUsr_id())
                .set("create_on", new Date())
                .set("create_org_id", uodpInfo.getOrg_id())
                .set("delete_flag", 0)
                .set("persist_version", 0)
                .set("attachment_count", attachmentList != null ? attachmentList.size() : 0);

        final List<Record> mainAccountList = new ArrayList<>();
        final List<Record> timesettingList = new ArrayList<>();

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                List<Record> detail = record.get("frequency_detail");
                //保存主单据
                boolean flag = Db.save("allocation_topic", topic);
                if (flag) {
                    for (Record de : detail) {
                        int frequency = TypeUtils.castToInt(topic.get("allocation_frequency"));
                        String frequencyDetal = TypeUtils.castToString(de.get("allocation_frequency_detail"));
                        String frequencyTime = TypeUtils.castToString(de.get("allocation_time"));
                        //保存归集频率
                        try {
                            Record timesetting = new Record();
                            timesetting.set("allocation_id", TypeUtils.castToLong(topic.get("id")))
                                    .set("allocation_frequency", frequency)
                                    .set("allocation_frequency_detail", frequencyDetal)
                                    .set("allocation_time", frequencyTime)
                                    .set("allocation_time_cron", QuartzKit.cronFormat(frequency, frequencyDetal, frequencyTime));

                            timesettingList.add(timesetting);
                        } catch (QuartzCronFormatException e) {
                            return false;
                        }
                    }
                    int[] result = Db.batchSave("allocation_timesetting", timesettingList, 1000);


                    if (ArrayUtil.checkDbResult(result)) {
                        //保存主账号
                        for (Record mainAccount : mainAccounts) {
                            List<Record> childAccounts = mainAccount.get("child_accounts");
                            if (childAccounts == null) {
                                return false;
                            }
                            //根据主账户id查询账户信息(当前机构下的直连账户)
                            SqlPara sqlPara = Db.getSqlPara("allocation.findAccount",
                                    Ret.by("map", new Record().set("acc_id", TypeUtils.castToLong(mainAccount.get("main_acc_id")))
                                            .set("interactive_mode", WebConstant.InactiveMode.DIRECTCONN.getKey())
                                            .set("org_id", uodpInfo.getOrg_id()).getColumns()));
                            Record mainAcc = Db.findFirst(sqlPara);

                            if (mainAcc == null) {
                                return false;
                            }

                            Record macc = new Record();
                            macc.set("tab", TypeUtils.castToString(mainAccount.get("tab")))
                                    .set("allocation_id", TypeUtils.castToLong(topic.get("id")))
                                    .set("main_acc_org_id", TypeUtils.castToLong(mainAcc.get("org_id")))
                                    .set("main_acc_org_name", TypeUtils.castToString(mainAcc.get("org_name")))
                                    .set("main_acc_id", TypeUtils.castToLong(mainAcc.get("acc_id")))
                                    .set("main_acc_no", TypeUtils.castToString(mainAcc.get("acc_no")))
                                    .set("main_acc_name", TypeUtils.castToString(mainAcc.get("acc_name")))
                                    .set("main_acc_bank_name", TypeUtils.castToString(mainAcc.get("bank_name")))
                                    .set("main_acc_bank_cnaps_code", TypeUtils.castToString(mainAcc.get("bank_cnaps_code")))
                                    .set("main_acc_bank_prov", TypeUtils.castToString(mainAcc.get("province")))
                                    .set("main_acc_bank_city", TypeUtils.castToString(mainAcc.get("city")))
                                    .set("main_acc_cur", TypeUtils.castToString(mainAcc.get("iso_code")))
                                    .set("child_account_count", childAccounts.size());
                            mainAccountList.add(macc);

                            flag = Db.save("allocation_main_account", macc);
                            if (flag) {
                                List<Record> childAccountList = new ArrayList<>();
                                //保存子账户
                                for (int i = 0; i < childAccounts.size(); i++) {
                                    if (childAccounts.get(i) == null) {
                                        return false;
                                    }

                                    macc.set("child_accounts", childAccounts);
                                    //根据子账户查询账户信息
                                    sqlPara = Db.getSqlPara("allocation.findAccount",
                                            Ret.by("map", new Record().set("acc_id", childAccounts.get(i)).getColumns()));
                                    Record childAcc = Db.findFirst(sqlPara);

                                    if (childAcc == null) {
                                        return false;
                                    }
                                    Record chacc = new Record();
                                    chacc.set("allocation_id", TypeUtils.castToLong(topic.get("id")))
                                            .set("allocation_main_account_id", TypeUtils.castToLong(macc.get("id")))
                                            .set("child_acc_org_id", TypeUtils.castToLong(childAcc.get("org_id")))
                                            .set("child_acc_org_name", TypeUtils.castToString(childAcc.get("org_name")))
                                            .set("child_acc_id", TypeUtils.castToLong(childAcc.get("acc_id")))
                                            .set("child_acc_no", TypeUtils.castToString(childAcc.get("acc_no")))
                                            .set("child_acc_name", TypeUtils.castToString(childAcc.get("acc_name")))
                                            .set("child_acc_bank_name", TypeUtils.castToString(childAcc.get("bank_name")))
                                            .set("child_acc_bank_cnaps_code", TypeUtils.castToString(childAcc.get("bank_cnaps_code")))
                                            .set("child_acc_bank_prov", TypeUtils.castToString(mainAcc.get("province")))
                                            .set("child_acc_bank_city", TypeUtils.castToString(mainAcc.get("city")))
                                            .set("child_acc_cur", TypeUtils.castToString(mainAcc.get("iso_code")));
                                    childAccountList.add(chacc);
                                    macc.set("child_accounts", childAccountList);
                                }
                                result = Db.batchSave("allocation_child_account", childAccountList, 1000);
                            }
                            if (!ArrayUtil.checkDbResult(result)) {
                                return false;
                            }
                        }

                        //保存附件
                        if (attachmentList != null && attachmentList.size() > 0) {
                            return CommonService.saveFileRef(WebConstant.MajorBizType.ALLOCATION.getKey(),
                                    TypeUtils.castToLong(topic.get("id")), attachmentList);
                        }
                        return true;

                    }
                    //保存归集频率失败
                    return false;
                }
                //保存主单据失败
                return false;
            }
        });

        if (flag) {
            topic.set("frequency_detail", timesettingList);
            topic.set("main_accounts", mainAccountList);

            return topic;
        }
        throw new DbProcessException("保存资金下拨设置失败!");
    }

    /**
     * 资金下拨修改
     *
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     * @throws BusinessException
     */
    public Record chg(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        int version = TypeUtils.castToInt(record.get("persist_version"));
        //根据id和version查询单据信息
        Record topicRec = Db.findFirst(Db.getSql("allocation.findAllocationTopicByIDVersion"), id, version);
        if (topicRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(topicRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        int status = TypeUtils.castToInt(topicRec.get("service_status"));
        final List<Record> mainAccounts = record.get("main_accounts");
        final List<Record> detail = record.get("frequency_detail");
        final List<Object> attachmentList = record.get("files");
        record.remove("main_accounts");
        record.remove("frequency_detail");
        record.remove("files");

        int mainAccountCount = mainAccounts.size();
        int childAccountCount = 0;
        for (Record mainAccount : mainAccounts) {
            List<Record> childAccouns = mainAccount.get("child_accounts");
            childAccountCount += childAccouns.size();
        }

//        final Record timesetting = new Record();
        final List<Record> timesettingList = new ArrayList<>();
//        final Record macc = new Record();
        final List<Record> mainAccountList = new ArrayList<>();
//        final Record chacc = new Record();

        if (status != WebConstant.BillStatus.SAVED.getKey() &&
                status != WebConstant.BillStatus.REJECT.getKey()) {
            throw new ReqDataException("该单据状态不正确，请重试!");
        }
        //修改主题信息
        final Record topic = new Record()
                .set("topic", record.get("topic"))
                .set("allocation_type", record.get("allocation_type"))
                .set("allocation_amount", record.get("allocation_amount"))
                .set("allocation_frequency", record.get("allocation_frequency"))
                .set("allocation_main_account_count", mainAccountCount)
                .set("allocation_child_account_count", childAccountCount)
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("summary", record.get("summary"))
                .set("is_activity", 0)
                .set("execute_id", null)
                .set("update_by", userInfo.getUsr_id())
                .set("update_on", new Date())
                .set("attachment_count", attachmentList != null ? attachmentList.size() : 0);


        final boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                int old_version = TypeUtils.castToInt(record.get("persist_version"));
                topic.set("persist_version", old_version + 1);
                record.remove("id");

                boolean result = CommonService.update("allocation_topic",
                        topic,
                        new Record().set("id", id).set("persist_version", old_version));
                if (result) {
                    //删除归集频率
                    Db.delete(Db.getSql("allocation.delTimesettingByAllocationId"), id);
                    //保存归集频率
                    for (Record de : detail) {
                        int frequency = TypeUtils.castToInt(topic.get("allocation_frequency"));
                        String frequencyDetal = TypeUtils.castToString(de.get("allocation_frequency_detail"));
                        String frequencyTime = TypeUtils.castToString(de.get("allocation_time"));
                        //保存归集频率
                        try {
                            Record timesetting = new Record();
                            timesetting.set("allocation_id", id)
                                    .set("allocation_frequency", frequency)
                                    .set("allocation_frequency_detail", frequencyDetal)
                                    .set("allocation_time", frequencyTime)
                                    .set("allocation_time_cron", QuartzKit.cronFormat(frequency, frequencyDetal, frequencyTime));

                            timesettingList.add(timesetting);
                        } catch (QuartzCronFormatException e) {
                            return false;
                        }
                    }
                    int[] timeRes = Db.batchSave("allocation_timesetting", timesettingList, 1000);
                    //删除归集主账号、子账号
                    Db.delete(Db.getSql("allocation.delMainAccByAllocationId"), id);
                    Db.delete(Db.getSql("allocation.delChildAccByAllocationId"), id);


                    if (ArrayUtil.checkDbResult(timeRes)) {
                        //保存主账号
                        for (Record mainAccount : mainAccounts) {
                            List<Record> childAccounts = mainAccount.get("child_accounts");
                            if (childAccounts == null) {
                                return false;
                            }

                            //根据主账户id查询账户信息(当前机构下的直连账户)
                            SqlPara sqlPara = Db.getSqlPara("allocation.findAccount",
                                    Ret.by("map", new Record().set("acc_id", TypeUtils.castToLong(mainAccount.get("main_acc_id")))
                                            .set("interactive_mode", WebConstant.InactiveMode.DIRECTCONN.getKey())
                                            .set("org_id", uodpInfo.getOrg_id()).getColumns()
                                    )
                            );
                            Record mainAcc = Db.findFirst(sqlPara);

                            if (mainAcc == null) {
                                return false;
                            }
                            Record macc = new Record();
                            macc.set("tab", TypeUtils.castToString(mainAccount.get("tab")))
                                    .set("allocation_id", id)
                                    .set("main_acc_org_id", TypeUtils.castToLong(mainAcc.get("org_id")))
                                    .set("main_acc_org_name", TypeUtils.castToString(mainAcc.get("org_name")))
                                    .set("main_acc_id", TypeUtils.castToLong(mainAcc.get("acc_id")))
                                    .set("main_acc_no", TypeUtils.castToString(mainAcc.get("acc_no")))
                                    .set("main_acc_name", TypeUtils.castToString(mainAcc.get("acc_name")))
                                    .set("main_acc_bank_name", TypeUtils.castToString(mainAcc.get("bank_name")))
                                    .set("main_acc_bank_cnaps_code", TypeUtils.castToString(mainAcc.get("bank_cnaps_code")))
                                    .set("main_acc_bank_prov", TypeUtils.castToString(mainAcc.get("province")))
                                    .set("main_acc_bank_city", TypeUtils.castToString(mainAcc.get("city")))
                                    .set("main_acc_cur", TypeUtils.castToString(mainAcc.get("iso_code")))
                                    .set("child_account_count", childAccounts.size());
                            mainAccountList.add(macc);

                            boolean flag = Db.save("allocation_main_account", macc);
                            if (flag) {
                                List<Record> childAccountList = new ArrayList<>();
                                //保存子账户
                                for (int i = 0; i < childAccounts.size(); i++) {
                                    if (childAccounts.get(i) == null) {
                                        return false;
                                    }

                                    //根据子账户查询账户信息
                                    sqlPara = Db.getSqlPara("allocation.findAccount",
                                            Ret.by("map", new Record().set("acc_id", childAccounts.get(i)).getColumns()));
                                    Record childAcc = Db.findFirst(sqlPara);

                                    if (childAcc == null) {
                                        return false;
                                    }
                                    Record chacc = new Record();
                                    chacc.set("allocation_id", id)
                                            .set("allocation_main_account_id", TypeUtils.castToLong(macc.get("id")))
                                            .set("child_acc_org_id", TypeUtils.castToLong(childAcc.get("org_id")))
                                            .set("child_acc_org_name", TypeUtils.castToString(childAcc.get("org_name")))
                                            .set("child_acc_id", TypeUtils.castToLong(childAcc.get("acc_id")))
                                            .set("child_acc_no", TypeUtils.castToString(childAcc.get("acc_no")))
                                            .set("child_acc_name", TypeUtils.castToString(childAcc.get("acc_name")))
                                            .set("child_acc_bank_name", TypeUtils.castToString(childAcc.get("bank_name")))
                                            .set("child_acc_bank_cnaps_code", TypeUtils.castToString(childAcc.get("bank_cnaps_code")))
                                            .set("child_acc_bank_prov", TypeUtils.castToString(mainAcc.get("province")))
                                            .set("child_acc_bank_city", TypeUtils.castToString(mainAcc.get("city")))
                                            .set("child_acc_cur", TypeUtils.castToString(mainAcc.get("iso_code")));
                                    flag = Db.save("allocation_child_account", chacc);

                                    childAccountList.add(chacc);
                                    macc.set("child_accounts", childAccountList);

                                    if (!flag) {
                                        //保存子账户失败
                                        return false;
                                    }
                                }
                            } else {
                                //保存主账户失败
                                return false;
                            }
                        }
                        //删除附件
                        CommonService.delFileRef(WebConstant.MajorBizType.ALLOCATION.getKey(), id);
                        if (attachmentList != null && attachmentList.size() > 0) {
                            //保存附件
                            return CommonService.saveFileRef(WebConstant.MajorBizType.ALLOCATION.getKey(),
                                    id, attachmentList);
                        }
                        return true;
                    }
                    //保存归集频率失败
                    return false;
                }
                //修改主题失败
                return false;
            }
        });
        if (flag) {
            topic.set("frequency_detail", timesettingList);
            topic.set("main_accounts", mainAccountList);
            topic.set("service_serial_number", TypeUtils.castToString(topicRec.get("service_serial_number")));
            topic.set("id", id);
            return topic;
        }

        throw new DbProcessException("修改资金下拨设置失败!");
    }

    /**
     * 资金下拨删除
     *
     * @param record
     * @throws DbProcessException
     */
    public void del(final Record record,final UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));

        Record billRec = Db.findById("allocation_topic","id",id);
        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(billRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("persist_version", old_version + 1);
        record.set("delete_flag", 1);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                record.remove("id");
                //要更新的列
                return CommonService.update("allocation_topic",
                        record,
                        new Record().set("id", id).set("persist_version", old_version));
            }
        });

        if (!flag) {
            throw new DbProcessException("删除资金下拨信息失败!");
        }
    }

    /**
     * 资金下拨查看详细
     *
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));

        Record topicRec = Db.findById("allocation_topic", "id", id);
        if (topicRec == null) {
            throw new ReqDataException("未找到有效的资金下拨信息!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if(record.get("wf_inst_id") == null || record.get("biz_type") == null){
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(),topicRec.getLong("create_org_id"));
        }

        //frequency_detail 根据主题id查询归集频率
        List<Record> frequencyDetailList = Db.find(Db.getSql("allocation.findTimesettingByAlloId"), id);
        if (frequencyDetailList == null || frequencyDetailList.size() == 0) {
            throw new ReqDataException("未找到有效的归集频率!");
        }
        topicRec.set("frequency_detail", frequencyDetailList);

        //主账号
        List<Record> mainAccList = Db.find(Db.getSql("allocation.findMainAccByAlloId"), id);
        if (mainAccList == null || mainAccList.size() == 0) {
            throw new ReqDataException("未找到有效的主账户信息!");
        }

        for (Record main : mainAccList) {
            //根据main_acc_id allocation_id查询子账户数据
            List<Record> childList = Db.find(Db.getSql("allocation.findChildAccByAlloId"), id, main.get("id"));
            main.set("child_accounts", childList);
        }


        topicRec.set("main_accounts", mainAccList);
        return topicRec;
    }

    public boolean pass(final Record record) {
        final long id = TypeUtils.castToLong(record.get("id"));

        final List<Record> timesettingList = Db.find(Db.getSql("allocation.findTimesettingByAlloId"), id);
        if (timesettingList != null && timesettingList.size() > 0) {
            return Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    for (Record timeSetting : timesettingList) {
                        Record quartz = new Record()
                                .set("name", WebConstant.CronTaskGroup.ALLOCATION.getDesc())
                                .set("class_name", WebConstant.CronTaskGroup.ALLOCATION.getClassPath())
                                .set("groups", WebConstant.CronTaskGroup.ALLOCATION.getPrefix() + id)
                                .set("cron", timeSetting.get("allocation_time_cron"))
                                .set("type", 1)
                                .set("is_need_scaned", 1)
                                .set("enable", 0)
                                .set("param", "{'id':'" + id + "'}");
                        boolean flag = Db.save("cfm_quartz", quartz);
                        if (!flag) {
                            return false;
                        }
                    }
                    return true;
                }
            });
        }
        return false;
    }


    /**
     * 查询参数组装
     *
     * @param record
     * @param sql
     * @param kv
     * @return
     */
    public SqlPara getListParam(final Record record, String sql, final Kv kv) {
        List<Record> mainAccountList = null;
        String mainacc = TypeUtils.castToString(record.get("main_account_query"));
        Record mainrec = new Record();
        //是否包含中文
        if (StringKit.isContainChina(mainacc)) {
            //名称
            mainrec.set("main_acc_name", mainacc);
        } else {
            //帐号
            mainrec.set("main_acc_no", mainacc);
        }
        if (mainacc != null && !"".equals(mainacc)) {
            //根据主账户号查询符号条件的主表账户id
            SqlPara sqlPara = Db.getSqlPara("allocation.findMainAccount", Ret.by("map", mainrec.getColumns()));
            mainAccountList = Db.find(sqlPara);
            Object[] poolids = null;
            if (mainAccountList.size() > 0) {
                poolids = mainAccountList.toArray();
            }
            kv.set("allocationids", poolids);
        }

        String queryKey = TypeUtils.castToString(record.get("query_key"));
        if (queryKey != null && !"".equals(queryKey)) {
            kv.set("query_key", queryKey);
        }

        Integer allocation_frequency = TypeUtils.castToInt(record.get("allocation_frequency"));
        if (allocation_frequency != null) {
            kv.set("allocation_frequency", allocation_frequency);
        }

        Integer allocation_type = TypeUtils.castToInt(record.get("allocation_type"));
        if (allocation_type != null) {
            kv.set("allocation_type", allocation_type);
        }

        List<Record> isacti = record.get("is_activity");
        if (isacti != null && isacti.size() > 0) {
            kv.set("is_activity", record.get("is_activity"));
        }

        kv.set("service_status", record.get("service_status"));
        return Db.getSqlPara(sql, Kv.by("map", kv));
    }


}
