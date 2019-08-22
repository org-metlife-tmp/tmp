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
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.ZftService;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhangyuan
 * @Date: 2018/8/9
 * @Description: 支付通
 */
public class ZftController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(ZftController.class);
    private ZftService service = new ZftService();

    /**
     * 新增制单
     *
     * @throws DbProcessException
     */
    @Auth(hasForces = {"ZFTMkBill"})
    public void addbill() throws Exception {
        logger.info("进入新增制单接口======");
        UserInfo userInfo = getUserInfo();
        UodpInfo uodpInfo = getCurUodp();
        Record record = getParamsToRecord();
        Record insertRecord = new Record();
        Record returnRecord = new Record();
        //获取付款方id
        long payAccountId = TypeUtils.castToLong(record.get("pay_account_id"));
        //先查看是否传输付款方id
        String supplier_acc_id = TypeUtils.castToString(TypeUtils.castToLong(record.get("recv_account_id")));
        Record supplier_info = null;
        try {
            Record pay_info = service.queryPayInfo(payAccountId);
            if (null == supplier_acc_id) {
                supplier_info = service.insertSupplier(record, userInfo);
            } else {
                supplier_info = service.querySupplierInfo(supplier_acc_id, record, userInfo);
            }
            returnRecord = service.insert(uodpInfo, userInfo, record, supplier_info, insertRecord, returnRecord, pay_info);
            renderOk(returnRecord);
        } catch (BusinessException e) {
            logger.error("新增制单接口失败!!");
            renderFail(e);
        }
    }

    /**
     * 修改单据
     *
     * @throws DbProcessException
     */
    @Auth(hasForces = {"ZFTMkBill", "ZFTViewBill"})
    public void chgbill() throws Exception {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = getCurUodp();
            //先查看是否传输付款方id
            String supplier_acc_id = TypeUtils.castToString(TypeUtils.castToLong(record.get("recv_account_id")));
            Record supplier_info = null;
            if (StringUtil.isBlank(supplier_acc_id)) {
                supplier_info = service.insertSupplier(record, userInfo);
                record.set("recv_account_id", supplier_info.get("id"));
            } else {
                supplier_info = service.querySupplierInfo(supplier_acc_id, record, userInfo);
            }
            record = service.chg(record, userInfo, uodpInfo, supplier_info);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    /**
     * 删除单据
     *
     * @throws Exception
     */
    @Auth(hasForces = {"ZFTViewBill"})
    public void delbill() throws Exception {
        logger.info("进入单据删除接口======");
        try {
            UserInfo userInfo = getUserInfo();
            service.deleteBill(getParamsToRecord(), userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 更多单据
     */
    @Auth(hasForces = {"ZFTMkBill", "ZFTPayBill"})
    public void morebills() throws Exception {
        logger.info("进入更多单据入口");
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        AccCommonService.setInnerMoreListStatus(record, "service_status");
        Page<Record> page = service.morelist(pageNum, pageSize, record, userInfo);
        //查询所有单据总金额
        Record totalRec = service.morelisttotal(record, userInfo);
        if (null != page) {
            List<Record> list = page.getList();
            if (null != list && list.size() > 0) {
                for (Record re : list) {
                    Record rev_Rec = Db.findById("supplier_acc_info", "id", re.get("recv_account_id"));
                    re.set("rev_persist_version", rev_Rec.get("persist_version"));
                }
            }
        }
        renderOkPage(page, totalRec);
    }

    /**
     * 单据列表
     */

    @Auth(hasForces = {"ZFTViewBill"})
    public void bills() {
        Record record = getRecordByParamsStrong();
        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);
            AccCommonService.setInnerDetailListStatus(record, "service_status");
//        record.set("org_id", getCurUodp().getOrg_id());
            Page<Record> page = service.detallist(pageNum, pageSize, record, uodpInfo);
            //查询所有单据总金额
            Record totalRec = service.detaillisttotal(record, uodpInfo);
            renderOkPage(page, totalRec);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 单据详情
     */
    @Auth(hasForces = {"ZFTViewBill", "MyWFPLAT"})
    public void billdetail() throws Exception {
        try {
            Record record = getRecordByParamsStrong();
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 收款方账户/户名列表查询
     */
//    @Auth(hasForces = {"ZFTMkBill"})
    public void recvacclist() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        Page<Record> page = service.recvacclist(pageNum, pageSize, record);
        renderOkPage(page);
    }


    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecordStrong();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodp = getCurUodp();
        //先查看是否传输付款方id
        String supplier_acc_id = TypeUtils.castToString(record.get("recv_account_id"));
        Record supplier_info = null;
        if (StringUtil.isBlank(supplier_acc_id)) {
            try {
                supplier_info = service.insertSupplier(record, userInfo);
                record.set("recv_account_id", supplier_info.get("id"));
            } catch (BusinessException e) {
                e.printStackTrace();
                renderFail(e);
            }
        } else {
            supplier_info = service.querySupplierInfo(supplier_acc_id, record, userInfo);
        }
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            return service.chg(record, userInfo, uodp, supplier_info);
        } else {
            //获取付款方id
            long payAccountId = TypeUtils.castToLong(record.get("pay_account_id"));
            Record pay_info = service.queryPayInfo(payAccountId);
            record.remove("id");
            Record insertRecord = new Record();
            Record returnRecord = new Record();
            return service.insert(uodp, userInfo, record, supplier_info, insertRecord, returnRecord, pay_info);
        }
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record innerRec = Db.findById("outer_zf_payment", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ZFT.getKey(), getCurUodp().getOrg_id(), biz_id);
    }


    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.ZFT, "outer_zf_payment", record) {
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
                return getSqlPara(inst_id, exclude_inst_id, record);
            }

            @Override
            public boolean hookPass() {
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
                        add(new WfRequestObj(WebConstant.MajorBizType.ZFT, "outer_zf_payment", rec) {
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
                            public boolean hookPass() {
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
        return Db.getSqlPara("zjzf.findPendingList", Kv.by("map", kv));
    }

    /**
     * 支付作废
     *
     * @throws Exception
     */
    @Auth(hasForces = {"ZFTPayBill"})
    public void payOff() throws Exception {
        logger.info("进入单据删除接口======");
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

    //支付通_支付列表
    @Auth(hasForces = {"ZFTPayBill"})
    public void payList() {
        logger.info("进入支付列表接口======");
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        try {
            UodpInfo uodpInfo = getCurUodp();
            AccCommonService.setInnerPayListStatus(record, "service_status");
            //不查询下级机构的单据。
            Page<Record> page = service.detallist(pageNum, pageSize, record, uodpInfo,false);
            //查询所有单据总金额
            Record totalRec = service.detaillisttotal(record, uodpInfo,false);
            renderOkPage(page, totalRec);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    /**
     * 支付通交易核对_单据列表
     */
    @Auth(hasForces = {"ZFTCheck"})
    public void checkbillList() throws Exception {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        UodpInfo uodpInfo = getCurUodp();
        record.set("org_id", uodpInfo.getOrg_id());
        //状态为已成功的单据
        AccCommonService.setInnerTradStatus(record, "service_status");
        Page<Record> page = service.checkbillList(pageNum, pageSize, record);
        renderOkPage(page);
    }

    @Auth(hasForces = {"ZFTCheck"})
    public void sendPayList() {
        Record record = getParamsToRecordStrong();
        List<Object> ids = record.get("ids");
        Long[] idArr = null;
        if (ids != null && ids.size() > 0) {
            idArr = new Long[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                idArr[i] = TypeUtils.castToLong(ids.get(i));
            }
        }
        service.sendPayList(idArr);
        renderOk(null);
    }


    /**
     * 支付通交易核对_未核对交易列表
     *
     * @throws ReqDataException
     */
    @Auth(hasForces = {"ZFTCheck"})
    public void checkTradeList() throws ReqDataException {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.checkTradeList(record);
        renderOk(page);
    }

    /**
     * 支付通交易核对_已核对交易列表
     *
     * @throws ReqDataException
     */
    @Auth(hasForces = {"ZFTCheck"})
    public void checkAlreadyTradeList() throws ReqDataException {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.checkAlreadyTradeList(record);
        renderOk(page);
    }

    
    /**
     * 支付通交易核对_确认
     *
     * @throws ReqDataException
     */
    @Auth(hasForces = {"ZFTCheck"})
    public void confirmCheck() {
        try {
            Record record = getRecordByParamsStrong();

            UserInfo userInfo = getUserInfo();

            Page<Record> page = service.confirm(record, userInfo);
            renderOkPage(page);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"ZFTViewBill", "MyWFPLAT"})
    public void detail() {
        try {
            Record record = getRecordByParamsStrong();
            record = service.detail(record,getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    /**
     * 支付确认，用于单据支付方式为 网联 的操作
     */
    @Auth(hasForces = {"ZFTPayBill"})
    public void payok() {
        try {
            Record record = getParamsToRecord();
            service.payok(record, getUserInfo().getUsr_id());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("支付确认单据失败！", e);
            renderFail(e);
        }
    }

}

