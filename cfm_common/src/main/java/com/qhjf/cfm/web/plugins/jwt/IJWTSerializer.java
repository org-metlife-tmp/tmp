package com.qhjf.cfm.web.plugins.jwt;

import com.jfinal.kit.Kv;

public interface IJWTSerializer {

    /**
     * 反序列化 获取反序列化对象
     * @return
     */
    Kv deSerialize();

    /**
     * 序列化
     * @param jwtStore
     */
    void serialize(Kv jwtStore);


    void reStroe();


    /**
     * 记录token的有效时间
     * @param token   token
     * @param interval 最长时间间隔
     */
    void recordTokenEffectiveTime(String token, long interval);


    /**
     * token 当前是否在有效时间内
     * @param token
     * @return
     */
    boolean isTokenTimeOut(String token);
}
