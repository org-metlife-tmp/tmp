package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysTradeResultPayBatchQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 实现原子接口：代收
 * 批量付交易明细查询job
 * @author CHT
 *
 */
public class TradeResultBatchPayQueryJob extends PubJob {
    private static Logger log = LoggerFactory.getLogger(PubJob.class);

	@Override
	public String getJobCode() {
		 return "TradeResultBatchPayQuery";
	}

	@Override
	public String getJobName() {
		return "批量付明细查询";
	}

	@Override
	public Logger getLog() {
		return log;
	}

	@Override
	public List<Record> getSourceDataList() throws JobExecutionException {
		return Db.find(Db.getSql("batchpayjob.getTradeResultBatchQrySourceListNew"));
	}

	@Override
	public String getInstrTableName() {
		//批量付查询状态指令表
        return "batch_pay_instr_queue_lock";
	}

	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysTradeResultPayBatchQueryInter();
	}

}
