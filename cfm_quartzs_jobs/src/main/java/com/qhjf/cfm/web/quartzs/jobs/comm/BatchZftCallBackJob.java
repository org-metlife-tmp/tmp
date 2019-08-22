package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant.BillStatus;
import com.qhjf.cfm.web.constant.WebConstant.PayStatus;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BatchZftCallBackJob implements Job{
	
	private static Logger log = LoggerFactory.getLogger(BatchZftCallBackJob.class);
	private ExecutorService executeService;
	private Future<String> future = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("BatchDbtCallBackJob开始执行");
		this.executeService = Executors.newFixedThreadPool(10);
		List<Record> executeList = Db.find(Db.getSql("quartzs_job_cfm.get_batch_zft_nocompletion"), BillStatus.NOCOMPLETION.getKey());
		if(executeList == null || executeList.size() ==0){
			return;
		}
		for(Record execute:executeList){
			this.future = executeService.submit(new ExecuteThread(execute));
		}
		executeService.shutdown(); // 关闭线程池,不在接收新的任务
		while (true) {
			if (future.isDone()) {
				break;
			}
		}
		log.debug("BatchDbtCallBackJob执行结束");
		
	}

	public class ExecuteThread implements Callable<String> {

		private Record baseInfo;

		public ExecuteThread(Record baseInfo) {
			this.baseInfo = baseInfo;
		}

		@Override
		public String call() throws Exception {
			Record record = Db.findFirst(Db.getSql("quartzs_job_cfm.get_batch_zft_count"), baseInfo.getStr("batchno"),PayStatus.INIT.getKey(),PayStatus.FAILD.getKey(),PayStatus.HANDLE.getKey());
			int count = record.getInt("cnt");
			if(count == 0){
				int flg = Db.update(Db.getSql("quartzs_job_cfm.update_batch_zft_status"),BillStatus.COMPLETION.getKey(), baseInfo.getLong("id"),baseInfo.getInt("persist_version"));
				if(flg == 0){
					log.debug("批量调拨回写状态失败,id="+baseInfo.getLong("id")+"version="+baseInfo.getInt("persist_version"));
				}
				
			}
			return "success";
		}
	}
		
}
