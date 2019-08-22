package com.qhjf.cfm.web.plugins;

import com.jfinal.plugin.redis.RedisPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CfmRedisPlugin extends RedisPlugin {

    private static final Logger logger = LoggerFactory.getLogger(CfmRedisPlugin.class);

    private boolean is_running = false;


    public CfmRedisPlugin(String cacheName, String host) {
        super(cacheName, host);
    }


    @Override
    public boolean start() {
        logger.debug("enter into start()");
        this.is_running = super.start();
        logger.debug("is_running is:"+this.is_running);
        return  this.is_running;
    }

    @Override
    public boolean stop() {
        logger.debug("enter into stop()");
        logger.debug("is_running is:"+this.is_running);
        if(is_running){
            super.stop();
        }
        this.is_running = false;
        return true;
    }
}
