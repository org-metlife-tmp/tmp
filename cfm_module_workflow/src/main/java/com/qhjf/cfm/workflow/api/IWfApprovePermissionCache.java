package com.qhjf.cfm.workflow.api;

import java.util.Set;

public interface IWfApprovePermissionCache {


    /**
     * 添加机构+职位与流程实例的对应关系
     * @param opName   机构+职位表达式
     * @param ruInstId 流程实例id
     */
    void addOpWorkflowPerminssions(String opName, Long ruInstId);

    /**
     * 添加用户参与流程实例的对应关系
     * @param userId  用户id
     * @param ruInstId 流程实例id
     */
    void addUserWorkflowPerminssionsMap(Long userId, Long ruInstId);

    /**
     * 添加用户不能参与流程实例的对应关系
     * @param userId 用户id
     * @param ruInstId 流程实例id
     */
    void addExcludeUserWorkflowPerminssions(Long userId, Long ruInstId);


    /**
     * 去除机构+职位与流程实例的对应关系
     * @param opName  机构+职位表达式
     * @param ruInstId 流程实例ids
     */
    void reduceOpWorkflowPerminssions(String opName, Long ruInstId);


    /**
     * 去除用户参与流程实例的对应关系
     * @param userId 用户id
     * @param ruInstId 流程实例id
     */
    void reduceUserWorkflowPerminssionsMap(Long userId, Long ruInstId);


    /**
     * 去除用户不能参与流程实例的对应关系
     * @param userId   用户id
     * @param ruInstId 流程实例id
     */
    void reduceExcludeUserWorkflowPerminssions(Long userId, Long ruInstId);


    /**
     * 添加授权及被授权关系
     * @param authorize_usr     授权用户ID
     * @param beAuthorize_usr   被授权用户ID
     */
    void addAuthorizeRelation(Long authorize_usr , Long beAuthorize_usr,WfAuthorizeRelation relation);


    /**
     * 去除授权及被授权关系
     * @param authorize_usr
     * @param beAuthorize_usr
     */
    void reduceAuthorizeRelation(Long authorize_usr , Long beAuthorize_usr );



    /**
     * 获取用户能参与的流程实例的集合
     * @param user_id  用户id
     * @return
     */
    Set<Long> getInstSetByUser(long user_id);


    /**
     * 获取机构+职位表达式能参与流程实例的集合
     * @param op  机构+职位表达式
     * @return
     */
    Set<Long> getInstSetByOp(String op);

    /**
     * 获取用户不能参与的流程实例的集合
     * @param user_id 用户id
     * @return
     */
    Set<Long> getExcludeInstSetByUser(long user_id);


    /**
     * 获取被授权参与的流程实例的集合
     * @param user_id
     * @return
     */
    Set<Long> getBeAuthorizeInstByUser(long user_id);
}
