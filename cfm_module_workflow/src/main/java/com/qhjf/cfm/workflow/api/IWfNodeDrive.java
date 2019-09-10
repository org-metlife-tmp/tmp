package com.qhjf.cfm.workflow.api;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.WfRequestObj;

/**
 * 节点驱动
 */
public interface IWfNodeDrive {

    /**
     * 获取开始节点
     * @param requestObj
     *          请求对象
     * @return
     */
    Record getStartNode(WfRequestObj requestObj) throws WorkflowException;


    /**
     *
     * @param wf_cur_inst
     *        当前流程实例
     * @return
     */
    Record getCurNode(Record wf_cur_inst);


    /**
     *  获取后驱节点
     * @param wfobj
     *          请求对象
     * @param curNode
     *          当前节点
     * @return
     * @throws WorkflowException
     */
    Record getNextNode(WfRequestObj wfobj, Record curNode) throws WorkflowException;


    /**
     * 获取前驱节点
     * @param wfobj
     *         请求对象
     * @param curNode
     *         当前节点
     * @return
     * @throws WorkflowException
     */
    Record getPreNode(WfRequestObj wfobj, Record curNode) throws  WorkflowException;


    /**
     * 当前节点是否是结束前的一个节点
     * @param curNode
     *          当前节点
     * @param nextNode
     *          下一个节点
     * @return
     * @throws WorkflowException
     */
    boolean isBeforeEnd(Record curNode , Record nextNode) throws WorkflowException;


    /**
     * 当前节点是否是开始后前的一个节点
     * @param curNode
     * @param preNode
     * @return
     */
    boolean isAfterStart(Record curNode, Record preNode) throws WorkflowException;
}
