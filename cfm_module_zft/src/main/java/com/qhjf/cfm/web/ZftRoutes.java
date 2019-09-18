package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9
 * @Description:
 */
public class ZftRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/zft", ZftController.class);
        add("/normal/supplier", SupplierAccController.class);
        add("/normal/zftbatch", ZftBatchController.class);
        add("/normal/zftbatchcheck", ZftBatchCheckController.class);
        add("/normal/zftrefund", ZftRefundController.class);
    }
}
