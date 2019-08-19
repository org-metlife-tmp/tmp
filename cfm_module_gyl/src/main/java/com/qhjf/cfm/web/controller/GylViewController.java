package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.GylViewService;

public class GylViewController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(GylViewController.class);

    private GylViewService service = new GylViewService();

     /**
       * 
       *  广银联列表查询
       */
    @Auth(hasForces = {"GYLBFJView"})
    public  void  collections() throws Exception{ 
    	log.info("============进入广银联_查看列表接口");
    	Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        record.set("create_org_id", getCurUodp().getOrg_id());
    	service.setBillListStatus(record, "service_status");
    	Page<Record> page = service.findList(pageNum,pageSize,record);
    	renderOkPage(page);
    }
    
    
    /**
     * 
     * 广银联_查看详情
    * @throws ReqDataException 
     */
    @Auth(hasForces = {"GYLBFJView","MyWFPLAT"})
   public  void  datail() throws ReqDataException {
   	
   	log.info("===========进入广银联_查看详情接口");
   	Record record = getRecordByParamsStrong();
   	try {
   		Record returnRecord = service.detail(record); 
   		renderOk(returnRecord);
   	}catch(BusinessException e) {
   		log.info("========获取广银联详情接口失败了");
   		e.printStackTrace();
        renderFail(e);
   	}
   	
   }
}
