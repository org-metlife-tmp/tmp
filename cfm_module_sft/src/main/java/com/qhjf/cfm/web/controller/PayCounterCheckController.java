package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.PayCounterCheckService;

import java.util.List;

/**
 * 柜面付 结算对账
 *
 * @author GJF
 * @date 2019年03月14日
 */
public class PayCounterCheckController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(PayCounterCheckController.class);
    private PayCounterCheckService service = new PayCounterCheckService();

    /**
     * 查询所有批次
     */
    @Auth(hasForces = {"PayCounterCheck"})
    public void batchlist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        //状态为已成功的单据
        AccCommonService.setInnerTradStatus(record, "service_status");
        try {

            Page<Record> page = service.batchlist(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    /**
     * 查找交易流水 非自动匹配勾选
     */
    @Auth(hasForces = {"PayCounterCheck"})
    public void tradingListNoAuto() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.tradingListNoAuto(record);
        renderOk(page);
    }

    /**
     * 查找交易流水 自动匹配勾选
     */
    @Auth(hasForces = {"PayCounterCheck"})
    public void tradingListAuto() {
        try {
            Record record = getRecordByParamsStrong();
            List<Record> page = service.tradingListAuto(record);
            renderOk(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 对账确认
     */
    @Auth(hasForces = {"PayCounterCheck"})
    public void confirm() {
        try {
            Record record = getRecordByParamsStrong();

            UserInfo userInfo = getUserInfo();

            Page<Record> page = service.confirm(record,userInfo);
            renderOkPage(page);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

}
