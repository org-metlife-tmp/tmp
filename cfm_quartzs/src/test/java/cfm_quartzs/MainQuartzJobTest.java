package cfm_quartzs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;

public class MainQuartzJobTest {
	DruidPlugin dp = null;
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
		dp.start();
		arp.start();
	}

	@After
	public void after() {
		if (null != arp)
			arp.stop();
		if (null != dp)
			dp.stop();
	}
	
	@Test
	public void testLoadPubJobSon(){
		String pck = "com.qhjf.cfm.web.quartzs.jobs";
		
	}
}
