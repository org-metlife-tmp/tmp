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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GylSettingService {

    public boolean hookPass(Record record) {
        long id = TypeUtils.castToLong(record.get("id"));
        List<Record> timeSettingList = Db.find(Db.getSql("gyl_setting.findTimeSettingList"), id);
        if (timeSettingList != null && timeSettingList.size() > 0) {
            List<Record> quartzRecordList = new ArrayList<>();
            //插入一条
            for (Record _ts : timeSettingList) {
                quartzRecordList.add(new Record()
                        .set("name", "广银联备付金定时任务")
                        .set("class_name", WebConstant.CronTaskGroup.GYL_ALLOCATION.getClassPath())
                        .set("groups", WebConstant.CronTaskGroup.GYL_ALLOCATION.getPrefix() + id)
                        .set("cron", _ts.get("gyl_allocation_time_cron"))
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

    public Page<Record> morebill(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("gyl_setting.morebill", Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
        List<Record> list = page.getList();
        GylCommonService.buildGylList(list);
        return page;
    }

    public Record detail(Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        Record gyl = Db.findById("gyl_allocation_topic", "id", id);
        if (gyl == null
                || TypeUtils.castToInt(gyl.get("delete_flag")) == WebConstant.YesOrNo.YES.getKey()) {
            throw new ReqDataException("此单据不存在，请刷新重试！");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), gyl.getLong("create_org_id"));
        }

        gyl.set("time_settings", Db.queryStr(Db.getSql("gyl_setting.timeSettings"), id));
        gyl.set("timesetting_list", Db.find(Db.getSql("gyl_setting.findTimeSettingList"), id));
        return gyl;
    }

    public void del(Record record, UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int oldVersion = TypeUtils.castToInt(record.get("persist_version"));
        Record gyl = Db.findById("gyl_allocation_topic", "id", id);
        if (gyl == null) {
            throw new ReqDataException("此单据不存在，请刷新重试!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(gyl.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }


        int service_status = TypeUtils.castToInt(gyl.get("service_status"));
        if (service_status != WebConstant.BillStatus.SAVED.getKey()
                && service_status != WebConstant.BillStatus.REJECT.getKey()) {
            throw new ReqDataException("此单据状态不正确，请刷新重试!");
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("gyl_allocation_topic", new Record()
                                .set("persist_version", oldVersion + 1)
                                .set("delete_flag", WebConstant.YesOrNo.YES.getKey()),
                        new Record()
                                .set("id", id)
                                .set("persist_version", oldVersion));
            }
        });
        if (!flag) {
            throw new DbProcessException("删除单据失败！");
        }
    }

    public Record chg(Record record, UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final List<Object> timesettingList = record.get("timesetting_list");
        Record oldGyl = Db.findById("gyl_allocation_topic", "id", id);
        if (oldGyl == null) {
            throw new ReqDataException("此单据不存在，请刷新重试!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(oldGyl.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }


        int service_status = TypeUtils.castToInt(oldGyl.get("service_status"));
        if (service_status != WebConstant.BillStatus.SAVED.getKey()
                && service_status != WebConstant.BillStatus.REJECT.getKey()) {
            throw new ReqDataException("此单据状态不正确，请刷新重试!");
        }
        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        final List<Object> fileList = record.get("files");
        final Record gyl = new Record()
                .set("topic", record.get("topic"))
                .set("gyl_allocation_type", record.get("gyl_allocation_type"))
                .set("gyl_allocation_amount", record.get("gyl_allocation_amount"))
                .set("gyl_allocation_frequency", record.get("gyl_allocation_frequency"))
                .set("summary", record.get("summary"))
                .set("persist_version", old_version + 1)
                .set("update_by", userInfo.getUsr_id())
                .set("update_on", new Date())
                .set("attachment_count", (fileList == null || fileList.isEmpty()) ? 0 : fileList.size())
                .set("pay_acc_org_id", record.get("pay_acc_org_id"))
                .set("pay_acc_org_name", record.get("pay_acc_org_name"))
                .set("pay_acc_id", record.get("pay_acc_id"))
                .set("pay_acc_no", record.get("pay_acc_no"))
                .set("pay_acc_name", record.get("pay_acc_name"))
                .set("pay_acc_bank_name", record.get("pay_acc_bank_name"))
                .set("pay_acc_bank_cnaps_code", record.get("pay_acc_bank_cnaps_code"))
                .set("recv_acc_no", record.get("recv_acc_no"))
                .set("recv_acc_name", record.get("recv_acc_name"))
                .set("service_status", WebConstant.BillStatus.SAVED.getKey());


        //更新单据信息
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                CommonService.delFileRef(WebConstant.MajorBizType.GYL.getKey(), id);
                Db.deleteById("gyl_allocation_timesetting", "gyl_allocation_id", id);
                boolean update = CommonService.update("gyl_allocation_topic",
                        gyl,
                        new Record().set("id", id).set("persist_version", old_version));
                if (!update) return false;
                //保存归集频率
                List<Record> collectTimeSettingList = new ArrayList<>();
                if (genCollectTimesettingList(id, collectTimeSettingList, timesettingList, gyl))
                    return false;
                int[] collectTimeSettings = Db.batchSave("gyl_allocation_timesetting", collectTimeSettingList, 1000);
                if (!ArrayUtil.checkDbResult(collectTimeSettings)) return false;
                if (fileList != null && fileList.size() > 0) {
                    return CommonService.saveFileRef(WebConstant.MajorBizType.GYL.getKey(),
                            id, fileList);
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改广银联备付金单据失败！");
        }
        return Db.findById("gyl_allocation_topic", "id", id);
    }

    public Record add(Record record, long usr_id, long org_id) throws BusinessException {
        final List<Object> fileList = record.get("files");
        final List<Object> timesettingList = record.get("timesetting_list");
        final Record gyl = new Record()
                .set("service_serial_number", BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.GYL))
                .set("topic", record.get("topic"))
                .set("gyl_allocation_type", record.get("gyl_allocation_type"))
                .set("gyl_allocation_amount", record.get("gyl_allocation_amount"))
                .set("gyl_allocation_frequency", record.get("gyl_allocation_frequency"))
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("summary", record.get("summary"))
                .set("is_activity", WebConstant.YesOrNo.NO.getKey())
                .set("create_by", usr_id)
                .set("create_on", new Date())
                .set("create_org_id", org_id)
                .set("delete_flag", WebConstant.YesOrNo.NO.getKey())
                .set("persist_version", 0)
                .set("attachment_count", (fileList == null || fileList.isEmpty()) ? 0 : fileList.size())
                .set("pay_acc_org_id", record.get("pay_acc_org_id"))
                .set("pay_acc_org_name", record.get("pay_acc_org_name"))
                .set("pay_acc_id", record.get("pay_acc_id"))
                .set("pay_acc_id", record.get("pay_acc_id"))
                .set("pay_acc_no", record.get("pay_acc_no"))
                .set("pay_acc_name", record.get("pay_acc_name"))
                .set("pay_acc_bank_name", record.get("pay_acc_bank_name"))
                .set("pay_acc_bank_cnaps_code", record.get("pay_acc_bank_cnaps_code"))
                .set("recv_acc_no", record.get("recv_acc_no"))
                .set("recv_acc_name", record.get("recv_acc_name"))
                .set("pay_acc_cur", record.get("pay_acc_cur"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean gylFlag = Db.save("gyl_allocation_topic", "id", gyl);
                if (!gylFlag) return false;
                long id = TypeUtils.castToLong(gyl.get("id"));
                //保存归集频率
                List<Record> collectTimeSettingList = new ArrayList<>();
                if (genCollectTimesettingList(id, collectTimeSettingList, timesettingList, gyl))
                    return false;
                int[] collectTimeSettings = Db.batchSave("gyl_allocation_timesetting", collectTimeSettingList, 1000);
                if (!ArrayUtil.checkDbResult(collectTimeSettings)) return false;
                if (fileList != null && fileList.size() > 0) {
                    return CommonService.saveFileRef(WebConstant.MajorBizType.GYL.getKey(),
                            TypeUtils.castToLong(gyl.get("id")), fileList);
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("新增广银联备付金单据失败！");
        }
        return gyl;
    }

    private boolean genCollectTimesettingList(long gyl_allocation_id, List<Record> timeSettingRecordList, List<Object> timesettingList, Record record) {
        for (Object s : timesettingList) {
            String string = TypeUtils.castToString(s);
            int frequencyType = TypeUtils.castToInt(record.get("gyl_allocation_frequency"));
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
                    .set("gyl_allocation_id", gyl_allocation_id)
                    .set("gyl_allocation_frequency", record.get("gyl_allocation_frequency"))
                    .set("gyl_allocation_frequency_detail", record.get("gyl_allocation_frequency_detail"))
                    .set("gyl_allocation_time_cron", cronFormat)
                    .set("gyl_allocation_time", string);
            timeSettingRecordList.add(_timesetting);
        }
        return false;
    }
}
