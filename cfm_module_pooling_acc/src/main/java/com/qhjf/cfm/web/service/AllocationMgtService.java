package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/19
 * @Description: 资金下拨管理
 */
public class AllocationMgtService {

    public List<Record> mgtlist(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws ReqDataException {
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());
        if (orgRec == null) {
            throw new ReqDataException("机构信息不存在!");
        }

        Kv kv = Kv.create();
        kv.set("delete_flag", 0);
        kv.set("level_code", orgRec.get("level_code"));
        SqlPara sqlPara = getListParam(record, "allocation.findViewList", kv);
        List<Record> list = Db.find(sqlPara);

        for (Record rec : list) {
            //根据topicid查询下拨频率
            List<Record> timesetList = Db.find(Db.getSql("allocation.findTimesettingByAlloId"), TypeUtils.castToLong(rec.get("id")));
            rec.set("frequency_detail", timesetList);
        }
        return list;
    }

    /**
     * 激活/暂停
     */
    public Record setisactivity(final Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final Record topicRec = Db.findById("allocation_topic", "id", id);
        if (topicRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        final int serviceStatus = TypeUtils.castToInt(topicRec.get("service_status"));
        final int isActivity = TypeUtils.castToInt(topicRec.get("is_activity"));

        if (serviceStatus != WebConstant.BillStatus.PASS.getKey() || serviceStatus == WebConstant.BillStatus.CANCEL.getKey()) {
            throw new ReqDataException("单据状态不正确，不能进行激活/暂停操作!");
        }

        if (serviceStatus == WebConstant.CollOrPoolRunStatus.SENDING.getKey()) {
            throw new ReqDataException("单据正在执行中，不能进行激活/暂停操作!");
        }

        if (isActivity == 1) {
            //暂停
            Long excuteid = TypeUtils.castToLong(topicRec.get("execute_id"));
            if (excuteid != null) {
                //根据执行id查询
                Record excuteRec = Db.findById("allocation_execute", "id", excuteid);
                if (excuteRec != null) {
                    int excutestatus = TypeUtils.castToInt(excuteRec.get("allocation_status"));
                    if (excutestatus == WebConstant.CollOrPoolRunStatus.SAVED.getKey() ||
                            excutestatus == WebConstant.CollOrPoolRunStatus.SENDING.getKey()) {
                        throw new ReqDataException("单据正在执行中,不能进行策略暂停操作!");
                    }
                    if (excutestatus == WebConstant.CollOrPoolRunStatus.FAILED.getKey()) {
                        throw new ReqDataException("单据有异常信息,不能进行策略暂停操作!");
                    }
                }
            }
        }


        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                Record topic = new Record();
                int old_version = TypeUtils.castToInt(record.get("persist_version"));
                topic.set("is_activity", isActivity == 0 ? 1 : 0);
                topic.set("persist_version", old_version + 1);
                record.remove("id");

                boolean result = CommonService.update("allocation_topic",
                        topic,
                        new Record()
                                .set("id", id)
                                .set("persist_version", old_version));
                if (result) {
                    //修改定时任务激活状态
                    if (isActivity == WebConstant.YesOrNo.NO.getKey()) {
                        return CommonService.updateQuartz(WebConstant.YesOrNo.YES, WebConstant.CronTaskGroup.ALLOCATION, id);
                    } else {
                        return CommonService.updateQuartz(WebConstant.YesOrNo.NO, WebConstant.CronTaskGroup.ALLOCATION, id);
                    }
                }

                return false;
            }
        });

        if (flag) {
            return Db.findById("allocation_topic", "id", id);
        }
        throw new DbProcessException("激活/暂停失败!");
    }

    /**
     * 作废
     */
    public Record cancel(final Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));

        final Record topicRec = Db.findById("allocation_topic", "id", id);
        if (topicRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        int status = TypeUtils.castToInt(topicRec.get("service_status"));
        long activity = TypeUtils.castToLong(topicRec.get("is_activity"));

        if (status != WebConstant.BillStatus.PASS.getKey()) {
            throw new ReqDataException("单据未通过审批，不能作废!");
        }

        if (status == WebConstant.BillStatus.CANCEL.getKey()) {
            throw new ReqDataException("单据已激活，不能作废!");
        }

        if (activity == 1) {
            throw new ReqDataException("单据已激活，不能作废!");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                Record topic = new Record();
                int old_version = TypeUtils.castToInt(record.get("persist_version"));
                topic.set("persist_version", old_version + 1);
                topic.set("service_status", WebConstant.BillStatus.CANCEL.getKey());
                record.remove("id");

                boolean result = CommonService.update("allocation_topic",
                        topic,
                        new Record().set("id", id).set("persist_version", old_version));
                if (result) {
                    //修改定时任务激活状态
                    return CommonService.updateQuartz(WebConstant.YesOrNo.NO, WebConstant.CronTaskGroup.ALLOCATION, id);
                }
                return false;
            }
        });

        if (flag) {
            return Db.findById("allocation_topic", "id", id);
        }
        throw new DbProcessException("资金下拨单据作废失败!");
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
