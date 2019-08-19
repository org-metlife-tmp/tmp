package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HandleRouteSettingServiceTest {

    private DruidPlugin dp;
    private ActiveRecordPlugin ap;
    private HandleRouteSettingService me = new HandleRouteSettingService();

    @Before
    public void steup() {
        dp = new DruidPlugin("jdbc:mysql://10.1.1.2:3306/corpzone_sunlife", "cfm", "cfm");
        ap = new ActiveRecordPlugin(dp);
        ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
        ap.addSqlTemplate("sql/mysql/admin_cfm.sql");
        this.dp.start();
        this.ap.start();
    }

    @Test
    @Ignore
    public void list() {

        String json = "{\"optype\":\"handleroute_list\", \"params\":{\"source_code\":\"XTLY000001\",\"insurance_type_exp\":1}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Page<Record> page = me.list(1, 10, getParamsToRecord(obj));

        org.junit.Assert.assertNotNull(page);

        destory();
    }

    @Test
    @Ignore
    public void add() throws BusinessException {

        String json = "{\"optype\":\"handleroute_add\", \"params\":{\n" +
                "\"source_code\":\"XTLY000001\",\n" +
                "\"pay_recv_mode\":1,\n" +
                "\"pay_item\":\"\",\n" +
                "\"is_activate\":1,\n" +
                "\"memo\":\"备注啦啦啦\",\n" +
                "\"org_exp\":\"@1@2@3@4@5@\",\n" +
                "\"biz_type_exp\":\"@1@2@3@\",\n" +
                "\"insurance_type_exp\":\"@1@2@3@4@\",\n" +
                "\"items\":[\n" +
                "{\n" +
                "\"channel_code\":\"000001\",\n" +
                "\"channel_interface_code\":\"\",\n" +
                "\"settle_or_merchant_acc_id\":1,\n" +
                "\"level\":1\n" +
                "},\n" +
                "{\n" +
                "\"channel_code\":\"000001\",\n" +
                "\"channel_interface_code\":\"\",\n" +
                "\"settle_or_merchant_acc_id\":1,\n" +
                "\"level\":2\n" +
                "}\n" +
                "]\n" +
                "}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");
        Record record = getRecordByParamsStrong(obj);
        record = me.add(record);

        org.junit.Assert.assertNotNull(record);

        destory();
    }

    @Test
    @Ignore
    public void chg() throws BusinessException {

        String json = "{\"optype\":\"handleroute_add\", \"params\":{\n" +
                "\"id\":23,\n" +
                "\"source_code\":\"XTLY000001\",\n" +
                "\"pay_recv_mode\":1,\n" +
                "\"pay_item\":\"\",\n" +
                "\"is_activate\":1,\n" +
                "\"memo\":\"备注啦啦啦\",\n" +
                "\"org_exp\":\"@1@2@3@4@5@\",\n" +
                "\"biz_type_exp\":\"@1@2@3@\",\n" +
                "\"insurance_type_exp\":\"@1@2@3@4@\",\n" +
                "\"items\":[\n" +
                "{\n" +
                "\"channel_code\":\"000001\",\n" +
                "\"channel_interface_code\":\"\",\n" +
                "\"settle_or_merchant_acc_id\":1,\n" +
                "\"level\":1\n" +
                "},\n" +
                "{\n" +
                "\"channel_code\":\"000001\",\n" +
                "\"channel_interface_code\":\"\",\n" +
                "\"settle_or_merchant_acc_id\":1,\n" +
                "\"level\":2\n" +
                "}\n" +
                "]\n" +
                "}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");
        Record record = getRecordByParamsStrong(obj);
        record = me.chg(record);

        org.junit.Assert.assertNotNull(record);

        destory();
    }

    @Test
    @Ignore
    public void detail() throws BusinessException {
        String json = "{\"optype\":\"handleroute_detail\", \"params\":{\"id\":23}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");
        Record record = getParamsToRecord(obj);
        record = me.detail(record);

        org.junit.Assert.assertNotNull(record);

        destory();
    }

    @Test
    @Ignore
    public void del() throws BusinessException {
        String json = "{\"optype\":\"handleroute_delete\", \"params\":{\"id\":27}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        me.del(record);

        destory();
    }

    @Test
    @Ignore
    public void setstatus() throws BusinessException {

        String json = "{\"optype\":\"handleroute_setstatus\", \"params\":{\"id\":23}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        me.setstatus(record);
        destory();
    }

    @Test
    @Ignore
    public void setormeracc() throws ReqDataException {
        String json = "{\"optype\":\"handleroute_setormeracc\", \"params\":{\"code\":\"cebmerchant\"}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        List<Record> recordList = me.setormeracc(record);
        org.junit.Assert.assertNotNull(recordList);
        org.junit.Assert.assertEquals(recordList.size(), 2);
        destory();
    }

    public void destory() {
        if (dp != null) {
            dp.stop();
        }
        if (ap != null) {
            ap.stop();
        }
    }

    protected Record getParamsToRecord(JSONObject param) {
        if (null != param) {
            Record record = new Record();
            for (String s : param.keySet()) {
                record.set(s, param.get(s));
            }
            return record;
        }
        return null;
    }

    protected Record getRecordByParamsStrong(JSONObject param) {
        if (param != null) {
            Record result = new Record();
            //一级基础信息数据
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                if (entry.getValue() instanceof JSONArray) {
                    result.set(entry.getKey(), addChild((JSONArray) entry.getValue()));
                } else {
                    result.set(entry.getKey(), entry.getValue());
                }
            }
            return result;
        }
        return null;
    }

    private List<Record> addChild(JSONArray array) {
        List<Record> result = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
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
        }
        return result;
    }
}