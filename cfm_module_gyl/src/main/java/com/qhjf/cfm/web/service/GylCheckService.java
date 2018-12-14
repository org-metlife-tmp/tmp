package com.qhjf.cfm.web.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;

public class GylCheckService {

	/**
	 * 可以核对单据的状态,默认成功
	 * @param record
	 * @param statusName
	 */
	public void setInnerTradStatus(Record record, String statusName) {
		List status = record.get(statusName);
        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
               WebConstant.CollOrPoolRunStatus.SUCCESS.getKey()
            });
        }
		
	}

	/**
	 * 
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @return
	 */
	public Page<Record> checkbillList(int pageNum, int pageSize, Record record) {
		SqlPara sqlPara = Db.getSqlPara("gyl_check.billList", Kv.by("map", record.getColumns()));
	    return Db.paginate(pageNum, pageSize, sqlPara);
	}

	
	/**
	 * 
	 * 根据单据信息,模糊查询出未核对的交易
	 * @param record
	 * @return
	 * @throws ReqDataException 
	 */
	public List<Record> checkNoCheckTradeList(Record record) throws ReqDataException {
		Long id = TypeUtils.castToLong(record.get("id"));
		Record billList = Db.findById("gyl_allocation_execute_instruction", "id", id);
		record.remove("id");
		record.set("pay_account_no", billList.get("pay_account_no"));
		record.set("recv_account_no", billList.get("recv_account_no"));
		record.set("collect_amount", billList.get("gyl_allocation_amount"));
		Date create = TypeUtils.castToDate(billList.get("create_on"));
		SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String create_on = sdf.format(create);
		record.set("create_on",create_on);
		SqlPara sqlPara = Db.getSqlPara("gyl_check.nochecktradingList", Kv.by("map", record.getColumns()));
		List<Record> find = Db.find(sqlPara);
		setBankName(find);
		return find ;
	}

	private void setBankName(List<Record> find) throws ReqDataException {
		if (null != find && find.size() > 0) {
			for (Record rec : find) {
				Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
				Record queryPayInfo = this.queryPayInfo(acc_id);
				String bank_name = TypeUtils.castToString(queryPayInfo.get("bank_name"));
				rec.set("bank_name", bank_name);
			}
		}
	}

	/**
	 * 根据付款方id查询付款方信息
	 * @param payAccountId
	 * @throws ReqDataException 
	 */
	public Record queryPayInfo(long payAccountId) throws ReqDataException {
        Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), payAccountId);
        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }
        return payRec ;
	}

	/**
	 * 根据已经核对的单据id查询交易
	 * @param record
	 * @return
	 * @throws ReqDataException 
	 */
	public List<Record> checkAlreadyTradeList(Record record) throws ReqDataException {
		List<Record> find = Db.find(Db.getSql("gyl_check.confirmTradingList"), record.getInt("id"));
		setBankName(find);
		return find;
	}

	/**
	 * 广银联
	 * @param record
	 * @param userInfo
	 * @return
	 * @throws DbProcessException 
	 * @throws ReqDataException 
	 */
	public Page<Record> confirm(Record record, UserInfo userInfo) throws DbProcessException, ReqDataException {
    	final Long billId = TypeUtils.castToLong(record.get("bill_id"));
    	
        Record innerRec = Db.findById("gyl_allocation_execute_instruction", "id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }    	
    	
    	final List<Integer> trade_id = record.get("trade_id");
    	final List<Record> records = CommonService.genConfirmRecords(trade_id,billId,userInfo);

    	
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
            	if(CommonService.update("gyl_allocation_execute_instruction", set, where)) {
            		for(Record r : records) {
            			boolean i = Db.save("gyl_allocation_checked", r);
            			boolean t = Db.update("acc_his_transaction", "id", new Record().set("id", r.getInt("trans_id")).set("is_checked", 1)
            					.set("ref_bill_id", billId).set("checked_ref", "gyl_allocation_execute_instruction"));
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
			rd.set("pay_account_org_id", userInfo.getCurUodp().getOrg_id());
        	setInnerTradStatus(rd, "gyl_allocation_status");
        	SqlPara sqlPara = Db.getSqlPara("gyl_check.billList", Kv.by("map", rd.getColumns()));
            return Db.paginate(1, 10, sqlPara);     	
        }
    }
}
