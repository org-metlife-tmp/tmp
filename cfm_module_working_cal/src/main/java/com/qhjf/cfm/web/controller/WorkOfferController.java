package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkOfferService;

import java.util.Map;

public class WorkOfferController extends CFMBaseController {
    private static final Log logger = LogbackLog.getLog(WorkWeekController.class);

    private WorkOfferService weekService = new WorkOfferService();
    /**
     * 报盘列表查询
     */
    @Auth(hasForces = {"OfferSet"})
    public void list() {
        Record record = getParamsToRecord();
        try {
        	Map<String,Object> list = weekService.findWorkOfferList(record);
        	renderOk(list);
        }catch(BusinessException e) {
            logger.error("请求失败!", e);
            renderFail(e);
        }
    }
    
    /**
     * 新增银行报盘日期
     */
    @Auth(hasForces = {"OfferSet"})
    public void add() {
    	Record record = getParamsToRecord();
    	try {
    		weekService.addWorkOfferList(record);
    		renderOk(null);
    	}catch(BusinessException e) {
    		logger.error("请求失败!", e);
    		renderFail(e);
    	}
    }
    
    /**
     * 启用 停用
     */
    @Auth(hasForces = {"OfferSet"})
    public void activity() {
    	Record record = getParamsToRecord();
    	try {
    		Record rc = weekService.updateWorkOfferActivity(record);
    		renderOk(rc);
    	}catch(BusinessException e) {
    		logger.error("请求失败!", e);
    		renderFail(e);
    	}
    }    
    
   
    
}
