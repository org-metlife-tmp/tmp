package com.qhjf.cfm.web.inter.manager;

import java.util.HashMap;
import java.util.Map;

import com.qhjf.cfm.web.constant.WebConstant;

public class SysInterManager {
	
	private static Map<String,String> statusFiledMap = new HashMap<String,String>();
	private static Map<String,Map<String,Integer>> statusEnumMap = new HashMap<String,Map<String,Integer>>();
	private static String defaultStatusField = "service_status";
	private static Integer defaultSuccessStatusEnum = WebConstant.BillStatus.SUCCESS.getKey();
	private static Integer defaultFailStatusEnum = WebConstant.BillStatus.FAILED.getKey();
	
	static{
		statusFiledMap.put("collect_execute_instruction", "collect_status");
		Map<String,Integer> collectStatusEnums = new HashMap<String,Integer>();
		collectStatusEnums.put("SUCCESS", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
		collectStatusEnums.put("FAIL", WebConstant.CollOrPoolRunStatus.FAILED.getKey());
		statusEnumMap.put("collect_execute_instruction", collectStatusEnums);


		statusFiledMap.put("inner_batchpay_bus_attach_detail","pay_status");
		statusFiledMap.put("outer_batchpay_bus_attach_detail","pay_status");
		Map<String,Integer> batchPayEnums  = new HashMap<String,Integer>();
		batchPayEnums.put("SUCCESS", WebConstant.PayStatus.SUCCESS.getKey());
		batchPayEnums.put("FAIL", WebConstant.PayStatus.FAILD.getKey());
		statusEnumMap.put("inner_batchpay_bus_attach_detail",batchPayEnums);
		statusEnumMap.put("outer_batchpay_bus_attach_detail",batchPayEnums);
		statusEnumMap.put("oa_branch_payment_item",batchPayEnums);
	}


	public static String getSourceRefPrimaryKey(String tableName){
		switch (tableName){
			case "inner_batchpay_bus_attach_detail":
			case "outer_batchpay_bus_attach_detail":
				return "detail_id";
			default:
				return "id";
		}
	}

	public static String getStatusFiled(String tableName){
		String statusField = statusFiledMap.get(tableName);
		if(statusField == null || statusField.length() ==0){
			statusField = defaultStatusField;
		}
		return statusField;
	}
	
	public static Integer getSuccessStatusEnum(String tableName){
		Integer statusEnum = defaultSuccessStatusEnum;
		Map<String,Integer> map = statusEnumMap.get(tableName);
		if(map != null){
			Integer status = map.get("SUCCESS");
			if(status != null){
				statusEnum = status;
			}
		}
		return statusEnum;
	}
	
	public static Integer getFailStatusEnum(String tableName){
		Integer statusEnum = defaultFailStatusEnum;
		Map<String,Integer> map = statusEnumMap.get(tableName);
		if(map != null){
			Integer status = map.get("FAIL");
			if(status != null){
				statusEnum = status;
			}
		}
		return statusEnum;
	}
}
