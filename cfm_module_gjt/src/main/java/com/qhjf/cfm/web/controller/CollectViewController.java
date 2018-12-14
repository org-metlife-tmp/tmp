package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CollectViewService;

/**
 * 归集通查看模块
 *
 * @author
 */
public class CollectViewController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CollectViewController.class);

    private CollectViewService service = new CollectViewService();

    /**
     * 归集通_查看列表
     *
     * @throws Exception
     */
    @Auth(hasForces = {"GJView"})
    public void collections() {
        log.info("============进入归集通_查看列表接口");
        Record record = getParamsToRecord();
        try {
            UodpInfo uodpInfo = getCurUodp();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);
            service.setBillListStatus(record, "service_status");
            Page<Record> page = service.findList(pageNum, pageSize, record, uodpInfo);
            renderOkPage(page);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    /**
     * 归集通_查看详情
     *
     * @throws ReqDataException
     */
    @Auth(hasForces = {"GJView", "MyWFPLAT"})
    public void datail() throws ReqDataException {

        log.info("===========进入归集通_查看详情接口");
        Record record = getRecordByParamsStrong();
        try {
            Record returnRecord = service.detail(record);
            renderOk(returnRecord);
        } catch (BusinessException e) {
            log.info("========获取归集通详情接口失败了");
            e.printStackTrace();
            renderFail(e);
        }

    }
}
