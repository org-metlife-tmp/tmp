package com.qhjf.cfm.sqlserver;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.service.DztAdjustService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

public class AdjustTest {
    DruidPlugin dp = null;
    ActiveRecordPlugin arp = null;

    @Before
    public void before() {
        dp = new DruidPlugin("jdbc:sqlserver://10.1.1.2:1433;DatabaseName=corpzone_pretest", "sa", "Admin123");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/dzt_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
        dp.start();
        arp.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
    }

    @Test
    public void dzt() {
        DztAdjustService service = new DztAdjustService();
        Record record = new Record();
        record.set("acc_id", 1);
        record.set("cdate", "2018-11");
        try {
            service.build(record);
        } catch (BusinessException | ParseException e) {
            e.printStackTrace();
        }
    }

}
