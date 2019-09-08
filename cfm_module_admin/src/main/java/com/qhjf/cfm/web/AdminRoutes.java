package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

public class AdminRoutes extends Routes {
    @Override
    public void config() {
        add("/admin/currency", BaseDataCurrMgtController.class);
        add("/admin/org", BaseDataOrgMgtController.class);
        add("/admin/dept", BaseDataDeptMgtController.class);
        add("/admin/usr", BaseDataUserMgtController.class);
        add("/admin/merchacc", MerchantAccInfoMgtController.class);
        add("/admin/settacc", SettaccController.class);
        add("/admin/handlechannel", HandlechannelController.class);
        add("/admin/usrgroup", UsrgroupController.class);
        add("/admin/handleroute", HandleRouteSettingController.class);
        add("/admin/usrmenu", UsrmenuController.class);
        add("/admin/position", BaseDataPositionController.class);
        add("/admin/biztype", BizTypeController.class);
        add("/admin/payBank", PayBankController.class);
    }
}
