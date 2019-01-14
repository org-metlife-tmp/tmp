package com.qhjf.cfm.web.inter.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.channel.util.TransQueryTimesGenUtil;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;

public class SysHisTransQueryInter implements ISysAtomicInterface {

	private static Logger log = LoggerFactory.getLogger(SysCurrBalQueryInter.class);
	private IMoreResultChannelInter channelInter;
	private ExecutorService executeService = Executors.newFixedThreadPool(10);
	private Future<String> future = null;
	private Record instr;

	@Override
	public void callBack(String jsonStr) throws Exception {
		Db.delete("trans_query_instr_queue_lock", instr);
		if(jsonStr == null || jsonStr.length() == 0){
			log.error("历史交易查询返回报文为空,不错处理");
			return;
		}
		int resultCount = channelInter.getResultCount(jsonStr);
		log.debug("resultCount" + resultCount);
		if (resultCount <= 0) {
			return;
		}
		Long accId = instr.getLong("acc_id");
		Record account = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account"), accId);
		if (account == null) {
			log.debug("账户id不存在:" + accId);
			return;
		}
		for (int i = 0; i < resultCount; i++) {
			Record parseRecord = channelInter.parseResult(jsonStr, i);
			this.future = executeService.submit(new ExecuteThread(parseRecord, account));
		}
		executeService.shutdown(); // 关闭线程池,不在接收新的任务
		while (true) {
			if (future.isDone()) {
				break;
			}
		}

		//历史交易查询完毕，更新历史交易作业最近执行日期表acc_his_transaction_jobext
		String preDay = getChannelInter().getInter().getChannelConfig("preday");
		Map<String, String> times =
				TransQueryTimesGenUtil.getInstance().getTransQueryTime(String.valueOf(accId), preDay);

		//查询acc_his_transaction_jobext
		List<Record> find = Db.find("select * from acc_his_transaction_jobext where acc_id=?", new Object[]{accId});
		if (null == find || find.size() == 0) {
			Record r = new Record().set("acc_id", accId)
					.set("latest_date", times.get("end"))
					.set("update_on", new Date());
			Db.save("acc_his_transaction_jobext", r);
		}else {
			Object[] params = new Object[]{times.get("end"), new Date(), accId};
			Db.update("update acc_his_transaction_jobext set latest_date=?,update_on=? where acc_id=?", params);
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

		public ExecuteThread(Record parseRecord, Record account) {
			this.parseRecord = parseRecord;
			this.account = account;
		}

		@Override
		public String call() throws Exception {
			try {
				Record accHisTrans = new Record();
				accHisTrans.set("acc_id", account.get("acc_id"));
				accHisTrans.set("acc_no", account.getStr("acc_no"));
				accHisTrans.set("acc_name", account.getStr("acc_name"));
				accHisTrans.set("bank_type", account.getStr("bank_cnaps_code").substring(0, 3));
				accHisTrans.set("data_source", WebConstant.DataSource.DIRECTCONN.getKey());
				accHisTrans.setColumns(parseRecord);
				String summary = accHisTrans.getStr("summary");
				if (summary != null && summary.length() >= 6) {
					String instructCode = summary.substring(0, 6);
					if(isValidInstructCode(instructCode)){
						accHisTrans.set("instruct_code", instructCode);
						accHisTrans.set("summary", summary.substring(6, summary.length()));
					}
				}
				StringBuffer inedtifierPlain = new StringBuffer();
				inedtifierPlain.append(accHisTrans.getStr("acc_no"));
				inedtifierPlain.append(accHisTrans.getStr("acc_name"));
				inedtifierPlain.append(accHisTrans.getStr("bank_type"));
				inedtifierPlain.append(accHisTrans.getStr("direction"));
				inedtifierPlain.append(accHisTrans.getBigDecimal("amount"));
				inedtifierPlain.append(accHisTrans.getStr("opp_acc_no"));
				inedtifierPlain.append(accHisTrans.getStr("opp_acc_name"));
				inedtifierPlain.append(accHisTrans.getStr("opp_acc_bank"));
				inedtifierPlain.append(accHisTrans.getStr("summary"));
				inedtifierPlain.append(accHisTrans.getStr("post_script"));
				inedtifierPlain.append(accHisTrans.getStr("trans_date"));
				inedtifierPlain.append(accHisTrans.getDate("trans_time"));
				inedtifierPlain.append(accHisTrans.getStr("identifier"));
				String identifier = MD5Kit.string2MD5(inedtifierPlain.toString());
				accHisTrans.set("identifier", identifier);
				Db.save("acc_his_transaction", accHisTrans);
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

	@Override
	public Record genInstr(Record record) {
		this.instr = new Record();
		Date date = new Date();
		instr.set("acc_id", record.getLong("acc_id"));
		Date hisDate = DateUtil.getSpecifiedDayBeforeDate(date);
		instr.set("start_date", DateKit.toStr(hisDate));
		instr.set("end_date", DateKit.toStr(hisDate));
		instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date, DateKit.timeStampPattern)));
		return this.instr;
	}

	@Override
	public Record getInstr() {
		return this.instr;
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

	public static void main(String[] args) {
		String str = "123456789";
		System.out.println(str.substring(0, 6));
		System.out.println(str.substring(6, 9));
	}
}
