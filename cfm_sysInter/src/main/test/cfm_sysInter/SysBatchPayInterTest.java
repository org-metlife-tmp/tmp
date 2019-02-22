package cfm_sysInter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchPayInter;

public class SysBatchPayInterTest {
	DruidPlugin dp = null;
	ActiveRecordPlugin arp = null;
	@Before
	public void start(){
		dp = new DruidPlugin("jdbc:sqlserver://127.0.0.1:1433;DatabaseName=corpzone_test", "sa", "Admin123");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/sysinter_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
        dp.start();
        arp.start();
	}
	@After
	public void stop(){
		this.dp.stop();
		this.arp.stop();
	}
	
	@Test
	public void findInstrTotalTest(){
		String detailBankServiceNumber = "20190222102336000002";
		try {
			SysBatchPayInter.findInstrTotal(null, detailBankServiceNumber, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
