package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

/**
 * 收付费管理
 * @author GJF
 * @date 2018年9月18日
 */
public class SftRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/sftchannel", ChannelSettingController.class);
        add("/normal/sftbankkey", BankkeySettingController.class);
        add("/normal/disksending", DiskSendingController.class);
        add("/normal/sftpaycheck", PayCheckController.class);
        add("/normal/sftdoubtful", DoubtfulController.class);
        add("/normal/checkbatch", CheckBatchForController.class);
        add("/normal/diskbacking", DiskBackingController.class);
        add("/normal/sftexcept", ExceptController.class);

    }
}
