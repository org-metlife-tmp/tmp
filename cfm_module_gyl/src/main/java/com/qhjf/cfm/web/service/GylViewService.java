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
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

public class GylViewService {

    private static final Log log = LogbackLog.getLog(GylViewService.class);

	
	/**
	 * 查询列表,单据默认状态
	 * @param record
	 * @param statusName
	 */
	public void setBillListStatus(Record record, String statusName) {
		List status = record.get(statusName);
        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey()
            });
        }
		
	}

	/**
	 * 
	 * 归集通列表
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @return
	 */
	public Page<Record> findList(int pageNum, int pageSize, Record record) {
		 List<Integer> is_activity = record.get("is_activity");
		 if(null == is_activity || is_activity.size() == 0) {
			 record.remove("is_activity");
		 }
		 SqlPara sqlPara = getlistparam(record, "gyl_view.findList");
	     return Db.paginate(pageNum, pageSize, sqlPara);
	}
	/**
	 * 拼接查询语句参数
	 * @param record
	 * @param sql
	 * @return
	 */
	private SqlPara getlistparam(Record record, String sql) {

		CommonService.processQueryKey(record,"pay_query_key","pay_acc_name","pay_acc_no");
		CommonService.processQueryKey(record,"recv_query_key","recv_acc_name","recv_acc_no");

	    //判断is_activity 字段
	    List<Integer> is_activity =  record.get("is_activity");
	    if( null == is_activity || is_activity.size() == 0 ) {
	    	record.remove("is_activity");
	    }	    

	    record.set("delete_flag", 0);
	    return Db.getSqlPara(sql, Kv.by("map", record.getColumns()));
	}

	/**
	 * 广银联查看详情
	 * @param record
	 * @return
	 * @throws ReqDataException 
	 */
	public Record detail(Record record) throws ReqDataException {
		Long id = TypeUtils.castToLong(record.get("id"));		
		Record findById = Db.findById("gyl_allocation_topic", "id", id);
		if(null == findById){
			log.info("================未找到相应的广银联信息");
			throw new ReqDataException("未找到相应的广银联信息");		
		}
		return findById;
	}
}
