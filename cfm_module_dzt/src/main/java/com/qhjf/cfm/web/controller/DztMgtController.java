package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DztMgtService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 对账管理
 */
public class DztMgtController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DztMgtController.class);
    private DztMgtService service = new DztMgtService();
}
