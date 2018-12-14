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
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.SktService;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 收款通
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class SktController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(SktController.class);
    private SktService service = new SktService();

    /**
     * 新增制单
     *
     * @throws DbProcessException
     */
    @Auth(hasForces = {"SKTMkBill"})
    public void addbill() throws Exception {
        logger.info("进入新增制单接口======");
        UserInfo userInfo = getUserInfo();
        UodpInfo uodpInfo = getCurUodp();
        Record record = getParamsToRecord();
        Record insertRecord = new Record();
        Record returnRecord = new Record();
        // 获取收款方id
        long supplier_acc_id = TypeUtils.castToLong(record.get("recv_account_id"));
        // 先查看是否传输付款方id
        String payAccountId = TypeUtils.castToString(TypeUtils.castToLong(record.get("pay_account_id")));
        Record payInfo = null;
        try {
            Record suppliInfo = service.queryPayInfo(supplier_acc_id);
            if (null == payAccountId) {
                payInfo = service.insertSupplier(record, userInfo);
            } else {
                payInfo = service.querySupplierInfo(payAccountId, record, userInfo);
            }
            returnRecord = service.insert(uodpInfo, userInfo, record, suppliInfo, insertRecord, returnRecord, payInfo);
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
    @Auth(hasForces = {"SKTMkBill", "SKTViewBill"})
    public void chgbill() throws Exception {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = getCurUodp();
            // 先查看是否传输付款方id
            String payAccountId = TypeUtils.castToString(TypeUtils.castToLong(record.get("pay_account_id")));
            Record payInfo = null;
            if (StringUtil.isBlank(payAccountId)) {
                payInfo = service.insertSupplier(record, userInfo);
            } else {
                payInfo = service.querySupplierInfo(payAccountId, record, userInfo);
            }
            record = service.chg(record, userInfo, uodpInfo, payInfo);
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
    @Auth(hasForces = {"SKTViewBill"})
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
    @Auth(hasForces = {"SKTMkBill"})
    public void morebills() throws Exception {
        logger.info("进入更多单据入口");
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        AccCommonService.setSktMoreListStatus(record, "service_status");
        Page<Record> page = service.morelist(pageNum, pageSize, record, userInfo);
        // 查询所有单据总金额
        Record totalRec = service.morelisttotal(record, userInfo);
        if (null != page) {
            List<Record> list = page.getList();
            if (null != list && list.size() > 0) {
                for (Record re : list) {
                    Record rev_Rec = Db.findById("supplier_acc_info", "id", re.get("pay_account_id"));
                    re.set("pay_persist_version", rev_Rec.get("persist_version"));
                }
            }
        }
        renderOkPage(page, totalRec);
    }

    /**
     * 单据列表
     */
    @Auth(hasForces = {"SKTViewBill"})
    public void bills() throws Exception {
        Record record = getRecordByParamsStrong();
        UodpInfo uodpInfo = getCurUodp();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        AccCommonService.setSktListStatus(record, "service_status");
        Page<Record> page = service.detallist(pageNum, pageSize, record, uodpInfo);
        // 查询所有单据总金额
        Record totalRec = service.detaillisttotal(record, uodpInfo);
        renderOkPage(page, totalRec);
    }

    /**
     * 单据详情
     */
    @Auth(hasForces = {"SKTViewBill", "MyWFPLAT"})
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

    public void chgservicestatus() {
        Record record = getRecordByParamsStrong();
        try {
            service.chgServiceStatus(record);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecordStrong();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodp = getCurUodp();
        //先查看是否传输付款方id
        String payAccountId = TypeUtils.castToString(record.get("pay_account_id"));
        Record payIfo = null;
        if (StringUtil.isBlank(payAccountId)) {
            try {
                payIfo = service.insertSupplier(record, userInfo);
            } catch (BusinessException e) {
                e.printStackTrace();
                renderFail(e);
            }
        } else {
            payIfo = service.querySupplierInfo(payAccountId, record, userInfo);
        }
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            return service.chg(record, userInfo, uodp, payIfo);
        } else {
            //获取收款方id
            long supplier_acc_id = TypeUtils.castToLong(record.get("recv_account_id"));
            Record suppliInfo = service.queryPayInfo(supplier_acc_id);
            record.remove("id");
            Record insertRecord = new Record();
            Record returnRecord = new Record();
            return service.insert(uodp, userInfo, record, suppliInfo, insertRecord, returnRecord, payIfo);
        }
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record innerRec = Db.findById("outer_sk_receipts", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.SKT.getKey(), getCurUodp().getOrg_id(), biz_id);
    }


    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.SKT, "outer_sk_receipts", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                Record bill_info = getBillRecord();
                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                    return bill_info.get("receipts_amount");
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
                        add(new WfRequestObj(WebConstant.MajorBizType.SKT, "outer_sk_receipts", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                Record bill_info = getBillRecord();
                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                                    return bill_info.get("receipts_amount");
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
        return Db.getSqlPara("skzf.findPendingList", Kv.by("map", kv));
    }
}
