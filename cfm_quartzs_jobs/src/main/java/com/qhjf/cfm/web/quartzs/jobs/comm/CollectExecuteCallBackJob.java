package com.qhjf.cfm.web.quartzs.jobs.comm;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;

public class CollectExecuteCallBackJob implements Job{
	
	private static Logger log = LoggerFactory.getLogger(CollectExecuteCallBackJob.class);
	private ExecutorService executeService;
	private Future<String> future = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("CollectExecuteCallBackJob开始执行");
		this.executeService = Executors.newFixedThreadPool(10);
		List<Record> executeList = Db.find(Db.getSql("collect_view.findExecuteList"), WebConstant.CollOrPoolRunStatus.SENDING.getKey());
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
		log.debug("CollectExecuteCallBackJob执行结束");
		
	}

	public class ExecuteThread implements Callable<String> {

		private Record execute;

		public ExecuteThread(Record execute) {
			this.execute = execute;
		}

		@Override
		public String call() throws Exception {
			List<Record> groups = Db.find(Db.getSql("collect_view.findInstructionGroup"), execute.getLong("id"));
			Integer successCount = 0;
			Integer failCount = 0;
			Integer sendingCount = 0;
			Integer cancelCount = 0;
			Integer childAccountCount = 0;
			Integer mainAccountCount = 0;
			BigDecimal amount = BigDecimal.ZERO;
			if(groups == null || groups.size() ==0){

				execute.set("collect_amount", amount);
				execute.set("success_count", successCount);
				execute.set("fail_count", failCount);
				execute.set("collect_status", 1);
				execute.set("child_account_count", childAccountCount);
				execute.set("mainAccountCount", mainAccountCount);
				Db.update("collect_execute", execute);
				return "success";
			}
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (Record group : groups) {
				map.put(group.getInt("collect_status"), group.getLong("cnt").intValue());
			}
			cancelCount = map.get(WebConstant.CollOrPoolRunStatus.CANCEL.getKey());
			sendingCount = map.get(WebConstant.CollOrPoolRunStatus.SENDING.getKey());
			failCount = map.get(WebConstant.CollOrPoolRunStatus.FAILED.getKey());
			successCount = map.get(WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
			if (sendingCount == null && failCount == null) {
				execute.set("success_count", successCount == null?0:successCount);
				execute.set("fail_count", cancelCount== null?0:cancelCount);
				List<Record> recvAccountList = Db.find(Db.getSql("collect_view.getSuccessMainAccount"), execute.getLong("id"),WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
				List<Record> payAccountList = Db.find(Db.getSql("collect_view.getSuccessChildAccount"), execute.getLong("id"),WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
				Record amountRcord = Db.findFirst(Db.getSql("collect_view.getSuccessAmount"), execute.getLong("id"),WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
				amount = amountRcord==null?BigDecimal.ZERO:amountRcord.getBigDecimal("sm");
				mainAccountCount = recvAccountList == null?0:recvAccountList.size();
				childAccountCount = payAccountList == null?0:payAccountList.size();
				execute.set("child_account_count",childAccountCount);
				execute.set("main_account_count",mainAccountCount);
				execute.set("collect_amount",amount);
				if (cancelCount != null && cancelCount > 0) {
					execute.set("collect_status", WebConstant.CollOrPoolRunStatus.FAILED.getKey());
				} else {
					execute.set("collect_status", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
				}
				Db.update("collect_execute", execute);
			}
			return "success";
		}
	}
		
}
