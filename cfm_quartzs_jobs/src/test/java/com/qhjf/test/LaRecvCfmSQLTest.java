package com.qhjf.test;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LaRecvCfmSQLTest {
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
		if (null != arp)
			arp.stop();
		if (null != dp)
			dp.stop();
		if (null != dp)
			dp.stop();
	}
	
	@Test
	public void getLARecvCallBackOriginListTest(){
		List<Record> list = Db.find(Db.getSql("la_recv_cfm.getLARecvCallBackOriginList")
				, WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey()
				, WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()
				, WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey()
				, WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey());
		System.out.println(list);
	}
	@Test
	public void getLARecvUnCheckedOriginListTest(){
		try {
			String sql = "insert into la_origin_recv_data(tmp_status,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,fee_mode,pay_mode,recv_date,amount,pay_acc_name,pay_cert_type,pay_cert_code,pay_bank_name,pay_acc_no,bank_key,sale_code,sale_name,op_code,op_name,sacscode,sacstyp,trans_code,job_no)values(2,'LA','1','1','1','1','1','1','1','1','2019-01-01',23,'张三','1','1','中国银行','651234254243','QQq','e','ee','地方','1','1','1','2','3')";
			int update = Db.update(sql);
			System.out.println("成功插入："+update+"条");
			
			List<Record> list = Db.find(Db.getSql("la_recv_cfm.getLARecvUnCheckedOriginList")
					, WebConstant.YesOrNo.NO.getKey());
			System.out.println(list);
		} finally {
			int delete = Db.delete("delete la_origin_recv_data where pay_code='1'");
			System.out.println("成功删除："+delete+"条");
		}
	}
	
	@Test
	public void insertLaRecvOrigin(){
		String sql = "insert into la_origin_recv_data(tmp_status,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,fee_mode,pay_mode,recv_date,amount,pay_acc_name,pay_cert_type,pay_cert_code,pay_bank_name,pay_acc_no,bank_key,sale_code,sale_name,op_code,op_name,sacscode,sacstyp,trans_code,job_no)values(2,'LA','1','1','1','1','1','1','1','1','2019-01-01',23,'张三','1','1','中国银行','651234254243','QQq','e','ee','地方','1','1','1','2','3')";
		int update = Db.update(sql);
	}
	@Test
	public void updLaRecvOriginStatusTest(){
		int flag = Db.update(Db.getSql("la_recv_cfm.updLaRecvOriginStatus"),
                WebConstant.YesOrNo.YES.getKey(),
                WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey(),
                "测试错误回写",
                15,
                0);
		System.out.println(flag);
	}
	@Test
	public void updLaRecvOriginProcessTest(){
		 int updRows = Db.update(Db.getSql("la_recv_cfm.updLaRecvOriginProcess"),
                 WebConstant.YesOrNo.YES.getKey(),
                 15l,
                 1);
		System.out.println(updRows);
	}
	@Test
	public void updLaRecvOriginTest(){
		int updRows = Db.update(Db.getSql("la_recv_cfm.updLaRecvOrigin")
        		, "1dsafadsf"
        		, 15l
        		, 2);
		System.out.println(updRows);
	}
}
