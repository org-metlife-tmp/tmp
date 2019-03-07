package com.qhjf.cfm.web.channel.icbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import com.qhjf.cfm.web.channel.util.AmountUtil;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *当日余额查询
 */
public class IcbcCurrBalBatchQueryInter implements IChannelBatchInter{
	private static final Logger log = LoggerFactory.getLogger(IcbcCurrBalBatchQueryInter.class);
	//批量接口限制发送银行账号个数
	public int batchSize = 10;
	/**
	 * 工行支持同时查询10个账号查询同步返回结果
	 */
	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> result = new HashMap<>();
		AtomicInterfaceConfig inter = getInter();
		
		Map<String,Object> pub = new HashMap<>();
		pub.put("CIS", inter.getChannelConfig("CIS"));//集团CIS号
		pub.put("fSeqno", inter.getChannelInfo().getSerialnoGenTool().next());//指令包序列号
		
		List<Map<String, Object>> rds = new ArrayList<Map<String, Object>>();
		
		@SuppressWarnings("unchecked")
		List<Record> recordList = (List<Record>)record.get("list");
		if (null == recordList || recordList.size() == 0) {
			log.error("当日余额批量查询时，账号为空");
			return result;
		}
		int index = 1;
		for (Record r : recordList) {
			Map<String,Object> rd = new HashMap<>();
			rd.put("iSeqno", String.valueOf(index++));//指令顺序号
			rd.put("AccNo", r.getStr("acc_no"));
			rds.add(rd);
		}
        
		result.put("TotalNum", String.valueOf(recordList.size()));//总笔数:需要查询的账号的个数，即提交包明细的笔数。
		result.put("SynFlag", "1");//是否同步返回标志:0否；1是,如果上送值为1,则只允许上送不超过10个账号。不送标签默认为0。
		result.put("pub", pub);
		result.put("rd", rds);
        return result;
	}

	@Override
	public int getResultCount(String json) throws Exception {
		JSONObject out = IcbcResultParseUtil.parseResult(json);
		JSONArray resp = out.getJSONArray("rd");
		if(resp == null){
			return 0;
		}
		return resp.size();
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record record = new Record();

		JSONObject out = IcbcResultParseUtil.parseResult(json);
		JSONArray rdArray = out.getJSONArray("rd");
		JSONObject rd = rdArray.getJSONObject(index);

		BigDecimal balance = AmountUtil.icbcAmountHandle(rd.getBigDecimal("Balance"));
		BigDecimal usableBalance = AmountUtil.icbcAmountHandle(rd.getBigDecimal("UsableBalance"));
		BigDecimal frzAmt = AmountUtil.icbcAmountHandle(rd.getBigDecimal("FrzAmt"));
		
		record.set("bal", balance);
		record.set("available_bal", usableBalance);
		record.set("frz_amt", frzAmt);
		record.set("acc_no", rd.getString("AccNo"));
		return record;
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.QACCBAL;
	}

	@Override
	public int getBatchSize() {
		return 10;
	}
}
