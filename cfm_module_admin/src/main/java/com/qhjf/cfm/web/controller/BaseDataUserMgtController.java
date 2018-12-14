package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BaseDataUsrService;

@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class BaseDataUserMgtController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(BaseDataUserMgtController.class);

    //private BaseDataUsrService usrService = enhance(BaseDataUsrService.class);
    private BaseDataUsrService usrService = new BaseDataUsrService();

    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = usrService.getUsrPage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    public void add() {
        try {
            Record result = usrService.saveUsrInfo(getRecordByParamsStrong());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("新增用户失败！", e);
            renderFail(e);
        }
    }

    public void detail() {
        Record usrInfo = usrService.findUsrInfo(getParamsToRecord());
        renderOk(usrInfo);
    }

    public void del() {
        try {
            usrService.deleteUsr(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除用户失败", e);
            renderFail(e);
        }
    }

    public void chg() {
        try {
            Record record = getRecordByParamsStrong();
            Record result = usrService.updateUsrInfo(record);
            renderOk(result);
        } catch (BusinessException e) {
            log.error("修改用户信息失败！", e);
            renderFail(e);
        }
    }

    public void setstatus() {
        try {
            Record record = getParamsToRecord();
            usrService.setStatus(record);
            renderOk(record.get("status"));
        } catch (BusinessException e) {
            log.error("修改用户状态失败", e);
            renderFail(e);
        }
    }
}
