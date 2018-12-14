package com.qhjf.cfm.web.service;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

/**
 * 已退票查看列表
 *
 * @author pc_liweibing
 */
public class RefundViewService {

    private static final Log log = LogbackLog.getLog(RefundViewService.class);


    /**
     * @param record
     * @param org_id
     * @param pageNum
     * @param pageSize
     * @return
     * @throws ReqDataException
     * @查询历史交易表中已退票交易
     */
    public Page<Record> alreadyRefundTradeList(Record record, Long org_id, int pageNum, int pageSize) throws ReqDataException {

        log.info("============已退票交易查询,登录org_id" + org_id);
        //Db.find("refund_view.alreadyRefundTradeList");
        Record re = new Record();
        re.set("org_id", org_id);
        List<String> accIds = new ArrayList<String>();
        //未传输账户号,查出当前登录人所在公司的所有账户
        SqlPara sqlPara = Db.getSqlPara("acc_comm.normallist", re.getColumns());
        List<Record> accounds = Db.find(sqlPara);
        if (null == accounds || accounds.size() < 1) {
            throw new ReqDataException("此登录用户所在公司未维护账户");
        }
        for (Record r : accounds) {
            accIds.add(TypeUtils.castToString(r.get("acc_id")));
        }
        record.set("acc_id", accIds);
        SqlPara sp = Db.getSqlPara("refund_view.alreadyRefundTradeList", Kv.by("map", record.getColumns()));
        log.info("=====================SqlPara====" + sp);
        Page<Record> page = Db.paginate(pageNum, pageSize, sp);
        for (Record rec : page.getList()) {
            String bankType = TypeUtils.castToString(rec.get("bank_type"));
            Record bankRec = Db.findById("const_bank_type", "code", bankType);
            rec.set("bank_name", TypeUtils.castToString(bankRec.get("name")));
        }
        return page;
    }

    /**
     * @param record
     * @return
     * @throws ReqDataException
     * @退票_单据列表
     */
    public Record billList(Record record) throws ReqDataException {
        Long tradeId = TypeUtils.castToLong(record.get("trade_id"));
        log.info("===============交易的id" + tradeId);
        Record trade = Db.findById("acc_his_transaction", "id", tradeId);
        if (null == trade) {
            log.error("==========此条交易记录在表中数据不存在");
            throw new ReqDataException("此交易已失效,请刷新页面");
        }
        Long billId = TypeUtils.castToLong(trade.get("refund_bill_id"));
        String billTable1 = TypeUtils.castToString(trade.get("refund_ref"));
        log.info("===========单据Id==" + billId + "==========单据表==" + billTable1);
        // 只可能存在6张表
        // inner_db_payment  outer_zf_payment  oa_head_payment  oa_branch_payment_item
        // outer_batchpay_bus_attach_detail  inner_batchpay_bus_attach_detail
        Record findById = null;
        if ("inner_db_payment,outer_zf_payment,oa_head_payment,oa_branch_payment_item".contains(billTable1)) {

            log.info("====================单表查询");
            findById = Db.findById(billTable1, "id", billId);
        } else if ("outer_batchpay_bus_attach_detail,inner_batchpay_bus_attach_detail".contains(billTable1)) {

            log.info("批量==============主表与详情表联表查询");
            String billTable = "inner_batchpay_baseinfo";
            if ("outer_batchpay_bus_attach_detail".equals(billTable1)) {
                billTable = "outer_batchpay_baseinfo";
            }
            log.info("============主表===" + billTable1);
            Record red = new Record();
            red.set("table_name", billTable).set("table_name1", billTable1).set("detail_id", billId);
            SqlPara sqlPara = Db.getSqlPara("refund_view.findBill", Kv.by("map", red));
            log.info("============sqlPara====" + sqlPara);
            findById = Db.findFirst(sqlPara);
        } else {
            throw new ReqDataException("==========此条数据在交易表内存储不符合标准");
        }
        return findById;
    }


}
