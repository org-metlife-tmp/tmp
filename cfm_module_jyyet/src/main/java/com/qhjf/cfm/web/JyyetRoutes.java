package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.JytController;
import com.qhjf.cfm.web.controller.YetController;

public class JyyetRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/yet", YetController.class);
        add("/normal/jyt", JytController.class);
    }
}
