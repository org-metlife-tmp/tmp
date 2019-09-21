package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkflowNodeLineService;

/**
 * @Auther: zhangyuan
 * @Date: 2018/7/4
 * @Description: 工作流节点(点 、 线)
 */
@Auth(withRoles = {"admin"})
public class WorkflowNodeLineController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(WorkflowNodeLineController.class);

    private WorkflowNodeLineService service = new WorkflowNodeLineService();

    public void add() {
        Record record = getParamsToRecordStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.add(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("保存流程失败!", e);
            renderFail(e);
        }
    }

}
