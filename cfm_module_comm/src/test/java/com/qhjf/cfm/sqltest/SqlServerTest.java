package com.qhjf.cfm.sqltest;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SqlServerTest {
    DruidPlugin dp = null;
    ActiveRecordPlugin arp = null;

    @Before
    public void before() {
        dp = new DruidPlugin("jdbc:sqlserver://10.1.1.158:1433;DatabaseName=corpzone_sunlife", "sa", ".");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/comm_cfm.sql");
        dp.start();
        arp.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
    }

    //测试 area.sql
    @Test
    public void testArea() {
        Area.allTopLevel();
        Area.areaList();
    }

    //测试 attachment.sql
    @Test
    public void testAttachment() {
        Attachment.files();
    }

    //测试 bank.sql
    @Test
    public void testBank() {
        Bank.allBankType();
        Bank.bankList();
    }

    //测试 cate.sql
    @Test
    public void testCate() {
        Cate.list();
    }

    //测试 user_query.sql
    @Test
    public void testUq() {
        Uq.list();
    }

    private static class Uq {
        private static boolean list() {
            Record record = new Record();
            record.set("name","111");
            SqlPara sqlPara = Db.getSqlPara("user_query.list", Kv.by("map", record.getColumns()));
            List<Record> list = Db.find(sqlPara);
            System.out.println(list);
            return true;
        }
    }

    private static class Cate {

        private static boolean list() {
            Record record = new Record();
            record.set("query_key", "111");
            SqlPara sqlPara = Db.getSqlPara("cate.list", Kv.by("map", record.getColumns()));
            List<Record> list = Db.find(sqlPara);
            System.out.println(list);
            return true;
        }
    }

    private static class Bank {

        private static boolean bankList() {
            Kv cond = Kv.create();
            cond.set("name", "1");
            SqlPara sql = Db.getSqlPara("bank.bankList", Kv.by("map", cond));
            List<Record> list = Db.find(sql);
            System.out.println(list);
            return true;
        }

        private static boolean allBankType() {
            Kv cond = Kv.create();
            cond.set("name", "1");
            SqlPara sql = Db.getSqlPara("bank.allBankType", Kv.by("map", cond));
            List<Record> list = Db.find(sql);
            System.out.println(list);
            return true;
        }
    }

    private static class Attachment {

        private static boolean files() {
            List<Record> list = Db.find(Db.getSql("attachment.files"), 1, 1);
            System.out.println(list);
            return true;
        }
    }

    private static class Area {

        private static boolean allTopLevel() {
            Kv cond = Kv.create();
            cond.set("name", "1");
            SqlPara sql = Db.getSqlPara("area.allTopLevel", Kv.by("map", cond));
            List<Record> list = Db.find(sql);
            System.out.println(list);
            return true;
        }

        private static boolean areaList() {
            Kv cond = Kv.create();
            cond.set("name", "1");
            SqlPara sql = Db.getSqlPara("area.areaList", Kv.by("map", cond));
            List<Record> list = Db.find(sql);
            System.out.println(list);
            return true;
        }

    }
}
