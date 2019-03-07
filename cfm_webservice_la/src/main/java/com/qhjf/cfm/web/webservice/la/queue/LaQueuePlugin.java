package com.qhjf.cfm.web.webservice.la.queue;

import com.jfinal.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列插件
 * @author yunxw
 *
 */
public class LaQueuePlugin implements IPlugin{
	private static Logger log = LoggerFactory.getLogger(LaConsumerQueue.class);
private static final String threadName = "LA消费者队列";
	
    private static int consumerCount = 10;   //定义最大的消费线程数


	@Override
	public boolean start() {
		log.debug("33333333333333333");
		LaConsumerQueue consumer = new LaConsumerQueue();
		for(int i = 0;i < consumerCount;i++){
			log.debug("consumer"+i);
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
