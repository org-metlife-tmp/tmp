package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.JSONUtil;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.workflow.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 工作流查询服务
 */
public class WorkflowQueryService {

    private static final Log logger = LogbackLog.getLog(WorkflowProcessService.class);

    private static final WfApprovePermissionTool approvePermission = WfApprovePermissionTool.getINSTANCE();


    /**
     * 根据工作流类型获取节点驱动工具
     *
     * @param workflow_type 工作流类型
     * @return
     * @throws WorkflowException
     */
    public static IWfNodeDrive getNodeDrive(int workflow_type) throws WorkflowException {
        if (workflow_type == WorkflowConstant.WorkFlowType.BUILDIN.getKey() ||
                workflow_type == WorkflowConstant.WorkFlowType.NORMAL.getKey()) {
            return new WfNodeDriveImpl();
        } else if (workflow_type == WorkflowConstant.WorkFlowType.CUSTOM.getKey()) {
            return new WfCustomNodeDriveImpl();
        } else {
            throw new WorkflowException("未定义的工作流类型[" + workflow_type + "]");
        }
    }


    /**
     * 获取当前节点的审批人列表
     *
     * @param obj 请求对象
     * @return
     */
    public List<Record> getCurrApproveUser(WfRequestObj obj) throws WorkflowException {
        Record wf_cur_ru_inst = Db.findById("cfm_workflow_run_execute_inst", "biz_type,bill_id", obj.getBizType().getKey(), obj.getBillId());
        return getCurrApproveUserRecords(wf_cur_ru_inst);
    }

    public List<Record> getCurrApproveUser(long billId, int bizType) throws WorkflowException {
        Record wf_cur_ru_inst = Db.findById("cfm_workflow_run_execute_inst", "biz_type,bill_id", bizType, billId);
        return getCurrApproveUserRecords(wf_cur_ru_inst);
    }

    private List<Record> getCurrApproveUserRecords(Record wf_cur_ru_inst) throws WorkflowException {
        List<Record> users = null;
        if (wf_cur_ru_inst != null) {
            if (wf_cur_ru_inst.getInt("shadow_execute") == WebConstant.YesOrNo.YES.getKey()) {
                Record speifiedUser = Db.findFirst(Db.getSql("wfprocess.findWfSpecifiedUser"), wf_cur_ru_inst.get("shadow_user_id"));
                if (speifiedUser != null) {
                    users = new ArrayList<>();
                    users.add(speifiedUser);
                }
            } else {
                Record curNode = Db.findById("cfm_workflow_def_node", "id", wf_cur_ru_inst.get("workflow_node_id"));
                if (curNode != null) {
                    WfApprovePermission bean = approvePermission.nodeExpressParse(curNode);
                    users = getApproveByWfApprovePermission(bean, wf_cur_ru_inst);
                }
            }
        }
        return users;
//        else {
//            throw new WorkflowException("未找到对象的工作流实例");
//        }
    }

    /**
     * 获取后续节点的审批人列表
     *
     * @param level 后续第几级， 0：当前节点， 1：下一级节点, 2: 下两级节点
     * @param obj   请求对象
     * @return
     * @throws WorkflowException
     */
    public List<Record> getNextApproveUser(Integer level, WfRequestObj obj) throws WorkflowException {
        List<Record> users = null;
        if (level == null) {
            throw new WorkflowException("请指定查询级别");
        }
        if (level.equals(0)) {
            return getCurrApproveUser(obj);
        }
        Record wf_cur_ru_inst = Db.findById("cfm_workflow_run_execute_inst", "biz_type,bill_id", obj.getBizType().getKey(), obj.getBillId());
        if (wf_cur_ru_inst != null) {
            IWfNodeDrive nodeDrive = WorkflowQueryService.getNodeDrive(wf_cur_ru_inst.getInt("workflow_type"));
            int index = 0;
            Record curNode = Db.findFirst(Db.getSql("wfprocess.findwfnodeById"), wf_cur_ru_inst.get("workflow_node_id"));
            try {
                for (index = 0; index < level; index++) {
                    Record next_node = nodeDrive.getNextNode(obj, curNode);
                    if (next_node != null) {
                        //将wf_cur_ru_inst 中的workflow_node_id 替换为 next_node,进入下次循环
                        wf_cur_ru_inst.set("workflow_node_id", next_node.get("id"));
                        curNode = next_node;
                    } else {
                        return new ArrayList<>();
                    }
                }
                WfApprovePermission bean = approvePermission.nodeExpressParse(curNode);
                return getApproveByWfApprovePermission(bean, wf_cur_ru_inst);
            } catch (WorkflowException e) {
                throw new WorkflowException("查找第" + (index + 1) + "级时错误:" + e.getMessage());
            }

        }
        return null;
//        else {
//            throw new WorkflowException("未找到对象的工作流实例");
//        }
    }


