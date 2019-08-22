package com.qhjf.cfm.web.channel.util;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class TransQueryTimesGenUtilTest {
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
	        dp.start();
	        arp.start();
	    }

	    @After
	    public void after() {
	        if (null != arp) arp.stop();
	        if (null != dp) dp.stop();
	    }
	@Test
	public void test(){
		String preDay = "366";
		String accId = "103";
		Record r = Db.findFirst("select latest_date from acc_his_transaction_jobext where acc_id=?", accId);
		
		System.out.println(r);
		Map<String, String> times = TransQueryTimesGenUtil.getInstance().getTransQueryTime(accId, preDay);
		System.out.println(times);
	}
}
