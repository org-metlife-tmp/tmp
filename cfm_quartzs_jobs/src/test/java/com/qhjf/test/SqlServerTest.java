package com.qhjf.test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.constant.WebConstant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class SqlServerTest {
    DruidPlugin dp = null;
    RedisPlugin cfmRedis = null;
    ActiveRecordPlugin arp = null;

    @Before
    public void before() {
        dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin",
                "User123$");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/la_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/la_recv_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
        dp.start();
        arp.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
        if (null != dp) dp.stop();
    }

    //测试 nbdb.sql
    @Test
    public void testNbdp() throws Exception {
        SqlServerTest la = new SqlServerTest();
        Record originRecord = new Record();
        originRecord.set("insure_bill_no", 1);
        originRecord.set("recv_acc_name", "e");
        originRecord.set("amount", 1200);
        originRecord.set("source_sys", 0);
        originRecord.set("pay_code", "123");
        originRecord.set("id", 10293);
        originRecord.set("pay_code", "123");
        originRecord.set("branch_code", "123");
        originRecord.set("org_code", "123");
        originRecord.set("tmp_org_id", 36);
        originRecord.set("tmp_org_code", "123");
        originRecord.set("preinsure_bill_no", "123");
        originRecord.set("pay_mode", "D");
        originRecord.set("recv_acc_no", "123");
        originRecord.set("bank_key", "123");
        originRecord.set("identification", "123");
        originRecord.set("is_doubtful", 0);
        originRecord.set("status", 0);
        try {
            WebConstant.YesOrNo flag = la.checkDoubtful(originRecord);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private WebConstant.YesOrNo checkDoubtful(Record originData) throws Exception {

        Record checkDoubtful = new Record();

        Map<String, Object> columns = originData.getColumns();
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            String key = entry.getKey();
            if(key.equals("id")){
                checkDoubtful.set("origin_id", entry.getValue());
                continue;
            }
            if (key.equals("persist_version") || key.equals("source_sys") || key.equals("channel_code")) {
                continue;
            }
            checkDoubtful.set(key, entry.getValue());
        }
        String identification = MD5Kit.string2MD5(originData.getStr("insure_bill_no")
                + "_" +originData.getStr("recv_acc_name")
                + "_" +originData.getStr("amount"));

        checkDoubtful.set("identification", identification);
        checkDoubtful.set("is_doubtful", 0);

        Db.save("la_check_doubtful", checkDoubtful);

        /**
         * 根据保单号，收款人，金额查询合法表中是否存在数据，如果存在视为可疑数据，将合法表中的数据删除，更新可疑表数据状态为可疑
         */
        List<Record> legalRecordList = Db.find(Db.getSql("la_cfm.getpaylegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                originData.getStr("amount"));
        if(legalRecordList!=null && legalRecordList.size()!=0){
            Record legalRecord = legalRecordList.get(0);
            //可疑数据
            CommonService.update("la_check_doubtful",
                    new Record().set("is_doubtful", 1),
                    new Record().set("id", checkDoubtful.getLong("id")));
            //删除合法表中数据和合法扩展表数据
            Db.deleteById("pay_legal_data","id",legalRecord.getLong("id"));
            Db.delete(Db.getSql("la_cfm.dellapaylegalext"),legalRecord.getLong("id"));
            CommonService.update("la_check_doubtful",
                    new Record().set("is_doubtful", 1),
                    new Record().set("origin_id", legalRecord.getLong("origin_id")));
            return WebConstant.YesOrNo.YES;

        }
        return WebConstant.YesOrNo.NO;
    }
    
}
