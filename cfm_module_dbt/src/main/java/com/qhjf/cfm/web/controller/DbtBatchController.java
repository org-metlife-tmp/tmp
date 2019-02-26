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
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.IWfRequestExtQuery;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.DbtBatchService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/11
 * @Description: 内部调部-批量制单（非直连归集）
 */
@SuppressWarnings("unused")
public class DbtBatchController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DbtBatchController.class);
    private DbtBatchService service = new DbtBatchService();

    /**
     * 列表信息
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void list() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodpInfo = null;
        DecimalFormat df = new DecimalFormat(",###,##0.00");
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal successAmount = new BigDecimal("0");

        try {
            uodpInfo = getCurUodp();
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setInnerBatchListStatus(record, "service_status");

        Page<Record> page = service.list(pageNum, pageSize, record, uodpInfo, userInfo);

        Record totalRec = new Record();
        totalRec = service.listtotal(record, userInfo, uodpInfo);

        renderOkPage(page, totalRec);
    }

    /**
     * 支付列表信息
     */
    @Auth(hasForces = {"DbtBatchPay"})
    public void paylist() {
        Record record = getRecordByParamsStrong();
        UodpInfo uodpInfo = null;
        DecimalFormat df = new DecimalFormat(",###,##0.00");
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal successAmount = new BigDecimal("0");

        try {
            uodpInfo = getCurUodp();
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setInnerBatchPayListStatus(record, "service_status");

        Page<Record> page = service.paylist(pageNum, pageSize, record, uodpInfo);

        renderOkPage(page);
    }

    @Auth(hasForces = {"DbtBatchView"})
    public void viewlist() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodpInfo = null;
        DecimalFormat df = new DecimalFormat(",###,##0.00");
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal successAmount = new BigDecimal("0");

        try {
            uodpInfo = getCurUodp();
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setInnerBatchViewListStatus(record, "service_status");

        Page<Record> page = service.viewlist(pageNum, pageSize, record, uodpInfo, userInfo);

        Record totalRec = new Record();
        totalRec = service.viewlisttotal(record, uodpInfo);
        renderOkPage(page, totalRec);
    }

    /**
     * 初始化临时信息
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void initchgtemp() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            service.initchgtemp(record, userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 插入临时表信息
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void imports() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.imports(record, userInfo);
            renderOk(record);
        } catch (DbProcessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 插入临时表信息
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void add() {
        Record record = getRecordByParamsStrong();
        try {
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = getCurUodp();
            record = service.add(record, userInfo, uodpInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 调拨批量修改
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void chg() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            UodpInfo uodpInfo = getCurUodp();
            record = service.chg(record, userInfo, uodpInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 删除调拨批量单据
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void del() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            service.del(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 临时表修改操作
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void removetemp() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.removetemp(record, userInfo);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
        renderOk(record);
    }

    /**
     * 查看批次汇总
     */
    @Auth(hasForces = {"DbtBatchView", "MyWFPLAT"})
    public void viewbill() {
        Record record = getRecordByParamsStrong();
        record = service.viewbill(record);
        renderOk(record);
    }

    /**
     * 详细列表
     */
    @Auth(hasForces = {"DbtBatchView", "MyWFPLAT"})
    public void detaillist() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.detaillist(pageNum, pageSize, record);
        Record totalRec = service.detaillisttotal(record);

        renderOkPage(page, totalRec);
    }

    /**
     * 指定附件详细列表
     */
    @Auth(hasForces = {"DbtBatchBill"})
    public void batchbillattlist() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.batchBillAttachPage(pageNum, pageSize, record);
        Record totalRec = service.batchBillAttachTotal(record);
        renderOkPage(page, totalRec);
    }

    @Auth(hasForces = {"DbtBatchView", "MyWFPLAT"})
    public void detail() {
        Record record = getRecordByParamsStrong();
        try {
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"DbtBatchPay"})
    public void cancel() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            service.cancel(record, userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"DbtBatchPay"})
    public void cancelids() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.cancelids(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"DbtBatchPay"})
    public void payconfirm() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.payconfirm(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"DbtBatchPay"})
    public void paybatchconfirm() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.paybatchconfirm(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"DbtBatchPay"})
    public void sendpaylist() {
        Record record = getRecordByParamsStrong();
        List<Long> idsList = record.get("detail_ids");
        long id = TypeUtils.castToLong(record.get("id"));
        service.sendPayList(idsList, id);
        renderOk(null);
    }


    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        WfRequestObj obj =  new WfRequestObj(WebConstant.MajorBizType.INNERDB_BATCH, "inner_batchpay_baseinfo", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                Record bill_info = getBillRecord();
                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                    return bill_info.get("total_amount");
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
                return pass(record);
            }
        };

        obj.setExtQuery(new IWfRequestExtQuery() {
            @Override
            public String getExtendS() {
                return  "ibb.biz_id,\n" +
                        "ibb.biz_name,\n" +
                        "ibb.pay_account_id,\n" +
                        "ibb.pay_account_no,\n" +
                        "ibb.pay_account_name,\n" +
                        "ibb.pay_account_cur,\n" +
                        "ibb.pay_account_bank,\n" +
                        "ibb.pay_bank_cnaps,\n" +
                        "ibb.pay_bank_prov,\n" +
                        "ibb.pay_bank_city,\n" +
                        "ibb.process_bank_type,\n" +
                        "ibb.total_num,\n" +
                        "ibb.total_amount,\n" +
                        "ibb.persist_version,\n" +
                        "ibb.batchno,\n" +
                        "ibb.payment_summary,\n" +
                        "ibb.service_status,\n" +
                        "ibb.attachment_count,\n" +
                        "ibb.pay_mode ";
            }

            @Override
            public String getExtendJ() {
                return " join inner_batchpay_baseinfo ibb on ibb.id = inst.bill_id  ";
            }
        });

        return obj;
    }

    private boolean pass(Record record) {
        long id = TypeUtils.castToLong(record.get("id"));
        //根据id查询单据信息
        Record innerRec = Db.findById("inner_batchpay_baseinfo", "id", id);

        int version = TypeUtils.castToInt(innerRec.get("persist_version"));
        int payMode = TypeUtils.castToInt(innerRec.get("pay_mode"));
        if (WebConstant.PayMode.ADDITIONAL.getKey() == payMode) {
            //线下补录审批通过后单据的状态直接变更为已成功
            Record set = new Record();
            Record where = new Record();

            set.set("service_status", WebConstant.BillStatus.COMPLETION.getKey());
            set.set("persist_version", (version + 1));

            where.set("id", id);
            where.set("persist_version", version);

            return CommonService.update("inner_batchpay_baseinfo", set, where)&&
                    CommonService.update("inner_batchpay_bus_attach_detail",
                            new Record().set("pay_status", WebConstant.PayStatus.SUCCESS.getKey()),
                            new Record().set("batchno", innerRec.getStr("batchno")));
        }
        return true;
    }

    @Override
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        Record record = getParamsToRecordStrong();
        final List<Record> wfRequestObjs = record.get("batch_list");

        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

            return new ArrayList<WfRequestObj>() {
                {
                    for (final Record rec : wfRequestObjs) {
                        add(new WfRequestObj(WebConstant.MajorBizType.INNERDB_BATCH, "inner_batchpay_baseinfo", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                Record bill_info = getBillRecord();
                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                                    return bill_info.get("total_amount");
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
        return Db.getSqlPara("batch.findInnerBatchBaseInfoPendingList", Kv.by("map", kv));
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
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record innerRec = Db.findById("inner_batchpay_baseinfo", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.INNERDB_BATCH.getKey(), getCurUodp().getOrg_id(), biz_id);
    }
}
