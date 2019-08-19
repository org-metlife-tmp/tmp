package com.qhjf.cfm.web.webservice.oa.server.processQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Queue {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Queue(){
		this.queue = new LinkedBlockingDeque();
	}
	private static Queue instance = new Queue();
	public static Queue getInstance(){
		return instance;
	}
	private BlockingQueue<ParentQueueBean> queue;

	protected BlockingQueue<ParentQueueBean> getQueue() {
		return queue;
	}

	protected void setQueue(BlockingQueue<ParentQueueBean> queue) {
		this.queue = queue;
	}

}
