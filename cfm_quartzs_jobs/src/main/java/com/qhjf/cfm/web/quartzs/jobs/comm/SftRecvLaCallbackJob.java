package com.qhjf.cfm.web.quartzs.jobs.comm;

import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;
/**
 * 对la_origin_pay_data表中，处理成功/失败，并且回调失败/未回调的数据进行   补偿回调
 * @author CHT
 *
 */
public class SftRecvLaCallbackJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Record> list = Db.find(Db.getSql("la_recv_cfm.getLARecvCallBackOriginList")
				, WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey()
				, WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()
				, WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey()
				, WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey());
		if(list != null){
			for(Record record : list){
				SftRecvCallBack callback = new SftRecvCallBack();
    			callback.callback(WebConstant.SftOsSource.LA.getKey(), record);
			}

		}
	}

}
