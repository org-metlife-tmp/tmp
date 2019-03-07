package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmbcBatchPayInter implements IChannelBatchInter {
	
	private static final Logger log = LoggerFactory.getLogger(CmbcBatchPayInter.class);
	public static final String ERROR_MSG = "批量支付回写，银行处理失败,失败原因:%s-%s";
	private JSONArray rsArray;

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> totMap = new HashMap<>();
        List<Map<String,Object>> details = new ArrayList<Map<String,Object>>();

        totMap.put("BUSCOD", "N02031");
        
        Record total = (Record)record.get("total");
        @SuppressWarnings("unchecked")
		List<Record> detail = (List<Record>)record.get("detail");
        if (detail != null && detail.size() > 0) {
        	log.debug("CMB批量支付：条数=【{}】；list=【{}】", detail.size(), detail);
        	for (Record r : detail) {
        		Map<String,Object> detailMap = new HashMap<String,Object>();
            	detailMap.put("YURREF", r.getStr("detail_bank_service_number"));
                detailMap.put("DBTACC", total.getStr("pay_account_no"));
                detailMap.put("DBTBBK", total.getStr("pay_bank_cnaps").substring(3,7));
                detailMap.put("TRSAMT", r.getStr("amount"));
                detailMap.put("CCYNBR", total.getStr("pay_account_cur"));
                detailMap.put("STLCHN", "N");
                //摘要长度：Z（62） 
                //String summary = r.getStr("instruct_code")+(CommKit.isNullOrEmpty(r.get("summary"))? "cfm" : r.getStr("summary"));
                detailMap.put("NUSAGE", "批量支付");
                detailMap.put("CRTACC", r.getStr("recv_account_no"));
                detailMap.put("CRTNAM", r.getStr("recv_account_name"));
                detailMap.put("BNKFLG", r.getStr("is_cross_bank"));
                detailMap.put("CRTBNK", r.getStr("recv_account_bank"));
                detailMap.put("CRTADR", r.getStr("recv_bank_prov")+"省"+r.getStr("recv_bank_city")+"市");

                details.add(detailMap);
			}
		}

        map.put("SDKPAYRQX", totMap);
        map.put("DCOPDPAYX", details);
        return map;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.DCPAYMNT;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		
		if(!"0".equals(resultCode)){
			throw new Exception(String.format(ERROR_MSG, resultCode, infoJson.getString("ERRMSG")));
		}
		
		JSONArray jsonArray = json.getJSONArray("NTQPAYRQZ");
		if(jsonArray == null || jsonArray.size() == 0){
			throw new Exception("批量支付回写，银行返回数据明细条数为0或null");
		}
		
		this.rsArray = jsonArray;
		return jsonArray.size();
	}
	/**
	 * 指令包序列号：无
	 * 指令包-子指令顺序号：无
	 * 指令包-子指令序列号：YURREF
	 */
	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record record = new Record();
		
		JSONObject jo = this.rsArray.getJSONObject(index);
		
		record.set("detail_bank_service_number", jo.getString("YURREF"));
		
		String isFinish = jo.getString("REQSTS");
		if(!"FIN".equals(isFinish)){
			record.set("status", 3);
			return record;
		}
		
		String result = jo.getString("RTNFLG");
		if("S".equals(result)){
			record.set("status", 1);
			return record;
		}else{
			record.set("status", 2);
			record.set("message", jo.getString("ERRTXT"));
			record.set("code", jo.getString("ERRCOD"));
			return record;
		}
	}

	@Override
	public int getBatchSize() {
		return 29;
	}

}
