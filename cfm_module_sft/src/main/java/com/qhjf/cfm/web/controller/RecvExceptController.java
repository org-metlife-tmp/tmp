package com.qhjf.cfm.web.controller;

import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.RecvExceptService;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量收异常处理
 *
 * @author GJF
 * @date 2019年01月21日
 */
public class RecvExceptController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(RecvExceptController.class);
    private RecvExceptService service = new RecvExceptService();

    /**
     * 异常处理列表
     */
    @Auth(hasForces = {"RecvBatchDoExcp"})
    public void exceptlist() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }

        AccCommonService.setExceptStatus(record, "service_status");
        try{
            Page<Record> page = service.exceptlist(pageNum, pageSize, record, uodpInfo);
            renderOkPage(page);
        }catch(BusinessException e){
            e.printStackTrace();
            renderFail(e);
        }

    }
    @Auth(hasForces = {"RecvBatchDoExcp", "MyWFPLAT"})
    public void detail() {
        try {
            Record record = getRecordByParamsStrong();
            UodpInfo uodpInfo = getCurUodp();
            UserInfo userInfo = getUserInfo();
            record = service.detail(record,userInfo,uodpInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 回退
     */
    @Auth(hasForces = {"RecvBatchDoExcp"})
    public void revoke() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = null;
            UodpInfo uodpInfo = null;
            try {
                userInfo = getUserInfo();
                uodpInfo = getCurUodp();
            } catch (ReqDataException e) {
                e.printStackTrace();
                renderFail(e);
            }
            service.revoke(record,userInfo,uodpInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.PLS_EXCEPT_BACK, "recv_batch_total", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
               return null;
            }

            @Override
            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                return getSqlPara(inst_id, exclude_inst_id, record);
            }

            @Override
            public boolean hookPass() {
                return service.hookPass(record);
            }
            @Override
            public boolean hookReject() {
                return service.hookReject(record);
            }

        };
    }

    @Override
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        Record record = getParamsToRecordStrong();
        final List<Record> wfRequestObjs = record.get("batch_list");

        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

            return new ArrayList<WfRequestObj>() {
                {
                    for (final Record rec : wfRequestObjs) {
                        add(new WfRequestObj(WebConstant.MajorBizType.PLS_EXCEPT_BACK, "recv_batch_total", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                return null;
                            }

                            @Override
                            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                                return getSqlPara(inst_id, exclude_inst_id, rec);
                            }

                            @Override
                            protected boolean hookPass() {
                                return service.hookPass(rec);
                            }

                            @Override
                            protected boolean hookReject() {
                                return service.hookReject(rec);
                            }
                        });
                    }
                }
            };

        }

        throw new WorkflowException("没有可操作的单据!");
    }

    //TODO
    private SqlPara getSqlPara(Long[] inst_id, Long[] exclude_inst_id, Record rec) {
        final Kv kv = Kv.create();
        if (inst_id != null) {
            kv.set("in", new Record().set("instIds", inst_id));
        }
        if (exclude_inst_id != null) {
            kv.set("notin", new Record().set("excludeInstIds", exclude_inst_id));
        }
        kv.set("biz_type", rec.get("biz_type"));
        return Db.getSqlPara("recvexcept.findExceptlistPendingList", Kv.by("map", kv));
    }

    /**
     * 导出异常数据
     */
    @Auth(hasForces = {"RecvBatchDoExcp"})
    public void listexport() {
        doExport();
    }

}
