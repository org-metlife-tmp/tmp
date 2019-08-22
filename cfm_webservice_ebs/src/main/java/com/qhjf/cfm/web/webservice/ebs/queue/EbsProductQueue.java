package com.qhjf.cfm.web.webservice.ebs.queue;

/**
 * 队列生产者
 * @author yunxw
 *
 */
public class EbsProductQueue implements Runnable{
	
	private EbsQueueBean queueBean;
	
	public EbsProductQueue(EbsQueueBean queueBean){
		this.queueBean = queueBean;
	}

	@Override
	public void run() {
		try {
			EbsQueue.getInstance().getQueue().put(queueBean);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
