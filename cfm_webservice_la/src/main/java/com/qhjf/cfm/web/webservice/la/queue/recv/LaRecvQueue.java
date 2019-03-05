package com.qhjf.cfm.web.webservice.la.queue.recv;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LaRecvQueue {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LaRecvQueue(){
		this.queue = new LinkedBlockingDeque();
	}
	private static LaRecvQueue instance = new LaRecvQueue();
	public static LaRecvQueue getInstance(){
		return instance;
	}
	private BlockingQueue<LaRecvQueueBean> queue;

	protected BlockingQueue<LaRecvQueueBean> getQueue() {
		return queue;
	}

	protected void setQueue(BlockingQueue<LaRecvQueueBean> queue) {
		this.queue = queue;
	}

}
