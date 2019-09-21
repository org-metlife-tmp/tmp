package com.qhjf.cfm.web.controller;


import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkflowDefineService;

@Auth(withRoles = {"admin"})
public class WorkflowDefineController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(WorkflowDefineController.class);

    private WorkflowDefineService service = new WorkflowDefineService();

    /**
     * 新增工作流定义
     */
    public void add() {
        try {
            Record record = getParamsToRecord();
            Record add = service.add(record, getUserInfo().getUsr_id());
            renderOk(add);
        } catch (BusinessException e) {
            logger.error("新建流程失败！", e);
            renderFail(e);
        }

    }


    /**
     * 修改工作流定义
     */
    public void chg() {

    }


    /**
     * 删除工作流定义
     */
    public void del() {
        try {
            Record record = getParamsToRecord();
            service.del(record);
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除流程失败！", e);
            renderFail(e);
        }
    }


    /**
     * 列出有效的工作流定义
     */
    public void list() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        Page<Record> page = service.getPage(page_num, page_size, record);
        renderOkPage(page);
    }

    /**
     * 设置流程状态失败
     */
    public void setstatus() {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.setstatus(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("设置流程状态失败!", e);
            renderFail(e);
        }
    }

}
