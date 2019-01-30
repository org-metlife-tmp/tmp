package com.qhjf.cfm.web.channel.icbc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcChannel;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.bankinterface.icbc.convertor.IcbcCurrTypeConvertor;
import com.qhjf.cfm.utils.ElectronicTemplateTool;
import com.qhjf.cfm.utils.UUIDUtil;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.AmountUtil;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;
import com.qhjf.cfm.web.config.ICBCTestConfigSection;
import com.qhjf.cfm.web.constant.BasicTypeConstant;

public class IcbcHisElectronicQueryInter implements IMoreResultChannelInter{
	
	private static ICBCTestConfigSection configSection = ICBCTestConfigSection.getInstance();
	private JSONArray resultList;//银行返回的数据列表

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String, Object> result = new HashMap<>();
		
		AtomicInterfaceConfig inter = getInter();
		Map<String, Object> pub = new HashMap<>();
		List<Map<String, Object>> rdList = new ArrayList<Map<String, Object>>();
		Map<String, Object> rd = new HashMap<String, Object>();
		
		pub.put("CIS", inter.getChannelConfig("CIS"));
		pub.put("fSeqno", inter.getChannelInfo().getSerialnoGenTool().next());
		
		rd.put("iSeqno", "1");//指令顺序号
		String accNo = record.getStr("acc_no");
		String areaCode = accNo.substring(0,4);//1-4位地区号
		String netCode = accNo.substring(4, 8);//5-8位网点号
		String accSeq = accNo.substring(11, 17);//12-17位账户序号
		rd.put("AreaCode", areaCode);
		rd.put("NetCode", netCode);
		Date date = new Date();
		int preDay = configSection.getPreDay();
        String preDate = DateUtil.getSpecifiedDayBefore(date, preDay + 1, "yyyyMMdd");
        rd.put("TranDate", preDate);
        rd.put("AccNo", accNo);
        rd.put("TranSerial", accSeq);
        rdList.add(rd);

        result.put("pub", pub);
        result.put("TotalNum", "1");
//		result.put("SignTime", DateUtil.getSpecifiedDayBefore(date, configSection.getPreDay(), "yyyyMMddHHmmssSSS"));
		result.put("rd", rdList);
        return result;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.BATEBILL;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject out = IcbcResultParseUtil.parseResult(jsonStr);
		JSONArray rdList = out.getJSONArray("rd");
		if(rdList == null){
			return 0;
		}
		this.resultList = rdList;
		return this.resultList.size();
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record record = new Record();
		JSONObject rd = this.resultList.getJSONObject(index);

		String channelCode = IcbcChannel.getInstance().getCode();
		record.set("channel_code", channelCode);
		record.set("eb_type", BasicTypeConstant.ICBC_EBILL_TYPE);
		record.set("amount", AmountUtil.icbcAmountHandle(rd.getBigDecimal("PayAmt")));
		String timeStamp = rd.getString("TimeStamp");
		if (null != timeStamp) {
			timeStamp = timeStamp.length() >= 10 ? timeStamp.substring(0, 10) : timeStamp;
		}
		record.set("eb_date", timeStamp);
		
		String uuid = UUIDUtil.getUUID(channelCode + "@" + BasicTypeConstant.ICBC_EBILL_TYPE);
		Map<String, Map<String, String>> map = ElectronicTemplateTool.ELECTRONIC_TEMPLATE_MAP;
		Map<String,String> cmbcMap = map.get(uuid);
		
		if (null == cmbcMap) {
			throw new Exception(String.format("工行电子回单模板uuid=[%s]不存在！", uuid));
		}
		
		for (Entry<String, Object> entry : rd.entrySet()) {
			String key = entry.getKey();
			Object value = conv(key,entry.getValue() == null ? null : entry.getValue().toString());
			String field = cmbcMap.get(key);
			if(field == null || field.length() == 0){
				continue;
			}
			record.set(field, value);    
		}
		return record;
	}
	
	public Object conv(String field,String value){
		if (null == value) {
			return value;
		}
		if("CurrType".equals(field)){
			IcbcCurrTypeConvertor conv = new IcbcCurrTypeConvertor();
			try {
				List<String> list = conv.converDest2Source(value);
				return list.get(0);
			} catch (Exception e) {
				return value;
			}
		}else if("PayAmt".equals(field)){
			return AmountUtil.icbcAmountHandle(new BigDecimal(value.toString()));
		}else {
			return value;
		}
	}
}
