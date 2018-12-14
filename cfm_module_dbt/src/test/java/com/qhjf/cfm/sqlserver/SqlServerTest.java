package com.qhjf.cfm.sqlserver;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
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
        arp.addSqlTemplate("/sql/sqlserver/dbt_cfm.sql");
        dp.start();
        arp.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
    }

    //测试 nbdb.sql
    @Test
    public void testNbdp() {
        Nbdp.findInnerDbPaymentMoreList();
        Nbdp.findInnerDbPaymentAllListTotal();
        Nbdp.findInnerDbPaymentDetailList();
        Nbdp.findInnerDbPaymentPayList();
        Nbdp.findAccountByAccId();
        Nbdp.chgDbPaymentByIdAndVersion();
        Nbdp.findInnerPayMentPendingList();
    }

    private static class Nbdp {

        private static boolean findInnerPayMentPendingList() {
            Kv kv = Kv.create();
            kv.set("in", new Record().set("instIds", new int[]{1, 2, 3}));
            kv.set("notin", new Record().set("excludeInstIds", new int[]{1, 2, 3}));
            Db.getSqlPara("nbdb.findInnerPayMentPendingList", Kv.by("map", kv));
            return true;
        }

        private static boolean chgDbPaymentByIdAndVersion() {
            Kv kv = Kv.create();
            //要更新的列
            kv.set("set", new Record().set("persist_version", 2).getColumns());
            //where条件约束
            kv.set("where", new Record().set("id", 1).set("persist_version", 1).getColumns());
            int result = Db.update(Db.getSqlPara("nbdb.chgDbPaymentByIdAndVersion", Kv.by("map", kv)));
            System.out.println(result);
            return true;
        }

        private static boolean findAccountByAccId() {
            Record record = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), 1);
            System.out.println(record);
            return true;
        }

        private static boolean findInnerDbPaymentPayList() {
            Kv kv = Kv.create();
            SqlPara sqlPara = Db.getSqlPara("nbdb.findInnerDbPaymentPayList", Kv.by("map", kv));
            List<Record> list = Db.find(sqlPara);
            System.out.println(list);
            return true;
        }

        private static boolean findInnerDbPaymentDetailList() {
            Kv kv = Kv.create();
            SqlPara sqlPara = Db.getSqlPara("nbdb.findInnerDbPaymentMoreList", Kv.by("map", kv));
            List<Record> list = Db.find(sqlPara);
            System.out.println(list);
            return true;
        }

        private static boolean findInnerDbPaymentMoreList() {
            Kv kv = Kv.create();
            kv.set("pay_account_no", "111");
            SqlPara sqlPara = Db.getSqlPara("nbdb.findInnerDbPaymentMoreList", Kv.by("map", kv));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static boolean findInnerDbPaymentAllListTotal() {
            Kv kv = Kv.create();
            SqlPara sqlPara = Db.getSqlPara("nbdb.findInnerDbPaymentAllListTotal", Kv.by("map", kv));
            Record first = Db.findFirst(sqlPara);
            System.out.println(first);
            return true;
        }
    }
}
