package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BankQueryService;

import java.util.List;

@Auth(hasRoles = {"admin", "normal"})
public class BankQueryController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(BankQueryController.class);

    private final BankQueryService service = new BankQueryService();

    /**
     * 获取银行大类列表
     */
    public void typelist(){
        logger.debug("Enter into typelist()");
        Record li = service.getBankType(getParamsToRecord());
        renderOk(li);
    }


    /**
     * 获取银行列表
     */
    public void list(){
        logger.debug("Enter into list()");
        List<Record> li = service.getBankList(getParamsToRecord());
        renderOk(li);
    }
}
