package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.VoucherQueryController;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/5/31
 * @Description: 凭证查询
 */
public class VoucherQueryRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/voucher", VoucherQueryController.class);
    }
}
