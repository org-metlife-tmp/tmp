package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmbcHisBalQueryInter  implements ISingleResultChannelInter{

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> map = new HashMap<>();
        List<Map<String,Object>> details = new ArrayList<Map<String,Object>>();
        Map<String,Object> detailMap1 = new HashMap<String,Object>();
        detailMap1.put("BBKNBR", record.getStr("bank_cnaps_code").substring(3, 7));
        detailMap1.put("ACCNBR", record.getStr("acc_no"));
        details.add(detailMap1);
        map.put("SDKACINFX", details);
        return map;
	}

	@Override
	public Record parseResult(String jsonStr) throws Exception {
		Record record = new Record();
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if("0".equals(resultCode)){
			JSONObject detailJson = (JSONObject) json.getJSONArray("NTQACINFZ").get(0);
			record.set("bal", detailJson.get("ACCBLV"));
			record.set("available_bal", detailJson.get("ACCBLV"));
			return record;
		}else{
			throw new Exception("银行处理失败,失败原因:"+infoJson.getString("ERRMSG"));
		}
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.GetAccInfo;
	}
}
