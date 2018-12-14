package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccountService;
import com.qhjf.cfm.web.service.AccCommonService;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;

/**
 * 账户信息维护
 *
 * @auther zhangyuanyuan
 * @create 2018/6/29
 */

@SuppressWarnings("unused")
//@Auth(hasRoles = {"normal"})
public class AccountController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AccountController.class);

    private AccountService service = new AccountService();

    /**
     * 账户信息列表
     */
    @Auth(hasForces = {"AccMtc"})
    public void list() throws ReqDataException {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setAccountListStatus(record, "service_status");

        Page<Record> page = service.findChangeToPage(pageNum, pageSize, record, getCurUodp());
        renderOkPage(page);
    }

    /**
     * 账户信息详情
     */
    @Auth(hasForces = {"AccMtc"})
    public void detail() {
        try {
            Record record = getParamsToRecord();
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("查看账户信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 账户信息修改
     */
    @Auth(hasForces = {"AccMtc"})
    public void chg() {
        try {
            Record record = getParamsToRecord();
            service.chg(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改账户信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 根据状态获取账户列表
     */
    public void accs() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            List<Record> list = service.findAccsByST(record);
            renderOk(list);
        } catch (ReqDataException e) {
            logger.error("获取账户列表失败！", e);
            renderFail(e);
        }

    }

    public void listexport() {
        Record record = getRecordByParamsStrong();
        AccCommonService.setAccountListStatus(record, "service_status");
        doExport(record);
    }
}
