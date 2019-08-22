package com.qhjf.cfm.web.webservice.oa.callback;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.oa.server.processQueue.CallBackQueueBean;
import com.qhjf.cfm.web.webservice.oa.server.processQueue.ProductQueue;


public class OaCallback {

	/**
	 * 推送
	 * OA单据付款状态
	 * @param originData
	 * @return
	 * @throws Exception
	 */
	public void callback(Record originData){
		CallBackQueueBean callBackQueueBean = new CallBackQueueBean(originData);
		ProductQueue productQueue = new ProductQueue(callBackQueueBean);
		new Thread(productQueue).start();
	}

}
