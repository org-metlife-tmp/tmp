package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.EleController;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/21
 * @Description:
 */
public class EleRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/ele", EleController.class);
    }
}
