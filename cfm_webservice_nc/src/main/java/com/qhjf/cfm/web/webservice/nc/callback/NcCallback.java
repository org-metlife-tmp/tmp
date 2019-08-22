package com.qhjf.cfm.web.webservice.nc.callback;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.nc.server.processQueue.CallBackQueueBean;
import com.qhjf.cfm.web.webservice.nc.server.processQueue.ProductQueue;


public class NcCallback {

	/**
	 * 推送
	 * nc单据付款状态
	 * @param originData
	 * @return
	 * @throws Exception
	 */
	public void callback(Record originData,Record tranRecord){
		CallBackQueueBean callBackQueueBean = new CallBackQueueBean(originData,tranRecord);
		ProductQueue productQueue = new ProductQueue(callBackQueueBean);
		new Thread(productQueue).start();
	}

}