    public Page<Record> getPendingApproveByUser(UserInfo userInfo, int pageNum, int pageSize) {
        Long[] inst_ids = getInstIdsWithUser(userInfo);      //符合条件的实例ID


        final Kv kv = Kv.create();
        if (inst_ids != null) {
            kv.set("in", new Record().set("instIds", inst_ids));
        } else {
            //符合条件是的实例ID为空时，流程实例返回为空
            return new Page<Record>(new ArrayList<Record>(0), pageNum, pageSize, 0, 0);
        }

        Long[] exclude_inst_ids = getExcludeInstIdsWithUser(userInfo); //需要排除实例ID
        if (exclude_inst_ids != null) {
            kv.set("notin", new Record().set("excludeInstIds", exclude_inst_ids));
        }
        SqlPara para = Db.getSqlPara("wfprocess.findwfrunexecuteinstlist", Kv.by("map", kv));

        return Db.paginate(pageNum, pageSize, para);
    }


    /**
     * 根据请求对象，用户信息获取待审批的流程实例
     *
     * @param obj
     * @param userInfo
     * @param pageNum
     * @param pageSize
     * @return
     * @throws WorkflowException
     */
    public Page<Record> getPendingApproveByUser(WfRequestObj obj, UserInfo userInfo, int pageNum, int pageSize) {

        Long[] inst_ids = getInstIdsWithUser(userInfo);      //符合条件的实例ID
        if (inst_ids == null) {
            //符合条件是的实例ID为空时，流程实例返回为空
            return new Page<Record>(new ArrayList<Record>(0), pageNum, pageSize, 0, 0);
        }
        Long[] exclude_inst_ids = getExcludeInstIdsWithUser(userInfo); //需要排除实例ID

        return Db.paginate(pageNum, pageSize, obj.getPendingWfSql(inst_ids, exclude_inst_ids));

    }

