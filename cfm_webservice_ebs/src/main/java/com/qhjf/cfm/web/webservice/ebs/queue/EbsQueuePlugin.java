package com.qhjf.cfm.web.webservice.ebs.queue;

import com.jfinal.plugin.IPlugin;

/**
 * 队列插件
 * @author yunxw
 *
 */
public class EbsQueuePlugin implements IPlugin{
	
private static final String threadName = "EBS消费者队列";
	
    private static int consumerCount = 10;   //定义最大的消费线程数


	@Override
	public boolean start() {
		EbsConsumerQueue consumer = new EbsConsumerQueue();
		for(int i = 0;i < consumerCount;i++){
			new Thread(consumer,threadName+"["+(i+1)+"]").start();
		}
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return true;
	}

}
