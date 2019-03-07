package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
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
		// 银行 type
		detail_map.put("recv_bank_type", record.get("recv_bank_type"));
		detail_map.put("pay_bank_type", record.get("pay_bank_type"));
		if(StringUtils.isNotBlank(record.getStr("insure_bill_no"))) {
			detail_map.put("insure_bill_no", record.getStr("insure_bill_no"));
		}else {
			detail_map.put("insure_bill_no", record.getStr("preinsure_bill_no"));
		}
	}

}
