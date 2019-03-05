package cfm_webservice_la;

import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;

public class WebserviceLaRecvCfmSQLTest {
	DruidPlugin dp = null;
	RedisPlugin cfmRedis = null;
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
		arp.addSqlTemplate("/sql/sqlserver/webservice_la_recv_cfm.sql");
		cfmRedis = new CfmRedisPlugin("cfm", "192.168.62.91");
		cfmRedis.setSerializer(new JdkSerializer());
		dp.start();
		arp.start();
		cfmRedis.start();
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
	public void updateCallbackingStatusTest(){
		int updNum = Db.update(Db.getSql("webservice_la_recv_cfm.updateCallbackingStatus"),
				WebConstant.SftCallbackStatus.SFT_CALLBACKING.getKey(), 
				new Date(), 
				123456789l,
				WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey(),
				WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey(), 
				123451234);
		System.out.println(updNum);
	}
	
	@Test
	public void getStatusByPayCodeTest(){
		Record record = Db.findFirst(Db.getSql("webservice_la_recv_cfm.getStatusByPayCode"), "000946483");
		System.out.println(record);
	}
	@Test
	public void getPayLegalByPayCodeTest(){
		Record payLegalRecord = Db.findFirst(Db.getSql("webservice_la_recv_cfm.getPayLegalByPayCode"), "000946224");
		System.out.println(payLegalRecord);
	}
	@Test
	public void getBankCodeByOrginTest(){
		String sql = Db.getSql("webservice_la_recv_cfm.getBankCodeByOrgin");
		List<Record> find = Db.find(sql, "ABC_YL", "BJ", "5");
		if (null != find && find.size() == 1) {
			System.out.println(find.get(0).getStr("bankcode"));
		}else {
			System.out.println("error");
		}
	}

}
