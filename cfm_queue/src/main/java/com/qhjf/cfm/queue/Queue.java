package com.qhjf.cfm.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 队列实现
 * @author yunxw
 *
 */
@SuppressWarnings("unchecked")
public class Queue {   //33建一个建行的队列，建个ccb

	@SuppressWarnings("rawtypes")
	private Queue(){
		this.cmbcQueue = new LinkedBlockingDeque();
		this.icbcQueue = new LinkedBlockingDeque();
		this.icbcSignQueue = new LinkedBlockingDeque();
		this.fingradQueue = new LinkedBlockingDeque<>();
	}
	private static Queue instance = new Queue();
	public static Queue getInstance(){
		return instance;
	}
	private BlockingQueue<QueueBean> cmbcQueue;
	
	private BlockingQueue<QueueBean> icbcQueue;
	
	private BlockingQueue<QueueBean> icbcSignQueue;

	private BlockingQueue<QueueBean> fingradQueue;

	public BlockingQueue<QueueBean> getCmbcQueue() {
		return cmbcQueue;
	}

	public void setCmbcQueue(BlockingQueue<QueueBean> cmbcQueue) {
		this.cmbcQueue = cmbcQueue;
	}

	public BlockingQueue<QueueBean> getIcbcQueue() {
		return icbcQueue;
	}

	public void setIcbcQueue(BlockingQueue<QueueBean> icbcQueue) {
		this.icbcQueue = icbcQueue;
	}

	public BlockingQueue<QueueBean> getIcbcSignQueue() {
		return icbcSignQueue;
	}

	public void setIcbcSignQueue(BlockingQueue<QueueBean> icbcSignQueue) {
		this.icbcSignQueue = icbcSignQueue;
	}

	public BlockingQueue<QueueBean> getFingradQueue() {
		return fingradQueue;
	}

	public void setFingradQueue(BlockingQueue<QueueBean> fingradQueue) {
		this.fingradQueue = fingradQueue;
	}
}
