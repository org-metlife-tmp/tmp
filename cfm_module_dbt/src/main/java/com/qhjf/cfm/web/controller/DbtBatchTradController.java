package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.DbtBatchTradService;

import java.util.List;

/**
 * 调拨通核对
 * @author GJF
 *
 */
@SuppressWarnings("unused")
public class DbtBatchTradController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DbtBatchTradController.class);
    private DbtBatchTradService service = new DbtBatchTradService();


    /**
     * 未核对单据查询
     */
    @Auth(hasForces = {"DbtCheck"})
    public void billList() throws Exception {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        UodpInfo uodpInfo = getCurUodp();
        record.set("org_id", uodpInfo.getOrg_id());
        //状态为已成功的单据
        AccCommonService.setInnerBatchTradStatus(record, "service_status");
        record.set("is_checked", 0);

        Page<Record> page = service.billList(pageNum, pageSize, record);

        renderOkPage(page);
    }

    /**
     * 勾选 查找交易流水
     */
    @Auth(hasForces = {"DbtCheck"})
    public void tradingList() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.tradingList(record);
        renderOk(page);
    }

    /**
     * 确认交易
     */
    @Auth(hasForces = {"DbtCheck"})
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
     * 已核对单据查询
     */
    @Auth(hasForces = {"DbtCheck"})
    public void confirmbillList() throws Exception {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        UodpInfo uodpInfo = getCurUodp();
        record.set("org_id", uodpInfo.getOrg_id());
        record.set("is_checked", 1);
        
        Page<Record> page = service.confirmbillList(pageNum, pageSize, record);

        renderOkPage(page);
    }
    
    /**
     * 已核对单据交易查询
     */
    @Auth(hasForces = {"DbtCheck"})
    public void confirmTradingList() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.confirmTradingList(record);
        renderOk(page);
    }

}
