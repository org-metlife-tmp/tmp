package com.qhjf.cfm.web.service;

import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.controller.CollectViewController;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

public class CollectReportService {

    private static final Log log = LogbackLog.getLog(CollectReportService.class);

	
	/**
	 * 
	  *  查询归集通报表
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @return
	 * @throws ReqDataException 
	 */
	public Page<Record> findList(int pageNum, int pageSize, Record record) throws ReqDataException {
		//根公司分组还是账户分组
		Long query_key = TypeUtils.castToLong(record.get("query_key"));
		log.info("query_key参数=============="+query_key);
		record.remove("query_key");	
		record.set("collect_status", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
		record = this.buildRecord(record);
		SqlPara sqlPara = Db.getSqlPara("collect_report.collectByOrgList", Kv.by("map", record.getColumns()));
		return Db.paginate(pageNum, pageSize, sqlPara);
	}
	
	
	/**
	 * 
	 * 查询归集通报表_报表
	 * @param record
	 * @return
	 */
	public List<Record> findChartList(Record record) throws ReqDataException {
		//根公司分组还是账户分组
		long query_key = TypeUtils.castToLong(record.get("query_key"));
		log.info("query_key参数=============="+query_key);
		record.remove("query_key");	
		SqlPara sqlPara = null;
		record.set("collect_status", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
		record = this.buildRecord(record);
		if(1 == query_key){
			//公司
			sqlPara = Db.getSqlPara("collect_report.collectByOrgChart", Kv.by("map", record.getColumns()));
		}else{
			//账号
			sqlPara = Db.getSqlPara("collect_report.collectByAccChart", Kv.by("map", record.getColumns()));
		}		
		return Db.find(sqlPara);
	}

	/**
	 * 封装sql语句参数
	 * @param record
	 */
    private Record buildRecord(Record record) {
    	 List<Integer> ids = record.get("pay_account_org_id");
    	 if(null == ids || ids.size() == 0 ){
    		 record.remove("pay_account_org_id");
    	 }
    	 List<String> npscodes = record.get("pay_bank_cnaps");
    	 if(null == npscodes || npscodes.size() == 0 ){
    		 record.remove("pay_bank_cnaps");
    	 }
    	 return record ;
	}

	/**
      * 
            * 归集通获取总汇总金额
      * @param record
      * @return
      */
	public Record collectSum(Record record) {
        return Db.findFirst(Db.getSqlPara("collect_report.collectSum", Kv.by("map", record.getColumns())));
	}


}
