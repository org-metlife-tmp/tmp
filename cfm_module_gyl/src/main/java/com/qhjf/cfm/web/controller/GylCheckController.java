package com.qhjf.cfm.web.controller;

import java.util.List;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.GylCheckService;

public class GylCheckController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(GylCheckController.class);

    private GylCheckService service = new GylCheckService();


    /**
     * 
   	*   广银联交易核对_单据列表
     */
@Auth(hasForces = {"GYLBFJCheck"})
 public void checkbillList() throws Exception {
	 log.info("================进入广银联单据列表");
     Record record = getRecordByParamsStrong();
     int pageNum = getPageNum(record);
     int pageSize = getPageSize(record);
     UodpInfo uodpInfo = getCurUodp();
     record.set("pay_account_org_id", uodpInfo.getOrg_id());
     //状态为已成功的单据
     service.setInnerTradStatus(record, "gyl_allocation_status");
     Page<Record> page = service.checkbillList(pageNum, pageSize, record);
     renderOkPage(page);
 }
 
 
    /**
        *   支付通交易核对_未核对交易列表  
      * 
      * @throws ReqDataException 
      */
 @Auth(hasForces = {"GYLBFJCheck"})
 public void checkNoCheckTradeList() throws ReqDataException {
     Record record = getRecordByParamsStrong();
     List<Record> page = service.checkNoCheckTradeList(record);
     renderOk(page);
 }
 
 /**
  * 支付通交易核对_已核对交易列表
  * @throws ReqDataException 
  */
 @Auth(hasForces = {"GYLBFJCheck"})
 public void checkAlreadyTradeList() throws ReqDataException {
     Record record = getRecordByParamsStrong();
     List<Record> page = service.checkAlreadyTradeList(record);
     renderOk(page);
 }
 
 /**
  *   核对
  */
@Auth(hasForces = {"GYLBFJCheck"})
 public void confirmCheck() throws Exception {
 	 try {
 	        Record record = getRecordByParamsStrong();
 	        
 	        UserInfo userInfo = getUserInfo();
 	        
 	        Page<Record> page = service.confirm(record,userInfo);
 	        renderOkPage(page);	        
 	    } catch (BusinessException e) {
 	        e.printStackTrace();
 	        renderFail(e);
 	    }
 }
    
    
}
