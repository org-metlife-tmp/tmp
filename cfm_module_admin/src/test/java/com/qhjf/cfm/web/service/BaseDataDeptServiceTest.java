package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.BusinessException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BaseDataDeptServiceTest {
    private DruidPlugin dp;
    private ActiveRecordPlugin ap;
    private BaseDataDeptService me = new BaseDataDeptService();

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
    public void findDeptPage() {

        String json = "{\"optype\":\"dept_list\", \"params\":{}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Page<Record> page = me.findDeptPage(1,10,getParamsToRecord(obj));

        org.junit.Assert.assertNotNull(page);
        org.junit.Assert.assertEquals(page.getList().size(), 6);
        org.junit.Assert.assertEquals("测试部不不不不", page.getList().get(0).get("name"));

        json = "{\"optype\":\"dept_list\", \"params\":{\"name\":\"测试\"}}";
        obj = JSONObject.parseObject(json).getJSONObject("params");

        page = me.findDeptPage(1,10,getParamsToRecord(obj));
        org.junit.Assert.assertEquals(page.getList().size(), 6);

        json = "{\"optype\":\"dept_list\", \"params\":{\"page_num\":1,\"page_size\":5}}";
        obj = JSONObject.parseObject(json).getJSONObject("params");

        page = me.findDeptPage(1,10,getParamsToRecord(obj));
        org.junit.Assert.assertEquals(page.getList().size(), 5);
        destory();
    }

    @Test
    @Ignore
    public void add() throws BusinessException {
        String json = "{\"optype\":\"dept_ass\", \"params\":{\"name\":\"测试\",\"desc\":\"测试test\"}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);
        record = me.add(record);

        org.junit.Assert.assertNotNull(record);
        org.junit.Assert.assertEquals("测试", record.get("name"));


        destory();
    }

    @Test
    @Ignore
    public void update() throws BusinessException {
        String json = "{\"optype\":\"dept_update\", \"params\":{\"dept_id\":1,\"name\":\"测试部\",\"desc\":\"BUG终结者a\"}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        record = me.update(record);

        org.junit.Assert.assertNotNull(record);
        org.junit.Assert.assertEquals("测试部", record.get("name"));

        destory();
    }

    @Test
    @Ignore
    public void delete() throws BusinessException {
        String json = "{\"optype\":\"dept_delete\", \"params\":{\"dept_id\":10}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        me.delete(record);

        org.junit.Assert.assertNotNull(record);
        org.junit.Assert.assertEquals("测试_" + TypeUtils.castToLong(record.get("dept_id")), record.get("name"));

        destory();
    }

    @Test
    @Ignore
    public void setstatus() throws BusinessException {
        String json = "{\"optype\":\"dept_setstatus\", \"params\":{\"dept_id\":8,\"status\":2}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        record = me.setstatus(record);

        org.junit.Assert.assertNotNull(record);
        org.junit.Assert.assertEquals(8L, record.get("dept_id"));

        destory();
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

    public void destory() {
        if (dp != null) {
            dp.stop();
        }
        if (ap != null) {
            ap.stop();
        }
    }
}