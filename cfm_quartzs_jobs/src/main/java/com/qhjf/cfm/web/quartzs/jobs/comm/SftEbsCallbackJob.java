package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
/**
 *对完结的原始数据回调EBS失败的情况，再次回调EBS
 */
public class SftEbsCallbackJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Record> list = Db.find(Db.getSql("ebs_cfm.getEBSCallBackOriginList"), WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey(),WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey(),WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey(),WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey());
		if(list != null) {
			for(Record record : list) {
				SftCallBack callback = new SftCallBack();
				callback.callback(WebConstant.SftOsSource.EBS.getKey(), record);
			}
		}
		
	}

}
