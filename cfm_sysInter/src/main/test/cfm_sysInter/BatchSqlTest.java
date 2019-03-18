package cfm_sysInter;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class BatchSqlTest {
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
	@Test
	public void findInstrTotalTest(){
		Record findFirst = Db.findFirst(Db.getSql("batchpay.findInstrTotal"), "123");
		System.out.println(findFirst);
	}
	@Test
	public void findInstrTotalByDetailTest(){
		Kv kv = Kv.create();
		kv.set("detailBankServiceNumber", "1");
		kv.set("packageSeq", "2");
		SqlPara findPayLockSqlPara = Db.getSqlPara("batchpay.findInstrTotalByDetail", Kv.by("map", kv));
		Record r = Db.findFirst(findPayLockSqlPara);
		System.out.println(r);
	}
	@Test
	public void findInstrDetailTest(){
		Kv kv = Kv.create();
		kv.set("detailBankServiceNumber", "1");
		kv.set("packageSeq", "2");
		kv.set("bankServiceNumber", "3");
		SqlPara findPayLockSqlPara = Db.getSqlPara("batchpay.findInstrDetail", Kv.by("map", kv));
		Record r = Db.findFirst(findPayLockSqlPara);
		System.out.println(r);
	}
	@Test
	public void countInstrDetailInHandlingTest(){
		int findFirst = Db.queryInt(Db.getSql("batchpay.countInstrDetailInHandling"), "123");
		System.out.println(findFirst);
	}
	@Test
	public void findInstrDetailByBaseIdTest(){
		List<Record> find = Db.find(Db.getSql("batchpay.findInstrDetailByBaseId"),1);
		System.out.println(find);
	}
	/*@Test
	public void updOrginLaTest(){
		SqlPara sqlPara = Db.getSqlPara("batchpay.updOrginLa", Kv.by("tb", "la_origin_pay_data"));
		int find = Db.update(sqlPara.getSql(), 1);
		System.out.println(find);
	}*/
	@Test
	public void updOrginSuccLaTest(){
		SqlPara sqlPara = Db.getSqlPara("batchpay.updOrginSuccLa");
//		int find = Db.update(sqlPara.getSql(), "2019-10-10", "11:11:11", "1234", "65421389484733213789", 1);
		int find = Db.update(sqlPara.getSql(), 1);
		System.out.println(find);
	}
	@Test
	public void updOrginSuccEbsTest(){
		SqlPara sqlPara = Db.getSqlPara("batchpay.updOrginSuccEbs");
		int find = Db.update(sqlPara.getSql(), "2019-10-10", "11:11:11", "1234", "65421389484733213789", 1);
		System.out.println(find);
	}
	@Test
	public void updBillTotalToFailTest(){
		int find = Db.update(Db.getSql("batchpay.updBillTotalToFail"), 1);
		System.out.println(find);
	}
	/*@Test
	public void updOriginFailTest(){
		SqlPara sqlPara = Db.getSqlPara("batchpay.updOriginFail", Kv.by("tb", "la_origin_pay_data"));
		int find = Db.update(sqlPara.getSql(), 1);
		System.out.println(find);
	}*/
	@Test
	public void updOriginFailLaTest(){
		SqlPara sqlPara = Db.getSqlPara("batchpay.updOriginFailLa");
		int find = Db.update(sqlPara.getSql(), 1);
		System.out.println(find);
	}
	@Test
	public void updOriginFailEbsTest(){
		SqlPara sqlPara = Db.getSqlPara("batchpay.updOriginFailEbs");
		int find = Db.update(sqlPara.getSql(), "2019-10-10", "11:11:11", "1234", "65421389484733213789", 1);
		System.out.println(find);
	}
	
	@After
	public void stop(){
		this.dp.stop();
		this.arp.stop();
	}
}