    /**
     * 根据请求对象，用户信息获取已审批的流程实例(全部业务类型)
     *
     * @param record
     * @param userInfo
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Record> getProcessedApproveByUser(Record record, UserInfo userInfo, int pageNum, int pageSize) {
        final Kv kv = Kv.create();
        kv.set("assignee_id", userInfo.getUsr_id());
        kv.set("biz_type", TypeUtils.castToInt(record.get("biz_type")));
        kv.set("init_user_name", TypeUtils.castToString(record.get("init_user_name")));
        kv.set("init_org_name", TypeUtils.castToString(record.get("init_org_name")));
        kv.set("init_dept_name", TypeUtils.castToString(record.get("init_dept_name")));
        kv.set("start_time", TypeUtils.castToDate(record.get("start_time")));
        kv.set("end_time", TypeUtils.castToDate(record.get("end_time")));
        SqlPara para = Db.getSqlPara("wfprocess.findwfhisexecuteinstlist", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, para);
    }


    /**
     * 根据请求对象，用户信息获取已审批的流程实例(特定的业务类型)
     * @param obj
     * @param userInfo
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Record> getProcessedApproveByUser(WfRequestObj obj, UserInfo userInfo, int pageNum, int pageSize){
        Kv base = Kv.by("assignee_id",userInfo.getUsr_id()).set("biz_type",obj.getBizType().getKey());
        if(obj.getExtQuery() != null){
            base.set("extendS",obj.getExtQuery().getExtendS()).set("extendJ",obj.getExtQuery().getExtendJ());
        }
        Kv map = null;
        Record record = obj.getRecord();
        if(record != null){
            map = Kv.create();
            map.set("init_user_name", TypeUtils.castToString(record.get("init_user_name")));
            map.set("init_org_name", TypeUtils.castToString(record.get("init_org_name")));
            map.set("init_dept_name", TypeUtils.castToString(record.get("init_dept_name")));
            map.set("start_time", TypeUtils.castToDate(record.get("start_time")));
            map.set("end_time", TypeUtils.castToDate(record.get("end_time")));
        }
        SqlPara para = Db.getSqlPara("query.findProcessedWfInst",base.set("map", map) );
        return Db.paginate(pageNum, pageSize,para);
    }


    /**
     * 根据用户信息获取所有的实例id
     *
     * @param userInfo
     * @return
     */
    public Long[] getInstIdsWithUser(UserInfo userInfo) {
        Long[] result = null;
        List<Long> all_inst_ids = new ArrayList<>();
        Set<Long> user_inst_id = approvePermission.getInstSetByUser(userInfo.getUsr_id());
        if (user_inst_id != null) {
            all_inst_ids.addAll(user_inst_id);
        }

        //添加被授权审批的实例ID
        Set<Long> be_authorized_inst_ids = approvePermission.getBeAuthorizeInstByUser(userInfo.getUsr_id());
        if (be_authorized_inst_ids != null) {
            all_inst_ids.addAll(be_authorized_inst_ids);
        }

        String[] ops = userInfo.getWfOPDefine();
        if (ops != null) {
            for (String op : ops) {
                Set<Long> op_insts = approvePermission.getInstSetByOp(op);
                if (op_insts != null) {
                    all_inst_ids.addAll(op_insts);
                }
            }
        }

        if (all_inst_ids != null && all_inst_ids.size() > 0) {
            result = all_inst_ids.toArray(new Long[0]);
        }
        return result;

    }


    /**
     * 根据用户信息获取所有已排除的实例id
     *
     * @param userInfo
     * @return
     */
    public Long[] getExcludeInstIdsWithUser(UserInfo userInfo) {
        Long[] result = null;
        List<Long> all_inst_ids = new ArrayList<>();
        Set<Long> exclude_inst_id = approvePermission.getExcludeInstSetByUser(userInfo.getUsr_id());
        if (exclude_inst_id != null) {
            all_inst_ids.addAll(exclude_inst_id);
        }
        if (all_inst_ids != null && all_inst_ids.size() > 0) {
            result = all_inst_ids.toArray(new Long[0]);
        }
        return result;
    }


    /**
     * 根据WfApprovePermission, 获取当前流程的审批用户
     *
     * @param bean           WfApprovePermission
     * @param wf_cur_ru_inst 当前流程实例
     * @return
     * @throws WorkflowException
     */
    private List<Record> getApproveByWfApprovePermission(WfApprovePermission bean, Record wf_cur_ru_inst) throws WorkflowException {
        List<Record> result = null;
        if (bean != null) {
            result = new ArrayList<>();
            if (bean.getUsers() != null && bean.getUsers().length > 0) {
                result.addAll(getApproveUserByUserIds(bean.getUsers()));
            }
            String org_ids = bean.getPush_org();
            Long[] poss = bean.getPosition();
            List<Long> orgIds = approvePermission.orgParse(org_ids, wf_cur_ru_inst);
            List<Record> positive_user = getApproveUserByOrgPos(orgIds, poss);
            List<Record> opposite_user = getExcludeApproveUser(bean.getExlude_users(), wf_cur_ru_inst);
            if (positive_user != null && positive_user.size() > 0) {
                result.addAll(positive_user);
            }
            if (opposite_user != null && opposite_user.size() > 0) {
                result.removeAll(opposite_user);
            }

        }
        return result;
    }


