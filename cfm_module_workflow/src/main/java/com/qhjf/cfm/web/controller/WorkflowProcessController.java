package com.qhjf.cfm.web.controller;

import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkflowProcessService;

import java.util.ArrayList;
import java.util.List;

public class WorkflowProcessController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(WorkflowProcessController.class);

    private WorkflowProcessService service = new WorkflowProcessService();


    /**
     * 提交
     */
    public void submit() {
        WfRequestObj wfobj = this.getAttr("_wfobj");
        UserInfo userInfo = this.getUserInfo();
        try {
            if (wfobj != null) {
                boolean flag = service.startWorkflow(wfobj, userInfo);
                if (flag) {
                    renderOk(flag);
                } else {
                    throw new WorkflowException("提交失败!");
                }
            } else {
                throw new WorkflowException("提交失败，空的提交请求数据！");
            }
        } catch (WorkflowException e) {
            logger.error("提交失败！", e);
            renderFail(e);
        }
    }


    /**
     * 撤回
     */
    public void revoke() {
        WfRequestObj wfobj = this.getAttr("_wfobj");
        UserInfo userInfo = this.getUserInfo();
        try {
            if (wfobj != null) {
                boolean flag = service.revoke(wfobj, userInfo);
                if (flag) {
                    renderOk(flag);
                } else {
                    throw new WorkflowException("提交失败!");
                }
            } else {
                throw new WorkflowException("提交失败，空的提交请求数据！");
            }
        } catch (WorkflowException e) {
            logger.error("提交失败！", e);
            renderFail(e);
        }
    }


    /**
     * 同意
     */
    public void agree() {
        WfRequestObj wfobj = this.getAttr("_wfobj");
        UserInfo userInfo = this.getUserInfo();
        try {
            if (wfobj != null) {
                boolean flag = service.approvAgree(wfobj, userInfo);
                if (flag) {
                    renderOk(flag);
                } else {
                    throw new WorkflowException("提交失败");
                }
            } else {
                throw new WorkflowException("审批同意失败，空的提交请求数据！");
            }
        } catch (Exception e) {
            logger.error("审批同意失败！", e);
            //补偿缓存
            service.compensateApprovePermission(wfobj, userInfo);
            if (e instanceof BusinessException) {
                renderFail((BusinessException) e);
            } else {
                renderFail(new WorkflowException("审批同意失败, 数据错误!"));
            }
        }
    }


    /**
     * 拒绝
     */
    public void reject() {
        WfRequestObj wfobj = this.getAttr("_wfobj");
        UserInfo userInfo = this.getUserInfo();
        try {
            if (wfobj != null) {
                boolean flag = service.approvReject(wfobj, userInfo);
                if (flag) {
                    renderOk(flag);
                } else {
                    throw new WorkflowException("提交失败");
                }
            } else {
                throw new WorkflowException("审批拒绝失败，空的提交请求数据！");
            }
        } catch (Exception e) {
            logger.error("审批拒绝失败！", e);
            //补偿缓存
            service.compensateApprovePermission(wfobj, userInfo);
            if (e instanceof BusinessException) {
                renderFail((BusinessException) e);
            } else {
                renderFail(new WorkflowException("审批拒绝失败, 数据错误!"));
            }
        }

    }


    /**
     * 加签
     */
    public void append() {
        WfRequestObj wfobj = this.getAttr("_wfobj");
        UserInfo userInfo = this.getUserInfo();
        try {
            if (wfobj != null) {
                boolean flag = service.approvAppend(wfobj, userInfo);
                if (flag) {
                    renderOk(flag);
                } else {
                    //补偿缓存
                    service.compensateApprovePermission(wfobj, userInfo);
                    throw new WorkflowException("提交失败");
                }
            } else {
                throw new WorkflowException("审批加签失败，空的提交请求数据！");
            }
        } catch (Exception e) {
            logger.error("审批加签失败！", e);
            //补偿缓存
            service.compensateApprovePermission(wfobj, userInfo);
            if (e instanceof BusinessException) {
                renderFail((BusinessException) e);
            } else {
                renderFail(new WorkflowException("审批加签失败, 数据错误!"));
            }
        }
    }

    @Override
    public void batchagree() {
        List<WfRequestObj> wfobjs = this.getAttr("_wfobjs");
        UserInfo userInfo = this.getUserInfo();
        List<String> errBillcodes = new ArrayList<>();
        if (wfobjs != null && wfobjs.size() > 0) {
            for (WfRequestObj wfobj : wfobjs) {
                try {
                    boolean flag = service.approvAgree(wfobj, userInfo);
                    if (!flag) {
                        //补偿缓存
                        logger.error("审批同意失败！bill_id is :" + wfobj.getBillCode());
                        service.compensateApprovePermission(wfobj, userInfo);
                        errBillcodes.add(wfobj.getBillCode());
                        continue;
                    }
                } catch (Exception e) {
                    logger.error("审批同意失败！", e);
                    //补偿缓存
                    service.compensateApprovePermission(wfobj, userInfo);
                    addErrWfobj(errBillcodes, wfobj);
                    renderFail(new WorkflowException("审批同意失败！"));
                    return;
                }
            }
            renderOk(Kv.create().set("error_list",errBillcodes));
        } else {
            renderFail(new WorkflowException("审批同意失败，空的提交请求数据！"));
        }
    }

    @Override
    public void batchappend() {
        List<WfRequestObj> wfobjs = this.getAttr("_wfobjs");
        UserInfo userInfo = this.getUserInfo();
        List<String> errBillcodes = new ArrayList<>();
        if (wfobjs != null && wfobjs.size() > 0) {
            for (WfRequestObj wfobj : wfobjs) {
                try {
                    boolean flag = service.approvAppend(wfobj, userInfo);
                    if (!flag) {
                        //补偿缓存
                        logger.error("审批加签失败！bill_id is :" + wfobj.getBillCode());
                        service.compensateApprovePermission(wfobj, userInfo);
                        errBillcodes.add(wfobj.getBillCode());
                        continue;
                    }
                } catch (Exception e) {
                    logger.error("审批加签失败！", e);
                    //补偿缓存
                    service.compensateApprovePermission(wfobj, userInfo);
                    addErrWfobj(errBillcodes, wfobj);
                }
            }
            renderOk(Kv.create().set("error_list",errBillcodes));
        } else {
            renderFail(new WorkflowException("审批加签失败，空的提交请求数据！"));
        }
    }

    /**
     * 记录审批错误的wfobj
     * @param errBillcodes
     * @param wfobj
     */
    private void addErrWfobj(final List<String> errBillcodes, WfRequestObj wfobj) {
        if (wfobj.getRecord() != null) {
            if (wfobj.getRecord().get("service_serial_number") != null) {
                errBillcodes.add(wfobj.getRecord().getStr("service_serial_number"));
            } else if (wfobj.getRecord().get("batchno") != null) {
                errBillcodes.add(wfobj.getRecord().getStr("batchno"));
            }
        }
    }
}
