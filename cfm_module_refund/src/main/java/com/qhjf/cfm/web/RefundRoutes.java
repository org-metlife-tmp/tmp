package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.DoubtfulRefundController;
import com.qhjf.cfm.web.controller.RefundTicketController;
import com.qhjf.cfm.web.controller.RefundViewController;

public class RefundRoutes extends Routes {

    @Override
    public void config() {
        add("/normal/refund", RefundTicketController.class);
        add("/normal/doubtfulrefund", DoubtfulRefundController.class);
        add("/normal/refundview", RefundViewController.class);
    }

}
