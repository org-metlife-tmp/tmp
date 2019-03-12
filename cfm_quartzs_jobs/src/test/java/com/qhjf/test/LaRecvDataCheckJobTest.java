package com.qhjf.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.quartzs.jobs.comm.LaRecvDataCheckJob;

public class LaRecvDataCheckJobTest {
	DruidPlugin dp = null;
//	RedisPlugin cfmRedis = null;
	ActiveRecordPlugin arp = null;

	@Before
	public void before() {
		dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin", "User123$");
		arp = new ActiveRecordPlugin(dp);
		arp.setDevMode(true);
		arp.setDialect(new SqlServerDialect());
		arp.setShowSql(true);
		arp.setBaseSqlTemplatePath(null);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/sql/sqlserver/quartzs_job_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/la_recv_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
//		cfmRedis = new CfmRedisPlugin("cfm", "192.168.62.91");
//		cfmRedis.setSerializer(new JdkSerializer());
		dp.start();
		arp.start();
//		cfmRedis.start();
	}

	@After
	public void after() {
		if (null != arp)
			arp.stop();
		if (null != dp)
			dp.stop();
		if (null != dp)
			dp.stop();
	}
	
	@Test
	public void executeTest(){
		try {
			new LaRecvDataCheckJob().execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}
}
