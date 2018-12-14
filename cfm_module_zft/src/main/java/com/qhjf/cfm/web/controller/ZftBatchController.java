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
import com.qhjf.cfm.web.service.ZftBatchService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ZftBatchController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(ZftBatchController.class);

    private ZftBatchService service = new ZftBatchService();

    @Auth(hasForces = {"ZFTBatchView", "MyWFPLAT"})
    public void billdetaillist() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        Page<Record> page = service.billdetaillist(page_num, page_size, record);
        Record ext = service.billdetailsum(record);
        renderOkPage(page, ext);
    }

    /**
     * 单据详情
     */
    @Auth(hasForces = {"ZFTBatchView", "MyWFPLAT"})
    public void billdetail() {
        try {
            renderOk(service.billdetail(getParamsToRecord(), getUserInfo()));
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    /**
     * 批量支付-查看
     */
    @Auth(hasForces = {"ZFTBatchView"})
    public void paybatchlist() {
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
     * 更多单据
     */
    @Auth(hasForces = {"ZFTBatchView"})
    public void morebill() {
        try {
            Record record = getParamsToRecord();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);

            AccCommonService.setInnerBatchListStatus(record, "service_status");

            Page<Record> page = service.morelist(page_num, page_size, record, getUserInfo());
            Record totalRec = new Record();
            totalRec = service.morelisttotal(record, getUserInfo(), getCurUodp());

            renderOkPage(page, totalRec);
        } catch (BusinessException e) {
            renderFail(e);
        }

    }

    /**
     * 删除单据
     */
    @Auth(hasForces = {"ZFTBatchView"})
    public void delbill() {
        try {
            Record record = getParamsToRecord();
            service.delbill(record, getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 获取excel详细信息
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void attclist() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        Page<Record> page = service.attclist(page_num, page_size, record);
        Record ext = service.getCollect(record);
        renderOkPage(page, ext);
    }

    /**
     * 预处理修改单据,点击修改按钮调用
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void prechgbill() {
        try {
            Record record = getParamsToRecord();
            Record result = service.prechgbill(record, getUserInfo());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("预处理修改批量支付单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 修改单据
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void chgbill() {
        try {
            Record record = getParamsToRecord();
            Record result = service.chgbill(record, getUserInfo());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("修改批量支付单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 新增单据
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void addbill() {
        try {
            Record record = getParamsToRecord();
            Record result = service.addbill(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("新增批量支付单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 只处理上传环节使用
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void addbillexcel() {
        try {
            Record record = getParamsToRecord();
            Record result = service.addbillexcel(record, getUserInfo().getUsr_id());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("新增批量支付文件失败！", e);
            renderFail(e);
        }
    }

    /**
     * 标记删除数据-只处理上传环节使用
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void delbillexcel() {
        try {
            Record record = getParamsToRecord();
            Record result = service.delbillexcel(record);
            renderOk(result);
        } catch (BusinessException e) {
            log.error("删除批量支付文件失败！", e);
            renderFail(e);
        }
    }

    /**
     * 指定附件详细列表
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void batchbillattlist() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.batchBillAttachPage(pageNum, pageSize, record);
        Record totalRec = service.batchBillAttachTotal(record);
        renderOkPage(page, totalRec);
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecord();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            record = service.chgbill(record, getUserInfo());
        } else {
            record.remove("id");
            record = service.addbill(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
        }
        return record;
    }

    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.OBP, "outer_batchpay_baseinfo", record) {
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
                //审批完结 是否需要清空 临时表数据。TODO
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
                        add(new WfRequestObj(WebConstant.MajorBizType.OBP, "outer_batchpay_baseinfo", rec) {
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
                                //审批完结 是否需要清空 临时表数据。TODO
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
        return Db.getSqlPara("zftbatch.findOBPPendingList", Kv.by("map", kv));
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record obb = Db.findById("outer_batchpay_baseinfo", "id", TypeUtils.castToLong(record.get("id")));
        if (obb != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.OBP.getKey(), getCurUodp().getOrg_id(), biz_id);
    }

    /**
     * 单据详情
     */
    @Auth(hasForces = {"ZFTBatchView", "MyWFPLAT"})
    public void detail() {
        try {
            renderOk(service.billdetail(getParamsToRecord(), getUserInfo()));
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    /**
     * 支付批量作废
     *
     * @throws Exception
     */
    @Auth(hasForces = {"ZFTBatchPay"})
    public void payOff() {
        try {
            service.payOff(getParamsToRecord(), getUserInfo().getUsr_id());
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    @Auth(hasForces = {"ZFTBatchPay"})
    public void sendpaylist() {
        Record record = getParamsToRecordStrong();
        long id = TypeUtils.castToLong(record.get("id"));
        List<Long> idsList = record.get("ids");
        service.sendPayList(idsList, id);
        renderOk(null);
    }


    /**
     * 支付明细单笔作废
     *
     * @throws Exception
     */
    @Auth(hasForces = {"ZFTBatchPay"})
    public void payOneOff() {
        try {
            service.payOneOff(getParamsToRecord(), getUserInfo().getUsr_id());
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    //支付通_支付列表
    @Auth(hasForces = {"ZFTBatchPay"})
    public void payList() throws BusinessException {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        AccCommonService.setInnerBatchPayListStatus(record, "service_status");

        //只是查询本公司的单据
        record.set("org_id", getCurUodp().getOrg_id());

        Page<Record> page = service.detallist(pageNum, pageSize, record);
        //查询所有单据总金额
        Record totalRec = service.detaillisttotal(record);
        renderOkPage(page, totalRec);
    }


    //批量支付退票_单据列表
    //@Auth(hasForces = {"ZFTBatchPay"})
    public void billrefund() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page;
        try {
            page = service.billRefund(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    //批量支付通退票处理_根据单据查询交易详情
    //@Auth(hasForces = {"ZFTViewBill","MyWFPLAT"})
    public void tradeDetailByBill() {
        try {
            Record record = getRecordByParamsStrong();
            List<Record> page = service.tradeDetailByBill(record);
            renderOk(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    //批量支付通退票处理_点击退票
    //@Auth(hasForces = {"ZFTViewBill","MyWFPLAT"})
    public void confirmRefund() {
        Record record = getRecordByParamsStrong();
        service.confirmRefund(record);
        renderOk(null);
    }

    @Auth(hasForces = {"ZFTBatchPay"})
    public void payok() {
        try {
            service.payok(getParamsToRecord(), getUserInfo().getUsr_id());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("批量支付确认单据失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"ZFTBatchPay"})
    public void payokbyids() {
        try {
            service.payokbyids(getParamsToRecord(), getUserInfo().getUsr_id());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("批量支付明细确认失败！", e);
            renderFail(e);
        }
    }
}
