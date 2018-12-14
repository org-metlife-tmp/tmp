package com.qhjf.cfm.web.service;

import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

public class CollectViewService {

    private static final Log log = LogbackLog.getLog(CollectViewService.class);

    /**
     * 根据条件查询归集列表
     *
     * @param record
     * @param pageSize
     * @param pageNum
     * @return
     */
    public Page<Record> findList(int pageNum, int pageSize, Record record, UodpInfo uodpInfo) {
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        record.set("level_code", orgRec.get("level_code"));
        record.set("level_num", orgRec.get("level_num"));

        SqlPara sqlPara = getlistparam(record, "collect_view.findCollectionList");
        return Db.paginate(pageNum, pageSize, sqlPara);
    }


    /**
     * 归集通_查看详情
     *
     * @param record
     * @throws ReqDataException
     */
    public Record detail(Record record) throws ReqDataException {
        Record returnRecord = new Record();
        log.info("=======进入归集通查看详情逻辑层");
        Long collect_id = TypeUtils.castToLong(record.get("id"));
        log.info("=======获取到的归集id=======" + collect_id);
        Record findById = Db.findById("collect_topic", "id", collect_id);
        if (null == findById) {
            throw new ReqDataException("未找到相应的归集信息");
        }
        log.info("=======开始拼接详情的返回对象=======" + collect_id);
        returnRecord.set("id", collect_id); //归集id
        returnRecord.set("service_serial_number", findById.get("service_serial_number"));  //单据编号
        returnRecord.set("topic", findById.get("topic")); //归集主题
        returnRecord.set("collect_type", findById.get("collect_type")); //归集类型  归集额度
        returnRecord.set("collect_amount", findById.get("collect_amount")); //归集金额
        returnRecord.set("collect_frequency", findById.get("collect_frequency")); //归集频率
        returnRecord.set("summary", findById.get("summary")); //摘要
        returnRecord.set("persist_version", findById.get("persist_version")); //版本号
        returnRecord.set("attachment_count", findById.get("attachment_count"));  //普通附件数量
        log.info("=======开始查询此collect_id下的主账号=======");

        List<Record> main_account = Db.find(Db.getSql("collect_view.findMainAccountList"), collect_id);
        if (null == main_account || main_account.size() == 0) {
            log.info("=======未找到归集号" + collect_id + "相应的主账号信息");
            return returnRecord;
        }
        for (Record main_record : main_account) {
            List<Record> child_account = Db.find(Db.getSql("collect_view.findChildAccountList"), collect_id, main_record.get("main_accountRecord_id"));
            if (null == child_account || child_account.size() == 0) {
                log.info("=======未找到归集号" + collect_id + "====主账号" + main_record.get("main_accountRecord_id") + "相应的子账号信息");
                continue;
            }
            main_record.set("child_account", child_account);
        }
        returnRecord.set("main_account", main_account);
        return returnRecord;
    }


    /**
     * 拼接查询语句参数
     *
     * @param record
     * @param string
     * @return
     */
    private SqlPara getlistparam(Record record, String sql) {

        String collect_main_key = record.get("collect_main_key");
        record.remove("collect_main_key");
        List<Integer> is_activity = record.get("is_activity");
        if (null == is_activity || is_activity.size() == 0) {
            record.remove("is_activity");
        }
        //归集主账号关键字是否包含中文
        boolean flag = StringKit.isContainChina(collect_main_key);
        if (flag) {
            //账户名称
            record.set("main_acc_name", collect_main_key);
        } else {
            //账户号
            record.set("main_acc_no", collect_main_key);
        }
        record.set("delete_flag", 0);
        return Db.getSqlPara(sql, Kv.by("map", record.getColumns()));
    }


    public void setBillListStatus(Record record, String statusName) {
        List status = record.get(statusName);
        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey()
            });
        }

    }


}
