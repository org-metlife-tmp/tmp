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
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BranchOrgOaService;

import java.util.ArrayList;
import java.util.List;

public class BranchOrgOaController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(BranchOrgOaController.class);


    private static final TableDataCacheUtil util = TableDataCacheUtil.getInstance();


    private BranchOrgOaService service = new BranchOrgOaService();

    /**
     * 分公司转账未处理列表
     *
     * @throws ReqDataException
     */
    @Auth(hasForces = {"OABranchPay"})
    public void oaTodoList() throws ReqDataException {
        log.info("========OA分公司付款未处理列表模块");
        Record record = getRecordByParamsStrong();
        String sql = Db.getSql("curyet.findCurrentBal");
		List<Record> find = Db.find(sql, "123123");
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        record.set("org_id", getCurUodp().getOrg_id());
        Page<Record> page = service.getTodoList(record, pageNum, pageSize);
        renderOkPage(page);
    }


    /**
     * 分公司转账已处理列表
     *
     * @throws ReqDataException
     */
    @Auth(hasForces = {"OABranchPay"})
    public void oaDoneList() throws ReqDataException {
        log.info("========OA分公司付款已处理列表模块");
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        record.set("org_id", getCurUodp().getOrg_id());
        Page<Record> page = service.getDoneList(record, pageNum, pageSize);
        renderOkPage(page);
    }


    /**
     * 根据登录用户的公司,展示出中间账户列表
     */
    @Auth(hasForces = {"OABranchPay"})
    public void accListByOrg() {
        log.info("========根据登录用户的org_id获取可选的账户列表");
        Record record = getRecordByParamsStrong();
        try {
            List<Record> list = service.normallist(record);
            renderOk(list);
        } catch (ReqDataException e) {
            log.info("获取账户列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 根据中间账户的银行大类 bank_type,在资金池内找到转账账户
     */
    @Auth(hasForces = {"OABranchPay", "OAHeadPay"})
    public void poolAccListByBankType() {
        log.info("========根据中间账户的bank_type获取可选的转账账户列表");
        Record record = getRecordByParamsStrong();
        try {
            Record list = service.poolAccList(record);
            renderOk(list);
        } catch (ReqDataException e) {
            log.info("获取账户列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 保存 ,入库oa_branch_payment_item 2条数据
     *
     * @return
     * @throws ReqDataException
     */
    @Auth(hasForces = {"OABranchPay"})
    public void chgBranchPayment() {

        log.info("=============进入保存模块");
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            Record chgBranch = service.chgBranch(record, userInfo);
            renderOk(chgBranch);
        } catch (ReqDataException | DbProcessException e) {
            renderFail(e);
        }
    }

    /**
     * 详情
     *
     * @return
     * @throws ReqDataException
     */
    @Auth(hasForces = {"OABranchPay", "MyWFPLAT"})
    public void detail() {
        log.info("=============进入分公司付款详情模块");
        Record record = getRecordByParamsStrong();
        try {
            Record detail = service.detail(record,getUserInfo());
            renderOk(detail);
        } catch (BusinessException e) {
            renderFail(e);
        }
    }


    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        Record chgBranch = service.chgBranch(record, userInfo);
        return chgBranch;
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record innerRec = Db.findById("oa_branch_payment", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        Record bill = record.get("brand_payment");
        log.debug("orgId=" + bill.getLong("org_id"));
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.OA_BRANCH_PAY.getKey(), TypeUtils.castToLong(bill.getLong("org_id")), biz_id);
    }


    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.OA_BRANCH_PAY, "oa_branch_payment", record) {
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
                return service.pass(record);
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
                        add(new WfRequestObj(WebConstant.MajorBizType.OA_BRANCH_PAY, "oa_branch_payment", rec) {
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
        return Db.getSqlPara("branch_org_oa.findPendingList", Kv.by("map", kv));
    }

    /**
     * @throws Exception
     * @ 支付作废
     */
    @Auth(hasForces = {"OABranchPay"})
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
     * 未处理列表导出
     */
    public void todolistexport() {
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 已处理列表导出
     */
    public void donelistexport() {
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
