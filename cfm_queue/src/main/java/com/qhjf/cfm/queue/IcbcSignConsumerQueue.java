package com.qhjf.cfm.queue;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qhjf.bankinterface.api.ProcessEntrance;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;

public class IcbcSignConsumerQueue implements Runnable{
	private static final long SLEEP_TIME = 650;
	private static Logger log = LoggerFactory.getLogger(IcbcSignConsumerQueue.class);

	@Override
	public void run() {
		while(true){
			//每次签名前间隔0.5-0.8秒
			mySleep(SLEEP_TIME, TimeUnit.MILLISECONDS);
			QueueBean signQueueBean = null;
			ISysAtomicInterface sysInter = null;
			try {
				signQueueBean = Queue.getInstance().getIcbcSignQueue().take();
				sysInter = signQueueBean.getSysInter();

				String jsonStr = null;
				IChannelInter channelInter = sysInter.getChannelInter();
				jsonStr = ProcessEntrance.getInstance().process(channelInter.getInter(), signQueueBean.getParams());
				sysInter.callBack(jsonStr);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					sysInter.callBack(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				continue;
			}
		}
	}
	public static void mySleep(long milliseccond, TimeUnit unit){
		try {
			unit.sleep(milliseccond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
