
package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.BusinessException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BaseDataCurrServiceTest {

    private DruidPlugin dp;
    private ActiveRecordPlugin ap;
    private BaseDataCurrService me = new BaseDataCurrService();

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
//    @Ignore
    public void findCurrencyPage() {

        String json = "{\"optype\":\"currencies_list\", \"params\":{\"query_key\":\"N\"}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Page<Record> page = me.findCurrencyPage(1,10,getParamsToRecord(obj));

        org.junit.Assert.assertNotNull(page);
//        org.junit.Assert.assertEquals(page.getList().size(), 1);
        org.junit.Assert.assertEquals(page.getList().size(), 10);
        org.junit.Assert.assertEquals("CNY", page.getList().get(0).get("iso_code"));

//        json = "{\"optype\":\"currencies_list\", \"params\":{\"name\":\"人\",\"iso_code\":\"CNY\"}}";
//        obj = JSONObject.parseObject(json).getJSONObject("params");
//
//        page = me.findCurrencyPage(getParamsToRecord(obj));
//        org.junit.Assert.assertEquals(page.getList().size(), 1);
//
//        json = "{\"optype\":\"currencies_list\", \"params\":{\"page_num\":1,\"page_size\":10}}";
//        obj = JSONObject.parseObject(json).getJSONObject("params");
//
//        page = me.findCurrencyPage(getParamsToRecord(obj));
//        org.junit.Assert.assertEquals(page.getList().size(), 10);

    }

    @Test
    @Ignore
    public void currSetDefault() throws BusinessException {
        String json = "{\"optype\":\"currency_setdefault\", \"params\":{\"id\":2,\"is_default\":1}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");
        Record record = me.currSetDefault(getParamsToRecord(obj));
        org.junit.Assert.assertEquals("港元", record.get("name"));

        json = "{\"optype\":\"currency_setdefault\", \"params\":{\"id\":1,\"is_default\":1}}";
        obj = JSONObject.parseObject(json).getJSONObject("params");
        record = me.currSetDefault(getParamsToRecord(obj));
        org.junit.Assert.assertEquals("人民币元", record.get("name"));

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