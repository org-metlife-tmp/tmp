package com.qhjf.cfm.web.webservice.oa.server;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.workflow.api.WfAuthorizeRelation;
import redis.clients.jedis.Jedis;

import java.util.*;

public class OaDataDoubtfulCache {
    private RedisCacheConfigSection ini = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

    private static final String oa_prefix = "OA_";

    public Set<Long> getOaCacheValue(String key) {
        return sGetValue(oa_prefix+key);
    }
    public void addOaCacheValue(String key, String value){
        sAddValue(oa_prefix+key, value);
    }
    public void sAddValue(String key , String value){
        Jedis jedis = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            jedis.sadd(key,value);
            
        } finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
    }

    public Set<Long> sGetValue(String key){
        Jedis jedis = null;
        Set<Long> result = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            Set<String> source = jedis.smembers(key);
            if(source != null && source.size() > 0){
                result = new HashSet<>();
                for (String value : source) {
                    result.add(Long.parseLong(value));
                }
            }
        }finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
        return result;
    }
    
    public Set<String> GetValue(String key){
        Jedis jedis = null;
        Set<String> result = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            Set<String> source = jedis.smembers(key);
            if(source != null && source.size() > 0){
                result = new HashSet<>();
                for (String value : source) {
                    result.add(value);
                }
            }
        }finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
        return result;
    }
    /**
     * 删除value中set集合中的特定值
     * @param pre
     * @param value
     */
	public void sremValue(String key, String value) {
		Jedis jedis = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            jedis.srem(key,value);           
        } finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
		
	}

}
