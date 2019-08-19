package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RecvCounterCheckService;
import com.qhjf.cfm.web.service.RecvCounterPosTransCheckService;
import com.qhjf.cfm.web.service.RecvCounterWaitingForMatchService;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 柜面收结算对账 pos明细与银行流水对账
 * @author pc_liweibing
 *
 */
public class RecvCounterWaitingForMatchController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(RecvCounterWaitingForMatchController.class);
    private RecvCounterWaitingForMatchService service = new RecvCounterWaitingForMatchService();

    /**
     * 柜面收款工作台-待匹配收款列表
     */
    @Auth(hasForces = {"RECVCOUNTERMATCH"})
    public void list() {
    	Record record = getRecordByParamsStrong();
        try {
        	int pageSize = getPageSize(record);
		    int pageNum = getPageNum(record);
		    Page<Record> list = service.list(pageSize,pageNum,record,getUserInfo(),getCurUodp());
		    renderOkPage(list);
        } catch (Exception e) {
			e.printStackTrace();			
		}
    	
    }
    
    
    /**
     * 柜面收款工作台-待匹配收款详情
     */
    @Auth(hasForces = {"RECVCOUNTERMATCH"})
    public void detail() {
    	Record record = getRecordByParamsStrong();
        try {
		    Record detail = service.detail(record,getUserInfo(),getCurUodp());
		    renderOk(detail);
        } catch (Exception e) {
			e.printStackTrace();			
		}
    	
    }
    
    /**
     * 柜面收款工作台-待匹配收款列表 撤销
     */
    @Auth(hasForces = {"RECVCOUNTERMATCH"})
    public void revoke() {
    	Record record = getRecordByParamsStrong();
        try {
		    service.revoke(record,getUserInfo(),getCurUodp());
		    renderOk(null);
        } catch (ReqDataException e) {
			e.printStackTrace();	
			renderFail(e);
		}
    	
    }
    
    
    
    /**
       * 柜面收款工作台-待匹配收款列表新增
     */
    @Auth(hasForces = {"RECVCOUNTERMATCH"})
    public void add() {
    	Record record = getRecordByParamsStrong();
        try {
		    service.add(record,getUserInfo(),getCurUodp());
		    renderOk(null);
        } catch (BusinessException e) {
			e.printStackTrace();	
			renderFail(e);
		}
    	
    }
    
    /**
     * 柜面收款工作台-待匹配收款列表匹配按钮
     * @throws UnsupportedEncodingException 
   */
    @Auth(hasForces = {"RECVCOUNTERMATCH"})
  public void match() throws UnsupportedEncodingException {
  	Record record = getRecordByParamsStrong();
      try {
		    Record match = service.match(record,getUserInfo(),getCurUodp());
		    renderOk(match);
      } catch (BusinessException e) {
			e.printStackTrace();	
			renderFail(e);
		}
  	
  }
  
  
  /**
   * 柜面收款工作台-退费
 * @throws UnsupportedEncodingException 
   */
    @Auth(hasForces = {"RECVCOUNTERMATCH"})
  public void refund() throws UnsupportedEncodingException {
  	Record record = getRecordByParamsStrong();
      try {
		    Record rec = service.refund(record,getUserInfo(),getCurUodp());
		    renderOk(rec);
      } catch (BusinessException e) {
			e.printStackTrace();	
			renderFail(e);
		}
  	
  }
  
  
    
    /**
     * 柜台收待匹配列表导出
     */
    @Auth(hasForces = {"RECVCOUNTERMATCH"}) 
    public void listexport() {
        doExport();
    }
}
