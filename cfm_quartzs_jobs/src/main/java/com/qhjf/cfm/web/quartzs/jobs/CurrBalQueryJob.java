package com.qhjf.cfm.web.quartzs.jobs;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysCurrBalQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;

public class CurrBalQueryJob extends PubJob{
	
	private static Logger log = LoggerFactory.getLogger(CurrBalQueryJob.class);
	@Override
	public String getJobCode() {
		return "CurrBalQuery";
	}
	@Override
	public String getJobName() {
		return "当日余额查询";
	}
	@Override
	public Logger getLog() {
		return log;
	}
	@Override
	public String getInstrTableName() {
		return "bal_query_instr_queue_lock";
	}
	@Override
	public Record getOldInstr(Record currInstr) {
		return Db.findFirst(Db.getSql("quartzs_job_cfm.get_bal_lock"), currInstr.getLong("acc_id"),currInstr.getStr("query_date"));
	}
	@Override
	public List<Record> getSourceDataList() {
		return Db.find(Db.getSql("quartzs_job_cfm.get_account_list"));
	}
	public boolean needReTrySaveInstr(){
		return true;
	}
	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysCurrBalQueryInter();
	}
	
}
