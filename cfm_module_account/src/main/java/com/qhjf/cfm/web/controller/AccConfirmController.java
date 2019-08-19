package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccConfirmService;

public class AccConfirmController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(AccConfirmController.class);

    private AccConfirmService service = new AccConfirmService();

    @Auth(hasForces = {"AccOpenConfirm"})
    public void list() {
        try {
            Record record = getParamsToRecord();
            int page_size = getPageSize(record);
            int page_num = getPageNum(record);
            record.set("org_id", getCurUodp().getOrg_id());
            Page<Record> page = service.getPage(page_num, page_size, record);
            renderOkPage(page);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccOpenConfirm"})
    public void setstatus() {
        try {
            Record record = getParamsToRecord();
            service.setstatus(record);
            renderOk(null);
        } catch (BusinessException e) {
            log.error("修改账户状态失败！", e);
            renderFail(e);
        }
    }

    /**
     * 销户确认列表
     */
    @Auth(hasForces = {"AccCloseConfirm"})
    public void closelist() {
        try {
            Record record = getParamsToRecord();
            int page_size = getPageSize(record);
            int page_num = getPageNum(record);
            record.set("org_id", getCurUodp().getOrg_id());
            Page<Record> page = service.getCloseList(page_num, page_size, record);
            renderOkPage(page);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 销户确认修改
     */
    @Auth(hasForces = {"AccCloseConfirm"})
    public void closesetstatus() {
        try {
            Record record = getParamsToRecord();
            service.setCloseStatus(record);
            renderOk(null);
        } catch (BusinessException e) {
            log.error("销户确认失败!", e);
            renderFail(e);
        }
    }
}
