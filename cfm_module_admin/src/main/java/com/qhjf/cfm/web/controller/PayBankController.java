package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccountQueryService;
import com.qhjf.cfm.web.service.PayBankService;

import java.util.List;

@Auth(withRoles = {"admin"})
public class PayBankController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(PayBankController.class);

    private PayBankService service = new PayBankService();

    /**
     * 获取正常状态的账户列表
     */
    public void send(){
        Record record = getParamsToRecord();
        if(record == null){
            renderOk("请求报文为空");
        }
        service.send(record);
        renderOk(null);
    }
}
