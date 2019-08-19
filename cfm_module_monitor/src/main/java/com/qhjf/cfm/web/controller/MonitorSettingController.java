package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.MonitorSettingService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 资金监控 - 监控规则设置
 */
public class MonitorSettingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(MonitorSettingController.class);
    private MonitorSettingService service = new MonitorSettingService();
}
