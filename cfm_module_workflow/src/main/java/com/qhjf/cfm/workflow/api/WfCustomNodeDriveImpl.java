package com.qhjf.cfm.workflow.api;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

public class WfCustomNodeDriveImpl implements  IWfNodeDrive {

    private static final Log logger = LogbackLog.getLog(WfCustomNodeDriveImpl.class);

    @Override
    public Record getStartNode(WfRequestObj requestObj) throws WorkflowException {
        return Db.findFirst(Db.getSql("wfprocess.findCustomerFirstNodes"), requestObj.getDefineId());
    }

    @Override
    public Record getCurNode(Record wf_cur_inst) {
        return Db.findById("cfm_workflow_def_custom_node","id", wf_cur_inst.get("workflow_node_id"));
    }

    @Override
    public Record getNextNode(WfRequestObj wfobj, Record curNode) throws WorkflowException {
        return Db.findById("cfm_workflow_def_custom_node","def_id,step_number",
                curNode.get("def_id"), curNode.getInt("step_number")+1);
    }

    @Override
    public Record getPreNode(WfRequestObj wfobj, Record curNode) throws WorkflowException {
        int reject_strategy = curNode.getInt("reject_strategy");
        Record preNode = null;
        if (reject_strategy == WorkflowConstant.RejectStrategy.BACKVEFORE.getKey()) { //回退到上一级
            //在历史流程示例中查找上一节点
            Record wf_pre_his_inst_node = Db.findFirst(Db.getSql("wfprocess.findPreHisInstNode"),WebConstant.YesOrNo.NO.getKey(),
                    WorkflowConstant.AssigneeType.AGREED, curNode.get("id"),curNode.get("base_id"),
                    curNode.get("define_id"), curNode.get("biz_type"), curNode.get("bill_id"));
            if(wf_pre_his_inst_node != null){
                preNode = Db.findById("cfm_workflow_def_custom_node","id",wf_pre_his_inst_node.get("workflow_node_id"));
            }else{
                logger.info("没有历史记录");
            }

        }else if(reject_strategy == WorkflowConstant.RejectStrategy.BACKCOMMIT.getKey()){


        }else if(reject_strategy == WorkflowConstant.RejectStrategy.CUSTOM.getKey()){
            throw  new WorkflowException("自定义回退未实现");
        }else{
            throw new WorkflowException("未定义的回退策略["+reject_strategy+"]");
        }
        return preNode;
    }

    @Override
    public boolean isBeforeEnd(Record curNode, Record nextNode) throws WorkflowException {
        if(nextNode == null){
            return true;
        }
        return false;
    }

    @Override
    public boolean isAfterStart(Record curNode, Record preNode) throws WorkflowException {
        if(preNode  == null){
            return true;
        }
        return false;
    }
}
