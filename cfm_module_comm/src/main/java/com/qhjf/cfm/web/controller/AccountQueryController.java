package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccountQueryService;

import java.util.List;

@Auth(hasRoles = {"admin", "normal"})
public class AccountQueryController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AccountQueryController.class);

    private AccountQueryService service = new AccountQueryService();

    /**
     * 获取正常状态的账户列表
     */
    public void normallist(){
        try {
            Record record = getParamsToRecord();
            if(record == null){
                record = new Record();
            }
            record.set("org_id", getCurUodp().getOrg_id());
            List<Record> list = service.normallist(record);
            renderOk(list);
        } catch (ReqDataException e) {
            logger.error("获取账户列表失败！", e);
            renderFail(e);
        }
    }
}
