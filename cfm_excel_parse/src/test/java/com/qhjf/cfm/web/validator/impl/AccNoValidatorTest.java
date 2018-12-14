package com.qhjf.cfm.web.validator.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.excel.config.validator.impl.AccNoValidator;

public class AccNoValidatorTest {
	DruidPlugin dp = null;
	ActiveRecordPlugin ap = null;
	
	@Before
	public void start(){
		this.dp = new DruidPlugin("jdbc:mysql://10.1.1.2:3306/corpzone_sunlife", "cfm", "cfm");
		this.ap = new ActiveRecordPlugin(dp);
		this.ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
		this.dp.start();
		this.ap.start();
	}
	@After
	public void stop(){
		this.dp.stop();
		this.ap.stop();
	}
	@Test
	public void test1() {
		String s = "88888888";
		AccNoValidator v = new AccNoValidator();
		v.doValidat(s);
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test2() {
		String s = "888888881";
		AccNoValidator v = new AccNoValidator();
		v.doValidat(s);
		System.out.println(v.getErrorMessage());
	}
	@Test
	public void test3() {
		String s = "select * from acc_his_transaction";
		Record r = Db.findFirst(s);
		Map<String, Object> columns = r.getColumns();
		Set<Entry<String,Object>> entrySet = columns.entrySet();
		for (Entry<String, Object> entry : entrySet) {
//			System.out.println(entry.getValue().getClass().getName() + "=" + entry.getValue());
			System.out.println(entry.getValue().toString());
		}
	}
}
