package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DztCheckService;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 已勾兑查询
 */
public class DztCheckController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DztCheckController.class);
    private DztCheckService service = new DztCheckService();
    
    /**
     * 未核对单据查询
     */
    @Auth(hasForces = {"DztInitCheck"})
    public void billList() throws Exception {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        record.set("is_checked", 0);

        Page<Record> page = service.billList(pageNum, pageSize, record);

        renderOkPage(page);
    }

    /**
     * 勾选 查找交易流水
     */
    @Auth(hasForces = {"DztInitCheck"})
    public void tradingList() {
        Record record = getRecordByParamsStrong();
        Integer creditOrDebit = TypeUtils.castToInt(record.get("credit_or_debit"));
        if(creditOrDebit == 1){
        	creditOrDebit = 2;
        }else if(creditOrDebit == 2){
        	creditOrDebit = 1;
        }
        record.set("credit_or_debit", creditOrDebit);
        List<Record> page = service.tradingList(record);
        renderOk(page);
    }

    /**
     * 确认交易
     */
    @Auth(hasForces = {"DztInitCheck"})
    public void confirm() {
        try {
            Record record = getRecordByParamsStrong();
            
            Page<Record> page = service.confirm(record);
            renderOkPage(page);
            
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 已核对单据查询
     */
    @Auth(hasForces = {"DztInitCheck"})
    public void confirmbillList() throws Exception {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        record.set("is_checked", 1);
        
        Page<Record> page = service.confirmbillList(pageNum, pageSize, record);

        renderOkPage(page);
    }
    
    /**
     * 已核对单据交易查询
     */
    @Auth(hasForces = {"DztInitCheck"})
    public void confirmTradingList() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.confirmTradingList(record);
        renderOk(page);
    }
}
