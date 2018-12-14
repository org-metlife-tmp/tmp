package com.qhjf.cfm.queue;

import com.jfinal.plugin.IPlugin;

/**
 * 队列插件
 * @author yunxw
 *
 */
public class QueuePlugin implements IPlugin{
	
	private static final String cmbcThreadName = "招行消费者队列";
	
	private static final String icbcThreadName = "工行消费者队列";

    private static int cmbcConsumerCount = 1;   //定义最大的消费线程数
    
    private static int icbcConsumerCount = 4;   //定义最大的消费线程数

	@Override
	public boolean start() {
		CmbcConsumerQueue cmbcConsumer = new CmbcConsumerQueue();
		for(int i = 0;i < cmbcConsumerCount;i++){
			new Thread(cmbcConsumer,cmbcThreadName+"["+(i+1)+"]").start();
		}
		
		IcbcConsumerQueue icbcConsumer = new IcbcConsumerQueue();
		for(int i = 0;i < icbcConsumerCount;i++){
			new Thread(icbcConsumer,icbcThreadName+"["+(i+1)+"]").start();
		}
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return true;
	}

}
