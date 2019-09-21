package com.qhjf.cfm.queue;

import com.qhjf.bankinterface.api.ProcessEntrance;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version 1.0
 * @program: sunlife_cfm_main->FingardConsumerQueue
 * @description: 保融消费者队列
 * @author: 耿鑫
 * @create: 2019-08-01 17:38
 **/
public class FingardConsumerQueue implements Runnable {

    private static Logger log = LoggerFactory.getLogger(FingardConsumerQueue.class);

    @Override
    public void run() {
        while (true) {
            QueueBean queueBean = null;
            ISysAtomicInterface sysInter = null;
            try {
                queueBean = Queue.getInstance().getFingradQueue().take();
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
