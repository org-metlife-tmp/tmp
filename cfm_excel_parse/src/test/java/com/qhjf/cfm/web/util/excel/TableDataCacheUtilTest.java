package com.qhjf.cfm.web.util.excel;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.utils.TableDataCacheUtil;

public class TableDataCacheUtilTest {
	DruidPlugin dp = null;
	ActiveRecordPlugin ap = null;

	@Before
	public void start() {
		this.dp = new DruidPlugin("jdbc:mysql://10.1.1.2:3306/corpzone_sunlife", "cfm", "cfm");
		this.ap = new ActiveRecordPlugin(dp);
		this.ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
		this.dp.start();
		this.ap.start();
	}

	@After
	public void stop() {
		this.dp.stop();
		this.ap.stop();
	}

	@Test
	public void test1() {
		TableDataCacheUtil instance = TableDataCacheUtil.getInstance();
		Map<String, Object> r1 = instance.getARowData("account", "acc_no", "88888888");
		System.out.println(r1);

		Map<String, Object> r2 = instance.getARowData("account", "acc_no", "88888888");
		System.out.println(r2);

		instance.removeRowData("account", "acc_no", "88888888");

		Map<String, Object> r3 = instance.getARowData("account", "acc_no", "88888888");
		System.out.println(r3);

	}
	@Test
	public void test2(){
		final TableDataCacheUtil instance = TableDataCacheUtil.getInstance();
		
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					int j = 0;
					while (true) {
						j++;
						instance.getARowData("account", "acc_no", "88888888");
						if (j == 10) {
							break;
						}
					}
				}
			}).start();
		}
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					int j = 0;
					while (true) {
						j++;
						instance.removeRowData("account", "acc_no", "88888888");
						if (j == 10) {
							break;
						}
					}
				}
			}).start();
		}
		
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
