package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.workflow.api.WfApprovePermissionTool;
import com.qhjf.cfm.workflow.api.WfAuthorizeRelation;

import java.sql.SQLException;

public class WorkflowTransService {

    private static final Log logger = LogbackLog.getLog(WorkflowTransService.class);

    private static final WfApprovePermissionTool approvePermission = WfApprovePermissionTool.getINSTANCE();


    /**
     * 新增编辑一调审批转移记录
     *
     * @param record   请求对象
     * @param userInfo 用户信息
     */
    public Record add(final Record record, final UserInfo userInfo) throws BusinessException {

        record.set("authorize_usr_id", userInfo.getUsr_id());
        record.set("authorize_usr_name", userInfo.getName());

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record logRecord = new Record();
                logRecord.set("authorize_usr_id", record.get("authorize_usr_id"));
                boolean result = CommonService.update("cfm_workflow_authorize_relation",
                        new Record().set("be_authorize_usr_id", record.get("be_authorize_usr_id"))
                                .set("be_authorize_usr_id", record.get("be_authorize_usr_id"))
                                .set("be_authorize_usr_name", record.get("be_authorize_usr_name"))
                                .set("start_date", record.get("start_date"))
                                .set("end_date", record.get("end_date")),
                        new Record().set("authorize_usr_id", userInfo.getUsr_id()));
                if (result) {
                    logRecord.set("memo", "【更新授权】被授权人: " + record.getStr("be_authorize_usr_name") +
                            "授权开始时间: " + record.getStr("start_date") +
                            "授权结束时间: " + record.getStr("end_date"));
                    Db.save("cfm_workflow_authorize_relation_log", logRecord);
                } else {
                    boolean saveFlag = Db.save("cfm_workflow_authorize_relation", "authorize_usr_id", record);
                    if (saveFlag) {
                        logRecord.set("memo", "【新增授权】被授权人: " + record.getStr("be_authorize_usr_name") +
                                "授权开始时间: " + record.getStr("start_date") +
                                "授权结束时间: " + record.getStr("end_date"));
                        Db.save("cfm_workflow_authorize_relation_log", logRecord);
                    } else {
                        return false;
                    }

                }
                return true;

            }
        });
        if (flag) {
            WfAuthorizeRelation relation = new WfAuthorizeRelation(
                    record.getLong("be_authorize_usr_id"), record.getStr("start_date"), record.getStr("end_date"));
            approvePermission.refreshAuthorized(record.getLong("authorize_usr_id"),
                    record.getLong("be_authorize_usr_id"), relation);
            return Db.findById("cfm_workflow_authorize_relation", "authorize_usr_id", record.get("authorize_usr_id"));
        }

        throw new DbProcessException("审批转移编辑失败!");
    }


    /**
     * 被授权人记录信息
     *
     * @param pageNum
     * @param pageSize
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record, UserInfo userInfo) throws BusinessException {
        SqlPara sqlPara = Db.getSqlPara("wftrans.getLog", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);

    }

    public Record initAuthorize(final UserInfo userInfo) {
        return Db.findById("cfm_workflow_authorize_relation", "authorize_usr_id", userInfo.getUsr_id());
    }

    /**
     * 查找被授权人
     *
     * @param record
     * @return
     */
    public Page<Record> findAuthorizeName(int pageNum, int pageSize, final Record record, final UserInfo userInfo) {

        Kv kv = Kv.create();
        kv.set("query_key", TypeUtils.castToString(record.get("query_key")));
        //不包含当前用户
        kv.set("notin", new Record().set("excludeIds", userInfo.getUsr_id()));
        SqlPara sqlPara = Db.getSqlPara("wftrans.findAuthorizeName", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

}
