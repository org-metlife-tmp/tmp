package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysHisTransQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HisTransQueryJob extends PubJob{
	
	private static Logger log = LoggerFactory.getLogger(HisTransQueryJob.class);
	@Override
	public String getJobCode() {
		return "HisTransQuery";
	}
	@Override
	public String getJobName() {
		return "历史交易查询";
	}
	@Override
	public Logger getLog() {
		return log;
	}
	@Override
	public String getInstrTableName() {
		return "trans_query_instr_queue_lock";
	}
	@Override
	public Record getOldInstr(Record currInstr) {
		return Db.findFirst(Db.getSql("quartzs_job_cfm.get_trans_lock"), currInstr.getLong("acc_id"),currInstr.getStr("start_date"),currInstr.getStr("end_date"));
	}
	@Override
	public List<Record> getSourceDataList() {
		return Db.find(Db.getSql("quartzs_job_cfm.get_account_list"));
	}
	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysHisTransQueryInter();
	}
	@Override
	public boolean needReTrySaveInstr(){
		return true;
	}
	

}
