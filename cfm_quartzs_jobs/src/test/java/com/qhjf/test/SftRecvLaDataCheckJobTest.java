package com.qhjf.test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.quartzs.jobs.comm.SftRecvLaDataCheckJob;
import com.qhjf.cfm.web.webservice.la.queue.recv.LaRecvQueuePlugin;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.JobExecutionException;

public class SftRecvLaDataCheckJobTest {
	DruidPlugin dp = null;
	RedisPlugin cfmRedis = null;
	ActiveRecordPlugin arp = null;

	@Before
	public void before() {
		dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin", "User123$");
//		dp = new DruidPlugin("jdbc:sqlserver://127.0.0.1:1433;DatabaseName=corpzone_test", "sa", "Admin123");
		arp = new ActiveRecordPlugin(dp);
		arp.setDevMode(true);
		arp.setDialect(new SqlServerDialect());
		arp.setShowSql(true);
		arp.setBaseSqlTemplatePath(null);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/sql/sqlserver/la_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/la_recv_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/webservice_la_recv_cfm.sql");
		cfmRedis = new CfmRedisPlugin("cfm", "10.164.26.48");
		cfmRedis.setSerializer(new JdkSerializer());
		
		LaRecvQueuePlugin la = new LaRecvQueuePlugin();
		dp.start();
		arp.start();
//		cfmRedis.start();
		la.start();
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
	
	/*@Test
	public void delete(){
		int delete = Db.update("delete from la_origin_recv_data where pay_code=1");
		System.out.println("delete ="+delete);
	}*/
	
	@Test
	public void excuteTest(){
		try {
			/*String sql = "insert into la_origin_recv_data(la_callback_status,is_process,tmp_status,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,fee_mode,pay_mode,recv_date,amount,pay_acc_name,pay_cert_type,pay_cert_code,pay_bank_name,pay_acc_no,bank_key,sale_code,sale_name,op_code,op_name,sacscode,sacstyp,trans_code,job_no)values(1,0,2,'LA','1','1','1','1','1','1','1','1','2019-01-01',23,'张三','1','1','中国银行','651234254243','QQq','e','ee','地方','1','1','1','2','3')";
			int insert = Db.update(sql);
			System.out.println("insert into ="+insert);*/
			
			SftRecvLaDataCheckJob job = new SftRecvLaDataCheckJob();
			job.execute(null);
			
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}finally {
			/*int delete = Db.update("delete from la_origin_recv_data where pay_code=1");
			System.out.println("delete ="+delete);*/
		}
	}
	@Test
	public void test1(){
		System.out.println(TableDataCacheUtil.getInstance().getARowData("const_bank_type", "code", "001"));
	}
	@Test
	public void updTotle(){
		List<Record> list = Db.find(Db.getSql("la_recv_cfm.getLARecvUnCheckedOriginList"), WebConstant.YesOrNo.NO.getKey());
        if (list == null || list.size() == 0) {
        	if (Db.update(Db.getSql("la_recv_cfm.updLARecvTotle")) > 0) {
				System.out.println("LA批收校验主子表数据，更新主表状态为4成功");
				list = Db.find(Db.getSql("la_recv_cfm.getLARecvUnCheckedOriginList"), WebConstant.YesOrNo.NO.getKey());
				if (list == null || list.size() == 0) {
					return;
				}
        	}else {
				System.out.println("LA批收校验主子表数据，更新主表状态为4失败");
				return;
			}
        }
        System.out.println(list);
	}
}
