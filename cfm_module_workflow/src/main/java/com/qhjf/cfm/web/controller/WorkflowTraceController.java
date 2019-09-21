package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkflowProcessService;
import com.qhjf.cfm.web.service.WorkflowQueryService;
import com.qhjf.cfm.web.service.WorkflowTraceService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/7/27
 * @Description: 审批中业务跟踪管理
 */
@Auth(withRoles = {"admin"})
public class WorkflowTraceController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(WorkflowTraceController.class);

    private WorkflowTraceService service = new WorkflowTraceService();
    private WorkflowQueryService queryService = new WorkflowQueryService();
    private WorkflowProcessService processService = new WorkflowProcessService();

    /**
     * 业务跟踪列表
     */
    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.list(pageNum, pageSize, record);

        for (Record rec : page.getList()) {
            long billId = TypeUtils.castToLong(rec.get("bill_id"));
            int bizType = TypeUtils.castToInt(rec.get("biz_type"));
            record.set("id", billId);
            record.set("biz_type", bizType);
            WfRequestObj wfobj = null;
            try {
                wfobj = new WfRequestObj(record) {
                    @Override
                    public <T> T getFieldValue(WebConstant.WfExpressType type) {
                        return null;
                    }

                    @Override
                    public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                        return null;
                    }
                };
                //查询当前处理人
                List<Record> currRec = queryService.getCurrApproveUser(wfobj);
                //上级处理人
                Kv kv = Kv.create();
                kv.set("bill_id", billId);
                kv.set("biz_type", bizType);
                List<Record> hisRec = Db.find(Db.getSqlPara("query.findhisexecuteinstList", Kv.by("map", kv)));
                //下级审批人
                List<Record> nextRec = queryService.getNextApproveUser(1, wfobj);

                rec.set("current", currRec);
                rec.set("history", hisRec);
                rec.set("future", nextRec);

                WebConstant.MajorBizType majorBizType = WebConstant.MajorBizType.getBizType(bizType);
                String tableName = majorBizType.getTableName();
                if ("".equals(tableName)) {
                    continue;
                }

                Record billRec = Db.findById(tableName, "id", billId);
                if (billRec == null) {
                    continue;
                }
                int service_status = TypeUtils.castToInt(billRec.get("service_status"));
                int persist_version = TypeUtils.castToInt(billRec.get("persist_version"));

                rec.set("service_status", service_status);
                rec.set("persist_version", persist_version);
            } catch (WorkflowException e) {
//                e.printStackTrace();
//                renderFail(e);
                rec.set("current", new ArrayList<>());
                rec.set("history", new ArrayList<>());
                rec.set("future", new ArrayList<>());
            }

        }
        renderOkPage(page);
    }

    public void approvrevoke() {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();

        WfRequestObj wfobj = null;
        try {
            wfobj = new WfRequestObj(record) {
                @Override
                public <T> T getFieldValue(WebConstant.WfExpressType type) {
                    return null;
                }

                @Override
                public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                    return null;
                }
            };
            boolean flag = processService.approvReject(wfobj, userInfo);
            if (flag) {
                renderOk(null);
            } else {
                throw new WorkflowException("撤回失败!");
            }
        } catch (WorkflowException e) {
//            e.printStackTrace();
            renderFail(e);
        }

    }

}
