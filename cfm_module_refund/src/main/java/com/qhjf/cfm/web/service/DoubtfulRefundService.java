package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

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
		Integer biz_type = TypeUtils.castToInt(record.get("biz_type"));
		String tableName = WebConstant.MajorBizType.getBizType(biz_type).getBillTableName();
		//判断几张表,从而知道是单笔还是批次
		Boolean flag =  false ;
		if(tableName.split(",").length  == 2){
			flag = true ;
		}
		BigDecimal amount = TypeUtils.castToBigDecimal(findById.get("amount"));
		Long acc_id = TypeUtils.castToLong(findById.get("acc_id"));
		//获取交易日期的前7天
	    Date date = TypeUtils.castToDate(findById.get("trans_date"));
	    Date hour = TypeUtils.castToDate(findById.get("trans_time"));
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm:ss");
	    SimpleDateFormat sdfTrans = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String transTime = sdfDate.format(date) + " " + sdfHour.format(hour) ;
	    Calendar instance = Calendar.getInstance();
	    Date end_date = sdfTrans.parse(transTime);
	    instance.setTime(sdfTrans.parse(transTime));
	    instance.add(Calendar.DAY_OF_YEAR, -7);
	    Date start_date = instance.getTime();
	    //组装查询参数
	    Record re = new Record();
	    // 区分直连单据表与其他单据表的差别
	    if(12 == biz_type){
	    	//直连归集 collect_status
	    	re.set("collect_amount", amount);
	    	re.set("collect_status", new int[]{
                    WebConstant.CollOrPoolRunStatus.SUCCESS.getKey()
            });
	    }else{	    	
	    	re.set("payment_amount", amount);
	    	re.set("delete_flag", 0);
	    }
	    if("8,9,15,20,21".contains(String.valueOf(biz_type))){
	    	re.set("service_status", new int[]{
	                    WebConstant.BillStatus.SUCCESS.getKey(),
	                    WebConstant.BillStatus.COMPLETION.getKey()
	            });
	    }
	    if("10,11".contains(String.valueOf(biz_type))){
	    	re.set("pay_status", new int[]{
                    WebConstant.PayStatus.SUCCESS.getKey()
            });	
	    }
	    if("19".equals(String.valueOf(biz_type))){
	    	re.set("pay_status", new int[]{
                    WebConstant.CollOrPoolRunStatus.SUCCESS.getKey()
            });	
	    }
	    //re.set("start_date", start_date);
	    //re.set("end_date", end_date);
	    re.set("start_date", DateKit.toStr(start_date,"yyyy-MM-dd HH:mm:ss"));
	    re.set("end_date", DateKit.toStr(end_date,"yyyy-MM-dd HH:mm:ss"));
	    re.set("pay_account_id", acc_id);
	    re.set("refund_flag", 0);
	    Kv kv = Kv.create();
        kv.set("where", re.getColumns());
        SqlPara sqlPara = null ;
        if(flag){
        	kv.set("table_name", tableName.split(",")[0]);
        	kv.set("table_name1", tableName.split(",")[1]);
        	if(19 == biz_type) {
        		sqlPara = Db.getSqlPara("refund_ticket.findCollectBatchBill", Kv.by("map", kv));
        	}else {       		
        		sqlPara = Db.getSqlPara("refund_ticket.findBatchBill", Kv.by("map", kv));
        	}
        }else{
        	kv.set("table_name", tableName);      	
        	sqlPara = Db.getSqlPara("refund_ticket.findBill", Kv.by("map", kv));
        }
        Page<Record> page = Db.paginate(pageNum, pageSize, sqlPara);
		return page;
	}

	/**
	 * @退票确认
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @param userInfo 
	 * @param curUodp 
	 * @return
	 * @throws ReqDataException 
	 * @throws WorkflowException 
	 */
	public Page<Record> confirm(int pageNum, int pageSize, Record record, UserInfo userInfo) throws ReqDataException, WorkflowException {
		//确认人id
		final Long usr_id = userInfo.getUsr_id(); 
		Integer biz_type = TypeUtils.castToInt(record.get("biz_type"));
		final Long bill_id = TypeUtils.castToLong(record.get("bill_id"));
		final Long trade_id = TypeUtils.castToLong(record.get("id"));
		Long bill_persist_version = TypeUtils.castToLong(record.get("bill_persist_version"));
		Record findById = Db.findById("acc_refundable_trans", "id", trade_id);
		if(null == findById){
		   throw new ReqDataException("此条可疑交易记录不存在,请刷新页面!");
		}
		final String identifier = TypeUtils.castToString(findById.get("identifier"));
		Object object = record.get("detail_id");
		final Record set = new Record() ;
		final Record where = new Record() ;
		boolean flag = false ;
		if(null == object){
			//单笔交易
			final String tableName = WebConstant.MajorBizType.getBizType(biz_type).getBillTableName();
			Record bill = Db.findById(tableName, "id", bill_id);
			if(null == bill){
				throw new ReqDataException("此单据不存在,请刷新页面!");
			}			
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
							  boolean delete = Db.deleteById("acc_refundable_trans", "id", trade_id);
							  return  delete  ;
						  }
						  log.info("=========更新失败=====acc_his_transaction");
						  return false ;
					  }
		              log.info("=========更新失败====="+tableName);
					  return false ;
				  }
			});
		}else{
			//批量交易
			final Long detail_persist_version = TypeUtils.castToLong(record.get("detail_persist_version"));
			final Long detail_id = TypeUtils.castToLong(object);
			final String tableName = WebConstant.MajorBizType.getBizType(biz_type).getBillTableName();
			Record bill = Db.findById(tableName.split(",")[0], "id", bill_id);
			if(null == bill){
				throw new ReqDataException("此单据不存在,请刷新页面!");
			}
			//先更新单据表_总表
			set.set("persist_version", bill_persist_version + 1);
			set.set("update_by", usr_id);
			set.set("update_on", new Date());
			where.set("id", bill_id);
			where.set("persist_version", bill_persist_version);
			flag = Db.tx(new IAtom(){
				  @Override
		          public boolean run() throws SQLException {
					  if(CommonService.update(tableName.split(",")[0], set, where)){
						  log.info("=========更新成功====="+tableName.split(",")[0]);
						  set.clear();
						  where.clear();
						  set.set("refund_flag", 1);
						  set.set("update_by", usr_id);
						  set.set("update_on", new Date());
						  set.set("persist_version", detail_persist_version+1);
						  where.set("detail_id", detail_id);
						  where.set("persist_version", detail_persist_version);
						  if(CommonService.update(tableName.split(",")[1], set, where)){
							  log.info("=========更新成功====="+tableName.split(",")[1]);
							  set.clear();
							  where.clear();
							  set.set("refund_ref", tableName.split(",")[1]);
							  set.set("refund_bill_id", detail_id);
							  where.set("identifier", identifier);	
							  if(CommonService.update("acc_his_transaction", set, where)){
								  log.info("=========更新成功=====acc_his_transaction");
								  boolean delete = Db.deleteById("acc_refundable_trans", "id", trade_id);
								  return  delete  ;
							  }
							  log.info("=========更新失败=====acc_his_transaction");
							  return false ;
						  }
						  log.info("=========更新失败====="+tableName.split(",")[1]);
						  return false ;
					  }
					  log.info("=========更新失败====="+tableName.split(",")[0]);
					  return false;
				  }
			});		
		}
		if(!flag) {
			throw new ReqDataException("退票确认失败!");
		}
		//操作成功,再返回交易列表
		Page<Record> activeRefund = this.tradeList(1, 10, new Record());
		return activeRefund;
	}

}
