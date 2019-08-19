package com.qhjf.cfm.web;


import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;


/**
 * 资金下拨
 *
 * @author
 * @date 2018年9月12日
 */
public class PoolAccRoutes extends Routes {
    @Override
    public void config() {
    	//资金池账户设置
        add("/normal/poolacc", PoolAccController.class);
        //资金池交易核对
        add("/normal/pooltrad", PoolTradController.class);
        add("/normal/allocset", AllocationSetController.class);
        add("/normal/allocview", AllocationViewController.class);
        add("/normal/allocmgt", AllocationMgtController.class);
        add("/normal/allocreport", AllocationReportController.class);
    }
}
