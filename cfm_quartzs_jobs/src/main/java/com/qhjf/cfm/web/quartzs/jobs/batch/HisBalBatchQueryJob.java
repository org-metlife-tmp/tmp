package com.qhjf.cfm.web.quartzs.jobs.batch;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.batch.SysHisBalBatchQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubBatchJob;

public class HisBalBatchQueryJob extends PubBatchJob {

	private static Logger log = LoggerFactory.getLogger(HisBalBatchQueryJob.class);
	@Override
	public String getJobCode() {
		return "HisBalBatchQuery";
	}
	@Override
	public String getJobName() {
		return "历史余额批量查询";
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
		return new SysHisBalBatchQueryInter();
	}

}
