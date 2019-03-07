package com.qhjf.cfm.web.utils.comm.file.tool;

import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;

public class DataDoubtfulCache {
    private RedisCacheConfigSection ini = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

    public enum DoubtfulType {
        OA, LA, EBS, LA_RECV
    }

    public Set<Long> getCacheValue(DoubtfulType type, String key) {
        return sGetValue(type.name() + "_" + key);
    }

    public void addCacheValue(DoubtfulType type, String key, String value) {
        sAddValue(type.name() + "_" + key, value);
    }

    private void sAddValue(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = Redis.use(ini.getCacheName()).getJedis();
            jedis.sadd(key, value);
        } finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
    }

    private Set<Long> sGetValue(String key) {
        Jedis jedis = null;
        Set<Long> result = null;
        try {
            jedis = Redis.use(ini.getCacheName()).getJedis();
            Set<String> source = jedis.smembers(key);
            if (source != null && source.size() > 0) {
                result = new HashSet<>();
                for (String value : source) {
                    result.add(Long.parseLong(value));
                }
            }
        } finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
        return result;
    }

}
