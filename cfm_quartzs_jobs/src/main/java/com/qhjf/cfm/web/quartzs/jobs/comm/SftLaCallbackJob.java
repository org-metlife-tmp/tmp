package com.qhjf.cfm.web.quartzs.jobs.comm;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.la.LaCallback;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;

public class SftLaCallbackJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Record> list = Db.find(Db.getSql("la_callback_cfm.getLAOriginList"), WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey(),WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey(),WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey(),WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey());
		if(list != null){
			for(Record record : list){
				SftCallBack callback = new SftCallBack();
    			callback.callback(WebConstant.SftOsSource.LA.getKey(), record);
			}

		}
	}

}
