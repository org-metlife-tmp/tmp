package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @Auther: zhangyuan
 * @Date: 2018/7/17 15:20
 * @Description:
 */
public class OpenIntentionServiceTest {

    private DruidPlugin dp;
    private ActiveRecordPlugin ap;

    @Before
    public void steup() {
        dp = new DruidPlugin("jdbc:mysql://10.1.1.2:3306/corpzone_sunlife", "cfm", "cfm");
        ap = new ActiveRecordPlugin(dp);
        ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
        ap.addSqlTemplate("chg.sql");
        this.dp.start();
        this.ap.start();
    }

    public void destory() {
        if (dp != null) {
            dp.stop();
        }
        if (ap != null) {
            ap.stop();
        }
    }


    @Test
    public void test() {
        Long[] inst_id = new Long[]{1L, 2L, 3L};
        Long[] exclude_inst_id = new Long[]{1L};
        final Kv kv = Kv.create();
        kv.set("in", new Record().set("instIds", inst_id));
        kv.set("notin", new Record().set("excludeInstIds", exclude_inst_id));
        SqlPara sqlPara = Db.getSqlPara("findAccChangePendingList", Kv.by("map", kv));
        List<Record> list =  Db.find(sqlPara);
        System.out.println(sqlPara);
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
