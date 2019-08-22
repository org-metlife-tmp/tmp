package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkCalService;

import java.util.HashMap;
import java.util.Map;

public class WorkCalController extends CFMBaseController {

    private static final Log logger = LogbackLog.getLog(WorkCalController.class);

    private WorkCalService calService = new WorkCalService();
    
    /**
     * 日历列表查询
     */
    @Auth(withRoles = {"normal"})
    public void list() {
        Record record = getParamsToRecord();
        try {
        	HashMap<String,Object> resultMap = calService.findWorkCalList(record);
        	renderOk(resultMap);
        }catch(BusinessException e) {
            logger.error("请求失败!", e);
            renderFail(e);
        }
    }
    
    /**
     * 日历列表初始化
     */
    @Auth(withRoles = {"admin"})
    public void init() {
    	Record record = getParamsToRecord();
    	try {
    		HashMap<String,Object> resultMap = calService.initWorkCalList(record);
    		renderOk(resultMap);
    	}catch(BusinessException e) {
    		logger.error("请求失败!", e);
    		renderFail(e);
    	}
    }
    
    /**
     * 修改是否为节假日
     */
    @Auth(withRoles = {"admin"})
    public void holiday() {
    	Record record = getParamsToRecord();
    	try {
    		calService.updateWorkCalHoliday(record);
    		renderOk(null);
    	}catch(BusinessException e) {
    		logger.error("请求失败!", e);
    		renderFail(e);
    	}
    }
    
    /**
     * 激活
     */
    @Auth(withRoles = {"admin"})
    public void activity() {
    	Record record = getParamsToRecord();
    	try {
    		Record rc = calService.updateWorkCalActivity(record);
    		renderOk(rc);
    	}catch(BusinessException e) {
    		logger.error("请求失败!", e);
    		renderFail(e);
    	}
    }
    
    /**
     * 新增银行报盘日期
     */
    @Auth(withRoles = {"normal"},hasForces = {"CheckoutSet"})
    public void checkoutset() {
    	Record record = getParamsToRecord();
    	try {
    		calService.updateWorkCalCheckout(record);
    		renderOk(null);
    	}catch(BusinessException e) {
    		logger.error("请求失败!", e);
    		renderFail(e);
    	}
    }      
 
    /**
     * 结账日列表查询
     */
    @Auth(withRoles = {"normal"},hasForces = {"CheckoutSet"})
    public void checkoutlist() {
        Record record = getParamsToRecord();
        try {
        	Map<String,Object> list = calService.findCheckoutList(record);
        	renderOk(list);
        }catch(BusinessException e) {
            logger.error("请求失败!", e);
            renderFail(e);
        }
    } 
    
    /**
     * 启用 停用 结账日
     */
    @Auth(withRoles = {"normal"},hasForces = {"CheckoutSet"})
    public void checkoutactivity() {
    	Record record = getParamsToRecord();
    	try {
    		Record rc = calService.updateCheckoutActivity(record);
    		renderOk(rc);
    	}catch(BusinessException e) {
    		logger.error("请求失败!", e);
    		renderFail(e);
    	}
    }    
    
}
