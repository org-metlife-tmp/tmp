package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.MonitorQueryService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 资金监控 - 查询
 */
public class MonitorQueryController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(MonitorQueryController.class);
    private MonitorQueryService service = new MonitorQueryService();
}
