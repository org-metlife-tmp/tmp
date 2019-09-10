package com.qhjf.cfm.workflow.api;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.web.WfRequestObj;

import java.util.List;

public class WfNodeDriveTool {

    private static final WfLineExpressParseTool lineExpressParseTool = WfLineExpressParseTool.getInstance();

    public static Record filterRecord(WfRequestObj wfobj, List<Record> possible_nodes) throws WorkflowException {
        Record destNode = null;
        if (possible_nodes != null && possible_nodes.size() > 0) {
            for (Record node : possible_nodes) {
                String drive_condition = node.getStr("drive_condition");
                if (CommKit.isNullOrEmpty(drive_condition)) {
                    destNode = node;
                    break;
                } else if (lineExpressParseTool.expressAllParse(wfobj, drive_condition)) {
                    destNode = node;
                    break;
                }
            }
        }
        return destNode;
    }
}
