package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.BankkeySettingService;
import com.qhjf.cfm.web.service.PayCheckService;

import java.util.List;

/**
 * 批量付 结算对账
 *
 * @author GJF
 * @date 2018年12月28日
 */
public class PayCheckController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(PayCheckController.class);
    private PayCheckService service = new PayCheckService();

    /**
     * 查询所有批次
     */
    @Auth(hasForces = {"PayBatchCheck"})
    public void batchlist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        //状态为已成功的单据
        AccCommonService.setSftCheckStatus(record, "service_status");
        Page<Record> page = service.batchlist(pageNum, pageSize, record);
        if(page.getList().size() != 0){
            renderOkPage(page, new Record().set("is_inner", page.getList().get(0).get("is_inner")));
        }else{
            renderOkPage(page, new Record().set("is_inner", ""));
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
