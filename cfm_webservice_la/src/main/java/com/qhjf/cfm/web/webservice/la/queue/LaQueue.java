package com.qhjf.cfm.web.webservice.la.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LaQueue {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private LaQueue(){
		this.queue = new LinkedBlockingDeque();
	}
	private static LaQueue instance = new LaQueue();
	public static LaQueue getInstance(){
		return instance;
	}
	private BlockingQueue<LaQueueBean> queue;

	protected BlockingQueue<LaQueueBean> getQueue() {
		return queue;
	}

	protected void setQueue(BlockingQueue<LaQueueBean> queue) {
		this.queue = queue;
	}
	
	public static void main(String[] args){
		System.out.println("22222222");
	}

}
