package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CheckDoubtfulNcService;

import java.util.List;

public class CheckDoubtfulNcController extends CFMBaseController {
    private static final Log log = LogbackLog.getLog(CheckDoubtfulNcController.class);

    private CheckDoubtfulNcService service = new CheckDoubtfulNcService();

    /**
     * 可疑单据列表
     */
    public void list() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 查看疑似重复单据列表
     */
    public void doubtlist() {
        Record record = getRecordByParamsStrong();
        List<Record> list = service.doubtlist(record);
        renderOk(list);
    }

    public void payoff() throws Exception {
        try {
            Record record = getParamsToRecord();
            service.payOff(record);
            renderOk(null);
        } catch (BusinessException e) {
            log.error("单据作废失败！", e);
            renderFail(e);
        }
    }

    public void pass() throws Exception {
        try {
            Record record = getParamsToRecord();
            service.pass(TypeUtils.castToLong(record.get("id")),TypeUtils.castToInt(record.get("persist_version")));
            renderOk(null);
        } catch (BusinessException e) {
            log.error("操作失败！", e);
            renderFail(e);
        }
    }

    /**
     * 可疑数据列表导出
     */
    public void listexport() {
        doExport();
    }
}
