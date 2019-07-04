package com.qhjf.cfm.web.controller;


import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RecvCounterService;




/**
 * 柜面收款工作台
 * @author pc_liweibing
 *
 */
public class RecvCounterController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(RecvCounterController.class);
	
    RecvCounterService service = new  RecvCounterService();
    
    /**
     * 批量收_个单新增
     * @throws ParseException 
     */   
    @Auth(hasForces = {"RECVCOUNTERPERSON"})
    public void add() throws ParseException {
    	
    	logger.info("进入批量收_个单新增");
    	Record record = getRecordByParamsStrong();
    	try {
    		UserInfo userInfo = getUserInfo();
    		UodpInfo curUodp = getCurUodp();
    		service.add(record,userInfo,curUodp);
    		renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}     	
    }
    
    /**
     * 柜面收个单列表
     * @throws UnsupportedEncodingException 
     */
    @Auth(hasForces = {"RECVCOUNTERPERSON"})
    public void list() throws UnsupportedEncodingException {
    	Record record = getRecordByParamsStrong();
    	try {
    		int pageSize = getPageSize(record);
    		int pageNum = getPageNum(record);
    		Page<Record> list = service.list(record,getCurUodp(),pageSize,pageNum);
    		renderOkPage(list);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
    
    
    /**
     * 柜面收个单详情
     * @throws UnsupportedEncodingException 
     */
    @Auth(hasForces = {"RECVCOUNTERPERSON", "MyWFPLAT"})
    public void detail() throws UnsupportedEncodingException {
    	Record record = getRecordByParamsStrong();
    	try {
    		Record rec = service.detail(record);
    		renderOk(rec);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
 
    /**
     * 柜面收个单列表详情确认
     */
    @Auth(hasForces = {"RECVCOUNTERPERSON"})
    public void detailConfirm() {
    	Record record = getRecordByParamsStrong();
    	try {
    		service.detailConfirm(record);
    		renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
    
    
    /**
     * 柜面收获取批次号
     */
    @Auth(hasForces = {"RECVCOUNTERPERSON","RECVCOUNTERMATCH","RECVCOUNTERGROUP"})
    public void getBatchProcessno() {
    	Record record = getRecordByParamsStrong();
    	try {
    		Record rec = service.getBatchProcessno(record);
    		renderOk(rec);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
 
    
    /**
     * 柜面收撤销
     */
    @Auth(hasForces = {"RECVCOUNTERPERSON"})
    public void revoke() {
    	Record record = getRecordByParamsStrong();
    	try {
    		service.revoke(record,getUserInfo(),getCurUodp());
    		renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
    /**
     *@ 柜面收导出
     */
    @Auth(hasForces = {"RECVCOUNTERPERSON"})
   public void listexport() {
       doExport();
   }
   
   
   /**
    * 柜面收获取保单信息
    */
    @Auth(hasForces = {"RECVCOUNTERPERSON"})
   public void getPolicyInfo() {
   	Record record = getRecordByParamsStrong();
   	try {
   		   Record policyInfo = service.getPolicyInfo(record);
   		   renderOk(policyInfo);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
   }

}
