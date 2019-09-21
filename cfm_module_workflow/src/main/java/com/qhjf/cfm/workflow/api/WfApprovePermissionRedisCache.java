package com.qhjf.cfm.workflow.api;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * 批流权限信息，存储对应的权限与审批流当前实例的对应关系
 * WF_U_+usr_id : 存储对应用户ID 能看到的流程实例， 存储结构为set
 * WF_OP_+usr_id: 存储对应机构——职位能看到的流程实例， 存储结构为set
 * WF_EX_+usr_id: 存储对应用户ID 被排除的能看到的流程实例， 存储结构为set
 * WF_AU_+usr_id :  存储授权关系， 存储结构为string , key值为授权人id的， value 被授权人id
 *  WF_BE_AU+被授权人ID+@+授权人ID ： 存储授权详情况, 存储结构为hash
 */
public class WfApprovePermissionRedisCache implements IWfApprovePermissionCache {

    private RedisCacheConfigSection ini = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

    private static final String user_prefix = "WF_U_";

    private static final String op_prefix = "WF_OP_";

    private static final String exclude_prefix = "WF_EX_";

    private static final String authorize_prefix = "WF_AU_";

    private static final String be_authorize_prefix = "WF_BE_AU_";


    @Override
    public void addOpWorkflowPerminssions(String opName, Long ruInstId) {
        sAddValue(op_prefix+opName,ruInstId+"");
    }

    @Override
    public void addUserWorkflowPerminssionsMap(Long userId, Long ruInstId) {
        sAddValue(user_prefix+userId,ruInstId+"");
    }

    @Override
    public void addExcludeUserWorkflowPerminssions(Long userId, Long ruInstId) {
        sAddValue(exclude_prefix+userId,ruInstId+"");
    }


    @Override
    public void reduceOpWorkflowPerminssions(String opName, Long ruInstId) {
        sRemValue(op_prefix+opName,ruInstId+"");
    }

    @Override
    public void reduceUserWorkflowPerminssionsMap(Long userId, Long ruInstId) {
        sRemValue(user_prefix+userId,ruInstId+"");
    }

    @Override
    public void reduceExcludeUserWorkflowPerminssions(Long userId, Long ruInstId) {
        sRemValue(exclude_prefix+userId,ruInstId+"");

    }

    @Override
    public void addAuthorizeRelation(Long authorize_usr, Long beAuthorize_usr, final WfAuthorizeRelation relation) {
        Jedis jedis = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            //获取旧的授权ID
            String old_beAuthorize_usr = jedis.get(authorize_prefix+authorize_usr);
            if(old_beAuthorize_usr != null && !"".equals(old_beAuthorize_usr)){
                reduceAuthorizeRelation(authorize_usr,Long.parseLong(old_beAuthorize_usr));
            }
            jedis.set(authorize_prefix+authorize_usr,beAuthorize_usr+"");
            final String usr_id = authorize_usr+"";
            final String startDate = DateKit.toStr(relation.getStartDate());
            final String endDate = DateKit.toStr(relation.getEndDate());
            jedis.hmset(be_authorize_prefix+beAuthorize_usr+"@"+authorize_usr,new HashMap<String, String>(){
                        {
                            put("usr_id",usr_id);
                            put("sdate",startDate);
                            put("edate",endDate);
                        }
                    });
        }finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
    }


    @Override
    public void reduceAuthorizeRelation(Long authorize_usr, Long beAuthorize_usr) {
        Jedis jedis = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            jedis.del(authorize_prefix+authorize_usr);
            jedis.del(be_authorize_prefix+beAuthorize_usr+"@"+authorize_usr);

        }finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }

    }

    @Override
    public Set<Long> getInstSetByUser(long user_id) {
        return sGetValue(user_prefix+user_id);
    }

    @Override
    public Set<Long> getInstSetByOp(String op) {
        return sGetValue(op_prefix+op);
    }

    @Override
    public Set<Long> getExcludeInstSetByUser(long user_id) {
        return sGetValue(exclude_prefix+user_id);
    }

    @Override
    public Set<Long> getBeAuthorizeInstByUser(long user_id) {
        Set<Long> result  = new HashSet<>();
        Jedis jedis = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            Set<String> keys = jedis.keys(be_authorize_prefix+user_id+"@"+"*");
            if(keys != null && keys.size()> 0){
                for (String key : keys) {
                    Map<String, String> map = jedis.hgetAll(key);
                    if(isValidate(map.get("sdate"),map.get("edate"))){
                        Set<Long> temp = sGetValue(user_prefix+map.get("usr_id"));
                        if(temp != null && temp.size() > 0){
                            result.addAll(temp);
                        }
                    }
                }
            }
        }finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
        return result;
    }


    private void sAddValue(String key , String value){
        Jedis jedis = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            jedis.sadd(key,value);
        }finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
    }


    private void sRemValue(String key, String value){
        Jedis jedis = null;
        try{
            jedis = Redis.use(ini.getCacheName()).getJedis();
            jedis.srem(key,value);
        }finally {
            Redis.use(ini.getCacheName()).close(jedis);
        }
    }

    private Set<Long> sGetValue(String key){
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


    private boolean isValidate(String sdate, String edate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(DateKit.toDate(sdate));
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND,0);
        Date start = cal.getTime();

        cal.setTime(DateKit.toDate(edate));
        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND,59);

        Date end = cal.getTime();

        Date now = new Date(System.currentTimeMillis()); //获取系统当前时间;
        return now.getTime() >= start.getTime() && now.getTime() <= end.getTime();

    }
}
