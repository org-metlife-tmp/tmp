package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.UploadController;

public class ExcelRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/excel", UploadController.class);
    }
}