package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.WorkCalController;
import com.qhjf.cfm.web.controller.WorkOfferController;
import com.qhjf.cfm.web.controller.WorkWeekController;

public class WorkingCalRoutes extends Routes {


    @Override
    public void config() {
    	add("/admin/workcal", WorkCalController.class);
    	add("/normal/workcal", WorkCalController.class);
    	add("/normal/workweek", WorkWeekController.class);
    	add("/normal/workoffer", WorkOfferController.class);
    }
}
