package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

public class NcRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/headorgnc", HeadOrgNcController.class);
        add("/normal/headorgnccheck", HeadOrgNcCheckController.class);
        add("/normal/checkdoubtfulnc", CheckDoubtfulNcController.class);
    }
}
