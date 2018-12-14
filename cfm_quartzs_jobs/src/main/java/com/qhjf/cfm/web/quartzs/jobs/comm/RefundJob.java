package com.qhjf.cfm.web.quartzs.jobs.comm;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.constant.WebConstant;

public class RefundJob implements Job{
	
	private static Logger log = LoggerFactory.getLogger(RefundJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("可疑退票定时任务开始执行。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
		Date date = new Date();
		String beginDate = DateUtil.getSpecifiedDayBefore(date,7,DateUtil.DEFAULT_DATEPATTERN);
		String endDate = DateUtil.getSpecifiedDayBefore(date,1,DateUtil.DEFAULT_DATEPATTERN);
		log.debug("beginDate="+beginDate);
		log.debug("endDate="+endDate);
		List<Record> payList = Db.find(Db.getSql("refund_cfm.get_histrans_group"),beginDate,endDate, WebConstant.PayOrRecv.PAYMENT.getKey());
		List<Record> recvList = Db.find(Db.getSql("refund_cfm.get_histrans_group"),beginDate,endDate, WebConstant.PayOrRecv.RECEIPT.getKey());
		if(payList != null && payList.size()>0 && recvList != null && recvList.size()>0){
			for(Record payRecord : payList){
				try {
					processRecv(payRecord,recvList);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
			}
		}
		log.debug("可疑退票定时任务执行结束。。。。。。。。。。。。。。。。。。。。。。。。。。。。");
		
	}
	
	private void processRecv(Record payRecord,List<Record> recvList)throws Exception{
		Long payAccId = payRecord.getLong("acc_id");
		BigDecimal payAmount = payRecord.getBigDecimal("amount");
		for(Record recvRecord : recvList){
			try{
				Long recvAccId = recvRecord.getLong("acc_id");
				BigDecimal recvAmount = recvRecord.getBigDecimal("amount");
				if(payAccId.equals(recvAccId) && payAmount.equals(recvAmount)){
					Record payRecordDetail = Db.findFirst(Db.getSql("refund_cfm.get_histrans_detail"), payAccId,payAmount,WebConstant.PayOrRecv.PAYMENT.getKey());
					Record recvRecordDetail = Db.findFirst(Db.getSql("refund_cfm.get_histrans_detail"), payAccId,payAmount,WebConstant.PayOrRecv.RECEIPT.getKey());
					if(payRecordDetail != null && recvRecordDetail != null){
						
						String payTime = payRecordDetail.getStr("transTime");
						String recvTime = recvRecordDetail.getStr("transTime");
						log.debug(payRecordDetail.getLong("id")+"付款交易时间="+payTime);
						log.debug(recvRecordDetail.getLong("id")+"收款交易时间="+recvTime);
						if(DateUtil.compareDate(payTime, recvTime)){
							process(payRecordDetail,recvRecordDetail);
							break;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	private void process(final Record payRecordDetail,final Record recvRecordDetail){
		
		Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
            	Record payRecordUpd = new Record();
        		payRecordUpd.set("id", payRecordDetail.getLong("id"));
        		payRecordUpd.set("is_refund_scan", 1);
        		
        		Record recvRecordUpd = new Record();
        		recvRecordUpd.set("id", recvRecordDetail.getLong("id"));
        		recvRecordUpd.set("is_refund_scan", 1);
        		
        		boolean payFlg = Db.update("acc_his_transaction", payRecordUpd);
        		boolean recvFlg = Db.update("acc_his_transaction", recvRecordUpd);
        		
        		if(payFlg && recvFlg){
        			Record refundableRecord = new Record();
        			refundableRecord.set("trans_id", recvRecordDetail.getLong("id"));
        			refundableRecord.set("acc_id", recvRecordDetail.getLong("acc_id"));
        			refundableRecord.set("acc_no", recvRecordDetail.getStr("acc_no"));
        			refundableRecord.set("acc_name", recvRecordDetail.getStr("acc_name"));
        			refundableRecord.set("bank_type", recvRecordDetail.getStr("bank_type"));
        			refundableRecord.set("direction", recvRecordDetail.getInt("direction"));
        			refundableRecord.set("amount", recvRecordDetail.getBigDecimal("amount"));
        			refundableRecord.set("opp_acc_no", recvRecordDetail.getStr("opp_acc_no"));
        			refundableRecord.set("opp_acc_name", recvRecordDetail.getStr("opp_acc_name"));
        			refundableRecord.set("opp_acc_bank", recvRecordDetail.getStr("opp_acc_bank"));
        			refundableRecord.set("summary", recvRecordDetail.getStr("summary"));
        			refundableRecord.set("post_script", recvRecordDetail.getStr("post_script"));
        			refundableRecord.set("trans_date", recvRecordDetail.getDate("trans_date"));
        			refundableRecord.set("trans_time", recvRecordDetail.getDate("trans_time"));
        			refundableRecord.set("data_source", recvRecordDetail.getInt("data_source"));
        			refundableRecord.set("identifier", recvRecordDetail.getStr("identifier"));
        			return Db.save("acc_refundable_trans", refundableRecord);
        		}
        		return false;
            }
        });
		
		
		
	}

}
