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
import com.qhjf.cfm.web.quartzs.jobs.utils.DDHSafeUtil;

/**
 * 大都会加解密测试
 * @author CHT
 *
 */
public class DDHDecryptEncryptTest {
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
        arp.addSqlTemplate("/sql/sqlserver/quartzs_job_cfm.sql");
        dp.start();
        arp.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
        if (null != dp) dp.stop();
    }
    
    @Test
    public void decryptTest(){
    	String secrypt = "0x00B84F0E73331A29DBEB513B4EFB192101000000E8D25A4E46E0B347A9C4FF4EBA346DA4825FE9EB373F596177B38CF57EFE0FC9587FA117B5A3EB011B05F14E661DA419";
    	System.out.println(DDHSafeUtil.decrypt(secrypt));
    }
    @Test
    public void encryptTest(){
    	String plainTest = "1324567897654321456";
    	System.out.println(DDHSafeUtil.encrypt(plainTest));
    }
    
}
