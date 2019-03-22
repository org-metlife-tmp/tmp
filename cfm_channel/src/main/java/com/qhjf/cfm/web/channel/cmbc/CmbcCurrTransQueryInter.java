package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;
import com.qhjf.cfm.web.constant.WebConstant;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CmbcCurrTransQueryInter  implements IMoreResultChannelInter{

	private static CMBCTestConfigSection configSection = CMBCTestConfigSection.getInstance();

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Date date = new Date();
		int preDay = configSection.getPreDay();
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> details = new HashMap<String,Object>();
        details.put("BBKNBR", record.getStr("bank_cnaps_code").substring(3, 7));
        details.put("ACCNBR", record.getStr("acc_no"));
        String preDate = DateUtil.getSpecifiedDayBefore(date, preDay, "yyyyMMdd");
        details.put("BGNDAT", preDate);
        details.put("ENDDAT", preDate);

        map.put("SDKTSINFX", details);
        return map;
	}

	@Override
	public Record parseResult(String jsonStr,int index) throws Exception {
		int preDay = configSection.getPreDay();
		Record record = new Record();
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONArray jsonArray = json.getJSONArray("NTQTSINFZ");
		JSONObject detail = jsonArray.getJSONObject(index);
		int direction = parseDirection(detail.getString("AMTCDR"));
		record.set("direction", direction);
		record.set("amount", parseAmount(direction,detail));
		record.set("opp_acc_no", detail.getString("RPYACC"));
		record.set("opp_acc_name", detail.getString("RPYNAM"));
		record.set("opp_acc_bank", detail.getString("RPYBNK"));
		record.set("summary", detail.getString("NARYUR"));
		String afterDate = DateUtil.getSpecifiedDayAfter(detail.getString("ETYDAT"), preDay, "yyyyMMdd");
		record.set("trans_date", afterDate);
		record.set("trans_time", DateUtil.formatDate(detail.getString("ETYTIM"),"hhmmss"));
		record.set("identifier", detail.getString("YURREF"));

		//获取指令码，在BUSNAR中
		record.set("instruct_code",detail.get("BUSNAR"));
		return record;

	}
	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if("0".equals(resultCode)){
			JSONArray jsonArray = json.getJSONArray("NTQTSINFZ");
			if(jsonArray == null){
				return 0;
			}
			return jsonArray.size();
		}else{
			throw new Exception("银行处理失败,失败原因:"+infoJson.getString("ERRMSG"));
		}
	}
	
	private int parseDirection(String str) throws Exception{
		if("D".equals(str)){
			return WebConstant.PayOrRecv.PAYMENT.getKey();
		}else if("C".equals(str)){
			return WebConstant.PayOrRecv.RECEIPT.getKey();
		}else{
			throw new Exception();
		}
	}
	
	private BigDecimal parseAmount(int direction,JSONObject json)throws Exception{
		if(direction == WebConstant.PayOrRecv.PAYMENT.getKey()){
			return json.getBigDecimal("TRSAMTD");
		}else if(direction == WebConstant.PayOrRecv.RECEIPT.getKey()){
			return json.getBigDecimal("TRSAMTC");
		}else{
			throw new Exception();
		}
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.GetTransInfo;
	}

	
}
