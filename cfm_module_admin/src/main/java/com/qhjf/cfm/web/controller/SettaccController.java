package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.SettaccService;

/**
 * 结算账户
 */
@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class SettaccController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(SettaccController.class);

    private SettaccService settaccService = new SettaccService();

    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = settaccService.getSettaccPage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    public void add() {
        try {
            Record record = getParamsToRecord();
            settaccService.add(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("新增结算账户失败！", e);
            renderFail(e);
        }
    }

    public void del() {
        try {
            Record record = getParamsToRecord();
            settaccService.del(record);
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除结算账户失败！", e);
            renderFail(e);
        }
    }

    public void chg() {
        try {
            Record record = getParamsToRecord();
            settaccService.chg(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("修改结算账户信息失败！", e);
            renderFail(e);
        }
    }

    public void setstatus() {
        try {
            Record record = getParamsToRecord();
            settaccService.setstatus(record);
            renderOk(record.get("status"));
        } catch (BusinessException e) {
            log.error("修改结算账户状态失败！", e);
            renderFail(e);
        }
    }
}
