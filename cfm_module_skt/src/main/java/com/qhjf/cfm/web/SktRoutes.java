package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.SktController;
import com.qhjf.cfm.web.controller.SktTradController;

/**
 * 收款通
 * @author GJF
 * @date 2018年9月18日
 */
public class SktRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/skt", SktController.class);
        add("/normal/skttrad", SktTradController.class);
    }
}
