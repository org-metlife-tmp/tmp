package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.GylManageService;

import java.util.List;

public class GylManageController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(GylManageController.class);

    private GylManageService service = new GylManageService();

    @Auth(hasForces = {"GYLBFJMgr"})
    public void list() {
        try {
            Record record = getParamsToRecord();
            record.set("create_org_id", getCurUodp().getOrg_id());
            List<Record> list = service.list(record);
            renderOk(list);
        } catch (BusinessException e) {
            log.error("获取广银联管理列表失败！", e);
            renderFail(e);
        }

    }

    @Auth(hasForces = {"GYLBFJMgr"})
    public void setstate() {
        try {
            renderOk(service.setstate(getParamsToRecord()));
        } catch (BusinessException e) {
            log.error("设置广银联激活状态失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"GYLBFJMgr"})
    public void cancel() {
        try {
            service.cancel(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("作废广银联单据失败！", e);
            renderFail(e);
        }
    }
}
