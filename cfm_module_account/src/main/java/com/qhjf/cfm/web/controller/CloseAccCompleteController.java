package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CloseAccCompleteService;
import com.qhjf.cfm.web.util.AccountUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsq on 2018/6/26.
 */
@SuppressWarnings("unused")
//@Auth(hasRoles = {"normal"})

public class CloseAccCompleteController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CloseAccCompleteController.class);

    private CloseAccCompleteService service = new CloseAccCompleteService();

    @Auth(hasForces = {"AccCloseComAppl"})
    public void todolist() {
        try {
            Record record = getRecordByParamsStrong();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            UodpInfo defaultUodp = getCurUodp();
            UserInfo userInfo = getUserInfo();
            record.set("create_by", userInfo.getUsr_id());
            record.set("org_id", getCurUodp().getOrg_id());
            record.set("user_id", userInfo.getUsr_id());
            Page<Record> todoPage = service.getTodoPage(page_num, page_size, record);
            renderOkPage(todoPage);
        } catch (BusinessException e) {
            log.error("获取销户事项补录列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccCloseComAppl"})
    public void todoadd() {
        try {
            Record record = getRecordByParamsStrong();
            record.set("create_by", getUserInfo().getUsr_id());
            record.set("user_name", getUserInfo().getName());
            Record todoadd = service.todoadd(record, getUserInfo());
            renderOk(todoadd);
        } catch (BusinessException e) {
            log.error("保存销户信息补录失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccCloseComAppl"})
    public void todochg() {
        try {
            Record record = getRecordByParamsStrong();
            record.set("update_by", getUserInfo().getUsr_id());
            record.set("user_name", getUserInfo().getName());
            Record todochg = service.todochg(record, getUserInfo());
            renderOk(todochg);
        } catch (BusinessException e) {
            log.error("修改销户事项补录信息失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccCloseComAppl"})
    public void tododel() {
        try {
            Record record = getParamsToRecord();
            service.tododel(record, getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除销户事项补录信息失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccCloseComAppl"})
    public void donelist() {
        Record record = getRecordByParamsStrong();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        record.set("create_by", getUserInfo().getUsr_id());
        Page<Record> page = service.getDonePage(page_num, page_size, record);
        renderOkPage(page);
    }

    @Auth(hasForces = {"AccCloseComAppl", "MyWFPLAT"})
    public void detail() throws ReqDataException {
        Record record = getParamsToRecord();
        try {
            renderOk(service.detail(record, getUserInfo()));
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getRecordByParamsStrong();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            record.set("update_by", getUserInfo().getUsr_id());
            record.set("user_name", getUserInfo().getName());
            Record todochg = service.todochg(record, getUserInfo());
            return todochg;
        } else {
            record.set("create_by", getUserInfo().getUsr_id());
            record.set("user_name", getUserInfo().getName());
            record.remove("id");
            Record todoadd = service.todoadd(record, getUserInfo());
            return todoadd;
        }
    }

    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getRecordByParamsStrong();
        return new WfRequestObj(WebConstant.MajorBizType.ACC_CLOSE_COM, "acc_close_complete_apply", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) {
                return null;
            }

            @Override
            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                return getSqlPara(inst_id, exclude_inst_id, record);
            }

            @Override
            protected boolean hookPass() {
                return pass(record);
            }
        };
    }

    private boolean pass(Record record) {
        long id = TypeUtils.castToLong(record.get("id"));
        Record comRec = Db.findById("acc_close_complete_apply", "id", id);
        long accId = TypeUtils.castToLong(comRec.get("acc_id"));
        Integer status = WebConstant.AccountStatus.CLOSED.getKey();
        return AccountUtil.chgAccountStatus(accId, status);
    }

    @Override
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        Record record = getParamsToRecordStrong();
        final List<Record> wfRequestObjs = record.get("batch_list");

        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

            return new ArrayList<WfRequestObj>() {
                {
                    for (final Record rec : wfRequestObjs) {
                        add(new WfRequestObj(WebConstant.MajorBizType.ACC_CLOSE_COM, "acc_close_complete_apply", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) {
                                return null;
                            }

                            @Override
                            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                                return getSqlPara(inst_id, exclude_inst_id, rec);
                            }

                            @Override
                            protected boolean hookPass() {
                                return pass(rec);
                            }
                        });
                    }
                }
            };

        }

        throw new WorkflowException("没有可操作的单据!");
    }

    private SqlPara getSqlPara(Long[] inst_id, Long[] exclude_inst_id, Record rec) {
        final Kv kv = Kv.create();
        if (inst_id != null) {
            kv.set("in", new Record().set("instIds", inst_id));
        }
        if (exclude_inst_id != null) {
            kv.set("notin", new Record().set("excludeInstIds", exclude_inst_id));
        }
        kv.set("biz_type", rec.get("biz_type"));
        return Db.getSqlPara("caf.findCloseCompletePendingList", Kv.by("map", kv));
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ACC_CLOSE_COM.getKey(), getCurUodp().getOrg_id(), null);
    }
}
