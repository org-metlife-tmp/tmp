package com.qhjf.cfm.queue;

/**
 * 队列生产者
 * @author yunxw
 *
 */
public class ProductQueue implements Runnable{
	
	private QueueBean queueBean;
	
	public ProductQueue(QueueBean queueBean){
		this.queueBean = queueBean;
	}

	@Override
	public void run() {
		try {
			if("308".equals(queueBean.getBankCode())){
				Queue.getInstance().getCmbcQueue().put(queueBean);
			}else if("102".equals(queueBean.getBankCode())){
				Queue.getInstance().getIcbcQueue().put(queueBean);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
