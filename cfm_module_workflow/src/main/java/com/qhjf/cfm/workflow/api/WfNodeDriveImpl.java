package com.qhjf.cfm.workflow.api;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.util.List;

/**
 * 默认流程节点驱动
 */
public class WfNodeDriveImpl implements IWfNodeDrive {

    private static final Log logger = LogbackLog.getLog(WfNodeDriveImpl.class);

    @Override
    public Record getStartNode(WfRequestObj requestObj) throws WorkflowException {
        List<Record> firstNodes = Db.find(Db.getSql("wfprocess.findFirstNodes"), requestObj.getDefineId());
        return WfNodeDriveTool.filterRecord(requestObj, firstNodes);
    }

    @Override
    public Record getCurNode(Record wf_cur_inst) {
        return Db.findById("cfm_workflow_def_node", "id", wf_cur_inst.get("workflow_node_id"));
    }

    @Override
    public Record getNextNode(WfRequestObj wfobj, Record curNode) throws WorkflowException {
        // 1.根据当前运行实例节点,找到可能的下级节点
        List<Record> possible_nodes = Db.find(Db.getSql("wfprocess.findPossibleNextNodes"), curNode.get("workflow_node_id"), curNode.get("define_id"));
        // 2.根据每个nodes对应的drive_condition,找到符合条件的node
        return WfNodeDriveTool.filterRecord(wfobj, possible_nodes);
    }

    @Override
    public Record getPreNode(WfRequestObj wfobj, Record curNode) throws WorkflowException {
        int reject_strategy = curNode.getInt("reject_strategy");
        Record preNode = null;
        if (reject_strategy == WorkflowConstant.RejectStrategy.BACKVEFORE.getKey()) { //回退到上一级
            //在历史流程示例中查找上一节点
            int stepNumber = curNode.get("step_number");
            if (stepNumber == 1) {
                return null;
            }

            List<Record> list = Db.find(Db.getSql("wfprocess.findPreHisInstNode"), WebConstant.YesOrNo.NO.getKey(),
                    WorkflowConstant.AssigneeType.AGREED.getKey(), curNode.get("id"), curNode.get("base_id"),
                    curNode.get("define_id"), curNode.get("biz_type"), curNode.get("bill_id"), (stepNumber - 1));
            Record wf_pre_his_inst_node = null;
            if (list != null && list.size() > 0) {
                wf_pre_his_inst_node = list.get(0);
            }
            if (wf_pre_his_inst_node != null) {
                preNode = Db.findById("cfm_workflow_def_node", "id", wf_pre_his_inst_node.get("workflow_node_id"));
            } else {
                logger.info("没有历史记录");
            }

        } else if (reject_strategy == WorkflowConstant.RejectStrategy.BACKCOMMIT.getKey()) { //回退到提交人
            logger.info("回退到提交人");

        } else if (reject_strategy == WorkflowConstant.RejectStrategy.CUSTOM.getKey()) { //自定义
            throw new WorkflowException("自定义回退未实现");
        } else {
            throw new WorkflowException("未定义的回退策略[" + reject_strategy + "]");
        }
        return preNode;
    }

    @Override
    public boolean isBeforeEnd(Record curNode, Record nextNode) throws WorkflowException {
        /**
         * 下一节点（nextNode）为空时，说明下一级没有可审批节点，只需要找到所对应的结束节点
         * */
        if (nextNode == null) {
            List<Record> possible_lines = Db.find(Db.getSql("wfprocess.findPossibleLinesWithStart"), curNode.get("def_id"), curNode.get("id"));
            if (possible_lines != null && possible_lines.size() > 0) {
                for (Record line : possible_lines) {
                    /**
                     * 当有多条连接结束节点的线时，用于过滤其他无用线
                     * 注：连接结束节点的线不允许存在驱动条件
                     * */
                    String drive_condition = TypeUtils.castToString(line.get("drive_condition"));
                    if (drive_condition != null) {
                        continue;
                    }
                    if (line.getInt("end_node_id") != WorkflowConstant.Endpoint.ENDPOINT.getKey()) {
                        logger.error("有后续节点不是结束节点");
                        throw new WorkflowException("工作流设置有误，请联系管理员！");
                    }
                }
                return true;
            } else {
                logger.error("找不到后续的线！");
                throw new WorkflowException("工作流设置有误，请联系管理员！");
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isAfterStart(Record curNode, Record preNode) throws WorkflowException {
        if (preNode == null) {
            List<Record> possible_lines = Db.find(Db.getSql("wfprocess.findPossibleLinesWithEnd"), curNode.get("def_id"), curNode.get("id"));
            if (possible_lines != null && possible_lines.size() > 0) {
                for (Record line : possible_lines) {
                    if (line.getInt("start_node_id") == WorkflowConstant.Endpoint.STARTPOINT.getKey()) {
                        return true;
                    }
                }
                logger.error("没有前驱节点是开始节点");
                throw new WorkflowException("工作流设置有误，请联系管理员！");

            } else {
                logger.error("找不到前驱的线！");
                throw new WorkflowException("工作流设置有误，请联系管理员！");
            }
        } else {
            return false;
        }
    }
}
