package com.qhjf.cfm.utils;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Record;
import org.junit.Test;

public class JSONUtilTest {

    @Test
    public void test(){
        String str = "{\n" +
                "    \"optype\":\"wfchart_add\",\n" +
                "    \"params\":{\n" +
                "    \"workflow_name\":\"测试流程图\",\t\t\n" +
                "    \"reject_strategy\":1,\t\t\n" +
                "    \"base_id\":1,\n" +
                "    \"lanes\":1,\n" +
                "    \"design_data\":{\n" +
                "        \"lines\":[\n" +
                "            {\n" +
                "                \"d_source_id\":\"-1\", \t\n" +
                "                \"d_target_id\":\"1\",\t\n" +
                "                \"rule\":\"\"\t\t\t\n" +
                "            },\n" +
                "            {\n" +
                "                \"d_source_id\":\"1\",\n" +
                "                \"d_target_id\":\"2\",\n" +
                "                \"rule\":\"10~100\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"d_source_id\":\"2\",\t\t\n" +
                "                \"d_target_id\":\"-2\",\n" +
                "                \"rule\":\"\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"nodes\":[\n" +
                "            {\n" +
                "                \"axis_x\":200,\t\t\t\n" +
                "                \"axis_y\":0,\t\t\t\t\n" +
                "                \"n_column\":\"1\",\t\t\t\n" +
                "                \"n_row\":\"1\",\t\t\t\n" +
                "                \"item_id\":\"1\",\t\t\n" +
                "                \"data\":{\n" +
                "                    \"user_type\":0,\n" +
                "                    \"users\":[\t\t\t\n" +
                "                        1,45\n" +
                "                    ]\n" +
                "                }\n" +
                "            },\n" +
                "            {\n" +
                "                \"axis_x\":500,\n" +
                "                \"axis_y\":0,\n" +
                "                \"n_column\":\"2\",\n" +
                "                \"n_row\":\"1\",\n" +
                "                \"item_id\":\"2\",\n" +
                "                \"data\":{\n" +
                "                    \"user_type\":1,\n" +
                "                    \"position\":[\n" +
                "                        1\n" +
                "                    ],\n" +
                "                    \"push_org\":\"0\"\n" +
                "                }\n" +
                "            }\n" +
                "        ]\n" +
                "    \t}\n" +
                "\t}\n" +
                "}";

        Record record = JSONUtil.parse(str);
        System.out.println(JsonKit.toJson(record));


    }


}