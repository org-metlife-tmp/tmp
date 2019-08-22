package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.DbtBatchController;
import com.qhjf.cfm.web.controller.DbtBatchTradController;
import com.qhjf.cfm.web.controller.DbtController;
import com.qhjf.cfm.web.controller.DbtTradController;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9
 * @Description:
 */
public class DbtRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/dbt", DbtController.class);
        add("/normal/dbttrad", DbtTradController.class);
        add("/normal/dbtbatch", DbtBatchController.class);
        add("/normal/dbtbatchtrad", DbtBatchTradController.class);
    }
}
