package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.workflow.api.IWfNodeDrive;
import com.qhjf.cfm.workflow.api.WfApprovePermissionTool;
import com.qhjf.cfm.workflow.api.WfIAtom;

import java.sql.SQLException;
import java.util.Date;

public class WorkflowProcessService {

    private static final Log logger = LogbackLog.getLog(WorkflowProcessService.class);

    private static final WfApprovePermissionTool approvePermission = WfApprovePermissionTool.getINSTANCE();


    /**
     * 开始新的工作流程
     *
     * @param requestObj 请求对象
     * @param userInfo   用户信息
     */
    public boolean startWorkflow(final WfRequestObj requestObj, UserInfo userInfo) throws WorkflowException {
        if(!canStartOrRevoke(requestObj,userInfo)){
            throw new WorkflowException("无权提交！");
        }
        //1、 根据define_id 查找 审批流定义
        final Record wfDefine = Db.findFirst(Db.getSql("wfprocess.findWFDefineByDefineId"), requestObj.getDefineId());
        if (wfDefine != null) {
            //2、根据定义的流程的类型，查找开始节点
            IWfNodeDrive nodeDrive = WorkflowQueryService.getNodeDrive(wfDefine.getInt("workflow_type"));
            Record startNode = nodeDrive.getStartNode(requestObj);
            if (startNode != null) {
                //设置工作流信息
                final Record workflow_inst = new Record().set("base_id", wfDefine.get("base_id"))
                        .set("workflow_name", wfDefine.get("workflow_name"))
                        .set("define_id", wfDefine.get("define_id"))
                        .set("workflow_type", wfDefine.get("workflow_type"))
                        .set("reject_strategy", wfDefine.get("reject_strategy"))
                        .set("def_version", wfDefine.get("def_version"))
                        .set("workflow_node_id", startNode.get("id"))
                        .set("step_number", 1)
                        .set("shadow_execute", WebConstant.YesOrNo.NO.getKey());
                //设置业务信息
                UodpInfo curUodpInfo = userInfo.getCurUodp();
                workflow_inst.set("biz_type", requestObj.getBizType().getKey())
                        .set("bill_id", requestObj.getBillId())
                        .set("bill_code", requestObj.getBillCode())
                        .set("submitter", userInfo.getUsr_id())
                        .set("submitter_name", userInfo.getName())
                        .set("submitter_pos_id", curUodpInfo.getPos_id())
                        .set("submitter_pos_name", curUodpInfo.getPos_name())
                        .set("init_user_id", userInfo.getUsr_id())
                        .set("init_user_name", userInfo.getName())
                        .set("init_org_id", curUodpInfo.getOrg_id())
                        .set("init_org_name", curUodpInfo.getOrg_name())
                        .set("init_dept_id", curUodpInfo.getDept_id())
                        .set("init_dept_name", curUodpInfo.getDept_name())
                        .set("start_time", new Date());

                /**
                 * 如果是oa分公司提交,重新初始化init_org_id
                 */
                if (WebConstant.MajorBizType.OA_BRANCH_PAY.equals(requestObj.getBizType())) {
                    Long bill_id = requestObj.getBillId();
                    Record record = Db.findById("oa_branch_payment", "id", bill_id);
                    if (record == null) {
                        throw new WorkflowException("oa_branch_payment" + ":" + bill_id + " not exists");
                    }
                    workflow_inst.set("init_org_id", record.getLong("org_id"));

                }
                WfIAtom iAtom = startWf(requestObj, workflow_inst, startNode);
                if (Db.tx(iAtom)) {
                    return true;
                } else {
                    throw new WorkflowException(iAtom.getErrMsg());
                }
            } else {
                throw new WorkflowException("未找到流程开始节点");
            }
        } else {
            throw new WorkflowException("无法找到对应的流程define_id:" + requestObj.getDefineId());
        }
    }


