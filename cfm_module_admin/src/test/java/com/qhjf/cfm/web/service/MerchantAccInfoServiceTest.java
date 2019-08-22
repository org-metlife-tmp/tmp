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

public class MerchantAccInfoServiceTest {

    private DruidPlugin dp;
    private ActiveRecordPlugin ap;
    private MerchantAccInfoService me = new MerchantAccInfoService();

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
        String json = "{\"optype\":\"merchacc_list\", \"params\":{\"query_key\":\"000\"}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Page<Record> page = me.list(1,10,getParamsToRecord(obj));

        org.junit.Assert.assertNotNull(page);
        org.junit.Assert.assertEquals(page.getList().size(), 1);

        destory();
    }

    @Test
    @Ignore
    public void detail() throws BusinessException {
        String json = "{\"optype\":\"merchacc_detail\", \"params\":{\"id\":1}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");
        Record record = getParamsToRecord(obj);
        record = me.detail(record);

        org.junit.Assert.assertNotNull(record);
        org.junit.Assert.assertEquals(TypeUtils.castToString(record.get("acc_no")), "6212260000000001");

        destory();
    }

    @Test
    @Ignore
    public void add() throws BusinessException {

        String json = "{\"optype\":\"merchacc_add\", \"params\":{\n" +
                "\"acc_no\":\"6212260000000004\",\n" +
                "\"acc_name\":\"测试商户4\",\n" +
                "\"org_id\":1,\n" +
                "\"curr_id\":1,\n" +
                "\"channel_code\":\"000001\",\n" +
                "\"open_date\":\"2018-5-24\",\n" +
                "\"org_seg\":\"机构段a\",\n" +
                "\"detail_seg\":\"明细段a\",\n" +
                "\"pay_recv_attr\":2,\n" +
                "\"settle_acc_id\":1,\n" +
                "\"memo\":\"aaaaa\"\n" +
                "}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");
        Record record = getParamsToRecord(obj);
        record = me.add(record);

        org.junit.Assert.assertNotNull(record);

        destory();
    }

    @Test
    @Ignore
    public void chg() throws BusinessException {
        String json = "{\"optype\":\"merchacc_chg\", \"params\":{\n" +
                "\"id\":1,\n" +
                "\"acc_no\":\"6212260000000001\",\n" +
                "\"acc_name\":\"测试商户1\",\n" +
                "\"org_id\":1,\n" +
                "\"curr_id\":1,\n" +
                "\"channel_code\":\"000001\",\n" +
                "\"open_date\":\"2018-5-24\",\n" +
                "\"org_seg\":\"机构段seg1\",\n" +
                "\"detail_seg\":\"明细段seg1\",\n" +
                "\"pay_recv_attr\":3,\n" +
                "\"settle_acc_id\":1,\n" +
                "\"memo\":\"备注啦啦啦\"\n" +
                "}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");
        Record record = getParamsToRecord(obj);
        record = me.chg(record);

        org.junit.Assert.assertNotNull(record);
        destory();
    }

    @Test
    @Ignore
    public void del() throws BusinessException {
        String json = "{\"optype\":\"merchacc_delete\", \"params\":{\"id\":4}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        me.del(record);

//        org.junit.Assert.assertNotNull(record);
//        org.junit.Assert.assertEquals("测试_" + TypeUtils.castToLong(record.get("dept_id")), record.get("name"));

        destory();
    }

    @Test
    @Ignore
    public void setstatus() throws BusinessException {

        String json = "{\"optype\":\"merchacc_setstatus\", \"params\":{\"id\":3,\"status\":1}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Record record = getParamsToRecord(obj);

        me.setstatus(record);

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
}