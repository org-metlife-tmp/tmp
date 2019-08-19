package com.qhjf.cfm.sqltest;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SqlTest {

    private static final String SQLSERVER = "sqlserverARP";
    private static final String MYSQL = "mysqlARP";

    DruidPlugin sqlserverDP = null;
    ActiveRecordPlugin sqlserverARP = null;

    DruidPlugin mysqlDP = null;
    ActiveRecordPlugin mysqlARP = null;

    @Before
    public void start() {
        sqlserverDP = new DruidPlugin("jdbc:sqlserver://10.1.1.2:1433;DatabaseName=corpzone_sunlife", "sa", "Admin123");
        sqlserverARP = new ActiveRecordPlugin(SQLSERVER, sqlserverDP);
        sqlserverARP.setDialect(new SqlServerDialect());
        sqlserverDP.start();
        sqlserverARP.start();

        mysqlDP = new DruidPlugin("jdbc:mysql://10.1.1.2:3306/corpzone_sunlife", "cfm", "cfm");
        mysqlARP = new ActiveRecordPlugin(MYSQL, mysqlDP);
        mysqlARP.setDialect(new MysqlDialect());
        mysqlDP.start();
        mysqlARP.start();
    }

    @After
    public void stop() {
        if (null != sqlserverARP) sqlserverARP.stop();
        if (null != sqlserverDP) sqlserverDP.stop();

        if (null != mysqlARP) mysqlARP.stop();
        if (null != mysqlDP) mysqlDP.stop();
    }

    @Test
    public void test() throws SQLException {
        List<String> tables = Db.use(MYSQL).query(" SELECT a.table_name FROM information_schema.tables a WHERE a.table_schema='corpzone_sunlife' AND LEFT(a.table_name,1) > 'a' and a.table_name in('const_bank_type_query') ");

        for (String table : tables) {
            //Db.use(SQLSERVER).update(" TRUNCATE TABLE "+table);
            long count = Db.use(MYSQL).queryLong(" select count(*) from " + table);
            if (count == 0) {
                continue;
            }
            //查询主键
            Record key = Db.use(MYSQL).findFirst("SELECT \n" +
                    "  c.column_name AS c_name,\n" +
                    "  t.AUTO_INCREMENT AS a_int\n" +
                    "FROM\n" +
                    "  INFORMATION_SCHEMA.`KEY_COLUMN_USAGE` c,\n" +
                    "  information_schema.TABLES t\n" +
                    "WHERE c.table_name = '" + table + "' \n" +
                    "  AND c.`TABLE_NAME` = t.`TABLE_NAME`\n" +
                    "  AND CONSTRAINT_SCHEMA = 'corpzone_sunlife' \n" +
                    "  AND constraint_name = 'PRIMARY' \n" +
                    "  ORDER BY a_int DESC");
            List<Record> list = Db.use(MYSQL).find(" select * from " + table);
            //删除主键，防止数据无法插入
            if (null != key && key.get("a_int") != null) {
                for (Record record : list) {
                    record.remove(key.getStr("c_name"));
                }
            }
            Record first = list.get(0);

            //存储 first.getColumns() 循环下标
            int index = 0;
            int h = 0;
            //存储数组列
            String[] columnArr = new String[first.getColumns().size()];
            //构建插入sql
            StringBuilder sql = new StringBuilder(" insert into " + table + "(");
            for (Map.Entry<String, Object> entry : first.getColumns().entrySet()) {
                if (index++ > 0) {
                    sql.append(",");
                }
                sql.append("[" + entry.getKey().trim() + "]");
                columnArr[h++] = entry.getKey().trim();
            }
            sql.append(") values (");
            for (int j = 0; j < index; j++) {
                if (j > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");
            PreparedStatement pst = Db.use(SQLSERVER).getConfig().getConnection().prepareStatement(sql.toString());


            int counter = 0;
            System.out.println("--------------------开始--------------------");
            for (int a = 0; a < list.size(); a++) {
                Record record = list.get(a);
                for (int b = 0; b < columnArr.length; b++) {
                    Object value = record.get(columnArr[b]);
                    if (value instanceof java.util.Date) {
                        if (value instanceof java.sql.Date) {
                            pst.setDate(b + 1, (java.sql.Date) value);
                        } else if (value instanceof java.sql.Timestamp) {
                            pst.setTimestamp(b + 1, (java.sql.Timestamp) value);
                        } else {
                            // Oracle、SqlServer 中的 TIMESTAMP、DATE 支持 new Date() 给值
                            java.util.Date d = (java.util.Date) value;
                            pst.setTimestamp(b + 1, new java.sql.Timestamp(d.getTime()));
                        }
                    } else {
                        pst.setObject(b + 1, value);
                    }
                }
                pst.addBatch();
                if (++counter >= 1000) {
                    counter = 0;
                    int[] r = pst.executeBatch();
                    System.out.println("---------------------------------------------------");
                    System.out.println("本次" + table + "表插入数据量：" + r.length);
                    System.out.println(sql.toString());
                    System.out.println(Arrays.toString(columnArr));
                    System.out.println(Arrays.toString(r));
                    System.out.println("---------------------------------------------------");
                }
            }
            int[] r = pst.executeBatch();
            System.out.println("---------------------------------------------------");
            System.out.println("本次" + table + "表插入数据量：" + r.length);
            System.out.println(sql.toString());
            System.out.println(Arrays.toString(columnArr));
            System.out.println(Arrays.toString(r));
            System.out.println("---------------------------------------------------");
            System.out.println("--------------------结束--------------------");
        }
    }
}
