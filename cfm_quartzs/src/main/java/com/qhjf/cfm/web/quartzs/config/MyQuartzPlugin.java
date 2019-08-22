package com.qhjf.cfm.web.quartzs.config;

import cn.dreampie.quartz.QuartzKey;
import cn.dreampie.quartz.QuartzKit;
import cn.dreampie.quartz.QuartzPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MyQuartzPlugin extends QuartzPlugin {
	
	private static Logger log = LoggerFactory.getLogger(MyQuartzPlugin.class);
	
	private MyQuartzPlugin(){};
	
	private static MyQuartzPlugin p = new MyQuartzPlugin();
	
	public static MyQuartzPlugin getInstance(){
		return p;
	}

	/**
	 * 启动定时任务
	 */
	@Override
	public void startPropertiesJobs() {
		log.debug("开始启动定时任务");
		Db.update(Db.getSql("quartzs_config_cfm.init_jobs_sql"));
		List<Record> mainJobs = Db.find(Db.getSql("quartzs_config_cfm.get_main_job"));
		if(mainJobs == null || mainJobs.size() != 1){
			throw new RuntimeException("启动定时任务失败,未找到主定时任务");
		}
		Record mainJob = mainJobs.get(0);
		startMainJob(mainJob);
		log.debug("启动定时任务结束");
	}
	
	/**
	 * 启动主任务
	 * @param mainJob
	 */
	private void startMainJob(Record mainJob){
		MyQuartzBean mainJobBean = getMyQuartzBean(mainJob);
		QuartzKey quartzKey = getQuartzKey(mainJobBean);
		new MyQuartzCronJob(quartzKey, mainJobBean).start();
	}
	
	public void startCommJobs() {
		List<Record> commJobs = Db.find(Db.getSql("quartzs_config_cfm.get_needScanjobs_list_sql"));
		for(Record job:commJobs){
			if(job.get("class_name") != null && !"".equals(job.getStr("class_name"))){
				startCommJob(job);
			}
		 }
		
	}
	
	/**
	 * 启动普通任务
	 * @param commJob
	 */
	private void startCommJob(Record commJob){
		MyQuartzBean commJobBean = getMyQuartzBean(commJob);
		QuartzKey quartzKey = getQuartzKey(commJobBean);
		MyQuartzCronJob quartzJob = (MyQuartzCronJob) QuartzKit.getJob(quartzKey);
		if (quartzJob != null) {
			MyQuartzBean oldJobBean = quartzJob.getQuartzBean();
			if (!commJobBean.equals(oldJobBean)) {
				quartzJob.stop();
				QuartzKit.removeQuartzJob(quartzJob);
				if (commJobBean.getEnable() == 1 ) {
					new MyQuartzCronJob(quartzKey, commJobBean).start();
				}
			}
		}else{
			if (commJobBean.getEnable() == 1 ) {
				new MyQuartzCronJob(quartzKey, commJobBean).start();
			}
		}

		
		Db.update(Db.getSql("quartzs_config_cfm.update_needScan_sql"),0,commJobBean.getId());
	}
	
	/**
	 * 生成MyQuartzBean
	 * @param job
	 * @return
	 */
	private MyQuartzBean getMyQuartzBean(Record job){
		Long id = job.getLong("id");
		String name = job.getStr("name");
		String className = job.getStr("class_name");
		String groups = job.getStr("groups");
		String cron = job.getStr("cron");
		Integer enable = job.getInt("enable");
		String param = job.getStr("param");

		MyQuartzBean myrQuartzBean = new MyQuartzBean(id, name, className, groups, cron, enable, param);
		return myrQuartzBean;
	}
	
	/**
	 * 生成QuartzKey
	 * @param bean
	 * @return
	 */
	private QuartzKey getQuartzKey(MyQuartzBean bean){
		QuartzKey quartzKey = new QuartzKey(bean.getId(), bean.getName(), bean.getGroups());
		return quartzKey;
	}
}
