package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: cht
 * @Date: 2019/2/26
 * @Description: 招行批量收付查询
 */
public class CmbcTradeResultBatchPayQueryInter implements IMoreResultChannelInter {
	private static final Logger log = LoggerFactory.getLogger(CmbcTradeResultBatchPayQueryInter.class);

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> SDKATDQYX = new HashMap<String,Object>();
        SDKATDQYX.put("REQNBR", record.get("reqnbr"));
        map.put("SDKATDQYX", SDKATDQYX);
        return map;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.GetAgentDetail;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONArray ja = parseResultToJSONArray(jsonStr);
		return ja.size();
	}

	@Override
	public Record parseResult(String jsonStr, int index) throws Exception {
		JSONArray jsonArray = parseResultToJSONArray(jsonStr);
		
		Record record = new Record();
		JSONObject detail = jsonArray.getJSONObject(index);
		
		// S:成功；F:失败；C：撤销；I:数据录入
		String sucOrFail = detail.getString("STSCOD");
		if ("I".equalsIgnoreCase(sucOrFail)) {
			record.set("status", 3);
			return record;
		}

		record.set("amount", detail.getString("TRSAMT"));
		record.set("pay_account_name", detail.getString("CLTNAM"));
		record.set("bank_err_msg", detail.getString("ERRDSP"));
		record.set("pay_account_no", detail.getString("ACCNBR"));
		record.set("zs", detail.getString("TRSDSP"));
		if("S".equalsIgnoreCase(sucOrFail)){
			record.set("status", 1);
			return record;
		}
		if("F".equalsIgnoreCase(sucOrFail) || "C".equalsIgnoreCase(sucOrFail)){
			record.set("bank_err_code", detail.getString("ERRCOD"));
			record.set("status", 2);
			return record;
		}
		throw new Exception("【合法状态 = S:成功；F:失败；C：撤销；I:数据录入】银行状态不合法，status=" + sucOrFail);
	}
	
	private JSONArray parseResultToJSONArray(String jsonStr) throws Exception{
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if(!"0".equals(resultCode)){
			throw new Exception(infoJson.getString("ERRMSG"));
		}
		
		JSONArray jsonArray = json.getJSONArray("NTQATDQYZ");
		if(jsonArray == null || jsonArray.size() == 0){
			throw new Exception("招行批收结果查询，返回结果条数为0！");
		}
		
		return jsonArray;
	}
}
