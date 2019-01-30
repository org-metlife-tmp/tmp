package com.qhjf.cfm.web;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.service.DztInitSettingService;

public class TestDztInitSettingService {
	DruidPlugin dp = null;
	ActiveRecordPlugin ap = null;
	@Before
	public void before() {
		this.dp = new DruidPlugin("jdbc:sqlserver://10.1.1.2:1433;DatabaseName=corpzone_sunlife", "sa", "Admin123");
		this.ap = new ActiveRecordPlugin(dp);
		this.ap.setDevMode(true);
		this.ap.setShowSql(true);
		this.ap.setDialect(new SqlServerDialect());
		this.ap.getEngine().setDevMode(true);
		this.ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
		this.ap.addSqlTemplate("/sql/sqlserver/dzt_cfm.sql");
		this.dp.start();
		this.ap.start();
	}

	@Test
	public void add1() {
		Record r = new Record();
		r.set("acc_id", 5)
			.set("year", "2017")
			.set("month", "3")
			.set("balance", 995210);
		List<Record> list = new ArrayList<>();
		Record re = new Record().set("data_type", 1)
				.set("credit_or_debit", "2").set("amount", 132321).set("memo", "摘要");
		list.add(re);
		list.add(re);
		r.set("list", list);
		
		try {
			Record add = new DztInitSettingService().add(r);
			System.out.println(add);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void chg1() {
		Record r = new Record();
		r.set("acc_id", 5)
			.set("acc_no", "1001190709023101878")
			.set("acc_name", "中美联泰大都会人寿保险有限公司")
			.set("year", "2015")
			.set("month", "4")
			.set("balance", 5433234);
		List<Record> list = new ArrayList<>();
		Record re = new Record().set("data_type", 1)
				.set("credit_or_debit", "2").set("amount", 132321).set("memo", "摘要1");
		list.add(re);
		list.add(re);
		r.set("list", list);
		
		try {
			Record add = new DztInitSettingService().chg(r);
			System.out.println(add);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void enable1() {
		Record r = new Record();
		r.set("acc_id", 5)
			.set("acc_no", "1001190709023101878")
			.set("acc_name", "中美联泰大都会人寿保险有限公司")
			.set("year", "2017")
			.set("month", "11")
			.set("balance", 88888);
		List<Record> list = new ArrayList<>();
		Record re = new Record().set("data_type", 1)
				.set("credit_or_debit", "2").set("amount", 132321).set("memo", "摘要2");
		list.add(re);
		list.add(re);
		r.set("list", list);
		
		try {
			Record add = new DztInitSettingService().enable(r);
			System.out.println(add);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void detail(){
		Record r = new Record();
		r.set("acc_id", 1)
			.set("acc_no", "1001190709023101878")
			.set("acc_name", "中美联泰大都会人寿保险有限公司")
			.set("year", "2018")
			.set("month", "2")
			.set("balance", 995210);
		List<Record> add = new DztInitSettingService().detail(r);
		System.out.println(add);
	}
	
	@Test
	public void list(){
		Page<Record> add = new DztInitSettingService().list(2, 1);
		System.out.println(add);
		System.out.println(add.getList());
	}
	

	@After
	public void after() {
		this.dp.stop();
		this.ap.stop();
	}
}
