package com.qhjf.cfm.web.webservice.la.queue.recv;

import com.jfinal.plugin.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列插件，web启动时启动该插件
 * @author yunxw
 *
 */
public class LaRecvQueuePlugin implements IPlugin{
	private static Logger log = LoggerFactory.getLogger(LaRecvQueuePlugin.class);
private static final String threadName = "LA批收消费者队列";
	
    private static int consumerCount = 10;   //定义最大的消费线程数


	@Override
	public boolean start() {
		LaRecvConsumerQueue consumer = new LaRecvConsumerQueue();
		for(int i = 0;i < consumerCount;i++){
			log.debug("LA批收消费者:"+i);
			new Thread(consumer,threadName+"["+(i+1)+"]").start();
		}
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}

}
