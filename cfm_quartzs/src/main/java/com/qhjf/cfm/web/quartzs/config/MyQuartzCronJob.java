package com.qhjf.cfm.web.quartzs.config;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.JobDetailImpl;

import com.alibaba.fastjson.JSONObject;

import cn.dreampie.quartz.QuartzKey;
import cn.dreampie.quartz.QuartzKit;
import cn.dreampie.quartz.job.JobState;
import cn.dreampie.quartz.job.QuartzCronJob;

public class MyQuartzCronJob extends QuartzCronJob{
	
	private MyQuartzBean quartzBean;

	public MyQuartzCronJob(QuartzKey quartzKey, MyQuartzBean quartzBean) {
		super(quartzKey, quartzBean.getCron(), quartzBean.getJobClass());
		this.quartzBean = quartzBean;
	}

	public void start(boolean force) {

		
	    long id = quartzKey.getId();
	    String name = quartzKey.getName();
	    String group = quartzKey.getGroup();
	    SchedulerFactory factory = QuartzKit.getSchedulerFactory();
	    try {
	      if (factory != null) {
	        Scheduler sched = factory.getScheduler();
	        // define the job and tie it to our HelloJob class
	        JobDetailImpl job = (JobDetailImpl) newJob(jobClass)
	            .withIdentity(JOB_MARK + SEPARATOR + name + SEPARATOR + id, GROUP_MARK + SEPARATOR + group + SEPARATOR + id)
	            .requestRecovery()
	            .build();

	        JobDataMap jobMap = job.getJobDataMap();
	        jobMap.put(group + SEPARATOR + name, id);
	        JSONObject paramsJson = quartzBean.getParamJson();
	        if(paramsJson != null && paramsJson.size()>0){
	        	 for (Map.Entry<String, Object> entry : paramsJson.entrySet()) {
	                 params.put(entry.getKey(), entry.getValue());
	             }
	        }
	       
	        if (params != null && params.size() > 0){
	        	jobMap.putAll(params);
	        }
	          
	        CronTrigger trigger = newTrigger()
	            .withIdentity(TRIGGER_MARK + SEPARATOR + name + SEPARATOR + id, GROUP_MARK + SEPARATOR + group + SEPARATOR + id)
	            .withSchedule(cronSchedule(getCron())).build();

	        this.scheduleTime = sched.scheduleJob(job, trigger);
	        sched.start();
	        this.state = JobState.STARTED;
	        QuartzKit.addQuartzJob(this);
	      }
	    } catch (Exception e) {
	      throw new RuntimeException("Can't start cron job.", e);
	    }

	  }
	
	  public String getCron() {
	    return super.getCron();
	  }

	  public void setCron(String cron) {
		  super.setCron(cron);
	  }

	public MyQuartzBean getQuartzBean() {
		return quartzBean;
	}

	public void setQuartzBean(MyQuartzBean quartzBean) {
		this.quartzBean = quartzBean;
	}
	  
	  
}
