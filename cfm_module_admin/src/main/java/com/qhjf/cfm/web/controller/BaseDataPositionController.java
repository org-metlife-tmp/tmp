package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.PositionService;

@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class BaseDataPositionController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(BaseDataPositionController.class);

    private PositionService service = new PositionService();

    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.getPositionPage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    public void add() {
        try {
            Record record = getParamsToRecord();
            service.add(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("新增职位失败！", e);
            renderFail(e);
        }
    }

    public void del() {
        try {
            service.del(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除用户失败", e);
            renderFail(e);
        }
    }

    public void chg() {
        try {
            Record record = getParamsToRecord();
            service.chg(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("修改用户信息失败！", e);
            renderFail(e);
        }
    }

    public void setstatus() {
        try {
            Record record = getParamsToRecord();
            service.setState(record);
            renderOk(record.get("status"));
        } catch (BusinessException e) {
            log.error("修改职位状态失败", e);
            renderFail(e);
        }
    }
}
