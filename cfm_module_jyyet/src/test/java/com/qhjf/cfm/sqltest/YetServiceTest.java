package com.qhjf.cfm.sqltest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.service.YetService;

public class YetServiceTest {
	DruidPlugin dp = null;
	ActiveRecordPlugin ap = null;
	@Before
	public void start(){
		this.dp = new DruidPlugin("jdbc:sqlserver://127.0.0.1:1433;DatabaseName=corpzone_sunlife", "sa", "Admin123");
		this.ap = new ActiveRecordPlugin(dp);
		this.ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
		this.ap.setDialect(new SqlServerDialect());
		this.dp.start();
		this.ap.start();
	}
	@Test
	public void update2(){
		List<Record> list = new ArrayList<>();
		list.add(getRecord("1", "2018-11-01", 123));
		list.add(getRecord("1", "2018-11-01", 124));
		list.add(getRecord("1", "2018-11-01", 125));
		list.add(getRecord("1", "2018-11-02", 123));
		list.add(getRecord("2", "2018-11-01", 123));
		list.add(getRecord("2", "2018-11-01", 124));
		list.add(getRecord("2", "2018-11-02", 125));
		list.add(getRecord("2", "2018-11-02", 126));
		try {
			new YetService().hisBlanceImport(list);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		
	}
	
	private Record getRecord(String accNo, String date, double bal){
		Record r1 = new Record();
		r1.set("acc_id", "5");
		r1.set("acc_no", accNo);
		r1.set("acc_name", "中美联泰大都会人寿保险有限公司");
		r1.set("bal", bal);
		r1.set("available_bal", 123);
		r1.set("data_source", 2);
		r1.set("bal_date", date);
		return r1;
	}
	@After
	public void stop(){
		this.dp.stop();
		this.ap.stop();
	}
}
