package com.qhjf.cfm.web.webservice.nc.server.processQueue;

/**
 * 队列生产者
 * @author yunxw
 *
 */
public class ProductQueue implements Runnable{
	
	private ParentQueueBean queueBean;
	
	public ProductQueue(ParentQueueBean queueBean){
		this.queueBean = queueBean;
	}

	@Override
	public void run() {
		try {
			Queue.getInstance().getQueue().put(queueBean);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
