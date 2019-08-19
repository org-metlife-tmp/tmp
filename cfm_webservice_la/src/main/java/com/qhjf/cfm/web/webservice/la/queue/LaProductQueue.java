package com.qhjf.cfm.web.webservice.la.queue;

/**
 * 队列生产者
 * @author yunxw
 *
 */
public class LaProductQueue implements Runnable{
	
	private LaQueueBean queueBean;
	
	public LaProductQueue(LaQueueBean queueBean){
		this.queueBean = queueBean;
	}

	@Override
	public void run() {
		try {
			LaQueue.getInstance().getQueue().put(queueBean);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
