package cfm_webservice_la;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.webservice.la.queue.recv.LaRecvQueuePlugin;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 批收 回调核心系统测试
 * 
 * @author CHT
 *
 */
public class LaRecvCallbackTest {
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
		arp.addSqlTemplate("/sql/sqlserver/webservice_la_cfm.sql");
		cfmRedis = new CfmRedisPlugin("cfm", "192.168.62.91");
		cfmRedis.setSerializer(new JdkSerializer());
		
		LaRecvQueuePlugin larecv = new LaRecvQueuePlugin();
		dp.start();
		arp.start();
		cfmRedis.start();
		larecv.start();
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
	public void callBackNullTest() {
		List<Record> records = new ArrayList<>();
		LaRecvCallback callBack = new LaRecvCallback();
		callBack.callBack(records);
	}

	/**
	 * LA批收测试
	 *//*
	@Test
	public void callBackRecvTest() {
		try {
			List<Record> records = new ArrayList<>();
			String sql = "insert into la_origin_recv_data(tmp_status,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,fee_mode,pay_mode,recv_date,amount,pay_acc_name,pay_cert_type,pay_cert_code,pay_bank_name,pay_acc_no,bank_key,sale_code,sale_name,op_code,op_name,sacscode,sacstyp,trans_code,job_no)values(2,'LA','1','branchCode','SH','1','1','1','1','1','2019-01-01',23,'张三','1','1','中国银行','651234254243','QQq','e','ee','地方','1','1','1','2','3')";
			int update = Db.update(sql);
			System.out.println("成功插入："+update+"条");
			
			Record find = Db.findFirst("select * from la_origin_recv_data where pay_code='1'");
			records.add(find);
			LaRecvCallback callBack = new LaRecvCallback();
			callBack.callBack(records);
		} finally {
			int delete = Db.delete("delete la_origin_recv_data where pay_code='1'");
			System.out.println("成功删除："+delete+"条");
		}
	}
	*//**
	 * LA批付测试
	 *//*
	@Test
	public void callBackPayTest() {
		try {
			List<Record> records = new ArrayList<>();
			String sql = "INSERT INTO [dbo].[la_origin_pay_data](tmp_status,[source_sys],[pay_code],[branch_code],[org_code],[preinsure_bill_no],[insure_bill_no],[biz_type],[pay_mode],[pay_date],[amount],[recv_acc_name],[recv_cert_type],[recv_cert_code],[recv_bank_name],[recv_acc_no],[bank_key],[sale_code],[sale_name],[op_code],[op_name])     VALUES	 (2, 'LA','1','5','BJ','020000420622','00647979','T512','C','2019-02-11',12863.91,'高艳丽','01','370303198301013987',NULL,'6228270011300456176','ABC_YL','01','顾问行销（代理人）','60004482','CLIENT00079715')";
			int update = Db.update(sql);
			System.out.println("成功插入："+update+"条");
			
			Record find = Db.findFirst("select * from la_origin_pay_data where pay_code='1'");
			records.add(find);
			LaCallback callBack = new LaCallback();
			callBack.callBack(records);
		} finally {
			int delete = Db.delete("delete la_origin_pay_data where pay_code='1'");
			System.out.println("成功删除："+delete+"条");
		}
	}
	public static void main(String[] args) {
		DruidPlugin dp = null;
		RedisPlugin cfmRedis = null;
		ActiveRecordPlugin arp = null;
		dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin", "User123$");
		arp = new ActiveRecordPlugin(dp);
		arp.setDevMode(true);
		arp.setDialect(new SqlServerDialect());
		arp.setShowSql(true);
		arp.setBaseSqlTemplatePath(null);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/sql/sqlserver/webservice_la_recv_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/webservice_la_cfm.sql");
		cfmRedis = new CfmRedisPlugin("cfm", "192.168.62.91");
		cfmRedis.setSerializer(new JdkSerializer());
		
		LaRecvQueuePlugin larecv = new LaRecvQueuePlugin();
		dp.start();
		arp.start();
		cfmRedis.start();
		larecv.start();
		
		
		
		try {
			List<Record> records = new ArrayList<>();
			String sql = "INSERT INTO [dbo].[la_origin_pay_data](tmp_status,[source_sys],[pay_code],[branch_code],[org_code],[preinsure_bill_no],[insure_bill_no],[biz_type],[pay_mode],[pay_date],[amount],[recv_acc_name],[recv_cert_type],[recv_cert_code],[recv_bank_name],[recv_acc_no],[bank_key],[sale_code],[sale_name],[op_code],[op_name])     VALUES	 (2, 'LA','1','5','BJ','020000420622','00647979','T512','C','2019-02-11',12863.91,'高艳丽','01','370303198301013987',NULL,'6228270011300456176','ABC_YL','01','顾问行销（代理人）','60004482','CLIENT00079715')";
			int update = Db.update(sql);
			System.out.println("成功插入："+update+"条");
			
			Record find = Db.findFirst("select * from la_origin_pay_data where pay_code='1'");
			records.add(find);
			LaCallback callBack = new LaCallback();
			callBack.callBack(records);
		} finally {
			int delete = Db.delete("delete la_origin_pay_data where pay_code='1'");
			System.out.println("成功删除："+delete+"条");
			
			if (null != arp)
				arp.stop();
			if (null != dp)
				dp.stop();
			if (null != dp)
				dp.stop();
		}
	}*/

}
