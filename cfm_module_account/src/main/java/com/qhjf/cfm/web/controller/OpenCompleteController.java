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
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.OpenCompleteService;

import java.util.ArrayList;
import java.util.List;

/**
 * 开户事项补录
 *
 * @auther zhangyuanyuan
 * @create 2018/6/27
 */

@SuppressWarnings("unused")
//@Auth(hasRoles = {"normal"})
public class OpenCompleteController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(OpenCompleteController.class);

    private OpenCompleteService service = new OpenCompleteService();

    /**
     * 开户补录待办列表
     */
    @Auth(hasForces = {"AccOpenComAppl"})
    public void todolist() {
        Page<Record> page = null;
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setTodoListStatus(record, "service_status");

        record.set("user_id", getUserInfo().getUsr_id());

        page = service.findOpenCompleteToPage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 开户补录已办列表
     */
    @Auth(hasForces = {"AccOpenComAppl"})
    public void donelist() {
        Page<Record> page = null;
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setDoneListStatus(record, "service_status");

        record.set("aoc.delete_flag", 0);
        record.set("aoc.create_by", getUserInfo().getUsr_id());

        page = service.findOpenCompleteDonePage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 开户补录新增
     */
    @Auth(hasForces = {"AccOpenComAppl"})
    public void add() {
        try {
            Record record = getParamsToRecord();
            UserInfo userInfo = getUserInfo();

            record = service.add(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("新增开事项补录失败!", e);
            renderFail(e);
        }
    }

    /**
     * 修改开户补录信息
     */
    @Auth(hasForces = {"AccOpenComAppl"})
    public void chg() {
        Record record = getParamsToRecord();
        UserInfo userInfo = getUserInfo();
        try {
            UodpInfo uodp = getCurUodp();

            record = service.chg(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改开事项补录失败!", e);
            renderFail(e);
        }
    }

    /**
     * 删除开户补录信息
     */
    @Auth(hasForces = {"AccOpenComAppl"})
    public void del() {
        Record record = getParamsToRecord();
        try {
            service.del(record,getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除开事项补录失败!", e);
            renderFail(e);
        }
    }

    /**
     * 查看开户补录信息
     */
    @Auth(hasForces = {"AccOpenComAppl", "MyWFPLAT"})
    public void detail() {
        Record record = getParamsToRecord();
        try {
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("查看开事项补录失败!", e);
            renderFail(e);
        }

    }

    @Override
    protected WfRequestObj genWfRequestObj() throws WorkflowException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.ACC_OPEN_COM, "acc_open_complete_apply", record) {
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
                    logger.debug("补录新增账户保存失败");
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
                        add(new WfRequestObj(WebConstant.MajorBizType.ACC_OPEN_COM, "acc_open_complete_apply", rec) {
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
                                    logger.debug("补录新增账户保存失败");
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
        return Db.getSqlPara("aoc.findOpenCompletePendingList", Kv.by("map", kv));
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecord();
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
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ACC_OPEN_COM.getKey(), getCurUodp().getOrg_id(), null);
    }
}
