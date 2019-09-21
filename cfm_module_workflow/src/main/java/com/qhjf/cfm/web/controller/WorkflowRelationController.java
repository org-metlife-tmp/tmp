package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkflowRelationService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/7/11
 * @Description: 工作流关系绑定
 */
@Auth(withRoles = {"admin"})
public class WorkflowRelationController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(WorkflowDefineController.class);

    private WorkflowRelationService service = new WorkflowRelationService();

    /**
     * 配置审批流程列表
     */
    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 新增配置审批流程
     */
    public void add() {
        Record record = getParamsToRecord();
        try {
            record = service.add(record);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("新增配置审批流程失败!", e);
            renderFail(e);
        }
    }

    /**
     * 修改配置审批流程
     */
    public void chg() {
        Record record = getParamsToRecord();
        try {
            record = service.chg(record);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改配置审批流程失败!", e);
            renderFail(e);
        }
    }

    /**
     * 删除配置审批流程
     */
    public void del() {
        Record record = getParamsToRecord();
        try {
            service.del(record);
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除配置审批流程失败!", e);
            renderFail(e);
        }
    }
}
