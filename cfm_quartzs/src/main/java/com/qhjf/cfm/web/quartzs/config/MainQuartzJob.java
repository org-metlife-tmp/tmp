package com.qhjf.cfm.web.quartzs.config;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MainQuartzJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MyQuartzPlugin quartzPlugin = MyQuartzPlugin.getInstance();
		quartzPlugin.startCommJobs();
		
	}

}
