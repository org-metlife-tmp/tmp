package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

public class GjtRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/collectmanage", CollectManageController.class);
        add("/normal/collectreport", CollectReportController.class);
        add("/normal/collectsetting", CollectSettingController.class);
        add("/normal/collectview", CollectViewController.class);
        add("/normal/collectcheck", CollectCheckController.class);
        add("/normal/ndc", NonDirectSettingController.class);
        add("/normal/collectbatchcheck", CollectBatchCheckController.class);
    }
}
