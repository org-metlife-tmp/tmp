package com.qhjf.test;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.quartzs.jobs.comm.SftLaDataCheckJob;

/**
 * batchpayjob_cfm.sql测试
 * @author CHT
 *
 */
public class BatchPayJobCfmSqlTest {
	DruidPlugin dp = null;
    RedisPlugin cfmRedis = null;
    ActiveRecordPlugin arp = null;

    @Before
    public void before() {
        dp = new DruidPlugin("jdbc:sqlserver://10.1.1.2:1433;DatabaseName=corpzone_test", "sa",
                "Admin123");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/batchpayjob_cfm.sql");
        cfmRedis = new CfmRedisPlugin("cfm", "10.1.1.2");
        cfmRedis.setSerializer(new JdkSerializer());
        dp.start();
        arp.start();
        cfmRedis.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
        if (null != dp) dp.stop();
    }
    //batchpayjob.getTradeResultBatchQrySourceList
    @Test
    public void getTradeResultBatchQrySourceListTest() throws Exception {
    	List<Record> sourceList = Db.find(Db.getSql("batchpayjob.getTradeResultBatchQrySourceList"), 10);
    	System.out.println(sourceList);
    }
    @Test
    public void getOldBatchTradeTest() throws Exception {
    	String bankSerialNumber = "111";
    	String detailBankServiceNumber = "222";
    	String packageSeq = "333";
    	Record findFirst = Db.findFirst(Db.getSql("batchpayjob.getOldBatchTrade")
				, bankSerialNumber
				, detailBankServiceNumber
				, packageSeq);
    	System.out.println(findFirst);
    }
}
