
package com.qhjf.cfm;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.service.OpenIntentionService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class IntentionServiceTest {

    private DruidPlugin dp;
    private ActiveRecordPlugin ap;
    private OpenIntentionService me = new OpenIntentionService();

    @Before
    public void steup() {
        dp = new DruidPlugin("jdbc:mysql://172.100.1.134:3306/corpzone_sunlife", "cfm", "cfm");
        ap = new ActiveRecordPlugin(dp);
        ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
        ap.addSqlTemplate("aoi.sql");
        this.dp.start();
        this.ap.start();
    }

    @Test
    @Ignore
    public void findCurrencyPage() {

        String json = "{\"optype\":\"currencies_list\", \"params\":{\"query_key\":\"\"}}";
        JSONObject obj = JSONObject.parseObject(json).getJSONObject("params");

        Page<Record> page = me.findOpenIntentionToPage(1,10,getParamsToRecord(obj));

        org.junit.Assert.assertNotNull(page);
        org.junit.Assert.assertEquals(page.getList().size(), 0);
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