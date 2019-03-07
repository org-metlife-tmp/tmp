package com.qhjf.cfm.web.inter.manager;

import com.qhjf.cfm.web.constant.WebConstant;

import java.util.HashMap;
import java.util.Map;

public class SysInterManager {
	/**
	 * 缓存表对应的状态fild
	 */
	private static Map<String,String> statusFiledMap = new HashMap<String,String>();
	/**
	 * 缓存表状态fild对应的 成功/失败值
	 */
	private static Map<String,Map<String,Integer>> statusEnumMap = new HashMap<String,Map<String,Integer>>();
	/**
	 * 汇总表 、明细表映射  
	 */
	private static Map<String, String> totalMapDetailTableName = new HashMap<String, String>();
	
	
	private static String defaultStatusField = "service_status";
	private static Integer defaultSuccessStatusEnum = WebConstant.BillStatus.SUCCESS.getKey();
	private static Integer defaultFailStatusEnum = WebConstant.BillStatus.FAILED.getKey();
	
	static{
		statusFiledMap.put("collect_execute_instruction", "collect_status");
		statusFiledMap.put("inner_batchpay_bus_attach_detail","pay_status");
		statusFiledMap.put("outer_batchpay_bus_attach_detail","pay_status");
		statusFiledMap.put("pay_batch_detail","status");
		statusFiledMap.put("la_origin_pay_data","la_callback_status");
		statusFiledMap.put("ebs_origin_pay_data","ebs_callback_status");
		statusFiledMap.put("batch_pay_instr_queue_total","status");
		statusFiledMap.put("batch_pay_instr_queue_detail","status");
		statusFiledMap.put("batch_recv_instr_queue_total","status");
		statusFiledMap.put("batch_recv_instr_queue_detail","status");
		
		
		Map<String,Integer> collectStatusEnums = new HashMap<String,Integer>();
		collectStatusEnums.put("SUCCESS", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());
		collectStatusEnums.put("FAIL", WebConstant.CollOrPoolRunStatus.FAILED.getKey());
		
		Map<String,Integer> batchPayEnums  = new HashMap<String,Integer>();
		batchPayEnums.put("SUCCESS", WebConstant.PayStatus.SUCCESS.getKey());
		batchPayEnums.put("FAIL", WebConstant.PayStatus.FAILD.getKey());
		
		Map<String,Integer> ddhBatchPayEnums  = new HashMap<String,Integer>();
		ddhBatchPayEnums.put("SUCCESS", 2);
		ddhBatchPayEnums.put("FAIL", 3);
		
		statusEnumMap.put("collect_execute_instruction", collectStatusEnums);
		statusEnumMap.put("inner_batchpay_bus_attach_detail",batchPayEnums);
		statusEnumMap.put("outer_batchpay_bus_attach_detail",batchPayEnums);
		statusEnumMap.put("oa_branch_payment_item",batchPayEnums);
		statusEnumMap.put("pay_batch_detail", batchPayEnums);
		statusEnumMap.put("la_origin_pay_data",ddhBatchPayEnums);
		statusEnumMap.put("ebs_origin_pay_data",ddhBatchPayEnums);
		statusEnumMap.put("batch_pay_instr_queue_total",batchPayEnums);
		statusEnumMap.put("batch_pay_instr_queue_detail",batchPayEnums);
		statusEnumMap.put("batch_recv_instr_queue_total",batchPayEnums);
		statusEnumMap.put("batch_recv_instr_queue_detail",batchPayEnums);
		
		totalMapDetailTableName.put("pay_batch_total", "pay_batch_detail");
		totalMapDetailTableName.put("recv_batch_total", "recv_batch_detail");
	}

	/**
	 * 获取传入表的主键fild
	 * @param tableName
	 * @return
	 */
	public static String getSourceRefPrimaryKey(String tableName){
		switch (tableName){
			case "inner_batchpay_bus_attach_detail":
			case "outer_batchpay_bus_attach_detail":
				return "detail_id";
			default:
				return "id";
		}
	}

	/**
	 * 获取传入表的状态fild
	 * @param tableName
	 * @return
	 */
	public static String getStatusFiled(String tableName){
		String statusField = statusFiledMap.get(tableName);
		if(statusField == null || statusField.length() ==0){
			statusField = defaultStatusField;
		}
		return statusField;
	}
	
	/**
	 * 获取传入表 状态fild的成功值
	 * @param tableName
	 * @return
	 */
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
	
	/**
	 * 获取传入表 状态fild的失败值
	 * @param tableName
	 * @return
	 */
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
	/**
	 * 获取传入表对应的明细表
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static String getDetailTableName(String tableName){
		String detail = totalMapDetailTableName.get(tableName);
		if (null == detail) {
			throw new RuntimeException(String.format("未 配置[%s]表对应的明细表！", tableName));
		}
		return detail;
	}
}
