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
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.OpenIntentionService;

import java.util.ArrayList;
import java.util.List;

/**
 * 开户事项申请
 *
 * @auther zhangyuanyuan
 * @create 2018/6/26
 */
@SuppressWarnings("unused")
//@Auth(hasRoles = {"normal"})
public class OpenIntentionController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(OpenIntentionController.class);

    private OpenIntentionService service = new OpenIntentionService();

    /**
     * 开户事项申请待办列表
     */
    @Auth(hasForces = {"AccOpenIntAppl"})
    public void todolist() {
        Page<Record> page = null;
        try {
            Record record = getRecordByParamsStrong();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);

            AccCommonService.setTodoListStatus(record, "service_status");

            record.set("create_by", getUserInfo().getUsr_id());
            record.set("org_id", getCurUodp().getOrg_id());
            record.set("delete_flag", 0);

            page = service.findOpenIntentionToPage(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 开户事项申请已办列表
     */
    @Auth(hasForces = {"AccOpenIntAppl"})
    public void donelist() {
        Page<Record> page = null;
        try {
            Record record = getRecordByParamsStrong();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);

            AccCommonService.setDoneListStatus(record, "service_status");

            record.set("create_by", getUserInfo().getUsr_id());
            record.set("org_id", getCurUodp().getOrg_id());
            record.set("delete_flag", 0);

            page = service.findOpenIntentionToPage(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 开户事项申请新增
     */
    @Auth(hasForces = {"AccOpenIntAppl"})
    public void add() {
        try {
            Record record = getParamsToRecord();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodp = getCurUodp();
            record = service.add(record, userInfo, uodp);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("新增开事项申请失败！", e);
            renderFail(e);
        }
    }

    /**
     * 开户事项申请修改
     */
    @Auth(hasForces = {"AccOpenIntAppl"})
    public void chg() {
        try {
            Record record = getParamsToRecord();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodp = getCurUodp();
            record = service.chg(record, userInfo, uodp);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改开事项申请失败！", e);
            renderFail(e);
        }
    }

    /**
     * 开户事项申请详情
     */
    @Auth(hasForces = {"AccOpenIntAppl","MyWFPLAT"})
    public void detail() {
        Record record = getParamsToRecord();
        try {
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("未找到有效的单据！", e);
            renderFail(e);
        }
    }


    /**
     * 开户事项申请删除
     */
    @Auth(hasForces = {"AccOpenIntAppl"})
    public void del() {
        Record record = getParamsToRecord();
        try {
            service.del(record,getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除开事项申请失败！", e);
            renderFail(e);
        }
    }

    /**
     * 开户事项申请分发
     */
    @Auth(hasForces = {"AccOpenIntAppl"})
    public void issue() {
        Record record = getRecordByParamsStrong();
        try {
            UserInfo userInfo = getUserInfo();
            Record issRec = service.issue(record, userInfo);
            renderOk(issRec);
        } catch (BusinessException e) {
            logger.error("开事项申请分发失败！", e);
            renderFail(e);
        }
    }

    /**
     * 开户事项申请办结
     */
    @Auth(hasForces = {"AccOpenIntAppl"})
    public void finish() {
        Record record = getParamsToRecord();
        try {
            service.finish(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("开事项申请办结失败！", e);
            renderFail(e);
        }
    }

    @Override
    protected WfRequestObj genWfRequestObj() throws WorkflowException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.ACC_OPEN_INT, "acc_open_intention_apply", record) {
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
                //分发给单据创建人
                try {
                    Record bill_info = getBillRecord();
                    return service.issueOp(record, new Long[]{TypeUtils.castToLong(bill_info.get("create_by"))});
                } catch (WorkflowException e) {
                    logger.error("hookPass failed ", e);
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
                        add(new WfRequestObj(WebConstant.MajorBizType.ACC_OPEN_INT, "acc_open_intention_apply", rec) {
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
                                //分发给单据创建人
                                try {
                                    Record bill_info = getBillRecord();
                                    return service.issueOp(rec, new Long[]{TypeUtils.castToLong(bill_info.get("create_by"))});
                                } catch (WorkflowException e) {
                                    logger.error("hookPass failed ", e);
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
        return Db.getSqlPara("aoi.findOpenIntentionPendingList", Kv.by("map", kv));
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodp = getCurUodp();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            return service.chg(record, userInfo, uodp);
        } else {
            record.remove("id");
            return service.add(record, userInfo, uodp);
        }
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ACC_OPEN_INT.getKey(), getCurUodp().getOrg_id(), null);
    }
}
