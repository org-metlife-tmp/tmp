package com.qhjf.cfm.web.webservice.la.queue.noResp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LaNoRespQueue {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LaNoRespQueue(){
		this.queue = new LinkedBlockingDeque();
	}
	private static LaNoRespQueue instance = new LaNoRespQueue();
	public static LaNoRespQueue getInstance(){
		return instance;
	}
	private BlockingQueue<LaNoRespQueueBean> queue;

	protected BlockingQueue<LaNoRespQueueBean> getQueue() {
		return queue;
	}

	protected void setQueue(BlockingQueue<LaNoRespQueueBean> queue) {
		this.queue = queue;
	}
	
	public static void main(String[] args){
		System.out.println("22222222");
	}

}
