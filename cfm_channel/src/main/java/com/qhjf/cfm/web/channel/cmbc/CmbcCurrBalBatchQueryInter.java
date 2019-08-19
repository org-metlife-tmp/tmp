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

public class CmbcCurrBalBatchQueryInter implements IChannelBatchInter{
	
	private static final Logger log = LoggerFactory.getLogger(CmbcCurrBalBatchQueryInter.class);

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> map = new HashMap<>();
        List<Map<String,Object>> details = new ArrayList<Map<String,Object>>();
        List<Record> recordList = (List<Record>)record.get("list");
		if (null == recordList || recordList.size() == 0) {
			log.error("招行当日余额批量查询时，账号为空");
			return map;
		}
		for(Record detail : recordList){
			Map<String,Object> detailMap = new HashMap<String,Object>();
	        detailMap.put("BBKNBR", detail.getStr("bank_cnaps_code").substring(3, 7));
	        detailMap.put("ACCNBR", detail.getStr("acc_no"));
	        details.add(detailMap);
		}
        
        map.put("SDKACINFX", details);
        return map;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.GetAccInfo;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if("0".equals(resultCode)){
			JSONArray jsonArray = json.getJSONArray("NTQACINFZ");
			if(jsonArray == null){
				return 0;
			}
			return jsonArray.size();
		}else{
			throw new Exception("银行处理失败,失败原因:"+infoJson.getString("ERRMSG"));
		}
	}

	@Override
	public Record parseResult(String jsonStr, int index) throws Exception {
		Record record = new Record();
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONArray jsonArray = json.getJSONArray("NTQACINFZ");
		JSONObject detail = jsonArray.getJSONObject(index);
		record.set("bal", detail.get("AVLBLV"));
		record.set("available_bal", detail.get("AVLBLV"));
		record.set("frz_amt", detail.get("HLDBLV"));
		record.set("acc_no", detail.getString("ACCNBR"));
		return record;
	}

	@Override
	public int getBatchSize() {
		return 0;
	}

}
