package com.qhjf.cfm.web.webservice.ebs.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class EbsQueue {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private EbsQueue(){
		this.queue = new LinkedBlockingDeque();
	}
	private static EbsQueue instance = new EbsQueue();
	public static EbsQueue getInstance(){
		return instance;
	}
	private BlockingQueue<EbsQueueBean> queue;

	protected BlockingQueue<EbsQueueBean> getQueue() {
		return queue;
	}

	protected void setQueue(BlockingQueue<EbsQueueBean> queue) {
		this.queue = queue;
	}

}
