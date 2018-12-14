package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.OrgService;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/7/4
 * @Description: 机构信息
 */
public class OrgController extends CFMBaseController {

    private static Log log = LogbackLog.getLog(OrgController.class);

    private OrgService service = new OrgService();

    /**
     * 机构信息列表
     */
    public void list() {
        List<Record> orgList = service.lsit();
        renderOk(orgList);
    }

    /**
     * 查询当前登录人的本级 及 下属机构
     */
    @Auth(hasRoles = {"admin", "normal"})
    public void curlist() {
        try {
            List<Record> orgList = service.curlist(getCurUodp().getOrg_id());
            renderOk(orgList);
        } catch (ReqDataException e) {
            log.error("获取当前登录人机构列表信息失败！", e);
            renderFail(e);
        }

    }
}
