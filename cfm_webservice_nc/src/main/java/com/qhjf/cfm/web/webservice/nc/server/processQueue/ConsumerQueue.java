package com.qhjf.cfm.web.webservice.nc.server.processQueue;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.nc.server.request.NCReciveDateReq;
import com.qhjf.cfm.web.webservice.nc.service.CallBackService;
import com.qhjf.cfm.web.webservice.nc.service.ProcessService;

/**
 * 队列消费者
 * @author yunxw
 *
 */
public class ConsumerQueue implements Runnable{
	
	

	@Override
	public void run() {
		while(true){
			try{
				ParentQueueBean queueBean = null;
				queueBean = Queue.getInstance().getQueue().take();
				int type = queueBean.getType();
				if(type == 1){
					ProcessQueueBean processQueueBean = (ProcessQueueBean) queueBean;
					NCReciveDateReq req = processQueueBean.getReq();
					Record originData = processQueueBean.getOriginData();
					new ProcessService().proces(req,originData);
				}else if(type == 2){
					CallBackQueueBean callBackQueueBean = (CallBackQueueBean) queueBean;
					new CallBackService().callback(callBackQueueBean.getOriginData(),callBackQueueBean.getTranRecord());
				}
				
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		
	}

	

	
	
}
