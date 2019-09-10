package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkflowTransService;

import java.text.SimpleDateFormat;

public class WorkflowTransController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(WorkflowTransController.class);

    private WorkflowTransService service = new WorkflowTransService();


    /**
     * 新增编辑一调审批转移记录
     */
    @Auth(hasForces = {"ApproveTransfer"})
    public void add() {

        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = this.getUserInfo();

            record = service.add(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 被授权人记录信息列表
     */
    @Auth(hasForces = {"ApproveTransfer"})
    public void list() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = this.getUserInfo();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);

            record.set("usr_id", userInfo.getUsr_id());

            Page<Record> page = service.list(pageNum, pageSize, record, userInfo);
            Record authorizeRec = service.initAuthorize(userInfo);
            if (authorizeRec != null) {
                authorizeRec.set("start_date", format.format(TypeUtils.castToDate(authorizeRec.get("start_date"))));
                authorizeRec.set("end_date", format.format(TypeUtils.castToDate(authorizeRec.get("end_date"))));
            }

            renderOkPage(page, authorizeRec);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 被授权人列表
     */
    @Auth(hasForces = {"ApproveTransfer"})
    public void findauthorizename() {
        Record record = getRecordByParamsStrong();
        final UserInfo userInfo = getUserInfo();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.findAuthorizeName(pageNum, pageSize, record, userInfo);

        renderOkPage(page);
    }


}
