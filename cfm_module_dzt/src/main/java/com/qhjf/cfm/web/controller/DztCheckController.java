package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DztCheckService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 已勾兑查询
 */
public class DztCheckController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DztCheckController.class);
    private DztCheckService service = new DztCheckService();
}
