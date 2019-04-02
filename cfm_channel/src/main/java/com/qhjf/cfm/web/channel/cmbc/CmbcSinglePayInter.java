package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.util.ChannelStringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmbcSinglePayInter  implements ISingleResultChannelInter{

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> totMap = new HashMap<>();
        List<Map<String,Object>> details = new ArrayList<Map<String,Object>>();
        Map<String,Object> detailMap1 = new HashMap<String,Object>();

        totMap.put("BUSCOD", "N02031");

        detailMap1.put("YURREF", record.getStr("bank_serial_number"));
        detailMap1.put("DBTACC", record.getStr("pay_account_no"));
        detailMap1.put("DBTBBK", record.getStr("pay_bank_cnaps").substring(3,7));
        detailMap1.put("TRSAMT", record.getStr("payment_amount"));
        detailMap1.put("CCYNBR", record.getStr("pay_account_cur"));
        detailMap1.put("STLCHN", "N");

        //摘要处理
		String summary = CommKit.isNullOrEmpty(record.getStr("summary")) ? "cfm" : record.getStr("summary");
		summary = ChannelStringUtil.getCmbFixLenStr(StringKit.filterSpecialChar(summary),62);
		detailMap1.put("NUSAGE",summary);
		//指令码放到招行接口接口的BUSNAR字段，做自动核对用，同交易信息中的BUSNAR比对
		detailMap1.put("BUSNAR", record.getStr("instruct_code"));

        detailMap1.put("CRTACC", record.getStr("recv_account_no"));
        detailMap1.put("CRTNAM", record.getStr("recv_account_name"));
        detailMap1.put("BNKFLG", record.getStr("is_cross_bank"));
        detailMap1.put("CRTBNK", record.getStr("recv_account_bank"));
        detailMap1.put("CRTADR", record.getStr("recv_bank_prov")+"省"+record.getStr("recv_bank_city")+"市");

        details.add(detailMap1);
        map.put("SDKPAYRQX", totMap);
        map.put("DCOPDPAYX", details);
        return map;
	}

	@Override
	public Record parseResult(String jsonStr) {
		Record record = new Record();
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if(!"0".equals(resultCode)){
			record.set("status", 2);
			record.set("message", infoJson.getString("ERRMSG"));
			return record;
		}
		JSONArray jsonArray = json.getJSONArray("NTQPAYRQZ");
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
		if("S".equals(result) || "B".equals(result)){
			record.set("status", 1);
			return record;
		}else{
			record.set("status", 2);
			record.set("message", detail.getString("ERRTXT"));
			return record;
		}
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.DCPAYMNT;
	}
}
