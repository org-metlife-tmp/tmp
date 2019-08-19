package com.qhjf.cfm.web.webservice.oa.server.processQueue;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.oa.server.request.OAReciveDateReq;
import com.qhjf.cfm.web.webservice.oa.service.CallBackService;
import com.qhjf.cfm.web.webservice.oa.service.ProcessService;

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
					OAReciveDateReq req = processQueueBean.getReq();
					Record originData = processQueueBean.getOriginData();
					new ProcessService().proces(req,originData);
				}else if(type == 2){
					CallBackQueueBean callBackQueueBean = (CallBackQueueBean) queueBean;
					new CallBackService().callback(callBackQueueBean.getOriginData());
				}
				
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		
	}

	

	
	
}
