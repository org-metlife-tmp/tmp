package com.qhjf.cfm.web.channel.cmbc;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;

public class CmbcTradeResultQueryInter  implements ISingleResultChannelInter{

	private static CMBCTestConfigSection configSection = CMBCTestConfigSection.getInstance();

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		int preDay = configSection.getPreDay();
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> details = new HashMap<String,Object>();

        details.put("BUSCOD", "N02031");
        details.put("YURREF", record.getStr("bank_serial_number"));
        
        String BGNDAT = DateUtil.getSpecifiedDayBefore(record.getDate("trade_date"), preDay+2, "yyyyMMdd");
        String ENDDAT = DateUtil.getSpecifiedDayBefore(record.getDate("trade_date"), preDay-2, "yyyyMMdd");
        details.put("BGNDAT", BGNDAT);
        details.put("ENDDAT", ENDDAT);

        map.put("NTQRYSTYX1", details);
        return map;
	}

	@Override
	public Record parseResult(String jsonStr) {
		Record record = new Record();
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if(!"0".equals(resultCode)){
			record.set("status", 3);
			record.set("message", infoJson.getString("ERRMSG"));
			return record;
		}
		JSONArray jsonArray = json.getJSONArray("NTSTLLSTZ");
		if(jsonArray == null || jsonArray.size() == 0){
			record.set("status", 3);
			return record;
		}
		JSONObject detail = jsonArray.getJSONObject(0);
		String isFinish = detail.getString("REQSTS");
		if(!"FIN".equals(isFinish)){
			record.set("status", 3);
			return record;
		}
		String result = detail.getString("RTNFLG");
		if("S".equals(result)){
			record.set("status", 1);
			return record;
		}else{
			record.set("status", 2);
			record.set("message", detail.getString("RTNNAR"));
			return record;
		}
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.NTQRYSTY;
	}
}
