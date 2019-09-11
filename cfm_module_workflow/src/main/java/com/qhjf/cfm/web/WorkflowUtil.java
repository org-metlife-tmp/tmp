package com.qhjf.cfm.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.JSONUtil;

import java.util.*;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/7/20
 * @Description:
 */
public class WorkflowUtil {

    /**
     * 校验工作流是否为有效流程
     *
     * @param nodesList
     * @param linesList
     * @return 0 则标识通过
     */
    public static void checkWorkflowIsEffective(List<Record> nodesList, List<Record> linesList) throws WorkflowException {

        Set<Long> startPoint = new HashSet<>();
        Set<Long> endPoint = new HashSet<>();
        for (Record line : linesList) {
            long sourceId = TypeUtils.castToLong(line.get("d_source_id"));//源节点
            if (sourceId > 0) {
                startPoint.add(sourceId);
            }
            long targetId = TypeUtils.castToLong(line.get("d_target_id"));//目标节点
            if (targetId > 0) {
                endPoint.add(targetId);
            }
        }
        for (Record node : nodesList) {
            long nodeId = TypeUtils.castToLong(node.get("item_id"));
            if (!endPoint.contains(nodeId)) {
                throw new WorkflowException(nodeId + " 没有前驱节点连接它！");
            }
            if (!startPoint.contains(nodeId)) {
                throw new WorkflowException(nodeId + " 没有连接后续节点！");
            }
        }
    }

    public static void main(String[] args) throws WorkflowException {
        String json = "{\n" +
                "    \"optype\":\"wfchart_add\",\n" +
                "    \"params\":{\n" +
                "        \"workflow_name\":\"测试流程图\",\n" +
                "        \"reject_strategy\":1,\n" +
                "        \"base_id\":1,\n" +
                "        \"lanes\":1,\n" +
                "        \"design_data\":{\n" +
                "            \"lines\":[\n" +
                "                {\n" +
                "                    \"d_source_id\":\"-1\",\n" +
                "                    \"d_target_id\":\"1\",\n" +
                "                    \"rule\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"d_source_id\":\"-1\",\n" +
                "                    \"d_target_id\":\"2\",\n" +
                "                    \"rule\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"d_source_id\":\"2\",\n" +
                "                    \"d_target_id\":\"5\",\n" +
                "                    \"rule\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"d_source_id\":\"5\",\n" +
                "                    \"d_target_id\":\"-2\",\n" +
                "                    \"rule\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"d_source_id\":\"-1\",\n" +
                "                    \"d_target_id\":\"3\",\n" +
                "                    \"rule\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"d_source_id\":\"1\",\n" +
                "                    \"d_target_id\":\"4\",\n" +
                "                    \"rule\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"d_source_id\":\"4\",\n" +
                "                    \"d_target_id\":\"-2\",\n" +
                "                    \"rule\":\"\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"d_source_id\":\"3\",\n" +
                "                    \"d_target_id\":\"-2\",\n" +
                "                    \"rule\":\"\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"nodes\":[\n" +
                "                {\n" +
                "                    \"axis_x\":200,\n" +
                "                    \"axis_y\":0,\n" +
                "                    \"n_column\":\"1\",\n" +
                "                    \"n_row\":\"1\",\n" +
                "                    \"item_id\":\"1\",\n" +
                "                    \"data\":{\n" +
                "                        \"user_type\":0,\n" +
                "                        \"users\":[\n" +
                "                            1,\n" +
                "                            45\n" +
                "                        ]\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"axis_x\":500,\n" +
                "                    \"axis_y\":0,\n" +
                "                    \"n_column\":\"2\",\n" +
                "                    \"n_row\":\"1\",\n" +
                "                    \"item_id\":\"2\",\n" +
                "                    \"data\":{\n" +
                "                        \"user_type\":1,\n" +
                "                        \"position\":[\n" +
                "                            1\n" +
                "                        ],\n" +
                "                        \"push_org\":\"0\"\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"axis_x\":500,\n" +
                "                    \"axis_y\":0,\n" +
                "                    \"n_column\":\"2\",\n" +
                "                    \"n_row\":\"1\",\n" +
                "                    \"item_id\":\"3\",\n" +
                "                    \"data\":{\n" +
                "                        \"user_type\":1,\n" +
                "                        \"position\":[\n" +
                "                            1\n" +
                "                        ],\n" +
                "                        \"push_org\":\"0\"\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"axis_x\":500,\n" +
                "                    \"axis_y\":0,\n" +
                "                    \"n_column\":\"2\",\n" +
                "                    \"n_row\":\"1\",\n" +
                "                    \"item_id\":\"4\",\n" +
                "                    \"data\":{\n" +
                "                        \"user_type\":1,\n" +
                "                        \"position\":[\n" +
                "                            1\n" +
                "                        ],\n" +
                "                        \"push_org\":\"0\"\n" +
                "                    }\n" +
                "                },\n" +
                "                {\n" +
                "                    \"axis_x\":500,\n" +
                "                    \"axis_y\":0,\n" +
                "                    \"n_column\":\"2\",\n" +
                "                    \"n_row\":\"1\",\n" +
                "                    \"item_id\":\"5\",\n" +
                "                    \"data\":{\n" +
                "                        \"user_type\":1,\n" +
                "                        \"position\":[\n" +
                "                            1\n" +
                "                        ],\n" +
                "                        \"push_org\":\"0\"\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");


        Record record = JSONUtil.parse(obj);
        //design_data
        Record designData = record.get("design_data");

        //nodes
        final List<Record> nodesList = designData.get("nodes");

        //lines
        final List<Record> linesList = designData.get("lines");

        checkWorkflowIsEffective(nodesList, linesList);
    }

    private static List<Object> addChild(JSONArray array) {
        List<Object> result = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i) instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) array.get(i);
                Record record = new Record();
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    if (entry.getValue() instanceof JSONArray) {
                        record.set(entry.getKey(), addChild((JSONArray) entry.getValue()));
                    } else {
                        record.set(entry.getKey(), entry.getValue());
                    }
                }
                result.add(record);
            } else {
                result.add(array.get(i));
            }
        }
        return result;
    }

}
