package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.HandlechannelService;

/**
 * 处理渠道
 */
@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class HandlechannelController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(HandlechannelController.class);

    private HandlechannelService service = new HandlechannelService();

    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.getHandlechannelPage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    public void add() {
        try {
            Record record = getParamsToRecord();
            service.add(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("新增处理渠道失败！", e);
            renderFail(e);
        }
    }

    public void setstatus() {
        try {
            Record record = getParamsToRecord();
            service.setstatus(record);
            renderOk(record.get("is_activate"));
        } catch (BusinessException e) {
            log.error("修改处理渠道状态失败！", e);
            renderFail(e);
        }
    }
}
