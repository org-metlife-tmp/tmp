package com.qhjf.cfm.web.plugins.jwt;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisJWTSerializer implements IJWTSerializer{

    private static final Logger log = LoggerFactory.getLogger(RedisJWTSerializer.class);

    private static RedisCacheConfigSection configSection = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);


    private static final String KEY_NAME = "JWT_TOKEN";


    @Override
    public Kv deSerialize() {
        log.debug("enter into deSerialize ...");
        Cache cache = Redis.use(configSection.getCacheName());
        Kv result = null;
        try{
            result =  cache.get(KEY_NAME);
        }catch (Exception e){
            e.printStackTrace();
            result =  Kv.create();
            cache.set(KEY_NAME, result);
        }
        if(result == null){
            result =  Kv.create();
            cache.set(KEY_NAME, result);
        }
        return result;
    }

    @Override
    public void serialize(Kv jwtStore) {
        log.debug("enter into serialize ...");
        Cache cache = Redis.use(configSection.getCacheName());
        cache.set(KEY_NAME,jwtStore);
        log.debug("leave   serialize  !");
    }

    @Override
    public void reStroe() {
        log.debug("enter into reStroe ...");
        Cache cache = Redis.use(configSection.getCacheName());
        cache.set(KEY_NAME,Kv.create());
        log.debug("leave   reStroe  !");
    }

    @Override
    public void recordTokenEffectiveTime(String token, long interval) {
        log.debug("enter into recordTokenEffectiveTime(token, interval) ...");
        Cache cache = Redis.use(configSection.getCacheName());
        cache.setex(token,(int)interval,interval);
        log.debug("leave   recordTokenEffectiveTime  !");
    }

    @Override
    public boolean isTokenTimeOut(String token) {
        log.debug("enter into isTokenTimeOut(token) ...");
        Cache cache = Redis.use(configSection.getCacheName());
        return cache.ttl(token) <= 0 ;
    }
}
