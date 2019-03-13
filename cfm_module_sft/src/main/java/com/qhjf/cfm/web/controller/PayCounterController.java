package com.qhjf.cfm.web.controller;


import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
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
		Record record = getRecordByParamsStrong();
		try {
			service.list(record);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
    
}
