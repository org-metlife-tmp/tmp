package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

public class CommRoutes extends Routes {
    @Override
    public void config() {
        add("/comm/area", AreaQueryController.class);
        add("/comm/bank", BankQueryController.class);
        add("/comm/user", UserController.class);
        add("/comm/attachment", AttachmentController.class);
        add("/comm/category", CategoryController.class);
        add("/comm/currency", CurrencyController.class);
        add("/comm/org", OrgController.class);
        add("/comm/biztype", BizTypeSettingController.class);
        add("/comm/account", AccountQueryController.class);
    }
}
