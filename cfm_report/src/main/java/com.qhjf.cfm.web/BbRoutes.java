package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.GjtController;
import com.qhjf.cfm.web.controller.OaController;

/**
 * 报表
 * @author wy
 * @date 2019年7月3日
 */
public class BbRoutes extends Routes {

    @Override
    public void config() {
        add("/normal/oaReport", OaController.class);
        add("/normal/gjtReport", GjtController.class);
        add("/normal/dzdReport", GjtController.class);
    }
}
