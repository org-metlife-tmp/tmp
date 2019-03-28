package com.qhjf.test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.quartzs.jobs.comm.SftEbsDataCheckJob;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionException;

public class SftEbsDataCheckJobTest {
	DruidPlugin dp = null;
//	RedisPlugin cfmRedis = null;
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
		arp.addSqlTemplate("/sql/sqlserver/quartzs_job_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/ebs_cfm.sql");
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
	public void excuteTest(){
		try {
			new SftEbsDataCheckJob().execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}
}
