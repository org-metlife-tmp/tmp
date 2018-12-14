package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CollectManageService;

import java.util.List;

public class CollectManageController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CollectManageController.class);

    private CollectManageService service = new CollectManageService();

    @Auth(hasForces = {"GJMgr"})
    public void list() {
        try {
            Record record = getParamsToRecord();
            record.set("create_org_id", getCurUodp().getOrg_id());
            List<Record> list = service.list(record);
            renderOk(list);
        } catch (BusinessException e) {
            log.error("获取归集通管理列表失败！", e);
            renderFail(e);
        }

    }

    @Auth(hasForces = {"GJMgr"})
    public void setstate() {
        try {
            renderOk(service.setstate(getParamsToRecord()));
        } catch (BusinessException e) {
            log.error("设置激活状态失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"GJMgr"})
    public void cancel() {
        try {
            service.cancel(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("作废归集通单据失败！", e);
            renderFail(e);
        }
    }

    public void sendPayList() {
        Record record = getParamsToRecord();
        List<Object> ids = record.get("ids");
        Long[] info_ids = null;
        if (ids != null && ids.size() > 0) {
            info_ids = new Long[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                info_ids[i] = TypeUtils.castToLong(ids.get(i));
            }
        }
        service.sendPayList(info_ids);
        renderOk(null);
    }

    public void instruction() {
        Record record = getParamsToRecord();
        List<Record> list = service.instruction(record);
        renderOk(list);
    }

    public void sendinstruction() {
        Record record = getParamsToRecord();
        try {
            service.sendinstruction(record);
            renderOk(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelinstruction() {
        Record record = getParamsToRecord();
        try {
            record = service.cancelinstruction(record);
            renderOk(record);
        } catch (DbProcessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
