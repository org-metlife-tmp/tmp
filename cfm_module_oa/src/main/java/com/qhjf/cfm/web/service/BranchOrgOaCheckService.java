package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author pc_liweibing
 * @ OA分公司交易核对
 */
public class BranchOrgOaCheckService {

    private BranchOrgOaService branchOrgOaService = new BranchOrgOaService();


    /**
     * branch_org_oa_check
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     * @OA分公司交易核对_单据列表
     */
    public Page<Record> checkbillList(int pageNum, int pageSize, Record record) {
        SqlPara sqlPara = Db.getSqlPara("branch_org_oa_check.billList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * @param record
     * @return
     * @throws ReqDataException
     * @OA分公司交易核对_未核对交易列表
     */
    public List<Record> checkTradeList(Record record) throws ReqDataException {
        Long id = TypeUtils.castToLong(record.get("id"));
        Record billList = Db.findById("oa_branch_payment_item", "id", id);
        record.remove("id");
        record.set("pay_account_no", billList.get("pay_account_no"));
        record.set("recv_account_no", billList.get("recv_account_no"));
        record.set("payment_amount", billList.get("payment_amount"));
        record.set("collect_amount", billList.get("payment_amount"));
        Date create = TypeUtils.castToDate(billList.get("create_on"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String create_on = sdf.format(create);
        record.set("create_on", create_on);
        Integer item_type = TypeUtils.castToInt(billList.get("item_type"));
        SqlPara sqlPara = null;
        if (1 == item_type) {
            //下拨 ,两条记录
            sqlPara = Db.getSqlPara("collect_check.nochecktradingList", Kv.by("map", record.getColumns()));
        } else if (2 == item_type) {
            //对外支付,一条记录
            sqlPara = Db.getSqlPara("zjzf.tradingList", Kv.by("map", record.getColumns()));
        } else {
            throw new ReqDataException("此条记录单据类型错误!");
        }
        List<Record> find = Db.find(sqlPara);
        if (null != find && find.size() > 0) {
            for (Record rec : find) {
                Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
                Record queryPayInfo = branchOrgOaService.queryPayInfo(acc_id);
                String bank_name = TypeUtils.castToString(queryPayInfo.get("bank_name"));
                rec.set("bank_name", bank_name);
            }
        }
        return find;
    }


    /**
     * @param record
     * @return
     * @throws ReqDataException
     * @OA分公司交易核对_已核对交易列表
     */
    public List<Record> checkAlreadyTradeList(Record record) throws ReqDataException {
        List<Record> find = Db.find(Db.getSql("branch_org_oa_check.alreaadyTradingList"), TypeUtils.castToLong(record.get("id")));
        if (null != find && find.size() > 0) {
            for (Record rec : find) {
                Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
                Record queryPayInfo = branchOrgOaService.queryPayInfo(acc_id);
                String bank_name = TypeUtils.castToString(queryPayInfo.get("bank_name"));
                rec.set("bank_name", bank_name);
            }
        }
        return find;
    }

    /**
     * @param record
     * @param userInfo
     * @return
     * @throws ReqDataException
     * @throws DbProcessException
     * @OA分公司交易核对_核对
     */
    public Page<Record> confirm(Record record, UserInfo userInfo) throws ReqDataException, DbProcessException {
        final Long billId = TypeUtils.castToLong((record.get("bill_id")));
        Record innerRec = Db.findById("oa_branch_payment_item", "id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }
        Integer item_type = TypeUtils.castToInt(innerRec.get("item_type"));
        final Long castToLong = TypeUtils.castToLong(innerRec.get("base_id"));
        final List<Integer> tradingId = record.get("trade_id");
        if (1 == item_type) {
            //下拨  2条
            if (tradingId.size() != 2) {
                throw new ReqDataException("交易流水应保留支付各一条!");
            }
            List<Record> nums = Db.find(Db.getSqlPara("dbttrad.findNumByTradingNo", Kv.by("tradingNo", tradingId)));
            if (nums.size() != 2) {
                throw new ReqDataException("交易流水应保留支付各一条!");
            }
        } else if (2 == item_type) {
            //对外支付 1条
            if (tradingId.size() != 1) {
                throw new ReqDataException("只可以选择一条付款交易记录进行核对!");
            }
        } else {
            throw new ReqDataException("此条记录单据类型错误!");
        }
        final List<Record> records = CommonService.genConfirmRecords(tradingId, billId, userInfo);


        final int old_version = TypeUtils.castToInt(record.get("persist_version"));

        final Map<Integer, Date> tradMap = CommonService.getPeriod(tradingId);//key= transid , value=所属结账日的年月

        // 进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                set.set("is_checked", 1);
                set.set("persist_version", old_version + 1);
                Record where = new Record();
                where.set("id", billId);
                where.set("persist_version", old_version);
                if (CommonService.update("oa_branch_payment_item", set, where)) {
                    for (Record r : records) {
                        boolean i = Db.save("oa_branch_payment_checked", r);
                        boolean t = Db.update("acc_his_transaction", "id",
                                new Record().set("id", r.getInt("trans_id")).set("is_checked", 1)
                                        .set("ref_bill_id", billId).set("checked_ref", "oa_branch_payment_item"));
                        if (!(i && t)) {
                            return false;
                        }
                    }

                    try {
                        //生成凭证信息
                        CheckVoucherService.sunVoucherData(tradingId, billId, WebConstant.MajorBizType.OA_BRANCH_PAY.getKey(), tradMap);
                    } catch (BusinessException e) {
                        e.printStackTrace();
                        return false;
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });
        if (!flag) {
            throw new DbProcessException("交易核对失败！");
        } else {
            // 返回未核对的单据列表
            Record rd = new Record();
            rd.set("is_checked", 0);
            rd.set("org_id", userInfo.getCurUodp().getOrg_id());
            AccCommonService.setInnerTradStatus(rd, "service_status");
            SqlPara sqlPara = Db.getSqlPara("branch_org_oa_check.billList", Kv.by("map", rd.getColumns()));
            return Db.paginate(1, 10, sqlPara);
        }
    }

}