    /**
     * 撤回
     *
     * @param wfobj    请求对象
     * @param userInfo 用户信息
     * @return
     * @throws WorkflowException
     */
    public boolean revoke(final WfRequestObj wfobj, UserInfo userInfo) throws WorkflowException {
        if(!canStartOrRevoke(wfobj,userInfo)){
            throw new WorkflowException("无权撤回！");
        }
        // 1.根据单据ID和业务类型获取当前流程运行实例
        final Record wf_cur_inst = Db.findById("cfm_workflow_run_execute_inst", "bill_id,biz_type", wfobj.getBillId(), wfobj.getBizType().getKey());
        // 2.判断当前运行实例是否为空,为空则视为数据过期,返回dataOut
        if (wf_cur_inst == null) {
            throw new WorkflowException("数据过期");
        }

        //3、判断对应单据的状态是否为"SUBMITED"
        Record record = Db.findById(wfobj.getTableName(), "id", wfobj.getBillId());
        if (record != null) {
            int bill_status = record.getInt("service_status");
            if (bill_status != WebConstant.BillStatus.SUBMITED.getKey()) {
                throw new WorkflowException("工作流对应单据对应的状态不正确，无法撤回！");
            }
        } else {
            throw new WorkflowException("工作流对应单据不存在！");
        }

        IWfNodeDrive nodeDrive = WorkflowQueryService.getNodeDrive(wf_cur_inst.getInt("workflow_type"));
        //3.获取当前节点
        Record curNode = nodeDrive.getCurNode(wf_cur_inst);

        //4.删除当前运行实例
        //5.删减当前运行实例的相关权限
        if (curNode != null) {
            WfIAtom iAtom = revokeWf(wfobj, wf_cur_inst, curNode);
            if (Db.tx(iAtom)) {
                return true;
            } else {
                throw new WorkflowException(iAtom.getErrMsg());
            }

        } else {
            throw new WorkflowException("找到的流程有误！");
        }
    }

    /**
     * 审批同意
     *
     * @param wfobj    请求对象
     * @param userInfo 用户信息
     * @throws WorkflowException
     */
    public boolean approvAgree(final WfRequestObj wfobj, UserInfo userInfo) throws WorkflowException {
        if(!canAgreeOrRejectOrAppend(wfobj,userInfo)){
            throw new WorkflowException("无权同意！");
        }
        //1、获取当前工作流实例
        final Record wf_cur_inst = Db.findById("cfm_workflow_run_execute_inst", "id", wfobj.getInstId());
        // 2.判断当前运行实例是否为空,为空则视为数据过期,返回dataOut
        if (wf_cur_inst == null) {
            throw new WorkflowException("数据过期");
        }
        IWfNodeDrive nodeDrive = WorkflowQueryService.getNodeDrive(wf_cur_inst.getInt("workflow_type"));
        //3.获取当前节点
        // 4.根据当前节点,找到下一节点
        Record curNode = nodeDrive.getCurNode(wf_cur_inst);
        Record nextNode = nodeDrive.getNextNode(wfobj, wf_cur_inst);
        boolean isBeforeTheEnd = nodeDrive.isBeforeEnd(curNode, nextNode);

        // 5.将当前运行实例添加的历史表
        // 6.删除当前运行实例,删减当前运行实例的相关权限
        // 7.生成下一节点运行实例
        // 8.增加下一节点实例的相关权限
        if (curNode != null && (nextNode != null || isBeforeTheEnd)) {
            final Record wf_his_inst = genHisInst(wf_cur_inst, userInfo, wfobj, WorkflowConstant.AssigneeType.AGREED);
            /**
             * 有下级节点，说明流程未结束，除了将当前运行实例添加的历史流程，删除当前运行实例
             * 同时生成新的运行实例，
             */
            if (nextNode != null) {
                final Record wf_new_inst = genNewCurInst(wf_cur_inst, nextNode);
                WfIAtom iAtom = haveNextNodeDrive(wfobj, wf_cur_inst, wf_his_inst, wf_new_inst, curNode, nextNode);
                if (Db.tx(iAtom)) {
                    return true;
                } else {
                    throw new WorkflowException(iAtom.getErrMsg());
                }
            } else if (isBeforeTheEnd) {
                WfIAtom iAtom = beforeEndNodeDrive(wfobj, wf_cur_inst, wf_his_inst, curNode);
                if (Db.tx(iAtom)) {
                    return true;
                } else {
                    throw new WorkflowException(iAtom.getErrMsg());
                }
            } else {
                throw new WorkflowException("工作流设定有问题，请联系管理员");
            }
        } else {
            throw new WorkflowException("找到的流程有误！");
        }

    }

