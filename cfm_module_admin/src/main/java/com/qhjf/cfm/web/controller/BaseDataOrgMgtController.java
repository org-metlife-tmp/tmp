package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BaseDataOrgService;

import java.util.List;

/**
 * 机构（公司）
 */
@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class BaseDataOrgMgtController extends CFMBaseController {

    private static Log log = LogbackLog.getLog(BaseDataOrgMgtController.class);

    //private BaseDataOrgService orgService = enhance(BaseDataOrgService.class);
    private BaseDataOrgService orgService = new BaseDataOrgService();

    public void list() {
        List<Record> orgList = orgService.getOrgList();
        renderOk(orgList);
    }

    public void add() {
        try {
            Record record = getRecordByParamsStrong();
            orgService.saveOrgInfo(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("新增机构信息失败", e);
            renderFail(e);
        }
    }

    public void chg() {
        try {
            Record record = getRecordByParamsStrong();
            orgService.chgOrgInfo(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("修改机构信息失败", e);
            renderFail(e);
        }

    }

    public void setstatus() {
        try {
            Record record = getParamsToRecord();
            orgService.setState(record);
            renderOk(record.get("status"));
        }catch (BusinessException e){
            log.error("修改机构状态失败", e);
            renderFail(e);
        }
    }

    public void del() {
        try {
            orgService.delOrgInfo(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除机构失败", e);
            renderFail(e);
        }
    }
}
