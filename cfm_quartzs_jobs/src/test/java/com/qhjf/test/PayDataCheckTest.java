package com.qhjf.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.quartzs.jobs.comm.SftLaDataCheckJob;

public class PayDataCheckTest {
	DruidPlugin dp = null;
	ActiveRecordPlugin arp = null;
	@Before
	public void start(){
		dp = new DruidPlugin("jdbc:sqlserver://10.164.25.42:1433;DatabaseName=TreasureDB", "tmpadmin", "User123$");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/sysinter_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/test_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/quartzs_job_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/la_cfm.sql");
        dp.start();
        arp.start();
	}
	
	@After
	public void stop(){
		this.dp.stop();
		this.arp.stop();
	}
	
	@Test
	public void test(){
		SftLaDataCheckJob job = new SftLaDataCheckJob();
//		Record record = Db.findById("la_origin_pay_data", "id", 310);
		Record record = Db.findFirst("select id,source_sys,pay_code,branch_code,org_code,preinsure_bill_no,insure_bill_no,biz_type,pay_mode,pay_date,amount,recv_acc_name,recv_cert_type,recv_cert_code,recv_bank_name,recv_acc_no,bank_key,sale_code,sale_name,op_code,op_name,persist_version from la_origin_pay_data where id=?", 310);
		job.executeProcess(record);
		
	}
}
