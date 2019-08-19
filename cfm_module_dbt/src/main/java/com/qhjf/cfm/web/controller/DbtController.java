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
import com.qhjf.cfm.web.IWfRequestExtQuery;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.DbtService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9
 * @Description: 调拨通
 */
@SuppressWarnings("unused")
public class DbtController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DbtController.class);
    private DbtService service = new DbtService();


    /**
     * 更多列表
     * 查询自己的单据信息列表
     */
    @Auth(hasForces = {"DbtMkBill"})
    public void morelist() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setInnerMoreListStatus(record, "service_status");

        Page<Record> page = service.morelist(pageNum, pageSize, record, userInfo);
        //查询所有单据总金额
        Record totalRec = service.morelisttotal(record, userInfo);

        renderOkPage(page, totalRec);
    }

    /**
     * 查看列表
     * 查询所有单据信息列表
     */
    @Auth(hasForces = {"DbtViewBill"})
    public void detaillist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setInnerDetailListStatus(record, "service_status");

        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }

        Page<Record> page = service.detallist(pageNum, pageSize, record, uodpInfo);
        //查询所有单据总金额
        Record totalRec = service.detaillisttotal(record, uodpInfo);
        renderOkPage(page, totalRec);
    }

    /**
     * 支付列表
     * 查询本机构所有单据信息列表
     */
    @Auth(hasForces = {"DbtPayBill"})
    public void paylist() {
        try {
            Record record = getRecordByParamsStrong();
            UodpInfo uodpInfo = getCurUodp();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);

            AccCommonService.setInnerPayListStatus(record, "service_status");

            Page<Record> page = service.paylist(pageNum, pageSize, record, uodpInfo);
            //查询所有单据总金额
            Record totalRec = service.paylisttotal(record, uodpInfo);
            renderOkPage(page, totalRec);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 新增单据
     */
    @Auth(hasForces = {"DbtMkBill"})
    public void add() {
        try {
            Record record = getRecordByParamsStrong();
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
     * 修改单据
     */
    @Auth(hasForces = {"DbtMkBill"})
    public void chg() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = getCurUodp();
            record = service.chg(record, userInfo, uodpInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 查看单据详情
     */
    @Auth(hasForces = {"DbtMkBill", "MyWFPLAT"})
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
     * 删除单据
     */
    @Auth(hasForces = {"DbtMkBill"})
    public void del() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            service.del(record, userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 单据发送
     */
    @Auth(hasForces = {"DbtPayBill"})
    public void send() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            service.send(record, userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 单据作废
     */
    @Auth(hasForces = {"DbtPayBill"})
    public void cancel() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            service.cancel(record, userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 付款方账户列表查询（本级以及下级）
     */
    @Auth(hasForces = {"DbtMkBill"})
    public void payacclist() {
        try {
            Record record = getRecordByParamsStrong();
            UodpInfo uodpInfo = getCurUodp();

            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);

            Page<Record> page = service.payacclist(pageNum, pageSize, record, uodpInfo);
            renderOkPage(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 收款方账户查询（本级以及下级）
     */
    @Auth(hasForces = {"DbtMkBill"})
    public void recvacclist() {
        try {
            Record record = getRecordByParamsStrong();
            UodpInfo uodpInfo = getCurUodp();

            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);

            Page<Record> page = service.recvacclist(pageNum, pageSize, record, uodpInfo);
            renderOkPage(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"DbtPayBill"})
    public void payconfirm() {
        Record record = getRecordByParamsStrong();
        try {
            service.payconfirm(record);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        WfRequestObj obj =  new WfRequestObj(WebConstant.MajorBizType.INNERDB, "inner_db_payment", record) {
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
            protected boolean hookPass() {
                return pass(record);
            }
        };
        obj.setExtQuery(new IWfRequestExtQuery() {
            @Override
            public String getExtendS() {
                return  "dbt.pay_account_id,\n" +
                        "dbt.pay_account_no,\n" +
                        "dbt.pay_account_name,\n" +
                        "dbt.pay_account_cur,\n" +
                        "dbt.pay_account_bank,\n" +
                        "dbt.pay_bank_cnaps,\n" +
                        "dbt.pay_bank_prov,\n" +
                        "dbt.pay_bank_city,\n" +
                        "dbt.recv_account_id,\n" +
                        "dbt.recv_account_no,\n" +
                        "dbt.recv_account_name,\n" +
                        "dbt.recv_account_cur,\n" +
                        "dbt.recv_account_bank,\n" +
                        "dbt.recv_bank_cnaps,\n" +
                        "dbt.recv_bank_prov,\n" +
                        "dbt.recv_bank_city,\n" +
                        "dbt.payment_amount,\n" +
                        "dbt.pay_mode,\n" +
                        "dbt.payment_type,\n" +
                        "dbt.payment_summary,\n" +
                        "dbt.service_status,\n" +
                        "dbt.service_serial_number ";
            }

            @Override
            public String getExtendJ() {
                return " join inner_db_payment dbt on dbt.id = inst.bill_id ";
            }
        });
        return obj;
    }

    private boolean pass(Record record) {
        long id = TypeUtils.castToLong(record.get("id"));
        //根据id查询单据信息
        Record innerRec = Db.findById("inner_db_payment", "id", id);

        int version = TypeUtils.castToInt(innerRec.get("persist_version"));
        int payMode = TypeUtils.castToInt(innerRec.get("pay_mode"));
        if (WebConstant.PayMode.ADDITIONAL.getKey() == payMode) {
            //线下补录审批通过后单据的状态直接变更为已成功
            Record set = new Record();
            Record where = new Record();

            set.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
            set.set("persist_version", (version + 1));

            where.set("id", id);
            where.set("persist_version", version);

            return CommonService.update("inner_db_payment", set, where);
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
                        add(new WfRequestObj(WebConstant.MajorBizType.INNERDB, "inner_db_payment", rec) {
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
                                return pass(rec);
                            }
                        });
                    }
                }
            };

        }

        throw new WorkflowException("没有可操作的单据!");
    }

    private SqlPara getSqlPara(Long[] inst_id, Long[] exclude_inst_id, Record record) {
        final Kv kv = Kv.create();
        if (inst_id != null) {
            kv.set("in", new Record().set("instIds", inst_id));
        }
        if (exclude_inst_id != null) {
            kv.set("notin", new Record().set("excludeInstIds", exclude_inst_id));
        }
        kv.set("biz_type", record.get("biz_type"));
        return Db.getSqlPara("nbdb.findInnerPayMentPendingList", Kv.by("map", kv));
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
        Record innerRec = Db.findById("inner_db_payment", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.INNERDB.getKey(), getCurUodp().getOrg_id(), biz_id);
    }

    @Auth(hasForces = {"DbtPayBill"})
    public void sendPayList() {
        Record record = getParamsToRecord();
        List<Object> ids = record.get("ids");
        Long[] info_ids = null;
        if (ids != null && ids.size() > 0) {
            info_ids = new Long[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                info_ids[i] = TypeUtils.castToLong(ids.get(i));
            }
        }
        service.sendPayList(info_ids);
        renderOk(null);
    }

}
