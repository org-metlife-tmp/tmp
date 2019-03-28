package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ConstructDetailMapService {

	/**
	 * 网片详情继续封装
	 * @param detail_map 
	 * @param record
	 * @param channel_code
	 * @param documentType
	 */
	public void constructDetailRecord(Map<String, Object> detail_map, Record record, Integer document_moudle) {
		//金额需要由 元 转换为 分
		BigDecimal detail_cent = record.getBigDecimal("amount").multiply(new BigDecimal(100)).setScale(0);
		detail_map.put("detail_cent", detail_cent);	
		detail_map.put("pay_code", record.getStr("pay_code"));
			
		// 银行 type
		if(null != record.get("recv_bank_type")) {
			//批付
			String recv_bank_type = record.getStr("recv_bank_type");
			List<Record> find = Db.find(Db.getSql("check_batch.findOppoBanktype"), recv_bank_type,document_moudle);
			if(null == find || find.size() == 0) {
				detail_map.put("recv_bank_type", record.get("recv_bank_type"));				
			}else {
				detail_map.put("recv_bank_type", find.get(0).get("oppo_bank_type"));	
			}
		}else {
			//批收
			String pay_bank_type = record.getStr("pay_bank_type");
			List<Record> find = Db.find(Db.getSql("check_batch.findOppoBanktype"), pay_bank_type,document_moudle);
			if(null == find || find.size() == 0) {
				detail_map.put("pay_bank_type", record.get("pay_bank_type"));				
			}else {
				detail_map.put("pay_bank_type", find.get(0).get("oppo_bank_type"));	
			}
		}
	}
	
	
	
	
	

}
