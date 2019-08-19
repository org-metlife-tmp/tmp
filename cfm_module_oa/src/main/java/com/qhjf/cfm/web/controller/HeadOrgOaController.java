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
import com.qhjf.cfm.web.service.HeadOrgOaService;

import java.util.ArrayList;
import java.util.List;

public class HeadOrgOaController extends CFMBaseController {
    private static final Log log = LogbackLog.getLog(HeadOrgOaController.class);

    private HeadOrgOaService service = new HeadOrgOaService();

    @Auth(hasForces = {"OAHeadPay"})
    public void todolist() {
        try {
            Record record = getParamsToRecord();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            record.set("org_id", getCurUodp().getOrg_id());
            AccCommonService.setOaTodoStatus(record, "service_status");
            Page<Record> page = service.getTodoPage(page_num, page_size, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            log.error("获取总公司付款单据待处理列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"OAHeadPay"})
    public void donelist() {
        try {
            Record record = getParamsToRecord();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            record.set("org_id", getCurUodp().getOrg_id());
            AccCommonService.setOaDoneStatus(record, "service_status");
            Page<Record> page = service.getDonePage(page_num, page_size, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            log.error("获取总公司付款单据已处理列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"OAHeadPay"})
    public void chg() {
        try {
            Record record = getParamsToRecordStrong();
            Record result = service.chg(record, getUserInfo());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("修改总公司付款单据失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"OAHeadPay"})
    public void del() {
        try {
            Record record = getParamsToRecord();
            service.del(record, getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除总公司付款单据失败！", e);
            renderFail(e);
        }
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecordStrong();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            record = service.chg(record, getUserInfo());
        } else {
            throw new ReqDataException("请求数据错误！");
        }
        return record;
    }

    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return service.genWfRequestObj(record);
    }

    @Override
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        Record record = getParamsToRecordStrong();
        final List<Record> wfRequestObjs = record.get("batch_list");

        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

            return new ArrayList<WfRequestObj>() {
                {
                    for (final Record rec : wfRequestObjs) {
                        add(new WfRequestObj(WebConstant.MajorBizType.OA_HEAD_PAY, "oa_head_payment", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                Record bill_info = getBillRecord();
                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                                    return bill_info.get("payment_amount");
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
                                return service.pass(rec);
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
        return Db.getSqlPara("head_org_oa.findHeadOrgOAPendingList", Kv.by("map", kv));
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record obb = Db.findById("oa_head_payment", "id", TypeUtils.castToLong(record.get("id")));
        if (obb != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.OA_HEAD_PAY.getKey(), getCurUodp().getOrg_id(), biz_id);
    }

    /**
     * 单据详情
     */
    @Auth(hasForces = {"OAHeadPay", "MyWFPLAT"})
    public void detail() {
        try {
            renderOk(service.detail(getParamsToRecord(), getUserInfo()));
        } catch (BusinessException e) {
            log.error("获取总公司付款单据详情失败！", e);
            renderFail(e);
        }

    }

    /**
     * @throws Exception
     * @ 支付作废
     */
    @Auth(hasForces = {"OAHeadPay"})
    public void payOff() throws Exception {
        try {
            UodpInfo uodpInfo = getCurUodp();
            UserInfo userInfo = getUserInfo();
            service.payOff(getParamsToRecord(), userInfo, uodpInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * OA总公司付款未处理导出
     */
    public void todolistexport() {
        doExport();
    }

    /**
     * OA总公司付款已处理导出
     */
    public void donelistexport() {
        doExport();
    }
}
