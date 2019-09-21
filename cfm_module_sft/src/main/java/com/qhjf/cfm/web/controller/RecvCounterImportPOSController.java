package com.qhjf.cfm.web.controller;


import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RecvCounterImportPOSService;
import com.qhjf.cfm.web.service.RecvCounterService;




/**
 * 柜面收款POS机导入 详情
 * @author pc_liweibing
 *
 */
public class RecvCounterImportPOSController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(RecvCounterImportPOSController.class);
	
    RecvCounterImportPOSService service = new  RecvCounterImportPOSService();
    
 
    
    /**
     * 柜面收pos机列表
     */
    @Auth(hasForces = {"RECVCOUNTEPOSIMPORT"})	
    public void list() {
    	logger.info("====进入pos机导入列表====");
    	Record record = getRecordByParamsStrong();
    	try {
    		int pageSize = getPageSize(record);
    		int pageNum = getPageNum(record);
    		Page<Record> list = service.list(record,getCurUodp(),pageSize,pageNum);
    		//查询单据总金额,总笔数
    		//Record totalRec = service.totalinfo(record);
    		//renderOkPage(list, totalRec);
    		renderOkPage(list);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
    
 
 
    
    /**
     *@ 柜面收POS机导出
     */
   @Auth(hasForces = {"RECVCOUNTEPOSIMPORT"})
   public void listexport() {
       doExport();
   }
   
}
