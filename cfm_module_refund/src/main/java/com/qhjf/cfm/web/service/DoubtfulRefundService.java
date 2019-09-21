package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @可疑退票
 * @author pc_liweibing
 *
 */
public class DoubtfulRefundService {

	private static final Log log = LogbackLog.getLog(DoubtfulRefundService.class);

	
	
	/**
	 * @可疑退票_交易列表
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @return
	 */
	public Page<Record> tradeList(int pageNum, int pageSize, Record record) {
		record.set("is_normal", 0);
        SqlPara sqlPara = Db.getSqlPara("acc_refundable_trans.RefundTradeList", Kv.by("map", record.getColumns()));
		Page<Record> page = Db.paginate(pageNum, pageSize, sqlPara);
		for(Record rc : page.getList()){
			try {
				Page<Record> billRecords = billList(1,1000, new Record().set("id", rc.get("id")));
				List<Record> billList = billRecords.getList();
				if(billList!=null && billList.size()>0){
					rc.set("billcount", billList.size());
				}else{
					rc.set("billcount", 0);
				}
			} catch (Exception e){
				e.printStackTrace();
				rc.set("billcount", 0);
			}
		}
        return page ;
	}

	/**
	 * @可疑退票_正确交易
	 * @param record
	 * @return
	 * @throws ReqDataException 
	 */
	public Page<Record> normalTrade(Record record) throws ReqDataException {
		Long id = TypeUtils.castToLong(record.get("id"));
		Record findById = Db.findById("acc_refundable_trans", "id", id);
		if(null == findById ){
			throw new ReqDataException("此条记录已被处理,请刷新页面!");
		}
		boolean deleteById = Db.deleteById("acc_refundable_trans", "id", id);
		if(!deleteById){
			throw new ReqDataException("处理失败");
		}
		Page<Record> page = this.tradeList(1, 10, new Record());
		return page;
	}

	/**
	 * @根据交易查询单据
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @throws ReqDataException 
	 * @throws WorkflowException 
	 * @throws ParseException 
	 */
	public Page<Record> billList(int pageNum, int pageSize, Record record) throws ReqDataException, WorkflowException, ParseException {
		Long id = TypeUtils.castToLong(record.get("id"));
		Record findById = Db.findById("acc_refundable_trans", "id", id);
		if(null == findById ){
			throw new ReqDataException("此条记录已被处理,请刷新页面!");
		}
		BigDecimal amount = TypeUtils.castToBigDecimal(findById.get("amount"));
		Long acc_id = TypeUtils.castToLong(findById.get("acc_id"));
		//获取交易日期的前7天
		String transTime = DateKit.toStr( TypeUtils.castToDate(findById.get("trans_date")), "yyyy-MM-dd")+ " " +
				DateKit.toStr( TypeUtils.castToDate(findById.get("trans_time")), "HH:mm:ss");
	    Date end_date = DateKit.toDate(transTime);
		Calendar instance = Calendar.getInstance();
		instance.setTime(end_date);
	    instance.add(Calendar.DAY_OF_YEAR, -7);
	    Date start_date = instance.getTime();
	    Record re = new Record();
	    re.set("start_date", DateKit.toStr(start_date,"yyyy-MM-dd HH:mm:ss"));
	    re.set("end_date", DateKit.toStr(end_date,"yyyy-MM-dd HH:mm:ss"));
	    re.set("pay_account_id", acc_id);
	    re.set("payment_amount", amount);
	    re.set("refund_flag", 0);
	    re.set("delete_flag", 0);
	    re.set("is_checked", 0);
		SqlPara sqlPara = Db.getSqlPara("refund_ticket.findBills", Kv.by("map", re.getColumns()));
        Page<Record> page = Db.paginate(pageNum, pageSize, sqlPara);
		return page;
	}

	/**
	 * @退票确认
	 * @param record
	 * @param userInfo 
	 * @return
	 * @throws ReqDataException 
	 * @throws WorkflowException 
	 */
	public void confirm(Record record, UserInfo userInfo) throws ReqDataException, WorkflowException {
		//确认人id
		final Long usr_id = userInfo.getUsr_id(); 
		WebConstant.MajorBizType biz_type = WebConstant.MajorBizType.getBizType(TypeUtils.castToInt(record.get("biz_type")));
		//获取单据id
		final Long bill_id = TypeUtils.castToLong(record.get("bill_id"));
		Long bill_persist_version = TypeUtils.castToLong(record.get("bill_persist_version"));
		//获取可疑数据id
		final Long trade_id = TypeUtils.castToLong(record.get("id"));
		Record findById = Db.findById("acc_refundable_trans", "id", trade_id);
		if(null == findById){
		   throw new ReqDataException("此条可疑交易记录不存在,请刷新页面!");
		}
		final String identifier = TypeUtils.castToString(findById.get("identifier"));

		String billTable = biz_type.getBillTableName();
		if(billTable.split(",").length > 1){
			billTable = billTable.substring(billTable.indexOf(","));
		}
		final String tableName = billTable;
		log.info("表名是["+tableName+"]");
		Record bill = Db.findById(tableName, "id", bill_id);
		if(null == bill){
			throw new ReqDataException("此单据不存在,请刷新页面!");
		}

		boolean flag = false ;
		final Record set = new Record();
		final Record where = new Record();
		set.set("refund_flag", 1);
		set.set("persist_version", bill_persist_version + 1);
		set.set("update_by", usr_id);
		set.set("update_on", new Date());
		where.set("persist_version", bill_persist_version);
		where.set("id", bill_id);
		flag = Db.tx(new IAtom(){
			  @Override
			  public boolean run() throws SQLException {
			  if(CommonService.update(tableName, set, where)){
				  log.info("=========更新成功====="+tableName+"开始更新acc_his_transaction表");
				  set.clear();
				  where.clear();
				  set.set("refund_ref", tableName);
				  set.set("refund_bill_id", bill_id);
				  where.set("identifier", identifier);
				  //更新acc_his_transaction
				  boolean update = CommonService.update("acc_his_transaction", set, where);
				  if(update){
					  log.info("=========更新成功=====acc_his_transaction,开始删除acc_refundable_trans表");
					  return Db.deleteById("acc_refundable_trans", "id", trade_id);
				  }
				  log.info("=========更新失败=====acc_his_transaction");
				  return false ;
			  }
			  log.info("=========更新失败====="+tableName);
			  return false ;
			  }
		});
		if(!flag) {
			throw new ReqDataException("退票确认失败!");
		}
	}

}
