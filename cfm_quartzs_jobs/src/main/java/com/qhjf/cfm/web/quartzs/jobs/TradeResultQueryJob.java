package com.qhjf.cfm.web.quartzs.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysTradeResultQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;

public class TradeResultQueryJob extends PubJob{
	
	private static Logger log = LoggerFactory.getLogger(HisTransQueryJob.class);
	@Override
	public String getJobCode() {
		return "TradeResultQuery";
	}
	@Override
	public String getJobName() {
		return "交易状态查询";
	}
	@Override
	public Logger getLog() {
		return log;
	}
	@Override
	public String getInstrTableName() {
		return "trade_result_query_instr_queue_lock";
	}
	@Override
	public Record getOldInstr(Record currInstr) {
		return Db.findFirst(Db.getSql("quartzs_job_cfm.get_trade_result_list"),currInstr.getStr("process_bank_type"),currInstr.getStr("bank_serial_number"));
	}
	@Override
	public List<Record> getSourceDataList() {
		return Db.find(Db.getSql("quartzs_job_cfm.get_instr_list"),3);
	}
	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysTradeResultQueryInter();
	}
	@Override
	public boolean needReTrySaveInstr(){
		return true;
	}
	
}