    /**
     * 审批拒绝
     *
     * @param wfobj
     * @param userInfo
     */
    public boolean approvReject(WfRequestObj wfobj, UserInfo userInfo) throws WorkflowException {
        if(!canAgreeOrRejectOrAppend(wfobj,userInfo)){
            throw new WorkflowException("无权拒绝！");
        }
        //1、获取当前工作流实例
        final Record wf_cur_inst = Db.findById("cfm_workflow_run_execute_inst", "id", wfobj.getInstId());
        // 2.判断当前运行实例是否为空,为空则视为数据过期,返回dataOut
        if (wf_cur_inst == null) {
            throw new WorkflowException("数据过期");
        }
        IWfNodeDrive nodeDrive = WorkflowQueryService.getNodeDrive(wf_cur_inst.getInt("workflow_type"));


        //3.获取当前节点
        //4.根据当前节点,找到回退节点
        Record curNode = nodeDrive.getCurNode(wf_cur_inst);
        Record preNode = null;
        boolean isAfterStart = true;
        /**
         * 如果当前用户为管理员，则默认返回初始提交人，preNode 默认为空,isAfterStart 为true
         * */
        if (userInfo.getIs_admin() != 1) {

            int reject_strategy = wf_cur_inst.getInt("reject_strategy");
            if (reject_strategy != WorkflowConstant.RejectStrategy.BACKCOMMIT.getKey()) {
                preNode = nodeDrive.getPreNode(wfobj, wf_cur_inst);
                isAfterStart = nodeDrive.isAfterStart(curNode, preNode);
            }

        }

        //5.将当前运行实例添加的历史表
        // 6.删除当前运行实例,删减当前运行实例的相关权限
        // 7.生成下一节点运行实例
        // 8.增加下一节点实例的相关权限
        if (curNode != null && (preNode != null || isAfterStart)) {
            final Record wf_his_inst = genHisInst(wf_cur_inst, userInfo, wfobj, WorkflowConstant.AssigneeType.REFUSED);
            /**
             * 有前驱节点，说明流程没有退回到提交之前，除了将当前运行实例添加的历史流程，删除当前运行实例
             * 同时生成新的运行实例，
             */
            if (preNode != null) {
                final Record wf_new_inst = genNewCurInst(wf_cur_inst, preNode);
                WfIAtom iAtom = haveNextNodeDrive(wfobj, wf_cur_inst, wf_his_inst, wf_new_inst, curNode, preNode);
                if (!Db.tx(iAtom)) {
                    throw new WorkflowException(iAtom.getErrMsg());
                } else {
                    return true;
                }
            } else if (isAfterStart) {
                WfIAtom iAtom = afterStartNodeDrive(wfobj, wf_cur_inst, wf_his_inst, curNode);
                if (Db.tx(iAtom)) {
                    return true;
                } else {
                    throw new WorkflowException(iAtom.getErrMsg());
                }

            } else {
                throw new WorkflowException("工作流设定有问题，请联系管理员");
            }
        } else {
            throw new WorkflowException("找到的流程有误！");
        }
    }


    /**
     * 审批加签
     *
     * @param wfobj
     * @param userInfo
     * @return
     * @throws WorkflowException
     */
    public boolean approvAppend(WfRequestObj wfobj, UserInfo userInfo) throws WorkflowException {
        if(!canAgreeOrRejectOrAppend(wfobj,userInfo)){
            throw new WorkflowException("无权加签！");
        }
        //1、获取当前工作流实例
        final Record wf_cur_inst = Db.findById("cfm_workflow_run_execute_inst", "id", wfobj.getInstId());
        // 2.判断当前运行实例是否为空,为空则视为数据过期,返回dataOut
        if (wf_cur_inst == null) {
            throw new WorkflowException("数据过期");
        }
        IWfNodeDrive nodeDrive = WorkflowQueryService.getNodeDrive(wf_cur_inst.getInt("workflow_type"));

        //3.获取当前节点
        Record curNode = nodeDrive.getCurNode(wf_cur_inst);
        //4.将当前运行实例添加的历史表
        //5.删除当前运行实例
        //6.生成加签运行实例
        //7.增加下一节点实例的相关权限
        //8.删减当前运行实例的相关权限
        if (curNode != null) {
            final Record wf_his_inst = genHisInst(wf_cur_inst, userInfo, wfobj, WorkflowConstant.AssigneeType.APPEND);
            final Record wf_new_inst = genAppendCurInst(wf_cur_inst, userInfo, wfobj);
            WfIAtom iAtom = appendNodeDrive(wfobj, wf_cur_inst, wf_his_inst, wf_new_inst, curNode);
            if (Db.tx(iAtom)) {
                return true;
            } else {
                throw new WorkflowException(iAtom.getErrMsg());
            }

        } else {
            throw new WorkflowException("找到的流程有误！");
        }
    }


