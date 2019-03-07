package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DoubtfulRefundService;

/**
 * @可疑退票模块
 * @author pc_liweibing
 *
 */
public class DoubtfulRefundController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(DoubtfulRefundController.class);
	
    private DoubtfulRefundService service = new DoubtfulRefundService();
	
    /**
	 * 
	 * @throws ReqDataException 
	 * @ 可疑退票_退票模块系列表
	 */
	public void tradeList() throws ReqDataException {
		log.info("=========进入可疑退票_交易列表模块");
		Record record = getRecordByParamsStrong();
		int pageNum = getPageNum(record);
		int pageSize = getPageSize(record);
		Page<Record> page = service.tradeList(pageNum,pageSize,record);
		renderOkPage(page);
	}
	
	   /**
		 * @throws ReqDataException 
		 * @ 可疑退票_判断为正常交易
		 */
		public void normalTrade() throws ReqDataException {
			log.info("=========进入可疑退票_判断为正常交易模块");
			Record record = getRecordByParamsStrong();
			Page<Record> page = service.normalTrade(record);
			renderOkPage(page);
		}
		
		 /**
		 * @throws ReqDataException 
		 * @throws WorkflowException 
		 * @ 可疑退票_根据交易查找单据
		 */
		
		public void billList() throws Exception {
			log.info("=========进入可疑退票_根据交易查找单据模块");
			Record record = getRecordByParamsStrong();
			int pageNum = getPageNum(record);
			int pageSize = getPageSize(record);
			Page<Record> page = service.billList(pageNum,pageSize,record);
			renderOkPage(page);
		}
		
		/**
		 * @可疑退票_退票确认
		 */
		public void confirm() throws Exception {
			log.info("=========进入可疑退票_根据交易查找单据模块");
			Record record = getRecordByParamsStrong();
			int pageNum = getPageNum(record);
			int pageSize = getPageSize(record);
			UserInfo userInfo = getUserInfo();
			Page<Record> page = service.confirm(pageNum,pageSize,record,userInfo);
			renderOkPage(page);
		}
}
