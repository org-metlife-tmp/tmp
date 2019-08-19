package com.qhjf.cfm.web.controller;


import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.excel.util.ExcelRedisUtil;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RecvCounterImportPOSService;
import com.qhjf.cfm.web.service.RecvCounterPosRecordCheckPosDetailService;
import com.qhjf.cfm.web.service.RecvCounterService;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


/**
 * 柜面收款  pos记录与明细对账
 * @author pc_liweibing
 *
 */
@Auth(hasRoles = {"admin", "normal"})
public class RecvCounterPosRecordCheckPosDetailController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(RecvCounterPosRecordCheckPosDetailController.class);
	
    RecvCounterPosRecordCheckPosDetailService service = new RecvCounterPosRecordCheckPosDetailService();
    
 
    
    /**
     * 柜面收pos记录查询   个单录入
     */
	@Auth(hasForces = {"RECVCOUNTERPOSCHECK"})
    public void list() {
    	logger.info("====POS收款记录与POS明细对账====");
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
     * 柜面收pos记录查询   pos记录导入
     */
	@Auth(hasForces = {"RECVCOUNTERPOSCHECK"})
    public void tradingList() {
    	logger.info("====POS机明细====");
    	Record record = getRecordByParamsStrong();
		List<Record> page = service.postlist(record);
		renderOk(page);
    }
    
    /**
     * 柜面收pos记录查询   pos记录导入
     */
	@Auth(hasForces = {"RECVCOUNTERPOSCHECK"})
    public void confirm() {
    	logger.info("====POS机明细与POS记录核对====");
    	Record record = getRecordByParamsStrong();
    	try {
			UserInfo userInfo = getUserInfo();
			service.confirm(record, userInfo);
    		renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
   
    /**
     * POS单据excel导入
     */
	@Auth(hasForces = {"RECVCOUNTERPOSCHECK"})
    public void importPos(){
    	Record paramsToRecord = getParamsToRecord();
		try {
			ExcelResultBean bean = ExcelRedisUtil.getExcelResultBean(paramsToRecord);
			UserInfo userInfo = getUserInfo();
			service.importPos(bean, userInfo);
    		renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
    
}
