package com.qhjf.cfm.web.controller;


import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccChangeService;
import com.qhjf.cfm.web.service.AccCommonService;

import java.util.ArrayList;
import java.util.List;

/**
 * 账户变更申请
 *
 * @auther zhangyuanyuan
 * @create 2018/6/29
 */

@SuppressWarnings("unused")
//@Auth(hasRoles = {"normal"})
public class AccChangeController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AccChangeController.class);

    private AccChangeService service = new AccChangeService();

    /**
     * 变更事项待处理列表
     */
    @Auth(hasForces = {"AccChgAppl"})
    public void todolist() {
        Page<Record> page = null;
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setTodoListStatus(record, "service_status");

        record.set("create_by", getUserInfo().getUsr_id());
        record.set("delete_flag", 0);

        page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 变更事项已处理列表
     */
    @Auth(hasForces = {"AccChgAppl"})
    public void donelist() {
        Page<Record> page = null;
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setDoneListStatus(record, "service_status");

        record.set("create_by", getUserInfo().getUsr_id());
        record.set("delete_flag", 0);

        page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }


    /**
     * 变更事项新增
     */
    @Auth(hasForces = {"AccChgAppl"})
    public void add() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            service.add(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("新增账户变更申请失败！", e);
            renderFail(e);
        }
    }

    /**
     * 变更事项修改
     */
    @Auth(hasForces = {"AccChgAppl"})
    public void chg() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            service.chg(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改账户变更申请失败！", e);
            renderFail(e);
        }
    }

    /**
     * 变更事项删除
     */
    @Auth(hasForces = {"AccChgAppl"})
    public void del() {
        Record record = getParamsToRecord();
        try {
            service.del(record,getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除账户变更申请失败！", e);
            renderFail(e);
        }
    }

    /**
     * 变更事项详情
     */
    @Auth(hasForces = {"AccChgAppl", "MyWFPLAT"})
    public void detail() {
        Record record = getParamsToRecord();
        try {
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("查看账户变更申请失败！", e);
            renderFail(e);
        }
    }

    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getRecordByParamsStrong();
        return new WfRequestObj(WebConstant.MajorBizType.ACC_CHG_APL, "acc_change_apply", record) {
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
                try {
                    return service.pass(record);
                } catch (BusinessException e) {
                    logger.debug("账户变更失败!");
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
                        add(new WfRequestObj(WebConstant.MajorBizType.ACC_CHG_APL, "acc_change_apply", rec) {
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
                                try {
                                    return service.pass(rec);
                                } catch (BusinessException e) {
                                    logger.debug("账户变更失败!");
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
        return Db.getSqlPara("chg.findAccChangePendingList", Kv.by("map", kv));
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            return service.chg(record, userInfo);
        } else {
            record.remove("id");
            return service.add(record, userInfo);
        }
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ACC_CHG_APL.getKey(), getCurUodp().getOrg_id(), null);
    }
}
