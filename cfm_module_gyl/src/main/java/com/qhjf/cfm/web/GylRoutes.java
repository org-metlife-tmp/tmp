package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.GylCheckController;
import com.qhjf.cfm.web.controller.GylManageController;
import com.qhjf.cfm.web.controller.GylSettingController;
import com.qhjf.cfm.web.controller.GylViewController;

public class GylRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/gylmanage", GylManageController.class);
        add("/normal/gylsetting", GylSettingController.class);
        add("/normal/gylview", GylViewController.class);
        add("/normal/gylcheck", GylCheckController.class);
    }
}