    /**
     * 通过用户id列表获取审批用户
     *
     * @param usr_ids 用户id列表
     * @return
     */
    private List<Record> getApproveUserByUserIds(Long[] usr_ids) {
        return getUserRecords(usr_ids);
    }


    /**
     * 通过机构id列表和职位列表获取审批用户
     *
     * @param org_ids 机构id列表
     * @param poss    职位id列表
     * @return
     */
    private List<Record> getApproveUserByOrgPos(List<Long> org_ids, Long[] poss) {
        List<Record> users = null;
        if (org_ids != null && org_ids.size() > 0) {
            users = new ArrayList<>();
            for (Long org_id : org_ids) {
                if (poss == null || poss.length == 0) {
                    List<Record> usr_in_org = Db.find(Db.getSql("wfprocess.findUsersInOrg"), org_id);
                    if (usr_in_org != null && usr_in_org.size() > 0) {
                        users.addAll(usr_in_org);
                    }
                } else {
                    for (Long pos_id : poss) {
                        List<Record> usr_with_org_pos = Db.find(Db.getSql("wfprocess.findUsersWithOrgPos"), org_id, pos_id);
                        if (usr_with_org_pos != null && usr_with_org_pos.size() > 0) {
                            users.addAll(usr_with_org_pos);
                        }
                    }
                }
            }
        }
        return users;
    }


    /**
     * 通过排除用户表达式和当前工作实莉获取被排除的用户
     *
     * @param exclude_usr_ids
     * @param wf_cur_ru_inst
     * @return
     */
    private List<Record> getExcludeApproveUser(Long[] exclude_usr_ids, Record wf_cur_ru_inst) {
        List<Record> users = null;
        if (exclude_usr_ids != null && exclude_usr_ids.length > 0) {
            Long[] exclude_users = approvePermission.excludeUsersParse(exclude_usr_ids, wf_cur_ru_inst);
            users = getUserRecords(exclude_users);
        }
        return users;
    }

    /**
     * 根据users_id获取user Recode 列表
     *
     * @param dest_users
     * @return
     */
    private List<Record> getUserRecords(Long[] dest_users) {
        List<Record> result = null;
        if (dest_users != null && dest_users.length > 0) {
            result = new ArrayList<>();
            for (Long usr_id : dest_users) {
                Record specifiedUser = Db.findFirst(Db.getSql("wfprocess.findWfSpecifiedUser"), usr_id);
                if (specifiedUser != null) {
                    result.add(specifiedUser);
                }
            }
        }
        return result;
    }

    /**
     * 查看工作流图明细
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record detail(Record record) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        Record base = Db.findById("cfm_workflow_base_info", "id", id);
        if (null == base) {
            throw new ReqDataException("流程不存在，请刷新重试！");
        }
        Record define = Db.findFirst(Db.getSql("define.findNewDefineVersion"), id);
        if (null == define) {
            throw new ReqDataException("未定义流程信息！");
        }
        //查出流程节点
        List<Record> nodes = Db.find(Db.getSql("define.nodes"), define.get("id"));
        if (null != nodes && nodes.size() > 0) {
            for (Record node : nodes) {
                String node_exp = TypeUtils.castToString(node.get("data"));
                Record parse = JSONUtil.parse(node_exp);
                if (node_exp.contains("users")) {//代表用户
                    //查找用户集合
                    List<Record> userList = Db.find(Db.getSqlPara("define.users", Kv.by("list", ((List<Object>) (parse.get("users"))).toArray())));
                    node.set("addUsers", userList);
                } else {
                    //查找职位
                    List<Record> posList = Db.find(Db.getSqlPara("define.poss", Kv.by("list", ((List<Object>) (parse.get("position"))).toArray())));
                    node.set("addUsers", posList);
                }
            }
        }
        //查询流程线
        List<Record> lines = Db.find(Db.getSql("define.lines"), define.get("id"));
        define.set("nodes", nodes);
        define.set("lines", lines);
        base.set("define", define);
        return base;
    }

}
