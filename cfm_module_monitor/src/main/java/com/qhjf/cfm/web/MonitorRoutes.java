package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.MonitorQueryController;
import com.qhjf.cfm.web.controller.MonitorSettingController;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 资金监控
 */
public class MonitorRoutes extends Routes {
    @Override
    public void config() {
        //监控规则设置
        add("/normal/monitorsetting", MonitorSettingController.class);
        //监控查询
        add("/normal/monitorquery", MonitorQueryController.class);
    }
}
