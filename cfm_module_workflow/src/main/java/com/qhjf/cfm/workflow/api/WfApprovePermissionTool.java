package com.qhjf.cfm.workflow.api;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 审批流权限工具
 */
public class WfApprovePermissionTool {

    private static final Log logger = LogbackLog.getLog(WfApprovePermissionTool.class);


    private GlobalConfigSection ini = GlobalConfigSection.getInstance();

    private IWfApprovePermissionCache cache ;

    private WfApprovePermissionTool(){
        init();
    }
    public static WfApprovePermissionTool INSTANCE;

    public static WfApprovePermissionTool getINSTANCE() {
        if(INSTANCE == null){
            INSTANCE = new WfApprovePermissionTool();
        }
        return INSTANCE;
    }

    /**
     * 初始化
     * 初始化 WfApprovePermissionMap
     */
    private void init(){
        logger.info("enter into init");
        List<Record> all_wf_run_inst = Db.find(Db.getSql("wfprocess.findAllWfRunInst"));

        if(ini.hasConfig(IConfigSectionType.DefaultConfigSectionType.Redis)){
            logger.debug("WfApprovePermissionTool cache is use redis");
            cache = new WfApprovePermissionRedisCache();
        }else{
            logger.debug("WfApprovePermissionTool cache is use Map");
            cache = WfApprovePermissionMap.getInstance();
        }
        if(all_wf_run_inst != null && all_wf_run_inst.size() > 0){
            for (Record record : all_wf_run_inst) {
                if(record.getInt("shadow_execute") == WebConstant.YesOrNo.YES.getKey()) { //加签名
                    addUserApprovePermissions(new Long[]{TypeUtils.castToLong(record.get("shadow_user_id"))},
                            TypeUtils.castToLong(record.get("id")));
                }else{
                    Record curNode = null;
                    if(record.getInt("workflow_type") == WorkflowConstant.WorkFlowType.CUSTOM.getKey()){
                        curNode = Db.findById("cfm_workflow_def_custom_node","id",record.get("workflow_node_id"));
                        if(curNode != null){
                            addUserApprovePermissions(new Long[]{TypeUtils.castToLong(curNode.get("user_id"))},
                                    TypeUtils.castToLong(record.get("id")));
                        }
                    }else{
                        curNode = Db.findById("cfm_workflow_def_node","id",record.get("workflow_node_id"));
                        if(curNode != null){
                            try {
                                addApprovePermissions(curNode,record);
                            } catch (WorkflowException e) {
                                logger.error("初始化 WfApprovePermissionMap 异常："+e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        logger.info("begin authorized process");
        List<Record> all_authorizd_lists = Db.find(Db.getSql("wfprocess.findAuthorizedRelation"));
        if(all_authorizd_lists != null && all_authorizd_lists.size() > 0){
            logger.info("all_authorizd_lists.size is :"+all_authorizd_lists.size());
            for (Record authorized : all_authorizd_lists) {
                Long au_usr_id = TypeUtils.castToLong(authorized.get("authorize_usr_id"));
                Long be_au_usr_id = TypeUtils.castToLong(authorized.get("be_authorize_usr_id"));
                Date sdate = TypeUtils.castToDate(authorized.get("start_date"));
                Date edate = TypeUtils.castToDate(authorized.get("end_date"));
                WfAuthorizeRelation relation = new WfAuthorizeRelation(au_usr_id, sdate,edate);
                cache.addAuthorizeRelation(au_usr_id,be_au_usr_id,relation);
            }

        }
        logger.info("leave  init");
    }


    /**
     * 添加审批实例的审批
     * @param wf_cur_node_define
     *          当前的实例节点定义
     * @param wf_ru_inst
     *          当前的流程实例
     * @throws WorkflowException
     */
    public void addApprovePermissions(Record wf_cur_node_define, Record wf_ru_inst)throws WorkflowException {
        Long instId = wf_ru_inst.getLong("id");
        WfApprovePermission approvePerminsionss = nodeExpressParse(wf_cur_node_define);
        Long[] userArr = approvePerminsionss.getUsers();
        String org = approvePerminsionss.getPush_org();
        Long[] exclude_users = approvePerminsionss.getExlude_users();
        Long[] poss = approvePerminsionss.getPosition();
        addUserApprovePermissions(userArr, instId);
        addOPApprovePermissions(org, poss,wf_ru_inst);
        addExcludeUserApprovePermissions(exclude_users,wf_ru_inst);
    }



    public void reduceApprovePermissions(Record wf_cur_node_define, Record wf_ru_inst)throws WorkflowException {
        Long instId = wf_ru_inst.getLong("id");
        WfApprovePermission approvePerminsionss = nodeExpressParse(wf_cur_node_define);
        Long[] userArr = approvePerminsionss.getUsers();
        String org = approvePerminsionss.getPush_org();
        Long[] exclude_users = approvePerminsionss.getExlude_users();
        Long[] poss = approvePerminsionss.getPosition();
        reduceUserApprovePermissions(userArr,instId);
        reduceOPApprovePermissions(org,poss,wf_ru_inst);
        reduceExcludeUserApprovePermissions(exclude_users,wf_ru_inst);
    }


    /**
     * 刷新授权记录
     * @param authorized_usr_id
     * @param be_authorized_usr_id
     * @param relation
     */
    public void refreshAuthorized(Long authorized_usr_id , Long be_authorized_usr_id, WfAuthorizeRelation relation){
        cache.addAuthorizeRelation(authorized_usr_id,be_authorized_usr_id,relation);
    }


    /**
     * 解析节点的节点表达式
     * @param node
     * @return
     * @throws WorkflowException
     */
    public WfApprovePermission nodeExpressParse(Record node) throws WorkflowException {
        String candidatesExpress = node.getStr("node_exp");
        if(candidatesExpress == null || candidatesExpress.length() <=0){
            throw new WorkflowException("节点候选人的表达式为空");
        }
        WfApprovePermission nodeExpress = com.alibaba.fastjson.JSONObject.parseObject(candidatesExpress, WfApprovePermission.class);
        return nodeExpress;
    }


    public void reduceUserApprovePermissions(Long[] userArr, Long instId) {
        if (userArr != null && userArr.length > 0) {
            for (Long user : userArr) {
                cache.reduceUserWorkflowPerminssionsMap(user,instId);
            }
        }
    }

    public void addUserApprovePermissions(Long[] userArr, Long instId) {
        if (userArr != null && userArr.length > 0) {
            for (Long user : userArr) {
                cache.addUserWorkflowPerminssionsMap(user,instId);
            }
        }
    }


    private void addOPApprovePermissions(String org, Long[] posAttr, Record ruInst)throws WorkflowException {
        // 如果公司和用户都不为空,则添加权限组的相关权限
        List<Long> orgIds = orgParse(org,ruInst);
        if (orgIds != null && orgIds.size()>0) {
            for(Long orgId:orgIds){
                if(posAttr == null || posAttr.length <= 0){
                    String permissionsKey = getPermissionsKey(orgId, null);
                    cache.addOpWorkflowPerminssions(permissionsKey, ruInst.getLong("id"));
                }else{
                    for (Long pos : posAttr) {
                        String permissionsKey = getPermissionsKey(orgId, pos);
                        cache.addOpWorkflowPerminssions(permissionsKey, ruInst.getLong("id"));
                    }
                }
            }
        }
    }

    private void reduceOPApprovePermissions(String org, Long[] posAttr, Record ruInst)throws WorkflowException{
        // 如果公司和用户都不为空,则添加权限组的相关权限
        List<Long> orgIds = orgParse(org,ruInst);
        if (orgIds != null && orgIds.size()>0  && posAttr != null && posAttr.length > 0) {
            for(Long orgId:orgIds){
                for (Long group : posAttr) {
                    String permissionsKey = getPermissionsKey(orgId, group);
                    cache.reduceOpWorkflowPerminssions(permissionsKey, ruInst.getLong("id"));
                }
            }

        }
    }


    private void addExcludeUserApprovePermissions(Long[] exclude_users, Record ruInst)throws WorkflowException {
        // 4.如果公司和用户都不为空,则添加权限组的相关权限
        Long[] desc_exclude_users = excludeUsersParse(exclude_users,ruInst);
        if (desc_exclude_users != null && desc_exclude_users.length>0) {
            for(Long userId:desc_exclude_users){
                cache.addExcludeUserWorkflowPerminssions(userId, ruInst.getLong("id"));
            }

        }
    }

    private void reduceExcludeUserApprovePermissions(Long[] exclude_users, Record ruInst) throws WorkflowException{
        // 如果公司和用户都不为空,则添加权限组的相关权限
        Long[] desc_exclude_users = excludeUsersParse(exclude_users,ruInst);
        if (desc_exclude_users != null && desc_exclude_users.length>0) {
            for(Long userId:desc_exclude_users){
                cache.reduceExcludeUserWorkflowPerminssions(userId, ruInst.getLong("id"));
            }

        }
    }


    /**
     * 公司列表解析
     * @param orgs
     *          公司表达式
     * @param ruInst
     *         当前的工作流实例
     * @return
     * @throws WorkflowException
     */
    public List<Long> orgParse(String orgs, Record ruInst)throws WorkflowException {
        if(orgs == null || orgs.length()<=0){
            return null;
        }
        List<Long> orgIds = new ArrayList<Long>();
        if (orgs.startsWith("+")){
            Integer orgLevel = Integer.parseInt(orgs.substring(1));
            Record init_org = Db.findById("organization","org_id", ruInst.get("init_org_id"));
            Record destOrg = null;
            if(init_org != null){
                if(init_org.get("parent_id") == null && orgLevel > 0){
                    throw new WorkflowException("一级机构没有上级机构");
                }
                long parent_id = TypeUtils.castToLong(init_org.get("parent_id"));

                for(int i =0 ; i< orgLevel ; i++){
                    destOrg = Db.findById("organization","org_id",parent_id);
                    if(destOrg == null){
                        throw new WorkflowException("上"+(i+1)+"公司不存在");
                    }
                    if(destOrg.get("parent_id") != null){
                        parent_id =  TypeUtils.castToLong(destOrg.get("parent_id"));
                    }else{
                        parent_id = -1;
                    }
                }

            }else{
                throw new WorkflowException("单据提交公司不存在！");
            }
            orgIds.add(TypeUtils.castToLong(destOrg.get("org_id")));
        }else if(orgs.startsWith("-")){
            Integer orgLevel = Integer.parseInt(orgs.substring(1));
            Record init_org = Db.findById("organization","org_id", ruInst.get("init_org_id"));
            if(init_org != null){
                List<Record> records = Db.find(Db.getSql("wfprocess.findSubOrg"), init_org.getStr("level_code")+"%",
                        init_org.getInt("level_num")+orgLevel);
                if(records != null && records.size() > 0){
                    for (Record record : records) {
                        orgIds.add(TypeUtils.castToLong(record.get("org_id")));
                    }
                }else{
                    throw new WorkflowException("下"+orgLevel+"公司不存在");
                }

            }else{
                throw new WorkflowException("单据提交公司不存在！");
            }
        }else if(orgs.equals("0")){
            orgIds.add(ruInst.getLong("init_org_id"));
        }else{
            String[] orgArr = orgs.split(",");
            for(String orgPer:orgArr){
                orgIds.add(Long.parseLong(orgPer));
            }

        }
        return orgIds;
    }



    /**
     * 获取公司+职位的key
     * @param orgId
     * @param groupId
     * @return
     */
    public String getPermissionsKey(Long orgId, Long groupId) {
        if (orgId != null) {
            if(groupId != null){
                return "O" + orgId + "P" + groupId;
            }else{
                return "O" + orgId;
            }
        }
        return null;
    }



    public Long[] excludeUsersParse(Long[] exclude_users, Record ruInst){
        if(exclude_users == null || exclude_users.length<=0){
            return null;
        }
        if(exclude_users.length == 1 && exclude_users[0] == -1){
            return new Long[]{ruInst.getLong("init_user_id")};
        }else{
            return exclude_users;
        }
    }



    public Set<Long> getInstSetByUser(long user_id){
        return cache.getInstSetByUser(user_id);
    }

    public Set<Long> getExcludeInstSetByUser(long user_id){
        return cache.getExcludeInstSetByUser(user_id);
    }

    public Set<Long> getInstSetByOp(String op){
        return cache.getInstSetByOp(op);
    }

    public Set<Long> getBeAuthorizeInstByUser(long user_id ){
        return cache.getBeAuthorizeInstByUser(user_id);
    }




}
