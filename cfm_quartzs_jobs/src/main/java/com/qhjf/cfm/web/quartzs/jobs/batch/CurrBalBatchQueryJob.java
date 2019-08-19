package com.qhjf.cfm.web.quartzs.jobs.batch;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.batch.SysCurrBalBatchQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubBatchJob;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CurrBalBatchQueryJob extends PubBatchJob{
	
	private static Logger log = LoggerFactory.getLogger(CurrBalBatchQueryJob.class);
	@Override
	public String getJobCode() {
		return "CurrBalBatchQuery";
	}
	@Override
	public String getJobName() {
		return "当日余额批量查询";
	}
	@Override
	public Logger getLog() {
		return log;
	}
	@Override
	public String getInstrTableName() {
		return "bal_query_instr_queue_lock";
	}


	//查询的可用直连账号中，排除工行的直连账号
	@Override
	public List<Record> getSourceDataList() throws JobExecutionException {
		getSupportChannel();
		
		SqlPara sqlPara = Db.getSqlPara("quartzs_job_cfm.get_account_list_balance_batch", new Record().set("cnpas", this.cnaps));
		return Db.find(sqlPara);
	}
	@Override
	public Record getOldInstr(Record currInstr) {
		return Db.findFirst(Db.getSql("quartzs_job_cfm.get_bal_lock"), currInstr.getStr("acc_id"),currInstr.getStr("query_date"));
	}
	public boolean needReTrySaveInstr(){
		return true;
	}
	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysCurrBalBatchQueryInter();
	}
	
}
