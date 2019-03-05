package com.qhjf.test;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.quartzs.jobs.comm.SftEbsDataCheckJob;
import com.qhjf.cfm.web.quartzs.jobs.comm.SftLaDataCheckJob;
import com.qhjf.cfm.web.quartzs.jobs.comm.SftRecvLaDataCheckJob;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SqlServerTest {
    DruidPlugin dp = null;
    RedisPlugin cfmRedis = null;
    ActiveRecordPlugin arp = null;

    @Before
    public void before() {
        dp = new DruidPlugin("jdbc:sqlserver://10.164.27.39:1433;DatabaseName=TreasureDB", "tmpadmin",
                "User123$");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/la_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/la_recv_cfm.sql");
        cfmRedis = new CfmRedisPlugin("cfm", "192.168.62.91");
        cfmRedis.setSerializer(new JdkSerializer());
        dp.start();
        arp.start();
        cfmRedis.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
        if (null != dp) dp.stop();
    }

    //测试 nbdb.sql
    @Test
    public void testNbdp() throws Exception {
        SftLaDataCheckJob ebs = new SftLaDataCheckJob();
        ebs.execute(null);
    }
    
}
