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
        add("/normal/sftrecvcheck", RecvCheckController.class);
        add("/normal/sftdoubtful", DoubtfulController.class);
        add("/normal/sftrecvdoubtful", RecvDoubtfulController.class);
        add("/normal/checkbatch", CheckBatchForController.class);
        add("/normal/diskbacking", DiskBackingController.class);
        add("/normal/sftexcept", ExceptController.class);
        add("/normal/sftrecvexcept", RecvExceptController.class);
        add("/normal/recvcheckbatch", RecvCheckBatchForController.class);
        add("/normal/recvdisksending", RecvDiskSendingController.class);
        add("/normal/recvdiskbacking", RecvDiskBackingController.class);
        add("/normal/paycounter", PayCounterController.class);
        add("/normal/sftpaycountercheck", PayCounterCheckController.class);
        add("/normal/sftvoucherlist", VoucherController.class);
    }
}
