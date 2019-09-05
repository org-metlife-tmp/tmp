package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallBackNcRespJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Date beforeDate = new Date(new Date().getTime() - 30*60*1000);
            List<Record> list = Db.find(Db.getSql("no_resp_callback.getLACallBackOriginList"), WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey(),WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey(),WebConstant.SftCallbackStatus.SFT_CALLBACKING.getKey(),beforeDate);
            List<Record> mailList=new ArrayList<>();
            if(list != null){
                for(Record record : list){
                    Integer callback_send_count=record.getInt("callback_send_count");
                    if (null == callback_send_count){
                        callback_send_count=0;
                    }
                    if (record.getInt("callback_send_count")<6 ){
                        try{
                            Record setRecord = new Record().set("callback_send_count", callback_send_count+1)
                                    .set("callback_update_time", new Date());
                            Record whereRecord = new Record().set("pay_code", record.getStr("pay_code"));
                            if(CommonService.updateRows("la_origin_pay_data", setRecord, whereRecord) != 1){
                                SftRecvCallBack callback = new SftRecvCallBack();
                                callback.callbackNoResp(WebConstant.SftOsSource.LA.getKey(), record);
                                continue;
                            };
                        }catch(Exception e){
                            e.printStackTrace();
                            continue;
                        }
                    }else {
                        mailList.add(record);
                    }
                }
            }
        }

}
