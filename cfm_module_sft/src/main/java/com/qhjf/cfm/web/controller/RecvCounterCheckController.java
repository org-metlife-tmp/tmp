package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RecvCounterCheckService;

import java.util.List;

/**
 * 柜面收结算对账 个单&团单
 * @author pc_liweibing
 *
 */
public class RecvCounterCheckController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(RecvCounterCheckController.class);
    private RecvCounterCheckService service = new RecvCounterCheckService();

    /**
     * 结算对账_单据列表 
     */
	@Auth(hasForces = {"RECVCOUNTERCHECK"})
    public void list() {
    	Record record = getRecordByParamsStrong();
        try {
        	int pageSize = getPageSize(record);
		    int pageNum = getPageNum(record);
		    Page<Record> list = service.list(pageSize,pageNum,record,getUserInfo(),getCurUodp());
		    renderOkPage(list);
        } catch (Exception e) {
			e.printStackTrace();			
		}
    	
    }

	/**
	 * 查找交易流水
	 */
	@Auth(hasForces = {"RECVCOUNTERCHECK"})
	public void tradingList() {
		Record record = getRecordByParamsStrong();
		List<Record> page = service.tradingList(record);
		renderOk(page);
	}

	/**
	 * 对账确认
	 */
	@Auth(hasForces = {"RECVCOUNTERCHECK"})
	public void confirm() {
		try {
			Record record = getRecordByParamsStrong();

			UserInfo userInfo = getUserInfo();
			service.confirm(record,userInfo);
			renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
	}

}
