package com.qhjf.cfm.web.controller;


import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CollectCheckService;

import java.util.List;

/**
 * 归集通校验核对
 * @author pc_liweibing
 *
 */
public class CollectCheckController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CollectCheckController.class);

    private CollectCheckService service = new CollectCheckService();

      /**
        * 
      	*   归集通交易核对_单据列表
        */
    @Auth(hasForces = {"GJCheck"})
    public void checkbillList() throws Exception {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        UodpInfo uodpInfo = getCurUodp();
        record.set("pay_account_org_id", uodpInfo.getOrg_id());
        //状态为已成功的单据
        service.setInnerTradStatus(record, "collect_status");
        Page<Record> page = service.checkbillList(pageNum, pageSize, record);
        renderOkPage(page);
    }
    
    
       /**
           *   支付通交易核对_未核对交易列表  
         * 
         * @throws ReqDataException 
         */
    @Auth(hasForces = {"GJCheck"})
    public void checkNoCheckTradeList() throws ReqDataException {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.checkNoCheckTradeList(record);
        renderOk(page);
    }
    
    /**
     * 支付通交易核对_已核对交易列表
     * @throws ReqDataException 
     */
    @Auth(hasForces = {"GJCheck"})
    public void checkAlreadyTradeList() throws ReqDataException {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.checkAlreadyTradeList(record);
        renderOk(page);
    }
    
    /**
     *   核对
     */
    @Auth(hasForces = {"GJCheck"})
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
