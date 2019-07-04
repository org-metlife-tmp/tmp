package com.qhjf.cfm.web.channel.icbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;

public class IcbcTradeResultBatchRecvQueryInter implements IMoreResultChannelInter {
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
		Map<String, Object> result = new HashMap<>();

		AtomicInterfaceConfig inter = getInter();
		Map<String, Object> pub = new HashMap<>();

		pub.put("CIS", inter.getChannelConfig("CIS"));

		// 指令包序列号
		String serianlNo = RedisSericalnoGenTool.genBankSeqNo();
		if (serianlNo == null) {
			return result;
		}
		pub.put("fSeqno", serianlNo);

		result.put("pub", pub);
		// 根据指令序列号查询
		result.put("QryfSeqno", record.getStr("bank_serial_number"));

		Map<String, Object> rd = new HashMap<>();
		rd.put("iSeqno", "");
		List<Map<String, Object>> rds = new ArrayList<Map<String, Object>>();
		rds.add(rd);
		result.put("rd", rds);
		return result;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.QPERDIS;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject out = IcbcResultParseUtil.parseResult(jsonStr);

		this.qryfSeqno = out.getString("QryfSeqno");
		JSONArray rdArray = out.getJSONArray("rd");
		this.rdArray = rdArray;
		if (rdArray == null) {
			return 0;
		}

		return rdArray.size();
	}

	@Override
	public Record parseResult(String jsonStr, int index) throws Exception {
		JSONObject rd = this.rdArray.getJSONObject(index);
		Record record = new Record();
		record.set("package_seq", rd.getString("iSeqno"));
		record.set("bank_err_code", rd.getString("iRetCode"));
		record.set("bank_err_msg", rd.getString("iRetMsg"));
		
		String result = rd.getString("Result");
		if ("7".equals(result)) {
			record.set("status", 1);
		} else if("6".equals(result) || "8".equals(result)){
			record.set("status", 2);
		} else {//0，1，2， 3， 4， 5， 9， 10， 11， 15， 16
			record.set("status", 3);
		}
		
		return record;
	}

}
