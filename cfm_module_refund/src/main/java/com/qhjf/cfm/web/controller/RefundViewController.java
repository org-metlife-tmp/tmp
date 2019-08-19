package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RefundViewService;

public class RefundViewController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(RefundViewController.class);

    private RefundViewService service = new RefundViewService();

    /**
     * @查询已经退票的交易列表(收方交易)
     */
    @Auth(hasForces = {"QRefund"})
    public void alreadyRefundTradeList() {
        log.info("==========进入退票交易查询");
        try {
            Record record = getRecordByParamsStrong();
            Long org_id = getCurUodp().getOrg_id();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);
            Page<Record> page = service.alreadyRefundTradeList(record, org_id, pageNum, pageSize);
            renderOkPage(page);
        } catch (BusinessException e) {
            log.error("查询已退票交易列表失败!!");
            renderFail(e);
        }
    }

    /**
     * @根据已退票的交易,查询出对应的单据
     */
    @Auth(hasForces = {"QRefund"})
    public void billList() {
        log.info("==========进入查询退票模块_单据列表");
        try {
            Record record = getRecordByParamsStrong();
            Record bill = service.billList(record);
            renderOk(bill);
        } catch (BusinessException e) {
            log.error("查询退票模块_单据列表失败");
            renderFail(e);
        }
    }
}
