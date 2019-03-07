package com.qhjf.cfm.web.channel.icbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IcbcTradeResultBatchQueryInter  implements IMoreResultChannelInter{
	private static final Logger log = LoggerFactory.getLogger(IcbcTradeResultBatchQueryInter.class);
	public static final String BATCH_ERROR_MSG = "批量支付结果查询，银行处理失败,失败原因:%s-%s";
	/**
	 * 批量指令包标识
	 */
	private String qryfSeqno;
	/**
	 * 银行返回结果列表
	 */
	private JSONArray rdArray;
	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> result = new HashMap<>();
		
		AtomicInterfaceConfig inter = getInter();
		Map<String, Object> pub = new HashMap<>();

		pub.put("CIS", inter.getChannelConfig("CIS"));
		pub.put("fSeqno", inter.getChannelInfo().getSerialnoGenTool().next());
		
		result.put("pub", pub);
		//根据指令序列号查询
		result.put("QryfSeqno", record.getStr("bank_serial_number"));
        return result;
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.QPAYENT;
	}

	@Override
	public int getResultCount(String json) throws Exception {
		return parseResultCount(json);
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		JSONObject rd = this.rdArray.getJSONObject(index);
		Record record = IcbcSinglePayInter.parseRsult(rd);
		record.set("bank_serial_number", this.qryfSeqno);
		record.set("package_seq", rd.getString("iSeqno"));
		if (!"0".equals(rd.getString("iRetCode"))) {
			record.set("code", rd.getString("iRetCode"));
		}
		return record;
	}
	
	private int parseResultCount(String jsonStr) throws Exception{
		JSONObject json = JSONObject.parseObject(jsonStr);
        JSONArray ebArray = json.getJSONArray("eb");
        JSONObject eb0 = ebArray.getJSONObject(0);
        JSONArray pubArray = eb0.getJSONArray("pub");
        JSONObject pub0 = pubArray.getJSONObject(0);
        String retCode = pub0.getString("RetCode");
        String retMsg = pub0.getString("RetMsg");
        
        if ("0".equals(retCode)) {
        	JSONArray outArray = eb0.getJSONArray("out");
        	JSONObject out0 = outArray.getJSONObject(0);
        	this.qryfSeqno = out0.getString("QryfSeqno");
        	JSONArray rdArray =  out0.getJSONArray("rd");
        	this.rdArray = rdArray;
        	return rdArray.size();
		}else {
			throw new Exception(String.format(BATCH_ERROR_MSG, retCode, retMsg));
		}
	}
}
