package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.List;

public class GylManageService {

    public void cancel(Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int oldVersion = TypeUtils.castToInt(record.get("persist_version"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean f = CommonService.update("gyl_allocation_topic",
                        new Record()
                                .set("service_status", WebConstant.BillStatus.CANCEL.getKey())
                                .set("persist_version", oldVersion + 1),
                        new Record()
                                .set("id", id)
                                .set("persist_version", oldVersion));
                if (f) {
                    return CommonService.updateQuartz(WebConstant.YesOrNo.NO, WebConstant.CronTaskGroup.GYL_ALLOCATION, id);
                }
                return f;
            }
        });
        if (!flag) {
            throw new DbProcessException("广银联备付金单据作废失败！");
        }
    }

    public List<Record> list(Record record) {
        SqlPara sqlPara = Db.getSqlPara("gyl_manage.list", Kv.by("map", record.getColumns()));
        List<Record> list = Db.find(sqlPara);
        GylCommonService.buildGylList(list);
        return list;
    }

    public Record setstate(Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));

        Record topic = Db.findById("gyl_allocation_topic", "id", id);

        if (topic == null
                || TypeUtils.castToInt(topic.get("delete_flag")) == WebConstant.YesOrNo.YES.getKey()
                || TypeUtils.castToInt(topic.get("service_status")) != WebConstant.BillStatus.PASS.getKey()) {
            throw new ReqDataException("请求单据不存在！");
        }

        int is_activity = TypeUtils.castToInt(topic.get("is_activity"));

        is_activity = is_activity == WebConstant.YesOrNo.NO.getKey() ? WebConstant.YesOrNo.YES.getKey() : WebConstant.YesOrNo.NO.getKey();

        final int finalIs_activity = is_activity;
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean f = CommonService.update("gyl_allocation_topic",
                        new Record()
                                .set("is_activity", finalIs_activity)
                                .set("persist_version", version + 1),
                        new Record()
                                .set("id", id)
                                .set("persist_version", version));
                if (f) {
                    if (finalIs_activity == WebConstant.YesOrNo.YES.getKey()) {
                        return CommonService.updateQuartz(WebConstant.YesOrNo.YES, WebConstant.CronTaskGroup.GYL_ALLOCATION, id);
                    } else {
                        return CommonService.updateQuartz(WebConstant.YesOrNo.NO, WebConstant.CronTaskGroup.GYL_ALLOCATION, id);
                    }
                }
                return f;
            }
        });

        if (!flag) {
            throw new DbProcessException("修改单据状态失败！");
        }
        return Db.findById("gyl_allocation_topic", "id", id);
    }
}
