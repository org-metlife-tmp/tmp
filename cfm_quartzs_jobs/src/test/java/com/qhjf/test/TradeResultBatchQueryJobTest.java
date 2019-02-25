package com.qhjf.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.channel.cmbc.CmbcTradeResultBatchQueryInter;
import com.qhjf.cfm.web.inter.impl.SysTradeResultBatchQueryInter;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.quartzs.jobs.TradeResultBatchQueryJob;

public class TradeResultBatchQueryJobTest {
	DruidPlugin dp = null;
    RedisPlugin cfmRedis = null;
    ActiveRecordPlugin arp = null;

    @Before
    public void before() {
        dp = new DruidPlugin("jdbc:sqlserver://127.0.0.1:1433;DatabaseName=corpzone_test", "sa", "Admin123");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/batchpayjob_cfm.sql");
        cfmRedis = new CfmRedisPlugin("cfm", "10.1.1.2");
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
    @Test
    public void getSourceDataListTest(){
    	System.out.println(new TradeResultBatchQueryJob().getSourceDataList());
    }
    @Test
    public void getInstrTest(){
    	List<Record> list = new TradeResultBatchQueryJob().getSourceDataList();
    	if (null != list && list.size() > 0) {
			Record r = list.get(0);
			System.out.println(new SysTradeResultBatchQueryInter().genInstr(r));
		}
    }
    @Test
    public void genParamsMap(){
    	List<Record> list = new TradeResultBatchQueryJob().getSourceDataList();
    	if (null != list && list.size() > 0) {
			Record r = list.get(0);
			System.out.println(new CmbcTradeResultBatchQueryInter().genParamsMap(r));
		}
    	
    }
}