    /**
     * 用于 approvAppend ，approvAgree ， approvReject 返回失败后补偿已清楚的缓存
     *
     * @param wfobj
     * @param userInfo
     */
    public void compensateApprovePermission(WfRequestObj wfobj, UserInfo userInfo) {
        try {
            //1、获取当前工作流实例
            final Record wf_cur_inst = Db.findById("cfm_workflow_run_execute_inst", "id", wfobj.getInstId());
            // 2.判断当前运行实例是否为空,为空则视为数据过期,返回dataOut
            if (wf_cur_inst == null) {
                throw new WorkflowException("数据过期");
            }
            IWfNodeDrive nodeDrive = WorkflowQueryService.getNodeDrive(wf_cur_inst.getInt("workflow_type"));


            //3.获取当前节点
            //4.根据当前节点,找到回退节点
            Record curNode = nodeDrive.getCurNode(wf_cur_inst);
            approvePermission.addApprovePermissions(curNode, wf_cur_inst);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("compensateApprovePermission fail:" + e.getMessage());
        }

    }

    /**
     * 生成历史流程实例Record
     *
     * @param wf_cur_inst 当前流程实例Recode
     * @param userInfo    当前用户信息
     * @param wfobj       请求对象
     * @param type        受理类型: 同意， 拒绝， 加签
     * @return
     */
    private Record genHisInst(Record wf_cur_inst, UserInfo userInfo, WfRequestObj wfobj, WorkflowConstant.AssigneeType type) {
        return new Record().set("ru_execute_inst_id", wf_cur_inst.get("id"))
                .set("base_id", wf_cur_inst.get("base_id"))
                .set("workflow_name", wf_cur_inst.get("workflow_name"))
                .set("define_id", wf_cur_inst.get("define_id"))
                .set("workflow_type", wf_cur_inst.get("workflow_type"))
                .set("reject_strategy", wf_cur_inst.get("reject_strategy"))
                .set("def_version", wf_cur_inst.get("def_version"))
                .set("workflow_node_id", wf_cur_inst.get("workflow_node_id"))
                .set("step_number", wf_cur_inst.get("step_number"))
                .set("shadow_execute", wf_cur_inst.get("shadow_execute"))
                .set("shadow_user_id", wf_cur_inst.get("shadow_user_id"))
                .set("shadow_user_name", wf_cur_inst.get("shadow_user_name"))
                .set("shadow_step_number", wf_cur_inst.get("shadow_step_number"))
                .set("assignee_id", userInfo.getUsr_id())
                .set("assignee", userInfo.getName())
                .set("assignee_type", type.getKey())
                .set("assignee_memo", wfobj.getApproveMemo())
                .set("biz_type", wf_cur_inst.get("biz_type"))
                .set("bill_id", wf_cur_inst.get("bill_id"))
                .set("bill_code", wf_cur_inst.get("bill_code"))
                .set("submitter", wf_cur_inst.get("submitter"))
                .set("submitter_name", wf_cur_inst.get("submitter_name"))
                .set("submitter_pos_id", wf_cur_inst.get("submitter_pos_id"))
                .set("submitter_pos_name", wf_cur_inst.get("submitter_pos_name"))
                .set("init_user_id", wf_cur_inst.get("init_user_id"))
                .set("init_user_name", wf_cur_inst.get("init_user_name"))
                .set("init_org_id", wf_cur_inst.get("init_org_id"))
                .set("init_org_name", wf_cur_inst.get("init_org_name"))
                .set("init_user_id", wf_cur_inst.get("init_user_id"))
                .set("init_user_name", wf_cur_inst.get("init_user_name"))
                .set("init_dept_id", wf_cur_inst.get("init_dept_id"))
                .set("init_dept_name", wf_cur_inst.get("init_dept_name"))
                .set("start_time", wf_cur_inst.get("start_time"))
                .set("end_time", new Date());
    }


