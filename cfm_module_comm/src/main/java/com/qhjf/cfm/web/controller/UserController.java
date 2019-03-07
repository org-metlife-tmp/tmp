package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.UserQueryService;

import java.util.List;

/**
 * Created by zhangsq on 2018/6/28.
 */
@Auth(hasRoles = {"admin", "normal"})
public class UserController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(UserController.class);

    private UserQueryService service = new UserQueryService();

    public void list() {
        try {
            Record record = getParamsToRecord();
            if (record == null) {
                record = new Record();
            }
            record.set("org_id", getCurUodp().getOrg_id());
            List<Record> list = service.list(record);
            renderOk(list);
        } catch (BusinessException e) {
            log.error("获取用户列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 个人设置用户信息
     */
    public void userinfo() {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();
        try {
            UodpInfo uodpInfo = getCurUodp();
            record = service.userinfo(record, userInfo, uodpInfo);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
        renderOk(record);
    }

    /**
     * 机构切换
     */
    public void switchuodp() {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();
        record = service.switchUodp(record, userInfo);
        renderOk(record);
    }

    /**
     * 用户信息修改
     */
    public void chg() {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.chg(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 修改密码
     */
    public void chgpwd() {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();
        try {
            service.chgpwd(record, userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

}
