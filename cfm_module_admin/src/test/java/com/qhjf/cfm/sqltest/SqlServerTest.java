package com.qhjf.cfm.sqltest;

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
        arp.addSqlTemplate("/sql/sqlserver/admin_cfm.sql");
        dp.start();
        arp.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
    }

    //测试 currency.sql
    @Test
    public void testCurrency() {
        Currency.getCurrencyList();
        Currency.findCurrencyById();
        Currency.currencyDefaultReset();
    }

    //测试 department.sql
    @Test
    public void testDepartment() {
        Department.getDepartmentList();
        Department.findDepartmentById();
        Department.checkDeptUse();
        Department.getDeptNumByName();
        Department.getDeptNumByNameExcludeId();
    }

    //测试 handle_channel_setting.sql
    @Test
    public void testHcs() {
        Hcs.getChannelById();
        Hcs.getChannelPage();
    }

    //测试 handle_route_setting.sql
    @Test
    public void testHrs() {
        Hrs.getHandleRouteSettingList();
    }

    //测试 handle_route_setting_item.sql
    @Test
    public void testHrsi() {
        Hrsi.getRouteItemList();
        Hrsi.deleteRouteItemByRouteId();
    }

    //测试 merchant_acc_info.sql
    @Test
    public void testMai() {
        Mai.getMerchantList();
        Mai.getMerchantAccInfoById();
        Mai.getMerchantAccInfoByAccNo();

    }

    //测试 organization.sql
    @Test
    public void testOrg() {
        Org.getOrgList();
        Org.findOrgInfoById();
        Org.getMAXCodeSql();
        Org.delOrgExt();
        Org.childOrgNum();
        Org.orgUserNum();
        Org.getOrgNumByCode();
        Org.getOrgNumByCodeExcludeId();
        Org.getCurrentUserOrgs();
    }

    //测试 position.sql
    @Test
    public void testPosi() {
        Posi.getPositionPage();
        Posi.checkUse();
        Posi.getPosInfo();
        Posi.getPosNumByName();
        Posi.getPosNumByNameExcludeId();
    }

    //测试 settle_account_info.sql
    @Test
    public void testSai() {
        Sai.getSettleById();
        Sai.settlePage();
    }

    //测试 user.sql
    @Test
    public void testUser() {
        User.userPage();
        User.checkUserLoginName();
        User.userExtInfo();
        User.usrUdopList();
        User.userInfo();
        User.userMenuPage();
        User.userGroupIds();
    }

    //测试 usergroup.sql
    @Test
    public void testUsergroup() {
        Usergroup.menus();
        Usergroup.modules();
        Usergroup.uodp_ids();
        Usergroup.usergroup();
        Usergroup.usergroupMenus();
    }

    private static class Usergroup {

        private static boolean uodp_ids() {
            List<Long> group_id = Db.query(Db.getSql("usergroup.uodp_ids"), 1);
            System.out.println(group_id);
            return true;
        }

        private static boolean usergroupMenus() {
            SqlPara ugMenusSqlPara = Db.getSqlPara("usergroup.usergroupMenus", 1);
            List<String> query = Db.query(ugMenusSqlPara.getSql(), ugMenusSqlPara.getPara());
            System.out.println(query);
            return true;
        }

        private static boolean usergroup() {
            Record record = new Record();
            SqlPara usergroupSqlPara = Db.getSqlPara("usergroup.usergroup", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(1, 10, usergroupSqlPara);
            System.out.println(page);
            return true;
        }

        private static boolean menus() {
            SqlPara menusSqlPara = Db.getSqlPara("usergroup.menus", Kv.by("mc", "code"));
            List<Record> list = Db.find(menusSqlPara);
            System.out.println(list);
            return true;
        }

        private static boolean modules() {
            SqlPara modulesSqlPara = Db.getSqlPara("usergroup.modules", Kv.by("isAdmin", 0));
            List<Record> modulesList = Db.find(modulesSqlPara);
            System.out.println(modulesList);
            return true;
        }
    }

    private static class User {

        private static boolean userGroupIds() {
            List<Object> uodp_id = Db.query(Db.getSql("user.userGroupIds"), 1);
            System.out.println(uodp_id);
            return true;
        }

        private static boolean userMenuPage() {
            Record record = new Record();
            record.set("name", "111");
            SqlPara usrSqlPara = Db.getSqlPara("user.userMenuPage", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(1, 10, usrSqlPara);
            System.out.println(page);
            return true;
        }

        private static boolean userInfo() {
            Record usrInfo = Db.findFirst(Db.getSql("user.userInfo"), 1);
            System.out.println(usrInfo);
            return true;
        }

        private static boolean usrUdopList() {
            List<Record> list = Db.find(Db.getSql("user.usrUdopList"), 1);
            System.out.println(list);
            return true;
        }

        private static boolean userExtInfo() {
            List<Record> list = Db.find(Db.getSql("user.userExtInfo"), 1);
            System.out.println(list);
            return true;
        }

        private static boolean checkUserLoginName() {
            Kv cond = Kv.by("status != ", 3).set("login_name = ", "111");
            SqlPara sqlPara = Db.getSqlPara("user.checkUserLoginName", Kv.by("map", cond));
            Long aLong = Db.queryLong(sqlPara.getSql(), sqlPara.getPara());
            System.out.println(aLong);
            return true;
        }

        public static boolean userPage() {
            Record record = new Record();
            record.set("name", "111");
            SqlPara sqlPara = Db.getSqlPara("user.userPage", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(1, 10, sqlPara);
            System.out.println(page);
            return true;
        }
    }

    private static class Sai {

        private static boolean settlePage() {
            Record record = new Record();
            record.set("query_key", "111");
            record.set("acc_no", "222");
            SqlPara sqlPara = Db.getSqlPara("settle.settlePage", Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static boolean getSettleById() {
            String settlesql = Db.getSql("settle.getSettleById");
            System.out.println(Db.findFirst(settlesql, 1));
            return true;
        }
    }

    private static class Posi {

        private static boolean getPosNumByNameExcludeId() {
            long re = Db.queryLong(Db.getSql("position.getPosNumByNameExcludeId"), "111", 1);
            System.out.println(re);
            return true;
        }

        private static boolean getPosNumByName() {
            long re = Db.queryLong(Db.getSql("position.getPosNumByName"), "111");
            System.out.println(re);
            return true;
        }

        private static boolean getPosInfo() {
            String sql = Db.getSql("position.getPosInfo");
            Record first = Db.findFirst(sql, 1);
            System.out.println(first);
            return true;
        }

        private static boolean checkUse() {
            String sql = Db.getSql("position.checkUse");
            long counts = Db.queryLong(sql, 1);
            System.out.println(counts);
            return true;
        }

        private static boolean getPositionPage() {
            Record record = new Record();
            record.set("name", "111");
            SqlPara sqlPara = Db.getSqlPara("position.getPositionPage", Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }
    }

    private static class Org {

        private static boolean getCurrentUserOrgs() {
            System.out.println(Db.find(Db.getSql("org.getCurrentUserOrgs"), 1));
            return true;
        }

        private static boolean getOrgNumByCodeExcludeId() {
            long re = Db.queryLong(Db.getSql("org.getOrgNumByCodeExcludeId"), "111", 1);
            System.out.println(re);
            return true;
        }

        private static boolean getOrgNumByCode() {
            long re = Db.queryLong(Db.getSql("org.getOrgNumByCode"), "111");
            System.out.println(re);
            return true;
        }

        private static boolean orgUserNum() {
            long orgUserNum = Db.queryLong(Db.getSql("org.orgUserNum"), 1);
            System.out.println(orgUserNum);
            return true;
        }

        private static boolean childOrgNum() {
            long childOrgNum = Db.queryLong(Db.getSql("org.childOrgNum"), 1);
            System.out.println(childOrgNum);
            return true;
        }

        private static boolean delOrgExt() {
            int delete = Db.delete(Db.getSql("org.delOrgExt"), 1);
            System.out.println(delete);
            return true;
        }

        private static boolean getMAXCodeSql() {
            Record maxCodeRecord = Db.findFirst(Db.getSql("org.getMAXCodeSql"), "1111", 1);
            System.out.println(maxCodeRecord);
            return true;
        }

        private static boolean findOrgInfoById() {
            Record record = Db.findFirst(Db.getSqlPara("org.findOrgInfoById", 1));
            System.out.println(record);
            return true;
        }

        private static boolean getOrgList() {
            String sql = Db.getSql("org.getOrgList");
            List<Record> list = Db.find(sql);
            System.out.println(list);
            return true;
        }
    }

    private static class Mai {

        private static boolean getMerchantAccInfoByAccNo() {
            String mersql = Db.getSql("merchant.getMerchantAccInfoByAccNo");
            Record merRec = Db.findFirst(mersql, "111");
            System.out.println(merRec);
            return true;
        }

        private static boolean getMerchantAccInfoById() {
            String sql = Db.getSql("merchant.getMerchantAccInfoById");
            Record first = Db.findFirst(sql, 1, 1);
            System.out.println(first);
            return true;
        }

        private static boolean getMerchantList() {
            Record record = new Record();
            record.set("acc_no", "111");
            record.set("acc_name", "222");
            record.set("channel_code", 333);
            SqlPara sqlPara = Db.getSqlPara("merchant.getMerchantList", Kv.by("cond", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }
    }

    private static class Hrsi {
        private static boolean getRouteItemList() {
            String sql = Db.getSql("routeitem.getRouteItemList");
            List<Record> routeItmeRec = Db.find(sql, 1);
            System.out.println(routeItmeRec);
            return true;
        }

        private static boolean deleteRouteItemByRouteId() {
            System.out.println(Db.delete(Db.getSql("routeitem.deleteRouteItemByRouteId"), 1));
            return true;
        }
    }

    private static class Hrs {
        private static boolean getHandleRouteSettingList() {
            Record record = new Record();
            record.set("org_exp", "111");
            record.set("biz_type_exp", "222");
            record.set("insurance_type_exp", "333");
            SqlPara sqlPara = Db.getSqlPara("route.getHandleRouteSettingList", Kv.by("cond", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }
    }

    private static class Hcs {

        private static boolean getChannelPage() {
            Record record = new Record();
            record.set("query_key", "111");
            SqlPara sqlPara = Db.getSqlPara("channel.getChannelPage", Kv.by("map", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static boolean getChannelById() {
            String channelsql = Db.getSql("channel.getChannelById");
            Record channelRec = Db.findFirst(channelsql, "111");
            System.out.println(channelRec);
            return true;
        }
    }

    private static class Department {

        private static boolean getDeptNumByNameExcludeId() {
            long re = Db.queryLong(Db.getSql("department.getDeptNumByNameExcludeId"), "111", 1);
            System.out.println(re);
            return true;
        }

        private static boolean getDeptNumByName() {
            long re = Db.queryLong(Db.getSql("department.getDeptNumByName"), "111");
            System.out.println(re);
            return true;
        }

        private static boolean checkDeptUse() {
            String usesql = Db.getSql("department.checkDeptUse");
            long checkuse = Db.queryLong(usesql, 1);
            System.out.println(checkuse);
            return true;
        }

        private static boolean findDepartmentById() {
            String sql = Db.getSql("department.findDepartmentById");
            Record first = Db.findFirst(sql, 1, 0);
            System.out.println(first);
            return true;
        }

        private static boolean getDepartmentList() {
            Record record = new Record();
            record.set("name", "111");
            SqlPara sqlPara = Db.getSqlPara("department.getDepartmentList", Kv.by("cond", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }
    }

    private static class Currency {

        private static boolean currencyDefaultReset() {
            System.out.println(Db.update(Db.getSql(installKey("currencyDefaultReset")), 1));
            return true;
        }

        private static boolean findCurrencyById() {
            System.out.println(Db.findFirst(Db.getSql(installKey("findCurrencyById")), 1));
            return true;
        }

        private static boolean getCurrencyList() {
            Record record = new Record();
            record.set("name", "111");
            SqlPara sqlPara = Db.getSqlPara(installKey("getCurrencyList"), Kv.by("cond", record.getColumns()));
            Page<Record> paginate = Db.paginate(1, 10, sqlPara);
            System.out.println(paginate);
            return true;
        }

        private static String installKey(String key) {
            return "currency." + key;
        }
    }

}
