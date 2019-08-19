package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AllocationReportService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/19
 * @Description: 资金下拨报表
 */
@SuppressWarnings("unused")
public class AllocationReportController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AllocationReportController.class);
    private AllocationReportService service = new AllocationReportService();

    /**
     * 报表 - 图 - 账户
     */
    @Auth(hasForces = {"ZJXBReport"})
    public void acctopchar() {
        Record record = getRecordByParamsStrong();
        try {
            UodpInfo uodpInfo = getCurUodp();
            record = service.acctopchart(record, uodpInfo);
            renderOk(record);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 报表 - 列表 - 账户
     */
    @Auth(hasForces = {"ZJXBReport"})
    public void acclist() {
        Record record = getRecordByParamsStrong();
        try {
            UodpInfo uodpInfo = getCurUodp();
            record = service.acclist(record, uodpInfo);
            renderOk(record);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 报表 - 图 - 公司
     */
    @Auth(hasForces = {"ZJXBReport"})
    public void orgtopchar() {
        Record record = getRecordByParamsStrong();
        try {
            UodpInfo uodpInfo = getCurUodp();
            record = service.orgtopchar(record, uodpInfo);
            renderOk(record);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 报表 - 图 - 公司
     */
    @Auth(hasForces = {"ZJXBReport"})
    public void orglist() {
        Record record = getRecordByParamsStrong();
        try {
            UodpInfo uodpInfo = getCurUodp();
            record = service.orglist(record, uodpInfo);
            renderOk(record);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
