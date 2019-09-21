package com.qhjf.cfm.web;

import com.jfinal.config.Routes;
import com.qhjf.cfm.web.controller.*;

public class WorkflowRoutes extends Routes {

    @Override
    public void config() {
        add("/admin/wfdefine", WorkflowDefineController.class);
        add("/admin/wfchart", WorkflowNodeLineController.class);
        add("/admin/wfrelation", WorkflowRelationController.class);
        add("/admin/wftrace", WorkflowTraceController.class);
        add("/normal/wfprocess", WorkflowProcessController.class);
        add("/comm/wfquery", WorkflowQueryController.class);
        add("/normal/wftrans", WorkflowTransController.class);

    }
}
