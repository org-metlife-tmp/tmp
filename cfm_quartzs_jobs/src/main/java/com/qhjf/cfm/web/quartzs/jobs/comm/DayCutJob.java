package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.jfinal.plugin.activerecord.Db;
import com.qhjf.cfm.web.quartzs.jobs.CurrBalQueryJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DayCutJob implements Job{
	
	private static Logger log = LoggerFactory.getLogger(CurrBalQueryJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("日切任务执行开始。。。。。。。。。");
		try{
			log.debug("将当日余额导入历史余额开始");
			Db.update(Db.getSql("quartzs_job_cfm.bal_from_cur_to_his"));
			log.debug("将当日余额导入历史余额成功");
			log.debug("删除当日余额开始");
			Db.update(Db.getSql("quartzs_job_cfm.delete_cur_bal"));
			log.debug("删除当日余额成功");
			log.debug("删除当日余额波动开始");
			Db.update(Db.getSql("quartzs_job_cfm.delete_cur_bal_wave"));
			log.debug("删除当日余额波动成功");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			log.debug("将当日交易导入历史交易开始");
			Db.update(Db.getSql("quartzs_job_cfm.trans_from_cur_to_his"));
			log.debug("将当日交易导入历史交易成功");
			log.debug("删除当日交易开始");
			Db.update(Db.getSql("quartzs_job_cfm.delete_cur_his"));
			log.debug("删除当日交易成功");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		log.debug("日切任务执行结束。。。。。。。。。");
	}

}
