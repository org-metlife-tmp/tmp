package com.qhjf.cfm.queue;

import com.qhjf.bankinterface.api.ProcessEntrance;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列消费者
 * @author yunxw
 *
 */
public class CcbConsumerQueue implements Runnable{
	
	private static Logger log = LoggerFactory.getLogger(CcbConsumerQueue.class);

	@Override
	public void run() {
		while(true){
			QueueBean queueBean = null;
			ISysAtomicInterface sysInter = null;
			try {
				queueBean = Queue.getInstance().getCcbQueue().take();
				sysInter = queueBean.getSysInter();
				IChannelInter channelInter = sysInter.getChannelInter();
				String jsonStr = ProcessEntrance.getInstance().process(channelInter.getInter(), queueBean.getParams());
				sysInter.callBack(jsonStr);
				log.debug("睡眠前。。。。。。。。。。");
				Thread.sleep(3000);
				log.debug("睡眠后。。。。。。。。。。");
			} catch (Exception e) {
				e.printStackTrace();
				try {
					sysInter.callBack(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				continue;
			}
		}
		
	}
}
