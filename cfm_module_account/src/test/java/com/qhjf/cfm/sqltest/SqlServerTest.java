package com.qhjf.cfm.sqltest;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
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
        arp.addSqlTemplate("/sql/sqlserver/account_cfm.sql");
        dp.start();
        arp.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
    }


    //测试 afd.sql
    @Test
    public void testAfd() {
        Afd.getPage();
        Afd.findFreezeAndDeFreezePendingList();
    }

    //测试 acc.sql
    @Test
    public void testAcc() {
        Acc.findAccountToPage();
        Acc.findAccountExtType();
        Acc.findAccountExtInfo();
        Acc.findCategory();
        Acc.listByST();
        Acc.getAccByAccId();
        Acc.chgAccountExtraInfo();
        Acc.findMainAccount();
    }

    //测试 aoc.sql
    @Test
    public void testAoc() {
        Aoc.findOpenCompleteToPage();
        Aoc.findOpenCompleteDonePage();
        Aoc.chgOpenCompleteByIdAndVersion();
        Aoc.fingOpenCompleteById();
        Aoc.findOpenCompletePendingList();
    }

    //测试 aoi.sql
    @Test
    public void testAoi() {
        Aoi.findOpenIntentionToPage();
        Aoi.getOpenIntentionById();
        Aoi.chgOpenIntentionByIdAndVersion();
        Aoi.findIssueList();
        Aoi.findIssueName();
        Aoi.findOpenIntentionPendingList();
    }

    //测试 caf.sql
    @Test
    public void testCaf() {
        Caf.getTodoPage();
        Caf.getDonePage();
        Caf.fingCloseCompleteById();
        Caf.getAdditional();
        Caf.findCloseCompletePendingList();
    }

    //测试 cai.sql
    @Test
    public void testCai() {
        Cai.getPage();
        Cai.update();
        Cai.usernames();
        Cai.findCloseIntentionPendingList();
    }

    //测试 chg.sql
    @Test
    public void testChg() {
        Chg.findChangeToList();
        Chg.fingchgById();
        Chg.chgChangeByIdAndVersion();
        Chg.delChangeDetailByApplyId();
        Chg.findChangeDetailByApplyId();
        Chg.findAccChangePendingList();
    }

    private static class Chg {

        private static boolean findAccChangePendingList() {
            Record record = new Record();
            record.set("in", new Record().set("instIds", new int[]{1, 2, 3}));
            record.set("notin", new Record().set("excludeInstIds", new int[]{1, 2, 3}));
            SqlPara sqlPara = Db.getSqlPara(installKey("findAccChangePendingList"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static boolean findChangeDetailByApplyId() {
            System.out.println(Db.findFirst(Db.getSql(installKey("findChangeDetailByApplyId")), 1));
            return true;
        }

        private static boolean delChangeDetailByApplyId() {
            int delChangeDetailByApplyId = Db.delete(Db.getSql(installKey("delChangeDetailByApplyId")), 1);
            System.out.println(delChangeDetailByApplyId);
            return true;
        }

        private static boolean chgChangeByIdAndVersion() {
            Kv kv = Kv.create();
            kv.set("set", new Record().set("memo", 1).getColumns());
            kv.set("where", new Record().set("id", 1).getColumns());
            SqlPara sqlPara = Db.getSqlPara(installKey("chgChangeByIdAndVersion"), kv.set("map", kv));
            int update = Db.update(sqlPara);
            System.out.println(update);
            return true;
        }

        private static boolean fingchgById() {
            Kv cond = Kv.create();
            cond.set("id", 1);
            SqlPara sqlPara = Db.getSqlPara(installKey("fingchgById"), Kv.by("cond", cond));
            Record first = Db.findFirst(sqlPara);
            System.out.println(first);
            return true;
        }

        private static boolean findChangeToList() {
            Record record = new Record();
            record.set("query_key", "111");
            record.set("start_date", new Date());
            record.set("end_date", new Date());
            SqlPara sqlPara = Db.getSqlPara(installKey("findChangeToList"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static String installKey(String key) {
            return "chg." + key;
        }
    }

    private static class Cai {

        private static boolean findCloseIntentionPendingList() {
            Record record = new Record();
            record.set("in", new Record().set("instIds", new int[]{1, 2, 3}));
            record.set("notin", new Record().set("excludeInstIds", new int[]{1, 2, 3}));
            SqlPara sqlPara = Db.getSqlPara(installKey("findCloseIntentionPendingList"), Kv.by("map", record.getColumns()));
            List<Record> list = Db.find(sqlPara);
            System.out.println(list);
            return true;
        }

        private static boolean usernames() {
            String usernames = Db.getSql(installKey("usernames"));
            Record first = Db.findFirst(usernames, 1);
            System.out.println(first);
            return true;
        }

        private static boolean update() {
            Kv kv = Kv.create();
            kv.set("set", new Record().set("memo", 1).getColumns());
            kv.set("where", new Record().set("id", 1).getColumns());
            SqlPara sqlPara = Db.getSqlPara(installKey("update"), kv.set("map", kv));
            int update = Db.update(sqlPara);
            System.out.println(update);
            return true;
        }

        private static boolean getPage() {
            Record record = new Record();
            record.set("query_key", "111");
            record.set("start_date", new Date());
            record.set("end_date", new Date());
            SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static String installKey(String key) {
            return "cai." + key;
        }
    }

    private static class Caf {

        private static boolean findCloseCompletePendingList() {
            Record record = new Record();
            record.set("in", new Record().set("instIds", new int[]{1, 2, 3}));
            record.set("notin", new Record().set("excludeInstIds", new int[]{1, 2, 3}));
            SqlPara sqlPara = Db.getSqlPara(installKey("findCloseCompletePendingList"), Kv.by("map", record.getColumns()));
            List<Record> list = Db.find(sqlPara);
            System.out.println(list);
            return true;
        }

        private static boolean getAdditional() {
            System.out.println(Db.findFirst(Db.getSql(installKey("getAdditional")), 1));
            return true;
        }

        private static boolean fingCloseCompleteById() {
            Kv cond = Kv.create();
            cond.set("id", 1);
            SqlPara sqlPara = Db.getSqlPara(installKey("fingCloseCompleteById"), Kv.by("cond", cond));
            Record first = Db.findFirst(sqlPara);
            System.out.println(first);
            return true;
        }

        private static boolean getDonePage() {
            Record record = new Record();
            record.set("query_key", "1");
            record.set("service_status", new int[]{1, 2, 3});
            record.set("start_date", new Date());
            record.set("end_date", new Date());
            SqlPara sqlPara = Db.getSqlPara(installKey("getDonePage"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static String installKey(String key) {
            return "caf." + key;
        }

        private static boolean getTodoPage() {
            Record record = new Record();
            record.set("query_key", "111");
            record.set("service_status", new int[]{1, 2, 3});
            SqlPara sqlPara = Db.getSqlPara(installKey("getTodoPage"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }
    }

    private static class Aoi {

        private static boolean findOpenIntentionPendingList() {
            Record record = new Record();
            record.set("in", new Record().set("instIds", new int[]{1, 2, 3}));
            record.set("notin", new Record().set("excludeInstIds", new int[]{1, 2, 3}));
            SqlPara sqlPara = Db.getSqlPara(installKey("findOpenIntentionPendingList"), Kv.by("map", record.getColumns()));
            List<Record> list = Db.find(sqlPara);
            System.out.println(list);
            return true;
        }

        private static boolean findIssueName() {
            String sql = Db.getSql(installKey("findIssueName"));
            Record first = Db.findFirst(sql, 1);
            System.out.println(first);
            return true;
        }

        private static boolean findIssueList() {
            String sql = Db.getSql(installKey("findIssueList"));
            Record record = Db.findFirst(sql, 1);
            System.out.println(record);
            return true;
        }

        private static boolean chgOpenIntentionByIdAndVersion() {
            Kv kv = Kv.create();
            kv.set("set", new Record().set("org_id", 1).getColumns());
            kv.set("where", new Record().set("id", 1).getColumns());
            SqlPara sqlPara = Db.getSqlPara(installKey("chgOpenIntentionByIdAndVersion"), Kv.by("map", kv));
            int update = Db.update(sqlPara);
            System.out.println(update);
            return true;
        }

        private static boolean getOpenIntentionById() {
            String sql = Db.getSql(installKey("getOpenIntentionById"));
            Record record = Db.findFirst(sql, 1, 1);
            System.out.println(record);
            return true;
        }

        private static boolean findOpenIntentionToPage() {
            Record record = new Record();
            record.set("query_key", "111");
            record.set("service_status", new int[]{1, 2, 3});
            record.set("start_date", new Date());
            record.set("end_date", new Date());
            SqlPara sqlPara = Db.getSqlPara(installKey("findOpenIntentionToPage"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static String installKey(String key) {
            return "aoi." + key;
        }
    }

    private static class Aoc {

        private static boolean findOpenCompletePendingList() {
            Record record = new Record();
            record.set("in", new Record().set("instIds", new int[]{1, 2, 3}));
            record.set("notin", new Record().set("excludeInstIds", new int[]{1, 2, 3}));
            SqlPara sqlPara = Db.getSqlPara(installKey("findOpenCompletePendingList"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static boolean fingOpenCompleteById() {
            Kv cond = Kv.create();
            cond.set("acc_id", 1);
            SqlPara sqlPara = Db.getSqlPara(installKey("fingOpenCompleteById"), Kv.by("cond", cond));
            Record first = Db.findFirst(sqlPara);
            System.out.println(first);
            return true;
        }

        private static boolean chgOpenCompleteByIdAndVersion() {
            Kv kv = Kv.create();
            kv.set("set", new Record().set("acc_id", 1).getColumns());
            kv.set("where", new Record().set("id", 1).getColumns());
            SqlPara sqlPara = Db.getSqlPara(installKey("chgOpenCompleteByIdAndVersion"), Kv.by("map", kv));
            int update = Db.update(sqlPara);
            System.out.println(update);
            return true;
        }

        private static boolean findOpenCompleteDonePage() {
            Record record = new Record();
            record.set("query_key", "111");
            record.set("service_status", new int[]{1, 2, 3});
            record.set("start_date", new Date());
            record.set("end_date", new Date());
            SqlPara sqlPara = Db.getSqlPara(installKey("findOpenCompleteDonePage"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static boolean findOpenCompleteToPage() {
            Record record = new Record();
            record.set("user_id", 1);
            record.set("query_key", "111");
            record.set("service_status", new int[]{1, 2, 3});
            record.set("start_date", new Date());
            record.set("end_date", new Date());
            SqlPara sqlPara = Db.getSqlPara(installKey("findOpenCompleteToPage"), Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static String installKey(String key) {
            return "aoc." + key;
        }
    }

    private static class Afd {

        private static boolean findFreezeAndDeFreezePendingList() {
            Record record = new Record();
            record.set("in", new Record().set("instIds", new int[]{1, 2, 3}));
            record.set("notin", new Record().set("excludeInstIds", new int[]{1, 2, 3}));
            System.out.println(Db.find(Db.getSqlPara(installKey("findFreezeAndDeFreezePendingList"), Kv.by("map", record.getColumns()))));
            return true;
        }

        private static boolean getPage() {
            Record record = new Record();
            record.set("query_key", 123);
            record.set("service_status", new int[]{1, 2, 3});
            record.set("start_date", new Date());
            record.set("end_date", new Date());
            record.set("status", new int[]{2, 3, 4});
            record.set("create_by", 1);
            record.set("id", 1);
            SqlPara sqlPara = Db.getSqlPara(installKey("getPage"), Kv.by("map", record.getColumns()));
            System.out.println(Db.paginate(1, 10, sqlPara));
            return true;
        }

        private static String installKey(String key) {
            return "afd." + key;
        }
    }

    private static class Acc {

        private static boolean findMainAccount() {
            Record record = new Record();
            record.set("query_key", 1);
            record.set("status", 1);
            record.set("level_code", "1");
            record.set("exclude_ids", 2);
            SqlPara sqlPara = Db.getSqlPara(installKey("findMainAccount"), Kv.by("map", record.getColumns()));
            System.out.println(Db.find(sqlPara));
            return true;
        }

        private static boolean chgAccountExtraInfo() {
            int chgAccountExtraInfo = Db.update(Db.getSql(installKey("chgAccountExtraInfo")), 1, 2, 3);
            System.out.println(chgAccountExtraInfo);
            return true;
        }

        private static boolean getAccByAccId() {
            System.out.println(Db.findFirst(Db.getSql(installKey("getAccByAccId")), 1));
            return true;
        }

        private static boolean listByST() {
            Kv cond = Kv.create().set("acc_id", 1).set("status", 1).set("org_id", 1);
            System.out.println(Db.find(Db.getSqlPara(installKey("listByST"), cond)));
            return true;
        }

        private static boolean findCategory() {
            System.out.println(Db.find(Db.getSql(installKey("findCategory")), 1, "2"));
            return true;
        }


        private static boolean findAccountExtInfo() {
            System.out.println(Db.find(Db.getSql(installKey("findAccountExtInfo")), 1));
            return true;
        }


        private static boolean findAccountExtType() {
            System.out.println(Db.find(Db.getSql(installKey("findAccountExtType"))));
            return true;
        }

        private static boolean findAccountToPage() {
            Kv kv = Kv.create();
            kv.set("query_key", 1);
            kv.set("service_status", new int[]{1, 2, 3});
            kv.set("org_name", "1");
            SqlPara findAccountToPage = Db.getSqlPara(installKey("findAccountToPage"), Kv.by("map", kv));
            System.out.println(findAccountToPage);
            List<Record> list = Db.find(findAccountToPage);
            System.out.println(list);
            return true;
        }

        private static String installKey(String key) {
            return "acc." + key;
        }
    }

}