    /**
     * 根据前一个当前流程实例生成当前流程实例
     *
     * @param wf_cur_inst 前一个当前实例
     * @param nextNode    根据前一个当前计算出来的下一个节点
     * @return
     */
    private Record genNewCurInst(Record wf_cur_inst, Record nextNode) {
//        UodpInfo curUodpInfo = userInfo.getCurUodp();
        return new Record().set("base_id", wf_cur_inst.get("base_id"))
                .set("workflow_name", wf_cur_inst.get("workflow_name"))
                .set("define_id", wf_cur_inst.get("define_id"))
                .set("workflow_type", wf_cur_inst.get("workflow_type"))
                .set("reject_strategy", wf_cur_inst.get("reject_strategy"))
                .set("def_version", wf_cur_inst.get("def_version"))
                .set("workflow_node_id", nextNode.get("id"))
                .set("step_number", wf_cur_inst.getInt("step_number") + 1)
                .set("shadow_execute", WebConstant.YesOrNo.NO.getKey())
                //设置业务信息
                .set("biz_type", wf_cur_inst.get("biz_type"))
                .set("bill_id", wf_cur_inst.get("bill_id"))
                .set("bill_code", wf_cur_inst.get("bill_code"))
                .set("submitter", wf_cur_inst.get("submitter"))
                .set("submitter_name", wf_cur_inst.get("submitter_name"))
                .set("submitter_pos_id", wf_cur_inst.get("submitter_pos_id"))
                .set("submitter_pos_name", wf_cur_inst.get("submitter_pos_name"))
                .set("init_user_id", wf_cur_inst.get("init_user_id"))
                .set("init_user_name", wf_cur_inst.get("init_user_name"))
                .set("init_org_id", wf_cur_inst.get("init_org_id"))
                .set("init_org_name", wf_cur_inst.get("init_org_name"))
                .set("init_dept_id", wf_cur_inst.get("init_dept_id"))
                .set("init_dept_name", wf_cur_inst.get("init_dept_name"))
                .set("start_time", new Date());
    }


    private Record genAppendCurInst(Record wf_cur_inst, UserInfo userInfo, WfRequestObj wfobj) throws WorkflowException {
        UodpInfo curUodpInfo = userInfo.getCurUodp();
        return new Record().set("base_id", wf_cur_inst.get("base_id"))
                .set("workflow_name", wf_cur_inst.get("workflow_name"))
                .set("define_id", wf_cur_inst.get("define_id"))
                .set("workflow_type", wf_cur_inst.get("workflow_type"))
                .set("reject_strategy", wf_cur_inst.get("reject_strategy"))
                .set("def_version", wf_cur_inst.get("def_version"))
                .set("workflow_node_id", wf_cur_inst.get("workflow_node_id"))
                .set("step_number", wf_cur_inst.getInt("step_number") + 1)

                .set("shadow_execute", WebConstant.YesOrNo.YES.getKey())     //设置加签标识
                .set("shadow_user_id", wfobj.getShadowUserId())
                .set("shadow_user_name", wfobj.getShadowUserName())

                //设置业务信息
                .set("biz_type", wf_cur_inst.get("biz_type"))
                .set("bill_id", wf_cur_inst.get("bill_id"))
                .set("bill_code", wf_cur_inst.get("bill_code"))
                .set("submitter", userInfo.getUsr_id())
                .set("submitter_name", userInfo.getName())
                .set("submitter_pos_id", curUodpInfo.getPos_id())
                .set("submitter_pos_name", curUodpInfo.getPos_name())
                .set("init_user_id", wf_cur_inst.get("init_user_id"))
                .set("init_user_name", wf_cur_inst.get("init_user_name"))
                .set("init_org_id", wf_cur_inst.get("init_org_id"))
                .set("init_org_name", wf_cur_inst.get("init_org_name"))
                .set("init_dept_id", wf_cur_inst.get("init_dept_id"))
                .set("init_dept_name", wf_cur_inst.get("init_dept_name"))
                .set("start_time", new Date());


    }


