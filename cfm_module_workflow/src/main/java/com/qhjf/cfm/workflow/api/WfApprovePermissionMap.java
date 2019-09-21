package com.qhjf.cfm.workflow.api;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 审批流权限信息，存储对应的权限与审批流当前实例的对应关系
 * 其中对应的权限有三种：
 * 一是公司信息加上职位信息，key值为O{orgid}P{posid}
 * 一是用户信息，key值为userid
 * 一是排除用户，key值为userid
 *
 */
class WfApprovePermissionMap implements Serializable, IWfApprovePermissionCache {


    private static final long serialVersionUID = 1l;

    private Map<String, Set<Long>> opWorkflowPermissionsMap;

    private Map<Long, Set<Long>> userWorkflowPermissionsMap;

    private Map<Long, Set<Long>> excludeUserWorkflowPermissionsMap;

    private Map<Long,Long> authorizeRelationMap ;   //授权信息对应表  key：授权人 ， value:被授权人

    private Map<Long,Map<Long,WfAuthorizeRelation>> beAuthorizeRelationMap; //被授权信息对应表 key： 被授权人 value: 被授权信息

    private Lock opWorkflowPermissionsMapLock = new ReentrantLock();

    private Lock userWorkflowPermissionsMapLock = new ReentrantLock();

    private Lock excludeUserWorkflowPermissionsMapLock  = new ReentrantLock();

    private Lock authorizeRelationLock = new ReentrantLock();


    private WfApprovePermissionMap(){
        this.opWorkflowPermissionsMap = new ConcurrentHashMap<>();
        this.userWorkflowPermissionsMap = new ConcurrentHashMap<>();
        this.excludeUserWorkflowPermissionsMap = new ConcurrentHashMap<>();
        this.authorizeRelationMap = new ConcurrentHashMap<>();
        this.beAuthorizeRelationMap = new ConcurrentHashMap<>();

    }


    private static WfApprovePermissionMap INSTANCE = new WfApprovePermissionMap();

    public static WfApprovePermissionMap getInstance(){
        return INSTANCE;
    }

    @Override
    public void addOpWorkflowPerminssions(String opName, Long ruInstId) {
        opWorkflowPermissionsMapLock.lock();
        try {
            addMap(opWorkflowPermissionsMap, opName, ruInstId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            opWorkflowPermissionsMapLock.unlock();
        }
    }

    @Override
    public void addUserWorkflowPerminssionsMap(Long userId, Long ruInstId) {
        userWorkflowPermissionsMapLock.lock();
        try {
            addMap(userWorkflowPermissionsMap, userId, ruInstId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userWorkflowPermissionsMapLock.unlock();
        }
    }

    @Override
    public void addExcludeUserWorkflowPerminssions(Long userId, Long ruInstId) {
        excludeUserWorkflowPermissionsMapLock.lock();
        try {
            addMap(excludeUserWorkflowPermissionsMap, userId, ruInstId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            excludeUserWorkflowPermissionsMapLock.unlock();
        }
    }

    @Override
    public void reduceOpWorkflowPerminssions(String opName, Long ruInstId) {
        opWorkflowPermissionsMapLock.lock();
        try {
            reduceMap(opWorkflowPermissionsMap, opName, ruInstId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            opWorkflowPermissionsMapLock.unlock();
        }
    }

    @Override
    public void reduceUserWorkflowPerminssionsMap(Long userId, Long ruInstId) {
        userWorkflowPermissionsMapLock.lock();
        try {
            reduceMap(userWorkflowPermissionsMap, userId, ruInstId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userWorkflowPermissionsMapLock.unlock();
        }
    }

    @Override
    public void reduceExcludeUserWorkflowPerminssions(Long userId, Long ruInstId) {
        excludeUserWorkflowPermissionsMapLock.lock();
        try {
            reduceMap(excludeUserWorkflowPermissionsMap, userId, ruInstId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            excludeUserWorkflowPermissionsMapLock.unlock();
        }
    }

    @Override
    public void addAuthorizeRelation(Long authorize_usr, Long beAuthorize_usr, WfAuthorizeRelation relation) {
        authorizeRelationLock.lock();
        try{
            Long old_beAuthorize_usr = authorizeRelationMap.get(authorize_usr);
            if(old_beAuthorize_usr != null){
                reduceAuthorizeRelation(authorize_usr,old_beAuthorize_usr);
            }
            authorizeRelationMap.put(authorize_usr,beAuthorize_usr);
            Map<Long, WfAuthorizeRelation> beMap = beAuthorizeRelationMap.get(beAuthorize_usr);
            if(beMap == null){
                beMap = new ConcurrentHashMap<>();
            }
            beMap.put(authorize_usr,relation);
            beAuthorizeRelationMap.put(beAuthorize_usr,beMap);
        }catch (Exception e){

        }finally {
            authorizeRelationLock.unlock();
        }
    }


    @Override
    public void reduceAuthorizeRelation(Long authorize_usr, Long beAuthorize_usr) {
        authorizeRelationMap.remove(authorize_usr);
        beAuthorizeRelationMap.remove(beAuthorize_usr);
    }

    @Override
    public Set<Long> getInstSetByUser(long user_id) {
        return  userWorkflowPermissionsMap.get(user_id);
    }

    @Override
    public Set<Long> getInstSetByOp(String op) {
        return opWorkflowPermissionsMap.get(op);
    }

    @Override
    public Set<Long> getExcludeInstSetByUser(long user_id) {
        return excludeUserWorkflowPermissionsMap.get(user_id);
    }

    @Override
    public Set<Long> getBeAuthorizeInstByUser(long user_id) {
        Set<Long> result = new HashSet<>();
        Map<Long,WfAuthorizeRelation> bMap = beAuthorizeRelationMap.get(user_id);
        if(bMap != null && bMap.size() > 0){
            for (Long authorize_usr_id : bMap.keySet()) {
                WfAuthorizeRelation relation = bMap.get(authorize_usr_id);
                if(relation.isValidate()){
                    result.addAll(getInstSetByUser(authorize_usr_id));
                }
                
            }
        }
        return result;
    }

    private static <T> void addMap(Map<T, Set<Long>> map, T key, Long value) {
        if (map == null) {
            map = new ConcurrentHashMap();
        }
        Set<Long> ruInstIdSet = map.get(key);
        if (ruInstIdSet == null) {
            ruInstIdSet = new HashSet<Long>();
        }
        ruInstIdSet.add(value);
        map.put(key, ruInstIdSet);
    }


    private static <T> void reduceMap(Map<T, Set<Long>> map, T key, Long value) {
        if (map != null) {
            Set<Long> values = map.get(key);
            if (values != null) {
                values.remove(value);
                if (values.size() == 0) {
                    map.remove(key);
                } else {
                    map.put(key, values);
                }
            }
        }

    }
}
