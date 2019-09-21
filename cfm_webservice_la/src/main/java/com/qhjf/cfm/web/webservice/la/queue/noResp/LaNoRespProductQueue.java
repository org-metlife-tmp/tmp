package com.qhjf.cfm.web.webservice.la.queue.noResp;

/**
 * 队列生产者
 * @author yunxw
 *
 */
public class LaNoRespProductQueue implements Runnable{
	
	private LaNoRespQueueBean queueBean;
	
	public LaNoRespProductQueue(LaNoRespQueueBean queueBean){
		this.queueBean = queueBean;
	}

	@Override
	public void run() {
		try {
			LaNoRespQueue.getInstance().getQueue().put(queueBean);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
