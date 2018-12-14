package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 资金下拨交易核对
 * @author GJF
 *
 */
public class PoolTradService {

	/**
	 * 未核对单据查询
	 * @param pageNum
	 * @param pageSize  
	 * @param record
	 * @return
	 */
    public Page<Record> billList(int pageNum, int pageSize, final Record record) { 
        SqlPara sqlPara = Db.getSqlPara("poolTrad.billList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 查找交易流水
     *
     * @param record
     * @return
     */
    public List<Record> tradingList(final Record record) {
    	SqlPara sqlPara = Db.getSqlPara("poolTrad.tradingList", Kv.by("map", record.getColumns()));
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
    	
        Record innerRec = Db.findById("allocation_execute_instruction", "id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }    	
    	
    	final List<Integer> tradingNo = record.get("trading_no");
    	
    	List<Record> nums = Db.find(Db.getSqlPara("dbttrad.findNumByTradingNo", Kv.by("tradingNo", tradingNo)));
    	if(nums.size() != 2) {
    		throw new ReqDataException("交易流水应保留支付各一条!");
    	}    	
    	
    	final List<Record> records = CommonService.genConfirmRecords(tradingNo,billId,userInfo);

    	
        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
    	
        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
            	
            	Record set = new Record();
            	set.set("persist_version", old_version+1);
            	set.set("is_checked", 1);
            	
            	Record where = new Record();
            	where.set("id", billId);
            	where.set("persist_version", old_version);
            	if(CommonService.update("allocation_execute_instruction", set, where)) {            	
            		for(Record r : records) {
            			boolean i = Db.save("allocation_trans_checked", r);
            			boolean t = CommonService.update("acc_his_transaction", 
            					new Record().set("is_checked", 1).set("checked_ref", "allocation_execute_instruction").set("ref_bill_id", billId), 
            					new Record().set("id", r.getInt("trans_id")));            			
            			if(!(i && t)) {
            				return false;
            			}
            		}
            		return true;
            	}else {
            		return false;
            	}
            }
        });
        if (!flag) {
            throw new DbProcessException("交易核对失败！");
        }else {
        	//返回未核对的单据列表
        	Record rd = new Record();
        	rd.set("is_checked", 0);
        	AccCommonService.setPoolTradStatus(rd, "service_status");
        	SqlPara sqlPara = Db.getSqlPara("poolTrad.billList", Kv.by("map", rd.getColumns()));
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
        SqlPara sqlPara = Db.getSqlPara("poolTrad.confirmbillList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }
    
    /**
     * 已核对单据交易查询
     *
     * @param record
     * @return
     */
    public List<Record> confirmTradingList(final Record record) {
        return Db.find(Db.getSql("poolTrad.confirmTradingList"), record.getInt("bill_no"));
    }

}
