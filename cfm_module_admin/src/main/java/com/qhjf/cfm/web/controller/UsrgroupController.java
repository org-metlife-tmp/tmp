package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.UsrgroupService;

/**
 * 用户组
 */
@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class UsrgroupController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(UsrgroupController.class);

    private UsrgroupService service = new UsrgroupService();

    public void busmenu() {
        renderOk(service.busmenu());
    }

    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    public void add() {
        try {
            Record record = getParamsToRecord();
            service.add(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("新增用户组失败！", e);
            renderFail(e);
        }
    }

    public void del() {
        try {
            Record record = getParamsToRecord();
            service.del(record);
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除用户组失败！", e);
            renderFail(e);
        }
    }

    public void chg() {
        try {
            Record record = getParamsToRecord();
            service.chg(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("修改用户组信息失败！", e);
            renderFail(e);
        }
    }

    public void allot(){
        try {
            Record record = getParamsToRecord();
            service.allot(record);
            renderOk(record);
        }catch (BusinessException e){
            log.error("用户组权限分配失败！",e);
            renderFail(e);
        }
    }

    /**
     * 用户组，带分配权限的用户。
     */
    public void list2(){
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.list2(pageNum, pageSize, record);
        renderOkPage(page);
    }
}
