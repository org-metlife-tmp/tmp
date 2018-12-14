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
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CollectSettingService;

import java.util.ArrayList;
import java.util.List;

public class CollectSettingController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CollectSettingController.class);

    private CollectSettingService service = new CollectSettingService();

    @Auth(hasForces = {"GJSetting"})
    public void add() {
        try {
            Record record = getParamsToRecordStrong();
            Record result = service.add(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("新增归集通单据失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"GJSetting"})
    public void chg() {
        try {
            Record record = getParamsToRecordStrong();
            Record result = service.chg(record, getUserInfo());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("修改归集通单据失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"GJSetting"})
    public void del() {
        try {
            Record record = getParamsToRecord();
            service.del(record,getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除归集通单据失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"GJSetting"})
    public void getchildacclist() {
        try {
            Record record = getParamsToRecord();
            List<Record> list = service.getChildAccList(record, getCurUodp().getOrg_id());
            renderOk(list);
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecordStrong();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            record = service.chg(record, getUserInfo());
        } else {
            record.remove("id");
            record = service.add(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
        }
        return record;
    }

    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.GJT, "collect_topic", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                Record bill_info = getBillRecord();
                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                    return bill_info.get("collect_amount");
                } else if (type.equals(WebConstant.WfExpressType.STATUS)) {
                    return bill_info.get("service_status");
                } else {
                    throw new WorkflowException("类型不支持");
                }
            }

            @Override
            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                return getSqlPara(inst_id, exclude_inst_id, record);
            }

            @Override
            protected boolean hookPass() {
                return service.hookPass(record);
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
                        add(new WfRequestObj(WebConstant.MajorBizType.GJT, "collect_topic", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                Record bill_info = getBillRecord();
                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                                    return bill_info.get("collect_amount");
                                } else if (type.equals(WebConstant.WfExpressType.STATUS)) {
                                    return bill_info.get("service_status");
                                } else {
                                    throw new WorkflowException("类型不支持");
                                }
                            }

                            @Override
                            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                                return getSqlPara(inst_id, exclude_inst_id, rec);
                            }

                            @Override
                            protected boolean hookPass() {
                                return service.hookPass(rec);
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
        return Db.getSqlPara("collect_setting.findGJTPendingList", Kv.by("map", kv));
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record obb = Db.findById("collect_topic", "id", TypeUtils.castToLong(record.get("id")));
        if (obb != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.GJT.getKey(), getCurUodp().getOrg_id(), biz_id);
    }

    /**
     * 单据详情
     */
    @Auth(hasForces = {"GJSetting", "MyWFPLAT"})
    public void detail() {
        try {
            renderOk(service.detail(getParamsToRecord(),getUserInfo()));
        } catch (BusinessException e) {
            log.error("获取归集单据详情失败！", e);
            renderFail(e);
        }

    }

    @Auth(hasForces = {"GJSetting"})
    public void morebill() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        record.set("create_by", getUserInfo().getUsr_id());
        Page<Record> page = service.morebill(page_num, page_size, record);
        renderOkPage(page);

    }

    @Auth(hasForces = {"GJSetting"})
    public void copy() {
        try {
            Record record = getParamsToRecord();
            service.copy(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("复制单归集通据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 根据状态获取账户列表
     */
    @Auth(hasForces = {"GJSetting"})
    public void accs() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            List<Record> list = service.findAccsByST(record);
            renderOk(list);
        } catch (BusinessException e) {
            log.error("获取账户列表失败！", e);
            renderFail(e);
        }

    }
}
