package com.qhjf.cfm.web.inter.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;

public class SysCurrTransQueryInter implements ISysAtomicInterface{
	
	private static Logger log = LoggerFactory.getLogger(SysCurrBalQueryInter.class);
	private IMoreResultChannelInter channelInter;
	private ExecutorService executeService = Executors.newFixedThreadPool(10);
	private Future<String> future = null;
	private Record instr;
	
	@Override
	public Record genInstr(Record record) {
		this.instr = new Record();
		Date date = new Date();
		instr.set("acc_id", record.getLong("acc_id"));
		instr.set("start_date", DateKit.toStr(date));
		instr.set("end_date", DateKit.toStr(date));
		instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date,DateKit.timeStampPattern)));
		return this.instr;
	}

	@Override
	public Record getInstr() {
		return this.instr;
	}

	@Override
	public void callBack(String jsonStr) throws Exception {
		Db.delete("trans_query_instr_queue_lock", instr);
		if(jsonStr == null || jsonStr.length() == 0){
        	log.error("当日交易查询返回报文为空,不错处理");
        	return;
        }
		int resultCount = channelInter.getResultCount(jsonStr);
		log.debug("resultCount"+resultCount);
		if(resultCount <= 0){
			return;
		}
		Long accId = instr.getLong("acc_id");
		Record account = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account"), accId);
		if(account == null){
			log.debug("账户id不存在:"+accId);
			return;
		}
		for(int i = 0;i < resultCount;i++){
			Record parseRecord = channelInter.parseResult(jsonStr, i);
			future = executeService.submit(new ExecuteThread(parseRecord,account));
		}
		executeService.shutdown(); // 关闭线程池,不在接收新的任务
		while (true) {
			if (future.isDone()) {
				break;
			}
		}
		
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IMoreResultChannelInter) channelInter;
		
	}
	
	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}
	
	public class ExecuteThread implements Callable<String> {

		private Record parseRecord;
		private Record account;

		public ExecuteThread(Record parseRecord,Record account) {
			this.parseRecord = parseRecord;
			this.account = account;
		}

		@Override
		public String call() throws Exception {
			try {
				Record accCurTrans = new Record();
				accCurTrans.set("acc_id", account.get("acc_id"));
				accCurTrans.set("acc_no", account.getStr("acc_no"));
				accCurTrans.set("acc_name", account.getStr("acc_name"));
				accCurTrans.set("bank_type", account.getStr("bank_cnaps_code").substring(0,3));
				accCurTrans.set("data_source", WebConstant.DataSource.DIRECTCONN.getKey());
				accCurTrans.setColumns(parseRecord);
				String summary = accCurTrans.getStr("summary");
				if (summary != null && summary.length() >= 6) {
					String instructCode = summary.substring(0, 6);
					if(isValidInstructCode(instructCode)){
						accCurTrans.set("summary", summary.substring(6, summary.length()));
					}
				}
				StringBuffer inedtifierPlain = new StringBuffer();
				inedtifierPlain.append(accCurTrans.getStr("acc_no"));
				inedtifierPlain.append(accCurTrans.getStr("acc_name"));
				inedtifierPlain.append(accCurTrans.getStr("bank_type"));
				inedtifierPlain.append(accCurTrans.getStr("direction"));
				inedtifierPlain.append(accCurTrans.getBigDecimal("amount"));
				inedtifierPlain.append(accCurTrans.getStr("opp_acc_no"));
				inedtifierPlain.append(accCurTrans.getStr("opp_acc_name"));
				inedtifierPlain.append(accCurTrans.getStr("opp_acc_bank"));
				inedtifierPlain.append(accCurTrans.getStr("summary"));
				inedtifierPlain.append(accCurTrans.getStr("post_script"));
				inedtifierPlain.append(accCurTrans.getStr("trans_date"));
				inedtifierPlain.append(accCurTrans.getDate("trans_time"));
				inedtifierPlain.append(accCurTrans.getStr("identifier"));
				String identifier = MD5Kit.string2MD5(inedtifierPlain.toString());
				accCurTrans.set("identifier", identifier);
				Db.save("acc_cur_transaction", accCurTrans);
				return "success";
			} catch (Exception e) {
				e.printStackTrace();
				return "fail";
			}
		}
		

	}

	@Override
	public void callBack(Exception e) throws Exception {
		Db.delete("trans_query_instr_queue_lock", instr);
	}
	
	public boolean isValidInstructCode(String instructCode) {
		String regEx = "[0-9a-fA-F]{6}";
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(instructCode);
		if (mat.matches()) {
			return true;
		} else {
			return false;
		}
	}

}
