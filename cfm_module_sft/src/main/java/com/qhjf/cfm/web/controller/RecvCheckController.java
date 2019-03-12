package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.RecvCheckService;

import java.util.List;

/**
 * 批量收 结算对账
 *
 * @author GJF
 * @date 2018年12月28日
 */
public class RecvCheckController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(RecvCheckController.class);
    private RecvCheckService service = new RecvCheckService();

    /**
     * 查询所有批次
     */
    @Auth(hasForces = {"RecvBatchCheck"})
    public void batchlist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        //状态为已成功的单据
        AccCommonService.setSftCheckStatus(record, "service_status");
        try{
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
    @Auth(hasForces = {"RecvBatchCheck"})
    public void tradingList() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.tradingList(record);
        renderOk(page);
    }

    /**
     * 对账确认
     */
    @Auth(hasForces = {"RecvBatchCheck"})
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
    @Auth(hasForces = {"RecvBatchCheck", "MyWFPLAT"})
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
    @Auth(hasForces = {"RecvBatchCheck"})
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

}
