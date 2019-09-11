package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WorkflowUtil;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @Auther: zhangyuan
 * @Date: 2018/7/4
 * @Description: 工作流节点(点 、 线)
 */
public class WorkflowNodeLineService {

    public Record add(final Record record, final UserInfo userInfo) throws BusinessException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final HashMap<Long, Long> nodeMap = new HashMap<>();
        final Record newBaseInfoRec = new Record();
        final long baseId = TypeUtils.castToInt(record.get("base_id") != null ? record.get("base_id") : 0);
        final String workflowName = TypeUtils.castToString(record.get("workflow_name"));
        final long reject_strategy = TypeUtils.castToLong(record.get("reject_strategy"));
        final long lanes = TypeUtils.castToLong(record.get("lanes"));
        final int maxLane = 0;
        int addOrChgFlag = -1;


        if (baseId <= 0) {
            addOrChgFlag = 0;
        } else {
            addOrChgFlag = 1;
        }

        checkWorkFlowName(workflowName, addOrChgFlag);

        //design_data
        Record designData = record.get("design_data");
        if (designData == null) {
            throw new ReqDataException("保存失败!");
        }

        //nodes
        final List<Record> nodesList = designData.get("nodes");

        //lines
        final List<Record> linesList = designData.get("lines");

        //校验工作流是否有效
        WorkflowUtil.checkWorkflowIsEffective(nodesList, linesList);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                long newBaseId = 0;
                long newbaseVersion = 0;
                long newbaseLasterVersion = 0;


                //如果baseId<=0  新增baseinfo信息
                if (baseId <= 0) {

                    Record base_info = new Record()
                            .set("workflow_name", workflowName)
                            .set("workflow_type", WorkflowConstant.WorkFlowType.NORMAL.getKey())
                            .set("laster_version", 0)
                            .set("is_activity", 1)
                            .set("create_on", new Date())
                            .set("create_by", userInfo.getUsr_id())
                            .set("persist_version", 0)
                            .set("delete_flag", WebConstant.YesOrNo.NO.getKey());

                    boolean baseFlag = Db.save("cfm_workflow_base_info", "id", base_info);
                    if (!baseFlag) {
                        return false;
                    }
                    newBaseId = TypeUtils.castToLong(base_info.get("id"));
                    newbaseVersion = TypeUtils.castToInt(base_info.get("persist_version"));
                    newbaseLasterVersion = TypeUtils.castToInt(base_info.get("laster_version"));

                } else {
                    newBaseId = TypeUtils.castToLong(record.get("base_id"));
                    Record baseRecord = Db.findById("cfm_workflow_base_info", "id", newBaseId);

                    newbaseVersion = TypeUtils.castToInt(baseRecord.get("persist_version"));
                    newbaseLasterVersion = TypeUtils.castToInt(baseRecord.get("laster_version"));
                }

                int newMaxLine = 0;
                List<Record> nodeRecs = new ArrayList<>();
                Record defindRec = new Record();
                //保存工作流定义信息：cfm_workflow_define
                defindRec.set("base_id", newBaseId);
                defindRec.set("def_version", newbaseLasterVersion + 1);
                defindRec.set("reject_strategy", reject_strategy);
                defindRec.set("lanes", lanes);
                defindRec.set("create_on", new Date());
                defindRec.set("create_by", userInfo.getUsr_id());

