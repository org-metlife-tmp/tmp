package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Record;

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
	}

}
