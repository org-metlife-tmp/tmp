package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DztInitSettingService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 对账初始化设置
 */
public class DztInitSettingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DztInitSettingController.class);
    private DztInitSettingService service = new DztInitSettingService();
}
