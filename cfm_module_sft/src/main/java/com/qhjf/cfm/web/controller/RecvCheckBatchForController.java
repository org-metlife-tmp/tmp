package com.qhjf.cfm.web.controller;

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
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CheckBatchForService;
import com.qhjf.cfm.web.service.RecvCheckBatchForService;

/**
 * 批收核对组批LA
 * @author pc_liweibing
 *
 */
public class RecvCheckBatchForController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(RecvCheckBatchForController.class);
	
    RecvCheckBatchForService service = new  RecvCheckBatchForService();
    
    /**
     *@ 批付组批LA列表
     */
    //@Auth(hasForces = {"PayCheckAllot"})
	public void  list(){
		logger.info("============获取核对组批LA列表");
		Record record = getRecordByParamsStrong();
        try {            	
        	int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);
        	Page<Record> page = service.list(pageNum , pageSize ,record,getCurUodp());	
        	//查询所有单据总金额
            Record totalRec = service.totalInfo(record,getCurUodp());
        	renderOkPage(page,totalRec);
		} catch (BusinessException e) {
			logger.error("获取组批LA列表失败");
			renderFail(e);
		}
	}	
	
	
	
	 /**
     *@ 组批LA撤回
     */
    //@Auth(hasForces = {"PayCheckAllot"})
	public void  revokeToLaOrEbs(){
		logger.info("============撤回至LA");
		Record record = getRecordByParamsStrong();
        try {
        	service.revokeToLaOrEbs(record);	
        	renderOk(null);
		} catch (BusinessException e) {
			logger.error("核对组批撤回失败");
			renderFail(e);
		}
	}	
	

	

	/**
	 * 组批确定按钮
	 */
    //@Auth(hasForces = {"PayCheckAllot"})
	public void confirm() {
        try {
        	Record record = getRecordByParamsStrong(); 
            service.confirm(record,getCurUodp(),getUserInfo());  
            renderOk(null);
        } catch (BusinessException e) {
            renderFail(e);
            logger.error("=======组批确认失败");
        }
    }
	
	
    
   
    /**
     * 通道编码列表
     * @throws ReqDataException 
     */
    public void channelCodeList() throws ReqDataException {   	
    	logger.info("===========进入通道编码列表");
    	Record record = getRecordByParamsStrong();
    	//Long org_id = getCurUodp().getOrg_id();
    	//record.set("org_id", org_id);
        List<Record> lists = service.channelCodeList(record);	
        renderOk(lists);    	
    }
    
    
    /**
      *@ 核对组批导出
      */
    //@Auth(hasForces = {"PayCheckAllot"})
    public void listexport() {
        doExport();
    }
    
    
    /**
     * @审批平台页面_根据主批次号查找子批次
     */
    @Auth(hasForces = {"PayCheckAllot", "MyWFPLAT"})
    public void findSonByMasterBatch() {
        Record record = getRecordByParamsStrong();
        List<Record> list = service.findSonByMasterBatch(record);
        renderOk(list);
    }
    
      
   
}
