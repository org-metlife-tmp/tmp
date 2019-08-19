package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

public class OaRoutes extends Routes {
    @Override
    public void config() {
        add("/normal/headorgoa", HeadOrgOaController.class);
        add("/normal/branchorgoa", BranchOrgOaController.class);
        add("/normal/origindataoa", OriginDataOaController.class);
        add("/normal/branchorgoacheck", BranchOrgOaCheckController.class);
        add("/normal/headorgoacheck", HeadOrgOaCheckController.class);
        add("/normal/checkdoubtfuloa", CheckDoubtfulOaController.class);
    }
}
