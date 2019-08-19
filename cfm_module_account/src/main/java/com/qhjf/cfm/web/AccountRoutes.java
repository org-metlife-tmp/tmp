package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

/**
 * Created by zhangsq on 2018/6/26.
 */
public class AccountRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/openintent", OpenIntentionController.class);
        add("/normal/opencom", OpenCompleteController.class);
        add("/normal/openchg", AccChangeController.class);
        add("/normal/account", AccountController.class);
        add("/normal/closeacc", CloseAccIntentionController.class);
        add("/normal/closeacccomple", CloseAccCompleteController.class);
        add("/normal/accfreeze", AccFreezeController.class);
        add("/normal/accdefreeze", AccDefreezeController.class);
        add("/normal/accconfirm", AccConfirmController.class);
    }
}
