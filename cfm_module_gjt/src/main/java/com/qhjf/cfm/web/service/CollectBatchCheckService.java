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
 * @ 批量交易核对
 */
public class CollectBatchCheckService {

    private CollectCheckService collectCheckService = new CollectCheckService();
    private CollectCheckService collectService = new CollectCheckService();

    /**
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     * @查询已核对/未核对列表
     */
    public Page<Record> checkbillList(int pageNum, int pageSize, Record record) {
        SqlPara sqlPara = Db.getSqlPara("collect_batch_check.billList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * @param record
     * @return
     * @throws ReqDataException
     * @根据单据查询未核对交易列表
     */
    public List<Record> checkTradeList(Record record) throws ReqDataException {
        Long id = TypeUtils.castToLong(record.get("id"));
        Record billList = Db.findById("collect_batch_bus_attach_detail", "detail_id", id);
        String sql = Db.getSql("collect_batch_check.selectInfoByBatchno");
        List<Record> batchno = Db.find(sql, billList.get("batchno"));
        if (null == batchno || batchno.size() < 1) {
            throw new ReqDataException("未根据详情找到原始数据!");
        }
        record.remove("id");
        record.set("pay_account_no", billList.get("pay_account_no"));
        record.set("recv_account_no", batchno.get(0).get("recv_account_no"));
        record.set("collect_amount", billList.get("payment_amount"));
        Date create = TypeUtils.castToDate(batchno.get(0).get("apply_on"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String apply_on = sdf.format(create);
        record.set("apply_on", apply_on);
        SqlPara sqlPara = Db.getSqlPara("collect_batch_check.nochecktradingList", Kv.by("map", record.getColumns()));
        List<Record> find = Db.find(sqlPara);
        if (null != find && find.size() > 0) {
            for (Record rec : find) {
                Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
                Record queryPayInfo = collectCheckService.queryPayInfo(acc_id);
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
     * @根据单据查询已核对交易列表
     */
    public List<Record> checkAlreadyTradeList(Record record) throws ReqDataException {
        List<Record> find = Db.find(Db.getSql("collect_batch_check.confirmTradingList"), record.getInt("id"));
        if (null != find && find.size() > 0) {
            for (Record rec : find) {
                Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
                Record queryPayInfo = collectCheckService.queryPayInfo(acc_id);
                String bank_name = TypeUtils.castToString(queryPayInfo.get("bank_name"));
                rec.set("bank_name", bank_name);
            }
        }
        return find;
    }

    /**
     * 确认核对
     *
     * @param record
     * @param userInfo
     * @return
     * @throws Exception
     */
    public Page<Record> confirm(Record record, UserInfo userInfo) throws Exception {
        final Long billId = TypeUtils.castToLong(record.get("bill_id"));
        Record innerRec = Db.findById("collect_batch_bus_attach_detail", "detail_id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }
        final List<Integer> trade_id = record.get("trade_id");
        if (trade_id.size() != 2) {
            throw new ReqDataException("交易流水应保留支付各一条!");
        }
        List<Record> nums = Db.find(Db.getSqlPara("dbttrad.findNumByTradingNo", Kv.by("tradingNo", trade_id)));
        if (nums.size() != 2) {
            throw new ReqDataException("交易流水应保留支付各一条!");
        }
        final List<Record> records = CommonService.genConfirmRecords(trade_id, billId, userInfo);

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));

        final Map<Integer, Date> tradMap = CommonService.getPeriod(trade_id);//key= transid , value=所属结账日的年月

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                set.set("persist_version", old_version + 1);
                set.set("is_checked", 1);
                Record where = new Record();
                where.set("detail_id", billId);
                where.set("persist_version", old_version);
                if (CommonService.update("collect_batch_bus_attach_detail", set, where)) {
                    for (Record r : records) {
                        boolean i = Db.save("collect_batch_trans_checked", r);
                        boolean t = Db.update("acc_his_transaction", "id", new Record().set("id", r.getInt("trans_id")).set("is_checked", 1)
                                .set("ref_bill_id", billId).set("checked_ref", "collect_batch_bus_attach_detail"));
                        if (!(i && t)) {
                            return false;
                        }
                    }

                    try {
                        //生成凭证信息
                        CheckVoucherService.sunVoucherData(trade_id, billId, WebConstant.MajorBizType.CBB.getKey(), tradMap);
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
            //返回未核对的单据列表
            Record rd = new Record();
            rd.set("is_checked", 0);
            rd.set("org_id", userInfo.getCurUodp().getOrg_id());
            AccCommonService.setInnerBatchTradStatus(record, "service_status");
            SqlPara sqlPara = Db.getSqlPara("collect_batch_check.billList", Kv.by("map", rd.getColumns()));
            return Db.paginate(1, 10, sqlPara);
        }
    }
}
