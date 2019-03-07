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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 调拨通交易核对
 *
 * @author GJF
 */
public class DbtBatchTradService {

    /**
     * 未核对单据查询
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> billList(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("dbtbatchtrad.billList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 查找交易流水
     *
     * @param record
     * @return
     */
    public List<Record> tradingList(final Record record) {
        SqlPara sqlPara = Db.getSqlPara("dbtbatchtrad.tradingList", Kv.by("map", record.getColumns()));
        return Db.find(sqlPara);
    }

    /**
     * 交易确认
     *
     * @param record
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public Page<Record> confirm(final Record record, UserInfo userInfo) throws BusinessException {
        final Long billId = TypeUtils.castToLong(record.get("bill_no"));

        Record innerRec = Db.findById("inner_batchpay_bus_attach_detail", "detail_id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        final List<Integer> tradingNo = record.get("trading_no");
        if (tradingNo.size() != 2) {
            throw new ReqDataException("交易流水应保留支付各一条!");
        }

        List<Record> nums = Db.find(Db.getSqlPara("dbtbatchtrad.findNumByTradingNo", Kv.by("tradingNo", tradingNo)));
        if (nums.size() != 2) {
            throw new ReqDataException("交易流水应保留支付各一条!");
        }

        final List<Record> records = CommonService.genConfirmRecords(tradingNo, billId, userInfo);


        final int old_version = TypeUtils.castToInt(record.get("persist_version"));

        final Map<Integer, Date> tradMap = CommonService.getPeriod(tradingNo);//key= transid , value=所属结账日的年月

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
                if (CommonService.update("inner_batchpay_bus_attach_detail", set, where)) {
                    for (Record r : records) {
                        boolean i = Db.save("inner_batchpay_trans_checked", r);
                        boolean t = CommonService.update("acc_his_transaction",
                                new Record().set("is_checked", 1).set("checked_ref", "inner_batchpay_bus_attach_detail").set("ref_bill_id", billId),
                                new Record().set("id", r.getInt("trans_id")));
                        if (!(i && t)) {
                            return false;
                        }
                    }

                    try {
                        //生成凭证信息
                        CheckVoucherService.sunVoucherData(tradingNo, billId, WebConstant.MajorBizType.INNERDB_BATCH.getKey(), tradMap);
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
            AccCommonService.setInnerBatchTradStatus(rd, "service_status");
            SqlPara sqlPara = Db.getSqlPara("dbtbatchtrad.billList", Kv.by("map", rd.getColumns()));
            return Db.paginate(1, 10, sqlPara);
        }
    }

    /**
     * 已核对单据查询
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> confirmbillList(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("dbtbatchtrad.confirmbillList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 已核对单据交易查询
     *
     * @param record
     * @return
     */
    public List<Record> confirmTradingList(final Record record) {
        return Db.find(Db.getSql("dbtbatchtrad.confirmTradingList"), record.getInt("bill_no"));
    }

}
