package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class ConstructDetailMapService {

	/**
	 * 网片详情继续封装
	 * @param detail_map 
	 * @param record
	 * @param integer 
	 * @param channel_code 
	 * @param channel_code
	 * @param documentType
	 */
	public void constructDetailRecord(Map<String, Object> detail_map, Record record, Integer document_moudle, String channel_code, Integer source_sys) {
		//金额需要由 元 转换为 分
		BigDecimal detail_cent = record.getBigDecimal("amount").multiply(new BigDecimal(100)).setScale(0);
		detail_map.put("detail_cent", detail_cent);	
		detail_map.put("pay_code", record.getStr("pay_code"));
			
		// 银行 type  部分银行接口的银行大类与我系统内不一致.在此映射.如果找不到就默认取我系统
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
		if(StringUtils.isNotBlank(record.getStr("insure_bill_no"))) {
			detail_map.put("insure_bill_no", record.getStr("insure_bill_no"));
		}else {
			detail_map.put("insure_bill_no", record.getStr("preinsure_bill_no"));
		}
		//根据bankkey 和 channel_code 来判断是否需要展示省份
		if( 0 == source_sys) {
			if("B3".equals(channel_code) && ("ABC,NINGBOBANK,BOC").contains(record.getStr("bank_key")) 
					||  "B1,B0".contains(channel_code) &&  ("BOC_YL,BOC,BJ_BOC").contains(record.getStr("bank_key")) 
					||  "B2".equals(channel_code) && ("ABC,BOC_YL,ABC_YL,DL_BOC,SY_BOC,SRCB,NINGBOBANK,FS_ABC,GZ_ABC,BOC,BJ_BOC").contains(record.getStr("bank_key"))
					) {
				detail_map.put("province", record.get("province"));	
			}
		}
	}
	
	
	
	
	

}
