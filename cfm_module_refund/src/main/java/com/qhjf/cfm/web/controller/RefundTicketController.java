package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RefundTicketService;

import java.text.ParseException;

/**
 * @退票模块
 * @author pc_liweibing
 *
 */
public class RefundTicketController extends CFMBaseController {
	
	private static final Log log = LogbackLog.getLog(RefundTicketController.class);
	
    private RefundTicketService service = new RefundTicketService();
	
	/**
	 * 
	 * @throws ReqDataException 
	 * @ 主动退票_交易列表
	 */
	public void tradeList() throws ReqDataException {
		log.info("=========进入主动退票_交易列表模块");
		try {
			Record record = getRecordByParamsStrong();
			int pageNum = getPageNum(record);
			int pageSize = getPageSize(record);
			UodpInfo uodpInfo = getDefaultUodp();
			Page<Record> page = service.activeRefund(pageNum, pageSize, record , uodpInfo);
			renderOkPage(page);			
		}catch (BusinessException e) {
			log.error("主动退票_交易列表展示失败");
			renderFail(e);
		}
	}
	
	/**
	 * 
	 * @throws WorkflowException 
	 * @throws ReqDataException 
	 * @throws ParseException 
	 * @ 主动退票_单据列表
	 */
	public void billList() throws WorkflowException, ReqDataException, ParseException {
		log.info("=========进入主动退票_单据列表模块");
		try {
			Record record = getRecordByParamsStrong();
			int pageNum = getPageNum(record);
			int pageSize = getPageSize(record);
			Page<Record> page = service.activeBillList(pageNum, pageSize, record);
			renderOkPage(page);			
		}catch (BusinessException e) {
			log.error("主动退票_单据列表模块失败");
			renderFail(e);
		}
	}
	
	/**
	 * 
	 * @throws WorkflowException 
	 * @throws ReqDataException 
	 * @throws ParseException 
	 * @ 主动退票_退票确认
	 */
	public void confirm() throws WorkflowException, ReqDataException{
		log.info("=========进入主动退票_确认退票");
		try {
			Record record = getRecordByParamsStrong();
			UserInfo userInfo = getUserInfo();
			UodpInfo curUodp = getCurUodp();
			Page<Record> page = service.confirm(record,userInfo,curUodp);
			renderOkPage(page);			
		}catch (BusinessException e) {
			log.error("主动退票_确认退票");
			renderFail(e);
		}
	}
}
