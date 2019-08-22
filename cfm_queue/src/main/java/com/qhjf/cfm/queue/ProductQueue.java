package com.qhjf.cfm.queue;

import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 队列生产者
 * @author yunxw
 *
 */
public class ProductQueue implements Runnable{
	private static final Logger log = LoggerFactory.getLogger(ProductQueue.class);
	private static final String ICBC_SIGN_LOG = "工行指令进入签名队列：原子接口[{}]，请求参数[{}]";
	private static final String ICBC_LOG = "工行指令进入一般队列：原子接口[{}]，请求参数[{}]";
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
				ISysAtomicInterface sysInter = queueBean.getSysInter();
				String className = sysInter.getClass().getSimpleName();
				if ("SysOaSinglePayInter".equals(className) 
						|| "SysSinglePayInter".equals(className) 
						|| "SysElectronicQueryInter".equals(className)
						|| "SysNcSinglePayInter".equals(className)
						|| "SysSftSinglePayInter".equals(className)
						|| "SysBatchRecvInter".equals(className)) {
					log.debug(ICBC_SIGN_LOG, className, queueBean.getParams());
					Queue.getInstance().getIcbcSignQueue().put(queueBean);
				}else {
					log.debug(ICBC_LOG, className, queueBean.getParams());
					Queue.getInstance().getIcbcQueue().put(queueBean);
				}
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
