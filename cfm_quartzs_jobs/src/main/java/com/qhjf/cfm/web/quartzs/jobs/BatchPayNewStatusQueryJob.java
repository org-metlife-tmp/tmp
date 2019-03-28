package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysBatchPayNewStatusQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 批付交易状态查询
 * 
 * @author CHT
 *
 */
public class BatchPayNewStatusQueryJob extends PubJob {
	private static Logger log = LoggerFactory.getLogger(PubJob.class);

	@Override
	public String getJobCode() {
		return "BatchPayRecvStatusQuery";
	}

	@Override
	public String getJobName() {
		return "pay批付交易状态查询作业";
	}

	@Override
	public Logger getLog() {
		return log;
	}

	@Override
	public List<Record> getSourceDataList() throws JobExecutionException {
		return Db.find(Db.getSql("batchpayjob.getNeedQueryStatusBatchPay"));
	}

	@Override
	public String getInstrTableName() {
		return "batch_pay_recv_status_queue_lock";
	}

	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysBatchPayNewStatusQueryInter();
	}

}
