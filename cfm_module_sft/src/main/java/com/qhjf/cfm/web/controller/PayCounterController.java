package com.qhjf.cfm.web.controller;


import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CheckBatchForService;
import com.qhjf.cfm.web.service.PayCounterService;


/**
 * 柜面付款工作台
 * @author pc_liweibing
 *
 */
public class PayCounterController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(PayCounterController.class);
	
    PayCounterService service = new  PayCounterService();
    
   /**
    * 柜面列表
    */
    
	public  void  list() throws ReqDataException {
		try {
			Record record = getRecordByParamsStrong();
	    	int pageNum = getPageNum(record);
	        int pageSize = getPageSize(record);   		
	    	Page<Record> page = service.list(pageNum,pageSize,record,getCurUodp().getOrg_id());
	    	renderOkPage(page);			
		} catch (BusinessException e) {
			logger.error("柜面列表获取失败");
			e.printStackTrace();
			renderFail(e);
		}
		
	}
    
	
	
	  /**
	    * 柜面补录
	    */	    
		public  void  supplement() {		
			try {
				Record record = getRecordByParamsStrong();	
				service.supplement(record,getUserInfo());
				renderOk(null);
			}catch (BusinessException e) {
				logger.error("=====柜面补录失败");
				e.printStackTrace();
				renderFail(e);
			}			
		}
		
		
		  /**
		    * 柜面拒绝
		    */	    
			public  void  revokeToLaOrEbs() {		
				try {
					Record record = getRecordByParamsStrong();	
					service.revokeToLaOrEbs(record,getUserInfo());
					renderOk(null);
				}catch (BusinessException e) {
					logger.error("=====柜面付拒绝失败");
					e.printStackTrace();
					renderFail(e);
				}			
			}
			
			/**
			    * 柜面确认/提交
			    */	    
				public  void  confirm() {		
					try {
						Record record = getRecordByParamsStrong();	
						service.confirm(record,getUserInfo(),getCurUodp());
						renderOk(null);
					}catch (BusinessException e) {
						logger.error("=====柜面确认失败");
						e.printStackTrace();
						renderFail(e);
					}			
				}	
}