                boolean flag = Db.save("cfm_workflow_define", "id", defindRec);
                if (flag) {

                    //修改流程名称：cfm_workflow_base_info
                    Record baseRec = new Record();
                    baseRec.set("id", newBaseId);
                    baseRec.set("workflow_name", workflowName);
                    flag = Db.update("cfm_workflow_base_info", "id", baseRec);
                    if (flag) {
                        //node点
                        if (nodesList != null && nodesList.size() > 0) {

                            for (Record node : nodesList) {
                                Record nodeRec = new Record();
                                nodeRec.set("def_id", TypeUtils.castToLong(defindRec.get("id")));
                                nodeRec.set("node_name", TypeUtils.castToInt(node.get("item_id")));
                                nodeRec.set("axis_x", TypeUtils.castToInt(node.get("axis_x")));
                                nodeRec.set("axis_y", TypeUtils.castToInt(node.get("axis_y")));
                                nodeRec.set("n_row", TypeUtils.castToInt(node.get("n_row")));
                                nodeRec.set("n_column", TypeUtils.castToInt(node.get("n_column")));

                                if (TypeUtils.castToInt(node.get("n_column")) > maxLane) {
                                    newMaxLine = TypeUtils.castToInt(node.get("n_column"));
                                }

                                Record data = node.get("data");

                                if (data != null) {
                                    nodeRec.set("lane_code", TypeUtils.castToInt(node.get("n_column")));

                                    int userType = TypeUtils.castToInt(data.get("user_type"));
                                    List<Object> usersOrPos = null;

                                    if (userType == 0) {//用户
                                        usersOrPos = data.get("users");
                                    } else if (userType == 1) {//职位
                                        usersOrPos = data.get("position");
                                    }
                                    StringBuilder builder = new StringBuilder("{");
                                    boolean isStart = false;
                                    if (TypeUtils.castToString(data.get("push_org")) != null && !"".equals(TypeUtils.castToString(data.get("push_org")))) {
                                        String push_org = TypeUtils.castToString(data.get("push_org"));
                                        if(!push_org.equals("0") && !push_org.startsWith("-") && !push_org.startsWith("+")){
                                            push_org = "+"+push_org;
                                        }
                                        builder.append("\"push_org\":\"").append(push_org).append("\"");
                                        isStart = true;
                                    }
                                    if (usersOrPos != null && usersOrPos.size() > 0) {
                                        boolean bj = true;
                                        if (isStart) {
                                            builder.append(",");
                                        }
                                        if (userType == 0) {//用户
                                            builder.append("\"users\":[");
                                        } else if (userType == 1) {//职位
                                            builder.append("\"position\":[");
                                        } else {
                                            bj = false;
                                        }
                                        if (bj) {
                                            StringBuilder userList = new StringBuilder();
                                            for (Object uop : usersOrPos) {
                                                userList.append(uop).append(",");
                                            }
                                            builder.append(userList.substring(0, userList.length() - 1));
                                        }
                                    }
                                    builder.append("]}");
                                    nodeRec.set("node_exp", builder.toString());

                                    nodeRecs.add(nodeRec);
                                    flag = Db.save("cfm_workflow_def_node", "id", nodeRec);
                                    if (!flag) {
                                        return false;
                                    }
                                    nodeMap.put(TypeUtils.castToLong(node.get("item_id")), TypeUtils.castToLong(nodeRec.get("id")));
                                }else{
                                    return false;
                                }
                            }
                        }

                        long sourceId = 0;
                        long targetId = 0;
                        //line线
                        if (linesList != null && linesList.size() > 0) {
                            List<Record> lineRecs = new ArrayList<>();
                            for (Record line : linesList) {
                                Record lineRec = new Record();
                                lineRec.set("def_id", defindRec.get("id"));
                                if (TypeUtils.castToLong(line.get("d_source_id")) > 0) {
                                    sourceId = nodeMap.get(TypeUtils.castToLong(line.get("d_source_id")));
                                } else {
                                    sourceId = TypeUtils.castToLong(line.get("d_source_id"));
                                }

                                if (TypeUtils.castToLong(line.get("d_target_id")) > 0) {
                                    targetId = nodeMap.get(TypeUtils.castToLong(line.get("d_target_id")));
                                } else {
                                    targetId = TypeUtils.castToLong(line.get("d_target_id"));
                                }
                                lineRec.set("start_node_id", sourceId);
                                lineRec.set("end_node_id", targetId);
                                lineRec.set("drive_condition", TypeUtils.castToString(line.get("rule")));

                                lineRecs.add(lineRec);
                            }
                            int[] result = Db.batchSave("cfm_workflow_def_line", lineRecs, 1000);
                            if (!ArrayUtil.checkDbResult(result)) {
                                return false;
                            }
                        }

                        defindRec.set("lanes", newMaxLine);
                        boolean defFlag = Db.update("cfm_workflow_define", "id", defindRec);

                        //修改cfm_workflow_base_info 版本号
                        baseRec.set("laster_version", TypeUtils.castToInt(defindRec.get("def_version")));
                        baseRec.set("persist_version", newbaseVersion + 1);
                        boolean baseFlag = Db.update("cfm_workflow_base_info", "id", baseRec);

                        if (defFlag && baseFlag) {
                            baseRec = Db.findById("cfm_workflow_base_info", "id", newBaseId);
                            newBaseInfoRec.set("id", TypeUtils.castToLong(baseRec.get("id")));
                            newBaseInfoRec.set("workflow_name", TypeUtils.castToString(baseRec.get("workflow_name")));
                            newBaseInfoRec.set("workflow_type", TypeUtils.castToInt(baseRec.get("workflow_type")));
                            newBaseInfoRec.set("laster_version", TypeUtils.castToInt(baseRec.get("laster_version")));
                            newBaseInfoRec.set("is_activity", TypeUtils.castToInt(baseRec.get("is_activity")));
                            newBaseInfoRec.set("persist_version", TypeUtils.castToInt(baseRec.get("persist_version")));
                            newBaseInfoRec.set("create_on", format.format(TypeUtils.castToDate(baseRec.get("create_on"))));
                            newBaseInfoRec.set("user_name", Db.findById("user_info", "usr_id",
                                    TypeUtils.castToLong(baseRec.get("create_by"))).get("name"));
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
        });
        if (flag) {
            return newBaseInfoRec;
        } else {
            throw new DbProcessException("流程基础信息不存在!");
        }
    }

    private void checkWorkFlowName(String name, int flag) throws BusinessException {
        Record record = null;
        if (flag == 0) {
            //新增
            record = Db.findById("cfm_workflow_base_info", "workflow_name", name);
        } else {
            //修改
            record = Db.findFirst(Db.getSql("define.checkWorkFlowNameisExist"), name, name);
        }
        if (null != record) {
            throw new ReqDataException("流程名称已使用！");
        }
    }

}