    /**
     * 初次提交的事务处理
     *
     * @param requestObj     请求对象
     * @param workflow_inst  当前工作实例
     * @param finalStartNode 开始节点
     * @return
     */
    private WfIAtom startWf(final WfRequestObj requestObj, final Record workflow_inst, final Record finalStartNode) {
        logger.debug("Enter into startWf");
        return new WfIAtom() {
            @Override
            public boolean run() throws SQLException {
                if (Db.save("cfm_workflow_run_execute_inst", workflow_inst)) {
                    //添加审批实例
                    try {
                        approvePermission.addApprovePermissions(finalStartNode, workflow_inst);
                    } catch (WorkflowException e) {
                        this.errMsg = "添加当前的工作流用户对应关系失败：" + e.getMessage();
                        return false;
                    }
                    if (requestObj.chgStatus(WebConstant.BillStatus.SUBMITED)) {
                        return true;
                    } else {
                        this.errMsg = "更新单据状态失败";
                        return false;
                    }
                } else {
                    this.errMsg = "添加当前流程实例失败";
                    return false;
                }
            }
        };
    }

    /**
     * @param wfobj       请求对象
     * @param wf_cur_inst 前一个当前实例
     * @param curNode     前一个当前实例的当前节点
     * @return
     */
    private WfIAtom revokeWf(final WfRequestObj wfobj, final Record wf_cur_inst, final Record curNode) {
        logger.debug("Enter into revokeWf");
        return new WfIAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    processPreWfCurInst(wf_cur_inst, curNode);
                } catch (WorkflowException e) {
                    this.errMsg = e.getMessage();
                    return false;
                }
                if (wfobj.chgStatus(WebConstant.BillStatus.SAVED)) {
                    return true;
                } else {
                    this.errMsg = "更新单据状态失败";
                    return false;
                }
            }
        };
    }


    /**
     * 有下个节点的事务处理
     *
     * @param wfobj         请求对象
     * @param wf_cur_inst   前一个当前实例
     * @param wf_his_inst   存储的历史实例
     * @param wf_new_inst   存储的当前实例
     * @param finalCurNode  前一个当前实例的当前节点
     * @param finalNextNode 根据前一个当前实例计算出来的下一个节点，即存储的当前实例的当前节点
     * @return
     */
    private WfIAtom haveNextNodeDrive(final WfRequestObj wfobj, final Record wf_cur_inst, final Record wf_his_inst, final Record wf_new_inst, final Record finalCurNode, final Record finalNextNode) {
        logger.debug("Enter into haveNextNodeDrive");
        return new WfIAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    processPreWfCurInst(wf_cur_inst, finalCurNode);
                } catch (WorkflowException e) {
                    this.errMsg = e.getMessage();
                    return false;
                }

                if (!Db.save("cfm_workflow_his_execute_inst", wf_his_inst)) {
                    this.errMsg = "将当前实例转换为历史实例失败";
                    return false;
                }

                try {
                    processNewWfCurInst(wf_new_inst, finalNextNode);
                } catch (WorkflowException e) {
                    this.errMsg = e.getMessage();
                    return false;
                }

                if (wfobj.chgStatus(WebConstant.BillStatus.AUDITING)) {
                    return true;
                } else {
                    this.errMsg = "更新单据状态失败";
                    return false;
                }
            }
        };
    }


    /**
     * @param wfobj        请求对象
     * @param wf_cur_inst  前一个当前实例
     * @param wf_his_inst  存储的历史实例
     * @param finalCurNode 前一个当前实例的当前节点
     * @return
     */
    private WfIAtom beforeEndNodeDrive(final WfRequestObj wfobj, final Record wf_cur_inst, final Record wf_his_inst, final Record finalCurNode) {
        logger.debug("Enter into beforeEndNodeDrive");
        return new WfIAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    processPreWfCurInst(wf_cur_inst, finalCurNode);
                } catch (WorkflowException e) {
                    this.errMsg = e.getMessage();
                    return false;
                }

                if (!Db.save("cfm_workflow_his_execute_inst", wf_his_inst)) {
                    this.errMsg = "将当前实例转换为历史实例失败";
                    return false;
                }

                if (wfobj.chgStatus(WebConstant.BillStatus.PASS)) {
                    return true;
                } else {
                    this.errMsg = "更新单据状态失败";
                    return false;
                }
            }
        };
    }


    /**
     * 下一步为返回初始提交人的事务处理
     *
     * @param wfobj        请求对象
     * @param wf_cur_inst  前一个当前实例
     * @param wf_his_inst  存储的历史实例
     * @param finalCurNode 前一个当前实例的当前节点
     * @return
     */
    private WfIAtom afterStartNodeDrive(final WfRequestObj wfobj, final Record wf_cur_inst, final Record wf_his_inst, final Record finalCurNode) {
        logger.debug("Enter into afterStartNodeDrive");
        return new WfIAtom() {
            @Override
            public boolean run() throws SQLException {

                try {
                    processPreWfCurInst(wf_cur_inst, finalCurNode);
                } catch (WorkflowException e) {
                    this.errMsg = e.getMessage();
                    return false;
                }

                if (!Db.save("cfm_workflow_his_execute_inst", wf_his_inst)) {
                    this.errMsg = "将当前实例转换为历史实例失败";
                    return false;
                }

                if (wfobj.chgStatus(WebConstant.BillStatus.REJECT)) {
                    return true;
                } else {
                    this.errMsg = "更新单据状态失败";
                    return false;
                }
            }
        };
    }


    /**
     * 加签事务处理
     *
     * @param wfobj        请求对象
     * @param wf_cur_inst  前一个当前实例
     * @param wf_his_inst  存储的历史实例
     * @param wf_new_inst  存储的当前实例
     * @param finalCurNode 前一个当前实例的当前节点
     * @return
     */
    private WfIAtom appendNodeDrive(final WfRequestObj wfobj, final Record wf_cur_inst, final Record wf_his_inst, final Record wf_new_inst, final Record finalCurNode) {
        logger.debug("Enter into appendNodeDrive");
        return new WfIAtom() {
            @Override
            public boolean run() throws SQLException {

                try {
                    processPreWfCurInst(wf_cur_inst, finalCurNode);
                } catch (WorkflowException e) {
                    this.errMsg = e.getMessage();
                    return false;
                }

                if (!Db.save("cfm_workflow_his_execute_inst", wf_his_inst)) {
                    this.errMsg = "将当前实例转换为历史实例失败";
                    return false;
                }

                if (Db.save("cfm_workflow_run_execute_inst", wf_new_inst)) {
                    try {
                        approvePermission.addUserApprovePermissions(new Long[]{wfobj.getShadowUserId()}, wf_new_inst.getLong("id"));
                    } catch (WorkflowException e) {
                        this.errMsg = "添加当前的工作流用户对应关系失败：" + e.getMessage();
                        return false;
                    }
                } else {
                    this.errMsg = "添加加签流程实例失败";
                    return false;
                }

                if (wfobj.chgStatus(WebConstant.BillStatus.AUDITING)) {
                    return true;
                } else {
                    this.errMsg = "更新单据状态失败";
                    return false;
                }
            }
        };
    }

    /**
     * 撤回事务处理
     *
     * @param wfobj        请求对象
     * @param wf_cur_inst  前一个当前实例
     * @param wf_his_inst  存储的历史实例
     * @param finalCurNode 前一个当前实例的当前节点
     * @return
     */
    private WfIAtom revokeDrive(final WfRequestObj wfobj, final Record wf_cur_inst, final Record wf_his_inst, final Record wf_new_inst, final Record finalCurNode) {
        logger.debug("Enter into revokeDrive");
        return new WfIAtom() {
            @Override
            public boolean run() throws SQLException {

                try {
                    processPreWfCurInst(wf_cur_inst, finalCurNode);
                } catch (WorkflowException e) {
                    this.errMsg = e.getMessage();
                    return false;
                }

                if (!Db.save("cfm_workflow_his_execute_inst", wf_his_inst)) {
                    this.errMsg = "将当前实例转换为历史实例失败";
                    return false;
                }

                if (wf_new_inst != null) {
                    if (Db.save("cfm_workflow_run_execute_inst", wf_new_inst)) {
                        try {
                            approvePermission.addUserApprovePermissions(new Long[]{wfobj.getShadowUserId()}, wf_new_inst.getLong("id"));
                        } catch (WorkflowException e) {
                            this.errMsg = "添加当前的工作流用户对应关系失败：" + e.getMessage();
                            return false;
                        }
                    } else {
                        this.errMsg = "添加加签流程实例失败";
                        return false;
                    }
                }

                if (wfobj.chgStatus(WebConstant.BillStatus.SAVED)) {
                    return true;
                } else {
                    this.errMsg = "更新单据状态失败";
                    return false;
                }
            }
        };
    }


    /**
     * 根据前一个当前实例的是否加签标识去除历史的用户与审批实例的的对应关系
     *
     * @param wf_cur_inst  前一个当前实例
     * @param finalCurNode 前一个当前实例的当前节点
     * @throws WorkflowException
     */
    private void reduceCurrNodeApprovePerminsionss(Record wf_cur_inst, final Record finalCurNode) throws WorkflowException {
        if (WebConstant.YesOrNo.YES.getKey() == wf_cur_inst.getInt("shadow_execute")) {
            approvePermission.reduceUserApprovePermissions(new Long[]{wf_cur_inst.getLong("shadow_user_id")}, wf_cur_inst.getLong("id"));
        } else {
            approvePermission.reduceApprovePermissions(finalCurNode, wf_cur_inst);
        }
    }


    /**
     * 处理前一个当前流程
     *
     * @param wf_cur_inst  前一个当前实例
     * @param finalCurNode 前一个当前实例的当前节点
     * @throws WorkflowException
     */
    private void processPreWfCurInst(final Record wf_cur_inst, final Record finalCurNode) throws WorkflowException {
        if (Db.delete("cfm_workflow_run_execute_inst", wf_cur_inst)) {
            try {
                reduceCurrNodeApprovePerminsionss(wf_cur_inst, finalCurNode);
            } catch (WorkflowException e) {
                throw new WorkflowException("去除前一个当前的工作流用户对应关系失败:" + e.getMessage());
            }
        } else {
            throw new WorkflowException("删除前一个当前实例失败");
        }
    }


    /**
     * 处理新生成的流程
     *
     * @param wf_new_inst   存储的当前实例
     * @param finalNextNode 根据前一个当前实例计算出来的下一个节点，即存储的当前实例的当前节点
     * @throws WorkflowException
     */
    private void processNewWfCurInst(final Record wf_new_inst, final Record finalNextNode) throws WorkflowException {
        if (Db.save("cfm_workflow_run_execute_inst", wf_new_inst)) {
            //添加审批实例
            try {
                approvePermission.addApprovePermissions(finalNextNode, wf_new_inst);
            } catch (WorkflowException e) {
                throw new WorkflowException("添加当前的工作流用户对应关系失败：" + e.getMessage());
            }
        } else {
            throw new WorkflowException("添加当前实例失败");
        }
    }


    /**
     * 判断是否能过提交和撤回工作流，只有单据的创建人（create_by）能提交和撤回工作流
     * @param requestObj
     * @param userInfo
     * @return
     */
    private boolean canStartOrRevoke(final WfRequestObj requestObj, UserInfo userInfo)  {
        if(requestObj != null){
            try {
                Record record = requestObj.getBillRecord();
                return (TypeUtils.castToLong(record.get("create_by")).longValue() == userInfo.getUsr_id().longValue())
                        || (WebConstant.COMMONUser.JZUser.getId().longValue() == TypeUtils.castToLong(record.get("create_by")).longValue());
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("根据WfRequestObj无法找到对应的单据信息！");
                return false;
            }
        }
        return false;

    }


    /**
     * 判断是否能过同意、拒绝或加签，只有单据的当前的审批人能提交和撤回工作流
     * @param requestObj
     * @param userInfo
     * @return
     */
    private boolean canAgreeOrRejectOrAppend(final WfRequestObj requestObj, UserInfo userInfo){
        Long curInstid = null;
        try {
            curInstid = requestObj.getInstId();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("根据WfRequestObj无法找到对应的实例信息");
            return false;
        }
        WorkflowQueryService queryService = new WorkflowQueryService();
        Long[] insInds = queryService.getInstIdsWithUser(userInfo);
        if(insInds != null && insInds.length > 0){
            for (Long ind : insInds) {
                if(ind.longValue() == curInstid.longValue()){
                    return true;
                }
            }
            return false;
        }else{
            logger.error("空实例id列表，无权限");
            return false;
        }
    }
}
