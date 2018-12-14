package com.qhjf.cfm.web.util.jyyet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.TransactionIdentify;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

public class CollectionMergeUtil {

    private static final LogbackLog log = LogbackLog.getLog(CollectionMergeUtil.class);

    public static final SimpleDateFormat DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat TIME = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat DATE_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 余额导入功能使用
     * 1.加数据源
     * 2.把‘余额’赋值给‘可用余额’
     * 3.把source中的[acc_id,bank_type,acc_name]字段合并到dest中
     * 最终返回List<Record>
     *
     * @param dest excel中的导入数据
     * @param source account数据
     * @param type	1:当日余额；2：历史余额
     * @throws ReqDataException 
     */
	public static List<Record> mergeAccountInfo(List<Map<String, Object>> dest ,int type) throws ReqDataException {
        List<Record> excelDataObj = new ArrayList<>();
        for (Map<String, Object> map : dest) {
            /**
             * “余额日期”：当日余额存当天、历史余额存excel中bal_date导入的数据
             */
        	Date date = new Date();
            if (type == 1) {
            	String bal_date = map.get("bal_date").toString();//HH:mm:ss
            	map.put("bal_date",date);
            	String today = DATE.format(date);
            	map.put("import_time", today.concat(" ").concat(bal_date));//余额日期数据库为date，不能存time，为了不影响其他业务，把余额时间存入导入时间
			}else {
				map.put("import_time", date);
			}
            
            Record record = new Record().setColumns(map)
                    .set("data_source", 2)// 添加字段：数据来源为手工导入
                    .set("available_bal", map.get("bal"));//余额与可用余额保持一致
           
            Map<String, Object> accountRow = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", map.get("acc_no").toString());
            if (accountRow == null) {
            	String errMes = String.format("从缓存中获取表【account】【acc_no=%s】的数据结果为null", map.get("acc_no").toString());
				log.error(errMes);
				throw new ReqDataException(errMes);
			}
            record.set("acc_id", accountRow.get("acc_id"))
	            .set("acc_name", accountRow.get("acc_name"))
	            .set("bank_type", accountRow.get("bank_type"));
            
            excelDataObj.add(record);
        }
        return excelDataObj;
    }

    /**
     * 交易导入功能使用
     * 1.加数据源
     * 2.把source中的[acc_id,bank_type,acc_name]字段合并到dest中
     *
     * @param dest
     * @param source
     * @return
     * @throws BusinessException
     */
    public static List<Record> transMergeAccountInfo(List<Map<String, Object>> dest) throws BusinessException {
        List<Record> excelDataObj = new ArrayList<>();
        for (Map<String, Object> map : dest) {

            Record record = new Record().setColumns(map)
                    .set("data_source", 2);// 添加字段：数据来源为手工导入

            Map<String, Object> accountRow = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", map.get("acc_no").toString());
            if (accountRow == null) {
            	String errMes = String.format("从缓存中获取表【account】【acc_no=%s】的数据结果为null", map.get("acc_no").toString());
				log.error(errMes);
				throw new ReqDataException(errMes);
			}
            record.set("acc_id", accountRow.get("acc_id"))
	            .set("acc_name", accountRow.get("acc_name"))
	            .set("bank_type", accountRow.get("bank_type"));
            if (TypeUtils.castToDate(map.get("trans_date")) == null) {
            	record.set("trans_date", DATE.format(new Date()));
            }
            

            //添加“交易唯一标识”
            String identify = null;
            try {
                identify = new TransactionIdentify(Long.parseLong(record.get("acc_id").toString())
                        , new BigDecimal(record.get("amount").toString())
                        , Integer.parseInt(record.get("direction").toString())
                        , record.get("opp_acc_no") == null ? "" : record.get("opp_acc_no").toString()
                        , record.get("opp_acc_name") == null ? "" : record.get("opp_acc_name").toString()
                        , record.get("trans_date").toString().concat(" ").concat(record.get("trans_time").toString())
                        , record.get("summary") == null ? "" : record.get("summary").toString()
                        , null
                        , record.get("post_script") == null ? "" : record.get("post_script").toString())
                		.getIdentify();
                record.set("identifier", identify);
            } catch (NumberFormatException e) {
                log.error("生成交易唯一标识异常", e);
                throw new ReqDataException("生成交易唯一标识异常");
            }
            excelDataObj.add(record);
        }
        return excelDataObj;
    }
}
