package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.PoolTradService;

import java.util.List;

/**
 * 资金下拨交易核对
 *
 * @author GJF
 */
@SuppressWarnings("unused")
public class PoolTradController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(PoolTradController.class);
    private PoolTradService service = new PoolTradService();


    /**
     * 未核对单据查询
     */
    @Auth(hasForces = {"ZJXBChecked"})
    public void billList() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        //状态为已成功的单据
        AccCommonService.setPoolTradStatus(record, "service_status");
        record.set("is_checked", 0);

        Page<Record> page = service.billList(pageNum, pageSize, record);

        renderOkPage(page);
    }

    /**
     * 勾选 查找交易流水
     */
    @Auth(hasForces = {"ZJXBChecked"})
    public void tradingList() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.tradingList(record);
        renderOk(page);
    }

    /**
     * 确认交易
     */
    @Auth(hasForces = {"ZJXBChecked"})
    public void confirm() {
        try {
            Record record = getRecordByParamsStrong();

            UserInfo userInfo = getUserInfo();

            Page<Record> page = service.confirm(record, userInfo);
            renderOkPage(page);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 已核对单据查询
     */
    @Auth(hasForces = {"ZJXBChecked"})
    public void confirmbillList() {
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
    @Auth(hasForces = {"ZJXBChecked"})
    public void confirmTradingList() {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.confirmTradingList(record);
        renderOk(page);
    }

}
