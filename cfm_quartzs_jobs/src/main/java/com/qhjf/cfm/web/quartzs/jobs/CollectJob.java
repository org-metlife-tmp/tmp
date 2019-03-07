package com.qhjf.cfm.web.quartzs.jobs;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysSinglePayInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CollectJob extends PubJob{
	
	private static Logger log = LoggerFactory.getLogger(CollectJob.class);
	private Record topic;
	private Record execute;

	@Override
	public String getJobCode() {
		return "SinglePay";
	}
	@Override
	public String getJobName() {
		return "归集通定时任务";
	}

	@Override
	public Logger getLog() {
		return log;
	}

	@Override
	public List<Record> getSourceDataList() throws JobExecutionException {
		Long id = TypeUtils.castToLong(getContext().getMergedJobDataMap().get("id"));
		if(id == null){
			throw new JobExecutionException("参数有误");
		}
		List<Record> sendList = Db.find(Db.getSql("collect_view.findExecuteListByCollectId"), id, WebConstant.CollOrPoolRunStatus.SENDING.getKey());
		if(sendList != null && sendList.size()>0){
			throw new JobExecutionException("有未完成的任务");
		}
		this.topic = Db.findFirst(Db.getSql("collect_view.findTopicById"), id);
		if(topic == null){
			throw new JobExecutionException("单据不存在");
		}
		int status = topic.getInt("service_status");
		if(status != WebConstant.BillStatus.PASS.getKey()){
			throw new JobExecutionException("单据状态有误");
		}
		int isActivity = topic.getInt("is_activity");
		if(isActivity != WebConstant.YesOrNo.YES.getKey()){
			throw new JobExecutionException("单据未激活");
		}
		List<Record> billRecords = Db.find(Db.getSql("collect_view.findInstrList"), id);
		if(billRecords != null && billRecords.size() >0){
			this.execute = new Record();
			execute.set("collect_id", id);
			execute.set("execute_time", new Date());
			execute.set("collect_status", WebConstant.CollOrPoolRunStatus.SENDING.getKey());
			Db.save("collect_execute", execute);
			
		}
		return billRecords;
	}

	@Override
	public String getInstrTableName() {
		return "single_pay_instr_queue";
	}
	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysSinglePayInter();
	}

	@Override
	public Boolean beforeProcess(Record record){
		
		Boolean flag = true ;

		String bankSerialNumber = null;
		int repeatCount = 0;
		int status = WebConstant.CollOrPoolRunStatus.SENDING.getKey();
		String feedBack = null;
		try{
			String payAccCnaps = record.getStr("child_acc_bank_cnaps_code");
			String bankCode = payAccCnaps.substring(0, 3);
			bankSerialNumber = ChannelManager.getSerianlNo(bankCode);
			repeatCount = 1;
		}catch(Exception e){
			e.printStackTrace();
			 String errMsg = null;
             if (e.getMessage() == null || e.getMessage().length() > 1000) {
                 errMsg = "发送银行失败！";
             } else {
                 errMsg = e.getMessage();
             }
            feedBack = errMsg;
			flag = false ;
		}
		Integer collect_type = TypeUtils.castToInt(topic.get("collect_type"));
		log.info("归集类型====collect_type="+collect_type);
		log.info("归集id======collect_id="+topic.getLong("id"));
		
		Record instruction = new Record();		
		instruction.set("collect_id", topic.getLong("id"));
		instruction.set("collect_execute_id", execute.getLong("id"));
		instruction.set("bank_serial_number", bankSerialNumber);
		instruction.set("repeat_count", repeatCount);
		instruction.set("pay_account_id", record.getLong("child_acc_id"));
		instruction.set("pay_account_org_id", record.getLong("child_acc_org_id"));
		instruction.set("pay_account_org_name", record.getStr("child_acc_org_name"));
		instruction.set("pay_account_no", record.getStr("child_acc_no"));
		instruction.set("pay_account_name", record.getStr("child_acc_name"));
		instruction.set("pay_account_cur", record.getStr("child_acc_cur"));
		instruction.set("pay_account_bank", record.getStr("child_acc_bank_name"));
		instruction.set("pay_bank_cnaps", record.getStr("child_acc_bank_cnaps_code"));
		instruction.set("pay_bank_prov", record.getStr("child_acc_bank_prov"));
		instruction.set("pay_bank_city", record.getStr("child_acc_bank_city"));
		instruction.set("recv_account_id", record.getLong("main_acc_id"));
		instruction.set("recv_account_org_id", record.getLong("main_acc_org_id"));
		instruction.set("recv_account_org_name", record.getStr("main_acc_org_name"));
		instruction.set("recv_account_no", record.getStr("main_acc_no"));
		instruction.set("recv_account_name", record.getStr("main_acc_name"));
		instruction.set("recv_account_cur", record.getStr("main_acc_cur"));
		instruction.set("recv_account_bank", record.getStr("main_acc_bank_name"));
		instruction.set("recv_bank_cnaps", record.getStr("main_acc_bank_cnaps_code"));
		instruction.set("recv_bank_prov", record.getStr("main_acc_bank_prov"));
		instruction.set("recv_bank_city", record.getStr("main_acc_bank_city"));
		instruction.set("collect_status", status);
		instruction.set("create_on", new Date());
		instruction.set("update_on", new Date());
		instruction.set("memo", topic.getStr("summary"));
		instruction.set("feed_back", feedBack);
		instruction.set("instruct_code",RedisSericalnoGenTool.genShortSerial());
		
		if(1 == collect_type){
			log.info("=======定额归集");			
			instruction.set("collect_amount", topic.getBigDecimal("collect_amount"));
		}else if(2 == collect_type){
			log.info("=======留存余额");
			//查询付款账户余额,确定此子账户需要归集的金额
			String sql = Db.getSql("curyet.findCurrentBal");
			log.info("归集子账户=="+record.getStr("child_acc_no"));
			List<Record> find = Db.find(sql, record.getStr("child_acc_no"));
			if(null != find && find.size() == 1){
				Record rec = find.get(0);
				BigDecimal amount = TypeUtils.castToBigDecimal(rec.get("bal"));
				if(amount.compareTo(topic.getBigDecimal("collect_amount")) < 0){
					log.error("=======账号=="+record.getStr("child_acc_no")+"余额小于要求的留存余额");
					feedBack = feedBack == null ? "付款账户余额小于留存余额" : feedBack+",付款账户余额小于留存余额" ;
					flag = false ;
				}else if(amount.compareTo(topic.getBigDecimal("collect_amount")) == 0){
					log.error("=======账号=="+record.getStr("child_acc_no")+"余额等于留存余额，不进行此次归集");
					feedBack = feedBack == null ? "付款账户余额等于留存余额，不进行此次归集" : feedBack+",付款账户余额等于留存余额，不进行此次归集" ;
					flag = false ;
				}else{
					BigDecimal collectAmount = amount.subtract(topic.getBigDecimal("collect_amount"));
					instruction.set("collect_amount", collectAmount);
				}
			}else{
				log.error("=========未在acc_cur_balance表内查询到余额信息,账号=="+record.getStr("child_acc_no"));
				feedBack = feedBack == null ? "暂未查询到付款账户余额" : feedBack+",暂未查询到付款账户余额" ;
				flag = false ;
			}
		}else if(3 == collect_type){
			log.info("=======全额归集");
			String sql = Db.getSql("curyet.findCurrentBal");
			log.info("归集子账户=="+record.getStr("child_acc_no"));
			List<Record> find = Db.find(sql, record.getStr("child_acc_no"));
			if(null != find && find.size() == 1){
				Record rec = find.get(0);
				BigDecimal collectAmount = TypeUtils.castToBigDecimal(rec.get("bal"));
				instruction.set("collect_amount", collectAmount);
			}else{
				log.error("=========未在acc_cur_balance表内查询到余额信息,账号=="+record.getStr("child_acc_no"));
				feedBack = feedBack == null ? "暂未查询到付款账户余额" : feedBack+",暂未查询到付款账户余额" ;
				flag = false ;
			}
		}else {
			log.error("===========数据失效,不存在此归集类型");
			feedBack = feedBack == null ? "数据失效,归集类型有误" : feedBack+",数据失效,归集类型有误" ;
			flag = false ;
		}
        if(!flag) {
        	instruction.set("feed_back", feedBack);
        	instruction.set("collect_status", WebConstant.CollOrPoolRunStatus.FAILED.getKey());
        	instruction.set("collect_amount", new BigDecimal("0.00"));
        }
		Db.save("collect_execute_instruction", instruction);
		record.setColumns(instruction);
		record.set("bank_cnaps_code", instruction.getStr("pay_bank_cnaps"));
		record.set("bank_serial_number", bankSerialNumber);
		record.set("source_ref", "collect_execute_instruction");
		record.set("payment_amount", instruction.getBigDecimal("collect_amount"));
		record.set("process_bank_type", record.getStr("child_acc_bank_cnaps_code").subSequence(0, 3));
		record.set("payment_summary", topic.getStr("summary"));	
		return flag ;
	}
	
	@Override
	public  Map<String,Object> getParams(IChannelInter inter,Record instrRecord,Record sourceRecord){
		return inter.genParamsMap(instrRecord);
	}
	
}
