package com.qhjf.cfm.web.channel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;

public class SerialNoTest {
	
	private RedisPlugin cfmRedis = null;
	
	@Test
	public void Test(){
		cfmRedis = new CfmRedisPlugin("cfm", "10.164.26.48");
        cfmRedis.setSerializer(new JdkSerializer());
        cfmRedis.start();
		final Set<String> idSet = Collections.synchronizedSet(new HashSet<String>());
        ExecutorService es = Executors.newFixedThreadPool(100);
        for (int i=0; i<2000; i++){
        	final int j= i;
            es.submit(new Runnable() {
                public void run() {
                    String val = null;
					try {
						
						val = ChannelManager.getSerianlNo("308");
						System.out.println(j+":"+val);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
                    if (idSet.contains(val)){
                        System.out.println("重复了: " + val);
                    }else{
                        idSet.add(val);
                    }
                }
            });
        }
        es.shutdown();
        System.out.println("启用顺序关闭");
        while(true){
            if(es.isTerminated()){
                System.out.println("所有的子线程都结束了！");
                break;
            }
           
        }
	}

}
