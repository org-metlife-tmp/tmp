package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.channel.util.ErrMsgTransfor;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 批量交易状态查询
 * @author CHT
 *
 */
public class CmbcTradeResultBatchQueryInter implements IMoreResultChannelInter {
	private static final Logger log = LoggerFactory.getLogger(CmbcTradeResultBatchQueryInter.class);
	private static CMBCTestConfigSection configSection = CMBCTestConfigSection.getInstance();

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		int preDay = configSection.getPreDay();
		log.debug("招行读取配置文件preDay={}", preDay);
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> details = new HashMap<String,Object>();

        details.put("BUSCOD", "N02031");
        details.put("YURREF", record.getStr("detail_bank_service_number"));
        
        String BGNDAT = DateUtil.getSpecifiedDayBefore(record.getDate("trade_date"), preDay+2, "yyyyMMdd");
        String ENDDAT = DateUtil.getSpecifiedDayBefore(record.getDate("trade_date"), preDay-2, "yyyyMMdd");
        details.put("BGNDAT", BGNDAT);
        details.put("ENDDAT", ENDDAT);

        map.put("NTQRYSTYX1", details);
        return map;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.NTQRYSTY;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		parseResultToJSONArray(jsonStr);
		//招行只能单条查询，兼容批量查询的渠道
		return 1;
	}

	@Override
	public Record parseResult(String jsonStr, int index) throws Exception {
		JSONArray jsonArray = parseResultToJSONArray(jsonStr);
		
		Record record = new Record();
		JSONObject detail = jsonArray.getJSONObject(0);
		record.set("detail_bank_service_number", detail.getString("YURREF"));
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
			record.set("code", result);
			record.set("message", ErrMsgTransfor.getCmbcErrMsg(result));
			return record;
		}
	}
	
	private JSONArray parseResultToJSONArray(String jsonStr) throws Exception{
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if(!"0".equals(resultCode)){
			throw new Exception(infoJson.getString("ERRMSG"));
		}
		
		JSONArray jsonArray = json.getJSONArray("NTSTLLSTZ");
		if(jsonArray == null || jsonArray.size() == 0){
			throw new Exception("招行批付结果查询，返回结果条数为0！");
		}
		
		return jsonArray;
	}
}
