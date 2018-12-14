package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DztAdjustService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 余额调节表
 */
public class DztAdjustController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DztAdjustController.class);
    private DztAdjustService service = new DztAdjustService();
}
