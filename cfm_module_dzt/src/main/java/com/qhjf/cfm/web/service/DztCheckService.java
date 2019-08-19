package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;

import java.sql.SQLException;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 已勾兑查询
 */
public class DztCheckService {
	
	/**
     * 未核对单据查询
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> billList(int pageNum, int pageSize, final Record record) {
    	CommonService.processQueryKey(record, "query_key", "acc_name", "acc_no");
        SqlPara sqlPara = Db.getSqlPara("dztcheck.billList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 查找交易流水
     *
     * @param record
     * @return
     */
    public List<Record> tradingList(final Record record) {
    	String month = record.getStr("month");
    	if(month != null && month.length() == 1){
    		month = "0"+month;
    	}
    	StringBuffer createOn = new StringBuffer(record.getStr("year")).append("-").append(month).append("-").append("01");
    	record.set("create_on", createOn.toString());
        SqlPara sqlPara = Db.getSqlPara("dztcheck.tradingList", Kv.by("map", record.getColumns()));
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
    public Page<Record> confirm(final Record record) throws BusinessException {
    	final Long billId = TypeUtils.castToLong((record.get("bill_no")));
        Record innerRec = Db.findById("check_voucher_initdata_item", "id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        final List<Integer> tradingIds = record.get("trading_no");
        if (tradingIds.size() != 1) {
            throw new ReqDataException("只可以选择一条付款交易记录进行核对!");
        }

        // 进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                set.set("is_checked", 1);
                Record where = new Record();
                where.set("id", billId);
                if (CommonService.update("check_voucher_initdata_item", set, where)) {
                	Db.update("acc_his_transaction", "id",
                            new Record().set("id", tradingIds.get(0)).set("is_checked", 1)
                                    .set("ref_bill_id", billId).set("checked_ref", "check_voucher_initdata_item"));

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
            SqlPara sqlPara = Db.getSqlPara("dztcheck.billList", Kv.by("map", rd.getColumns()));
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
    	CommonService.processQueryKey(record, "query_key", "acc_name", "acc_no");
        SqlPara sqlPara = Db.getSqlPara("dztcheck.billList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 已核对单据交易查询
     *
     * @param record
     * @return
     */
    public List<Record> confirmTradingList(final Record record) {
        return Db.find(Db.getSql("dztcheck.confirmTradingList"), record.getInt("bill_no"));
    }
}
