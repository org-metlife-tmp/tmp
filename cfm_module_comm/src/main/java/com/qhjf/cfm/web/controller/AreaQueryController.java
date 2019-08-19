package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AreaQueryService;

import java.util.List;

@SuppressWarnings("unused")
@Auth(hasRoles = {"admin", "normal"})
public class AreaQueryController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AreaQueryController.class);

    private final AreaQueryService service = new AreaQueryService();


    /**
     * 获取省级机构（toplevel）列表
     */
    public void toplevel(){
        logger.debug("Enter into toplevel()");
        List<Record> li = service.getAllTopLevel(getParamsToRecord());
        renderOk(li);
    }


    /**
     * 返回地区列表
     */
    public void list(){
        logger.debug("Enter into list()");
        List<Record> li = service.getAreaList(getParamsToRecord());
        renderOk(li);
    }









}
