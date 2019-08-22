package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.UsrmenuService;

/**
 * 用户菜单
 */
@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class UsrmenuController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(UsrmenuController.class);

    private UsrmenuService service = new UsrmenuService();

    /**
     * 返回用户列表
     */
    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.getUsrPage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    public void allot() {
        try {
            Record record = getParamsToRecord();
            service.allot(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("用户分配菜单失败！", e);
            renderFail(e);
        }
    }
}
