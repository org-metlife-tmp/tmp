package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.PayCheckService;
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
    @Auth(hasForces = {"PayBatchCheck"})
    public void batchlist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        try {
            Page<Record> page = service.batchlist(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    /**
     * 查找交易流水
     */
    @Auth(hasForces = {"PayBatchCheck"})
    public void tradingList() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.tradingList(record);
        renderOk(page);
    }

    /**
     * 对账确认
     */
    @Auth(hasForces = {"PayBatchCheck"})
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

    /**
     * 根据子批次id查询明细
     */
    @Auth(hasForces = {"PayBatchCheck", "MyWFPLAT"})
    public void getdetailbybaseid() {
        try {
            Record record = getRecordByParamsStrong();
            List<Record> returnRecord = service.getdetailbybaseid(record);
            renderOk(returnRecord);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 根据批次查询交易
     */
    @Auth(hasForces = {"PayBatchCheck"})
    public void gettradbybatchno() {
        try {
            Record record = getRecordByParamsStrong();
            Record returnRecord = service.gettradbybatchno(record);
            renderOk(returnRecord);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 查找所有银行账号
     */
    public void getallaccountno() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.getallaccountno(record);
        renderOk(page);
    }

}
