package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.QuartzCronFormatException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.QuartzKit;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.seeyon.ctp.common.authenticate.domain.xsd.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CollectSettingService {

    public void copy(Record record, long usr_id, long org_id) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        //查询 topic
        Record topic = Db.findById("collect_topic", "id", id);
        checkTopicST(topic);
        //查询归集频率
        final List<Record> timeSettingList = Db.find(Db.getSql("collect_setting.findTimeSettingList"), id);

        //查询归集主账户
        final List<Record> mainAccList = Db.find(Db.getSql("collect_setting.findMainAccList"), id);

        //查询归集子账户
        final List<Record> childAccList = Db.find(Db.getSql("collect_setting.findChildAccList"), id);

        //查询附件
        final List<Record> attachmentFile = CommonService.findFiles(WebConstant.MajorBizType.GJT.getKey(), id);

        final Record topicCopy = topic.remove("id")
                .set("service_serial_number", BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.GJT))
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("delete_flag", WebConstant.YesOrNo.NO.getKey())
                .set("is_activity", WebConstant.YesOrNo.NO.getKey())
                .set("create_by", usr_id)
                .set("create_on", new Date())
                .set("create_org_id", org_id)
                .set("persist_version", 0);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //保存归集单据信息
                boolean topicCopyFlag = Db.save("collect_topic", "id", topicCopy);
                if (!topicCopyFlag) return false;
                long collect_id = TypeUtils.castToLong(topicCopy.get("id"));
                //保存附件
                if (saveCopyData(attachmentFile, "common_attachment_info_ref", collect_id)) return false;
                //保存主账户
                if (saveCopyData(mainAccList, "collect_main_account", collect_id)) return false;
                //保存子账户
                if (saveCopyData(childAccList, "collect_child_account", collect_id)) return false;
                //保存归集频率
                if (saveCopyData(timeSettingList, "collect_timesetting", collect_id)) return false;
                return true;
            }
        });

        if (!flag) {
            throw new DbProcessException("复制单归集通据失败！");
        }
    }

    private boolean saveCopyData(List<Record> list, String table_name, long collect_id) {
        if (list != null && list.size() > 0) {
            for (Record _ts : list) {
                _ts.remove("id");
                _ts.set("collect_id", collect_id);
            }
            int[] tsres = Db.batchSave(table_name, list, 1000);
            if (!ArrayUtil.checkDbResult(tsres)) return true;
        }
        return false;
    }

    public Record add(final Record record, final long usr_id, final long org_id) throws BusinessException {
        //主账户与子账户信息
        final List<Record> main_list = record.get("main_list");

        //归集频率  数组形式，使用 - 分隔
        final List<Object> timesettingList = record.get("timesetting_list");
        //附件信息
        final List<Object> fileList = record.get("files");
        //归集单据表信息(collect_topic)
        //计算被归集子账户数量。
        int childCount = getChildCount(main_list);
        final Record topic = new Record()
                .set("service_serial_number", BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.GJT))
                .set("collect_type", record.get("collect_type"))
                .set("topic", record.get("topic"))
                .set("collect_frequency", record.get("collect_frequency"))
                .set("collect_main_account_count", main_list.size())
                .set("collect_child_account_count", childCount)
                .set("collect_amount", TypeUtils.castToBigDecimal(record.get("collect_amount")))
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("summary", record.get("summary"))
                .set("is_activity", WebConstant.YesOrNo.NO.getKey())
                .set("create_by", usr_id)
                .set("create_on", new Date())
                .set("create_org_id", org_id)
                .set("delete_flag", WebConstant.YesOrNo.NO.getKey())
                .set("persist_version", 0)
                .set("attachment_count", (fileList == null || fileList.isEmpty()) ? 0 : fileList.size());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean topicFlag = Db.save("collect_topic", "id", topic);
                if (!topicFlag) return topicFlag;
                //得到单据ID
                long collect_id = TypeUtils.castToLong(topic.get("id"));
                //存储附件信息
                boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.GJT.getKey(), collect_id, fileList);
                if (!saveFileRef) return false;


                //子账户列表 - 批量存储
                List<Record> childAccList = new ArrayList<>();
                //存储主账户信息
                boolean mainAccFlag = saveMainAccListAndFillChildAccList(collect_id, childAccList, main_list);
                if (!mainAccFlag) return false;

                int[] childAccounts = Db.batchSave("collect_child_account", childAccList, 1000);
                if (!ArrayUtil.checkDbResult(childAccounts)) return false;

                //保存归集频率
                List<Record> collectTimeSettingList = new ArrayList<>();
                if (genCollectTimesettingList(collect_id, collectTimeSettingList, timesettingList, record))
                    return false;
                int[] collectTimeSettings = Db.batchSave("collect_timesetting", collectTimeSettingList, 1000);
                if (!ArrayUtil.checkDbResult(collectTimeSettings)) return false;
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("创建归集单据失败！");
        }

        return topic;
    }

    private int getChildCount(List<Record> main_list) throws BusinessException {
        int childCount = 0;
        for (Record _p : main_list) {
            List<Record> _cl = _p.get("child_list");
            if (_cl == null || _cl.isEmpty()) {
                throw new ReqDataException("归集主账户【" + TypeUtils.castToString(_p.get("main_acc_no")) + "】下的归集子账户为空！");
            }
            childCount += _cl.size();
        }
        return childCount;
    }

    private boolean genCollectTimesettingList(long collect_id, List<Record> collectTimeSettingList, List<Object> timesettingList, Record record) {
        for (Object s : timesettingList) {
            String string = TypeUtils.castToString(s);
            int frequencyType = TypeUtils.castToInt(record.get("collect_frequency"));
            String cronFormat = null;
            try {
                if (frequencyType == WebConstant.CollOrPoolFrequency.DAILY.getKey()) {
                    cronFormat = QuartzKit.cronFormat(frequencyType, "", string);
                } else {
                    String[] _ts = string.split("-");
                    cronFormat = QuartzKit.cronFormat(
                            frequencyType,
                            TypeUtils.castToString(_ts[0]),//date
                            TypeUtils.castToString(_ts[1])//time
                    );
                }
            } catch (QuartzCronFormatException e) {
                e.printStackTrace();
                return true;
            }
            Record _timesetting = new Record()
                    .set("collect_id", collect_id)
                    .set("collect_frequency", record.get("collect_frequency"))
                    .set("collect_frequency_detail", record.get("collect_frequency_detail"))
                    .set("collect_time_cron", cronFormat)
                    .set("collect_time", string);
            collectTimeSettingList.add(_timesetting);
        }
        return false;
    }

    private boolean saveMainAccListAndFillChildAccList(long collect_id, List<Record> childAccList, List<Record> mainAccList) {
        for (Record m : mainAccList) {
            //取到 tab 的 main
            List<Record> childAcc = m.get("child_list");
            Record _mainAcc = new Record()
                    .set("tab", m.get("tab"))
                    .set("collect_id", collect_id)
                    .set("main_acc_org_id", m.get("main_acc_org_id"))
                    .set("main_acc_org_name", m.get("main_acc_org_name"))
                    .set("main_acc_id", m.get("main_acc_id"))
                    .set("main_acc_no", m.get("main_acc_no"))
                    .set("main_acc_name", m.get("main_acc_name"))
                    .set("main_acc_bank_name", m.get("main_acc_bank_name"))
                    .set("main_acc_bank_cnaps_code", m.get("main_acc_bank_cnaps_code"))
                    .set("main_acc_cur", m.get("main_acc_cur"))
                    .set("main_acc_bank_prov", m.get("main_acc_bank_prov"))
                    .set("main_acc_bank_city", m.get("main_acc_bank_city"))
                    .set("child_account_count", childAcc.size());
            boolean mainAccFlag = Db.save("collect_main_account", "id", _mainAcc);
            if (!mainAccFlag) return mainAccFlag;
            long _mainId = TypeUtils.castToLong(_mainAcc.get("id"));

            //当前主账户下 子账户信息
            for (Record _c : childAcc) {
                // 子账户信息  child
                Record _childAcc = new Record()
                        .set("collect_id", collect_id)
                        .set("collect_main_account_id", _mainId)
                        .set("child_acc_org_id", _c.get("child_acc_org_id"))
                        .set("child_acc_org_name", _c.get("child_acc_org_name"))
                        .set("child_acc_id", _c.get("child_acc_id"))
                        .set("child_acc_no", _c.get("child_acc_no"))
                        .set("child_acc_name", _c.get("child_acc_name"))
                        .set("child_acc_bank_name", _c.get("child_acc_bank_name"))
                        .set("child_acc_bank_cnaps_code", _c.get("child_acc_bank_cnaps_code"))
                        .set("child_acc_cur", _c.get("child_acc_cur"))
                        .set("child_acc_bank_prov", _c.get("child_acc_bank_prov"))
                        .set("child_acc_bank_city", _c.get("child_acc_bank_city"));
                childAccList.add(_childAcc);
            }

        }
        return true;
    }

    public Record chg(final Record record, final UserInfo userInfo) throws BusinessException {
        final long collect_id = TypeUtils.castToLong(record.get("id"));
        final Record topic = Db.findById("collect_topic", "id", collect_id);
        if (topic == null) {
            throw new ReqDataException("此单据不存在，请刷新重试!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(topic.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }


        int service_status = TypeUtils.castToInt(topic.get("service_status"));
        if (service_status != WebConstant.BillStatus.SAVED.getKey()
                && service_status != WebConstant.BillStatus.REJECT.getKey()) {
            throw new ReqDataException("此单据状态不正确，请刷新重试!");
        }
        final int oldversion = TypeUtils.castToInt(record.get("persist_version"));
        //主账户与子账户信息 主账户规则 key  main0 main1 main2
        final List<Record> main_list = record.get("main_list");

        //归集频率  数组形式，使用 - 分隔
        final List<Object> timesettingList = record.get("timesetting_list");
        //附件信息
        final List<Object> fileList = record.get("files");
        //归集单据表信息(collect_topic)
        //计算被归集账户数量。
        int childCount = getChildCount(main_list);

        topic.remove("id");
        topic.set("collect_type", record.get("collect_type"))
                .set("topic", record.get("topic"))
                .set("collect_frequency", record.get("collect_frequency"))
                .set("collect_main_account_count", main_list.size())
                .set("collect_child_account_count", childCount)
                .set("collect_amount", TypeUtils.castToBigDecimal(record.get("collect_amount")))
                .set("summary", record.get("summary"))
                .set("update_by", userInfo.getUsr_id())
                .set("update_on", new Date())
                .set("persist_version", oldversion + 1)
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("attachment_count", (fileList == null || fileList.isEmpty()) ? 0 : fileList.size());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空归集频率
                Db.deleteById("collect_timesetting", "collect_id", collect_id);
                //清空附件信息
                CommonService.delFileRef(WebConstant.MajorBizType.GJT.getKey(), collect_id);
                //清空归集子账户信息
                Db.deleteById("collect_child_account", "collect_id", collect_id);
                //清空归集主账户信息
                Db.deleteById("collect_main_account", "collect_id", collect_id);

                //更新单据信息
                boolean update = CommonService.update("collect_topic",
                        topic,
                        new Record().set("id", collect_id).set("persist_version", oldversion));
                if (!update) return false;
                //存储附件信息
                boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.GJT.getKey(), collect_id, fileList);
                if (!saveFileRef) return saveFileRef;

                //子账户列表 - 批量存储
                List<Record> childAccList = new ArrayList<>();
                //存储主账户信息
                boolean mainAccFlag = saveMainAccListAndFillChildAccList(collect_id, childAccList, main_list);
                if (!mainAccFlag) return false;

                int[] childAccounts = Db.batchSave("collect_child_account", childAccList, 1000);
                if (!ArrayUtil.checkDbResult(childAccounts)) return false;

                //保存归集频率
                List<Record> collectTimeSettingList = new ArrayList<>();

                if (genCollectTimesettingList(collect_id, collectTimeSettingList, timesettingList, record))
                    return false;

                int[] collectTimeSettings = Db.batchSave("collect_timesetting", collectTimeSettingList, 1000);
                if (!ArrayUtil.checkDbResult(collectTimeSettings)) return false;
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("创建归集单据失败！");
        }
        topic.set("id", collect_id);
        return topic;
    }

    public void del(Record record, final UserInfo userInfo) throws BusinessException {
        final long collect_id = TypeUtils.castToLong(record.get("id"));
        Record topic = Db.findById("collect_topic", "id", collect_id);
        checkTopic(topic, userInfo);
        final int oldversion = TypeUtils.castToInt(record.get("persist_version"));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("collect_topic",
                        new Record()
                                .set("persist_version", oldversion + 1)
                                .set("delete_flag", WebConstant.YesOrNo.YES.getKey()),
                        new Record()
                                .set("id", collect_id)
                                .set("persist_version", oldversion));
            }
        });
        if (!flag) {
            throw new DbProcessException("删除单据失败！");
        }
    }

    private void checkTopic(Record topic, UserInfo userInfo) throws BusinessException {
        if (topic == null) {
            throw new ReqDataException("此单据不存在，请刷新重试!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(topic.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }


        int service_status = TypeUtils.castToInt(topic.get("service_status"));
        if (service_status != WebConstant.BillStatus.SAVED.getKey()
                && service_status != WebConstant.BillStatus.REJECT.getKey()) {
            throw new ReqDataException("此单据状态不正确，请刷新重试!");
        }
    }

    public List<Record> getChildAccList(Record record, long org_id) {
        Kv cond = Kv.create();
        cond.set(record.getColumns());
        cond.set("org_id", org_id);
        SqlPara sqlPara = Db.getSqlPara("collect_setting.getChildAccList", Kv.by("map", cond));
        List<Record> list = Db.find(sqlPara);
        List<Record> accs = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Record ca : list) {
                long child_acc_org_id = TypeUtils.castToLong(ca.get("child_acc_org_id"));
                String child_acc_org_name = TypeUtils.castToString(ca.get("child_acc_org_name"));

                if (accs.size() > 0) {
                    boolean flag = false;
                    List<Record> subAccs = null;
                    for (Record r : accs) {
                        long old_org_id = TypeUtils.castToLong(r.get("org_id"));
                        if (old_org_id == child_acc_org_id) {
                            flag = true;
                            subAccs = r.get("accounts");
                            break;
                        }
                    }
                    if (flag) {
                        subAccs.add(ca);
                    } else {
                        Record rr = new Record()
                                .set("org_id", child_acc_org_id)
                                .set("org_name", child_acc_org_name);
                        subAccs = new ArrayList<>();
                        subAccs.add(ca);
                        rr.set("accounts", subAccs);
                        accs.add(rr);
                    }
                } else {
                    //第一次构建
                    Record rr = new Record()
                            .set("org_id", child_acc_org_id)
                            .set("org_name", child_acc_org_name);
                    List<Record> subAccs = new ArrayList<>();
                    subAccs.add(ca);
                    rr.set("accounts", subAccs);
                    accs.add(rr);
                }
            }
        }
        return accs;
    }

    public Record detail(Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        Record topic = Db.findById("collect_topic", "id", id);
        checkTopicST(topic);

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), topic.getLong("create_org_id"));
        }


        //查询归集频率
        List<Record> timeSettingList = Db.find(Db.getSql("collect_setting.findTimeSettingList"), id);

        //查询归集主账户
        List<Record> mainAccList = Db.find(Db.getSql("collect_setting.findMainAccList"), id);

        //查询归集子账户
        List<Record> childAccList = Db.find(Db.getSql("collect_setting.findChildAccList"), id);

        //组装数据
        for (Record _ma : mainAccList) {
            long _maId = TypeUtils.castToLong(_ma.get("id"));
            List<Record> _caList = new ArrayList<>();
            for (Record _ca : childAccList) {
                long _caMaId = TypeUtils.castToLong(_ca.get("collect_main_account_id"));
                if (_caMaId == _maId) {
                    _caList.add(_ca);
                }
            }
            _ma.set("child_list", _caList);
        }
        topic.set("main_acc_list", mainAccList);
        topic.set("time_setting_list", timeSettingList);
        topic.set("time_settings", Db.queryStr(Db.getSql("collect_setting.timeSettings"), id));
        return topic;
    }

    private void checkTopicST(Record topic) throws BusinessException {
        if (topic == null
                || TypeUtils.castToInt(topic.get("delete_flag")) == WebConstant.YesOrNo.YES.getKey()) {
            throw new ReqDataException("此单据不存在，请刷新重试！");
        }
    }

    public Page<Record> morebill(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("collect_setting.morebill", Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
        List<Record> list = page.getList();
        GjtCommonService.buildTopicList(list);
        return page;
    }

    public List<Record> findAccsByST(Record record) {
        List<Record> list = null;
        if (record == null) {
            return null;
        }
        Kv cond = Kv.by("status", TypeUtils.castToInt(record.get("status"))).set("org_id", TypeUtils.castToLong(record.get("org_id")));
        if (record.get("acc_id") != null) {
            cond.set(Kv.by("acc_id", TypeUtils.castToLong(record.get("acc_id"))));
        }
        if (record.get("exclude_ids") != null) {
            cond.set("exclude_ids", record.get("exclude_ids"));
        }
        SqlPara sqlPara = Db.getSqlPara("collect_setting.listByST", cond);
        list = Db.find(sqlPara);

        return list;
    }

    public boolean hookPass(Record record) {
        long id = TypeUtils.castToLong(record.get("id"));
        List<Record> timeSettingList = Db.find(Db.getSql("collect_setting.findTimeSettingList"), id);
        if (timeSettingList != null && timeSettingList.size() > 0) {
            List<Record> quartzRecordList = new ArrayList<>();
            //插入一条
            for (Record _ts : timeSettingList) {
                quartzRecordList.add(new Record()
                        .set("name", "归集通定时任务")
                        .set("class_name", WebConstant.CronTaskGroup.COLLECT.getClassPath())
                        .set("groups", WebConstant.CronTaskGroup.COLLECT.getPrefix() + id)
                        .set("cron", _ts.get("collect_time_cron"))
                        .set("type", 1)
                        .set("is_need_scaned", 1)
                        .set("enable", WebConstant.YesOrNo.NO.getKey())
                        .set("param", "{\"id\":" + id + "}"));
            }
            int[] quartzres = Db.batchSave("cfm_quartz", quartzRecordList, 1000);
            if (!ArrayUtil.checkDbResult(quartzres)) return false;
        }
        return true;
    }
}
