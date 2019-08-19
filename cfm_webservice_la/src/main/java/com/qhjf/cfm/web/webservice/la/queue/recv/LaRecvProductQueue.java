package com.qhjf.cfm.web.webservice.la.queue.recv;

/**
 * 队列生产者
 * @author yunxw
 *
 */
public class LaRecvProductQueue implements Runnable{
	
	private LaRecvQueueBean queueBean;
	
	public LaRecvProductQueue(LaRecvQueueBean queueBean){
		this.queueBean = queueBean;
	}

	@Override
	public void run() {
		try {
			LaRecvQueue.getInstance().getQueue().put(queueBean);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
