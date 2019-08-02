package com.qhjf.cfm.queue;

import com.jfinal.plugin.IPlugin;

/**
 * 队列插件
 * @author yunxw
 *
 */
public class QueuePlugin implements IPlugin{    //建个工行，
	
	private static final String cmbcThreadName = "招行消费者队列";
	
	private static final String icbcThreadName = "工行消费者队列";
	
	private static final String icbcSignThreadName = "工行签名消费者队列";

	private static final String fingardThreadName = "保融消费者队列";

    private static int cmbcConsumerCount = 1;   //定义最大的消费线程数
    
    private static int icbcConsumerCount = 2;   //定义最大的消费线程数
    
    
    private static int icbcSignConsumerCount = 1;   //定义最大的消费线程数

	private static int fingardConsumerCount = 1;   //定义最大的消费线程数

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
		
		IcbcSignConsumerQueue icbcSignConsumer = new IcbcSignConsumerQueue();
		for(int i = 0;i < icbcSignConsumerCount;i++){
			new Thread(icbcSignConsumer,icbcSignThreadName+"["+(i+1)+"]").start();
		}

		FingardConsumerQueue fingardConsumerQueue = new FingardConsumerQueue();
		for(int i = 0;i < fingardConsumerCount;i++){
			new Thread(fingardConsumerQueue,fingardThreadName+"["+(i+1)+"]").start();
		}
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		return true;
	}

}
