package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Record;

public class ConstructTotalMapService {

	/**
	 * 构建汇总信息的Record
	 * @param map 
	 * @param record
	 * @param documentType
	 * @param document_typerecord
	 */
	public  void  constructTotalRecord(Map<String, Object> map, Record record, Integer document_moudle){
		//提交日期  yyyyMMdd
		String submit_date = DateKit.toStr(new Date(), DateKit.datePattern).replaceAll("-", "");
		map.put("submit_date", submit_date);
		// 总金额  部分渠道需要单位为分
		if(null != record.get("pay_total_amount")) {
			BigDecimal total_cent = record.getBigDecimal("pay_total_amount").multiply(new BigDecimal(100)).setScale(0);
			map.put("total_cent", total_cent);			
		}
		if(null != record.get("recv_total_amount")) {
			// 总金额 批收 部分渠道需要单位为分
			BigDecimal recv_total_cent = record.getBigDecimal("recv_total_amount").multiply(new BigDecimal(100)).setScale(0);
			map.put("recv_total_cent", recv_total_cent);			
		}
	}	
}
