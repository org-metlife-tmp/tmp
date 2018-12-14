package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcChannel;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.bankinterface.cmbc.convertor.CmbcCurrencyConvetor;
import com.qhjf.cfm.utils.ElectronicTemplateTool;
import com.qhjf.cfm.utils.UUIDUtil;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CmbcHisElectronicQueryInter implements IMoreResultChannelInter{

	private static CMBCTestConfigSection configSection = CMBCTestConfigSection.getInstance();
	
	private JSONArray detailArray;

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Date date = new Date();
		int preDay = configSection.getPreDay();
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> details = new HashMap<String,Object>();
        details.put("EACNBR", record.getStr("acc_no"));
        String preDate = DateUtil.getSpecifiedDayBefore(date, preDay+1, "yyyyMMdd");
        details.put("BEGDAT", preDate);
        details.put("ENDDAT", preDate);
        details.put("RRCFLG", "1");

        map.put("CSRRCFDFY0", details);
        return map;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.SDKCSFDFBRT;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if("0".equals(resultCode)){
			String channelCode = CmbcChannel.getInstance().getCode();
			this.detailArray = new JSONArray();
			for (Map.Entry<String, Object> entry : json.entrySet()) {
				String jsonKey = entry.getKey();
				if(jsonKey.equals("INFO") || jsonKey.equals("CSRRCFDFZ1")){
					continue;
				}
				
				JSONArray array = (JSONArray) entry.getValue();
				for(int i = 0 ; i<array.size();i++){
					JSONObject jsonDetail = (JSONObject) array.get(i);
					jsonDetail.put("chlType", jsonKey);
					jsonDetail.put("sysUUID", UUIDUtil.getUUID(channelCode+"@"+jsonKey));
					this.detailArray.add(jsonDetail);
					System.out.println(jsonDetail.toJSONString());
				}
	            
			}
			return this.detailArray.size();
		}else{
			throw new Exception("银行处理失败,失败原因:"+infoJson.getString("ERRMSG"));
		}
		

		
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record record = new Record();
		JSONObject detail = this.detailArray.getJSONObject(index);
		record.set("channel_code", CmbcChannel.getInstance().getCode());
		record.set("eb_type", detail.getString("chlType"));
		record.set("amount", detail.getString("TRSAMT"));
		record.set("eb_date", detail.getString("ISUDAT"));
		Map<String, Map<String, String>> map = ElectronicTemplateTool.getINSTANCE().ELECTRONIC_TEMPLATE_MAP;
		Map<String,String> cmbcMap = map.get(detail.getString("sysUUID"));
		
		for (Entry<String, Object> entry : detail.entrySet()) {  
			String key = entry.getKey();
			Object value = conv(key,entry.getValue().toString());
			String field = cmbcMap.get(key);
			if(field == null || field.length() == 0){
				continue;
			}
			record.set(field, value);    
		}
		return record;

	}
	
	public Object conv(String field,String value){
		if("CCYNBR".equals(field)){
			CmbcCurrencyConvetor conv = new CmbcCurrencyConvetor();
			try {
				List<String> list = conv.converDest2Source(value);
				return list.get(0);
			} catch (Exception e) {
				return null;
			}
		}else{
			return value;
		}
	}
}
