package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CloseAccIntentionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangsq on 2018/6/26.
 */
@SuppressWarnings("unused")
//@Auth(hasRoles = {"normal"})
public class CloseAccIntentionController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CloseAccIntentionController.class);

    private CloseAccIntentionService service = new CloseAccIntentionService();

    /**********待办start**********/
    /**
     * 待办列表
     */
    @Auth(hasForces = {"AccCloseIntAppl"})
    public void todolist() {
        try {
            Record record = getRecordByParamsStrong();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            record.set("create_by", getUserInfo().getUsr_id());
            record.set("org_id", getCurUodp().getOrg_id());
            Page<Record> page = service.getTodoPage(page_num, page_size, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            log.error("获取销户事项待办列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 待办添加
     */
    @Auth(hasForces = {"AccCloseIntAppl"})
    public void todoadd() {
        try {
            Record record = getRecordByParamsStrong();
            record.set("create_by", getUserInfo().getUsr_id())
                    .set("org_id", getCurUodp().getOrg_id())
                    .set("update_by", getUserInfo().getUsr_id())
                    .set("dept_id", getCurUodp().getDept_id());
            service.todoadd(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("新增销户事项失败！", e);
            renderFail(e);
        }
    }

    /**
     * 待办修改
     */
    @Auth(hasForces = {"AccCloseIntAppl"})
    public void todochg() {
        try {
            Record record = getRecordByParamsStrong();
            record.set("update_by", getUserInfo().getUsr_id())
                    .set("update_on", new Date());
            service.todochg(record, getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            log.error("修改销户事项失败！", e);
            renderFail(e);
        }
    }

    /**
     * 待办删除
     */
    @Auth(hasForces = {"AccCloseIntAppl"})
    public void tododel() {
        try {
            Record record = getParamsToRecord();
            service.tododel(record, getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除销户事项失败！", e);
            renderFail(e);
        }
    }

    /**********待办end**********/

    /**********已办end**********/
    /**
     * 已办列表
     */
    @Auth(hasForces = {"AccCloseIntAppl"})
    public void donelist() {
        try {
            Record record = getRecordByParamsStrong();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            record.set("create_by", getUserInfo().getUsr_id());
            record.set("org_id", getCurUodp().getOrg_id());
            Page<Record> page = service.getDonePage(page_num, page_size, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            log.error("获取销户事项已办列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccCloseIntAppl"})
    public void doneissue() {
        try {
            Record record = getRecordByParamsStrong();
            String doneissue = service.doneissue(record, getUserInfo());
            renderOk(doneissue);
        } catch (BusinessException e) {
            log.error("销户事项分发失败", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccCloseIntAppl"})
    public void doneend() {
        try {
            Record record = getParamsToRecord();
            service.doneend(record, getUserInfo());
            renderOk(record.get("service_status"));
        } catch (BusinessException e) {
            log.error("销户事项办结失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"AccCloseIntAppl", "MyWFPLAT"})
    public void detail() throws DbProcessException {
        Record record = getParamsToRecord();
        try {
            renderOk(service.detail(record, getUserInfo()));
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    /**********已办end**********/

    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getRecordByParamsStrong();
        return new WfRequestObj(WebConstant.MajorBizType.ACC_CLOSE_INT, "acc_close_intertion_apply", record) {
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

                //审批通过默认分发给单据创建人
                try {
                    Record bill_info = getBillRecord();
                    return service.issueOp(record, new Long[]{TypeUtils.castToLong(bill_info.get("create_by"))});
                } catch (WorkflowException e) {
                    log.error("hookPass failed ", e);
                    return false;
                }
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
                        add(new WfRequestObj(WebConstant.MajorBizType.ACC_CLOSE_INT, "acc_close_intertion_apply", rec) {
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

                                //审批通过默认分发给单据创建人
                                try {
                                    Record bill_info = getBillRecord();
                                    return service.issueOp(rec, new Long[]{TypeUtils.castToLong(bill_info.get("create_by"))});
                                } catch (WorkflowException e) {
                                    log.error("hookPass failed ", e);
                                    return false;
                                }
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
        return Db.getSqlPara("cai.findCloseIntentionPendingList", Kv.by("map", kv));
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getRecordByParamsStrong();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            record.set("update_by", getUserInfo().getUsr_id())
                    .set("update_on", new Date());
            record = service.todochg(record, getUserInfo());
            return record;
        } else {
            record.set("create_by", getUserInfo().getUsr_id())
                    .set("org_id", getCurUodp().getOrg_id())
                    .set("update_by", getUserInfo().getUsr_id())
                    .set("dept_id", getCurUodp().getDept_id());
            record.remove("id");
            record = service.todoadd(record);
            return record;
        }
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ACC_CLOSE_INT.getKey(), getCurUodp().getOrg_id(), null);
    }
}
