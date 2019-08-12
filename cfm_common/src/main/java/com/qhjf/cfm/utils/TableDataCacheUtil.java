package com.qhjf.cfm.utils;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表数据缓存工具类
 *
 * @author CHT
 */
public class TableDataCacheUtil {

    private final static Log logger = LogbackLog.getLog(TableDataCacheUtil.class);
    private static final Object obj = new Object();
    private static final String ACCOUNT_SQL = "select *,left(bank_cnaps_code,3) as bank_type from %s where status=1 and is_activity=1 and %s='%s'";
    private static final String SUPPLIER_ACC_INFO_SQL = "select * from %s where delete_flag=0 and %s='%s'";

    private TableDataCacheUtil() {
    }

    /**
     * key：表名 value：表数据（key：列名；value：列值）
     */
    private static Map<String, List<Map<String, Object>>> tableList =
            new HashMap<String, List<Map<String, Object>>>();

    /**
     * 获取缓存的表数据（常用表数据做缓存，不是整张表）
     *
     * @param tableName 表名
     * @return
     */
    private List<Map<String, Object>> getTable(String tableName) {
        return tableList.get(tableName);
    }

    /**
     * 根据唯一键，获取缓存的表数据
     *
     * @param tableName         表名
     * @param uniqueColumnName  表的唯一键
     * @param uniqueColumnValue 表的唯一键对用的值（日期格式按照yyyy-MM-dd HH:mm:ss转换为字符串，其他格式原样转换为字符串）
     * @return
     */
    public synchronized Map<String, Object> getARowData(String tableName, String uniqueColumnName, String uniqueColumnValue) {
        synchronized (obj) {
            if (null == uniqueColumnValue || null == uniqueColumnName) {
                return null;
            }
            //传入的数据库表的列名为数字，则拒绝执行业务
            if (RegexUtils.isNumber(uniqueColumnName)) {
                return null;
            }

            Map<String, Object> result = null;
            //获取已经缓存了的数据
            List<Map<String, Object>> table = getTable(tableName);

            //如果查询的表没有缓存，就根据表的唯一键去数据库查询，否则读取缓存
            if (null == table || table.isEmpty()) {
                table = new ArrayList<>();
                //查询数据库
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("[Cache]-------表【%s】未缓存，查询数据库", tableName));
                }
                result = queryTableRow(tableName, uniqueColumnName, uniqueColumnValue);
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("[Cache]-------查询数据库结果：%s", result));
                }

                if (null != result) {
                    table.add(result);
                    //放入缓存
                    addTable(tableName, table);
                }
            } else {
                //读取缓存
                for (Map<String, Object> row : table) {
                    Object colomn = row.get(uniqueColumnName);
                    if (uniqueColumnValue.equals(colomn.toString())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(String.format("[Cache]-------读取到表【%s】缓存【%s=%s】数据【%s】", tableName, uniqueColumnName, uniqueColumnValue, row));
                        }
                        result = row;
                        break;
                    }
                }

                //没有读取到缓存则取查询数据库
                if (null == result) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("[Cache]-------未读取到表【%s】缓存【%s=%s】，查询数据库", tableName, uniqueColumnName, uniqueColumnValue));
                    }
                    result = queryTableRow(tableName, uniqueColumnName, uniqueColumnValue);
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("[Cache]-------查询数据库结果：%s", result));
                    }
                    //放入缓存
                    if (null != result) {
                        addTable(table, result);
                    }
                }
            }
            return result;
        }

    }

    /**
     * 删除缓存中的一行数据
     *
     * @param tableName         表名
     * @param uniqueColumnName  表列名
     * @param uniqueColumnValue 表列值
     */
    public synchronized void removeRowData(String tableName, String uniqueColumnName, String uniqueColumnValue) {
        synchronized (obj) {
            Map<String, Object> target = null;
            List<Map<String, Object>> table = getTable(tableName);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("删除前条数：%s", table == null ? 0 : table.size()));
                logger.debug(String.format("[Cache]-------删除表【%s】缓存【%s=%s】，开始删除。。。", tableName, uniqueColumnName,
                        uniqueColumnValue));
            }
            
            if (null != table) {
            	for (Map<String, Object> row : table) {
            		if (uniqueColumnValue.equals(row.get(uniqueColumnName).toString())) {
//					table.remove(row);
            			target = row;
            			break;
            		}
            	}
            	if (null != target) {
            		table.remove(target);
            	}
			}
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("删除后条数：%s", table == null ? 0 : table.size()));
                logger.debug(String.format("[Cache]-------删除表【%s】缓存【%s=%s】，删除结束：【%s】", tableName, uniqueColumnName,
                        uniqueColumnValue, target));
            }
        }

    }

    /**
     * 查询表
     *
     * @param tableName         表名
     * @param uniqueColumnName  列名
     * @param uniqueColumnValue 列值
     * @return
     */
    private Map<String, Object> queryTableRow(String tableName, String uniqueColumnName, String uniqueColumnValue) {
        String sql = "select * from %s where %s='%s'";
        Map<String, Object> result = null;
        if ("account".equals(tableName.toLowerCase())) {
            sql = ACCOUNT_SQL;
        }
        if ("supplier_acc_info".equals(tableName.toLowerCase())) {
            sql = SUPPLIER_ACC_INFO_SQL;
        }
        sql = String.format(sql, tableName, uniqueColumnName, uniqueColumnValue);
        Record record = Db.findFirst(sql);
        if (null != record) {
            result = record.getColumns();
        }
        return result;
    }

    private void addTable(String tableName, List<Map<String, Object>> tableData) {
        tableList.put(tableName, tableData);
    }

    private void addTable(List<Map<String, Object>> tableData, Map<String, Object> row) {
        tableData.add(row);
    }


    public static TableDataCacheUtil getInstance() {
        return InnerClass.INSTANCE;
    }

    private static class InnerClass {
        private static final TableDataCacheUtil INSTANCE = new TableDataCacheUtil();
    }

}
