package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccDefreezeService;
import com.qhjf.cfm.web.util.AccountUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangsq on 2018/6/28.
 */
@SuppressWarnings({"unused"})
//@Auth(hasRoles = {"normal"})
public class AccDefreezeController extends CFMBaseController {
    private static final Log log = LogbackLog.getLog(AccDefreezeController.class);

    private AccDefreezeService service = new AccDefreezeService();

    @Auth(hasForces = {"AccDefreezeAppl"})
    public void todolist() {
        Record record = getRecordByParamsStrong();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        record.set("create_by", getUserInfo().getUsr_id());
        Page<Record> page = service.getTodoPage(page_num, page_size, record);
        renderOkPage(page);
    }

    @Auth(hasForces = {"AccDefreezeAppl"})
    public void todoadd() {
        try {
            Record record = getParamsToRecord();
            record.set("create_by", getUserInfo().getUsr_id());
            service.todoadd(record, getUserInfo().getName());
            renderOk(record);
        } catch (BusinessException e) {
            log.error("添加账户解冻申请单失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccDefreezeAppl"})
    public void todochg() {
        try {
            Record record = getParamsToRecord();
            service.todochg(record, getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            log.error("修改账户解冻申请单失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccDefreezeAppl"})
    public void tododel() {
        try {
            Record record = getParamsToRecord();
            service.tododel(record,getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除账户解冻申请单失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccDefreezeAppl"})
    public void donelist() {
        Record record = getRecordByParamsStrong();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        record.set("create_by", getUserInfo().getUsr_id());
        Page<Record> page = service.getDonePage(page_num, page_size, record);
        renderOkPage(page);
    }

    @Auth(hasForces = {"AccDefreezeAppl", "MyWFPLAT"})
    public void detail() {
        Record record = getParamsToRecord();
        try {
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.ACC_DEFREEZE_APL, "acc_freeze_and_defreeze_apply", record) {
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

    @Override
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        Record record = getParamsToRecordStrong();
        final List<Record> wfRequestObjs = record.get("batch_list");

        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

            return new ArrayList<WfRequestObj>() {
                {
                    for (final Record rec : wfRequestObjs) {
                        add(new WfRequestObj(WebConstant.MajorBizType.ACC_DEFREEZE_APL, "acc_freeze_and_defreeze_apply", rec) {
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

    private boolean pass(Record rec) {
        long id = TypeUtils.castToLong(rec.get("id"));
        Record freezeRec = Db.findById("acc_freeze_and_defreeze_apply", "id", id);

        long accId = TypeUtils.castToLong(freezeRec.get("acc_id"));
        Integer status = WebConstant.AccountStatus.NORAMAL.getKey();
        return AccountUtil.chgAccountStatus(accId, status);
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
        kv.set("type", 2);
        return Db.getSqlPara("afd.findFreezeAndDeFreezePendingList", Kv.by("map", kv));
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecord();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            record = service.todochg(record, getUserInfo());
        } else {
            record.set("create_by", getUserInfo().getUsr_id());
            record.remove("id");
            record = service.todoadd(record, getUserInfo().getName());
        }
        return record;
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ACC_DEFREEZE_APL.getKey(), getCurUodp().getOrg_id(), null);
    }
}
