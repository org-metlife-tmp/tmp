package com.qhjf.cfm.web.channel.icbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qhjf.bankinterface.api.utils.OminiUtils;
import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;

public class IcbcBatchRecvInter implements IChannelBatchInter {
	private static final Logger log = LoggerFactory.getLogger(IcbcBatchRecvInter.class);
    public static final String ERROR_MSG = "批量收款，银行受理失败,失败原因:{}-{}";

	private static DDHLARecvConfigSection section = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);

	private JSONObject pub0;

	private String iSeqno;

	@Override
	public Map<String, Object> genParamsMap(Record record) {
    	
		Map<String, Object> result = new HashMap<>(1<<4);

		AtomicInterfaceConfig inter = getInter();
		Map<String, Object> pub = new HashMap<>(1<<3);
		List<Map<String, Object>> rdList = new ArrayList<Map<String, Object>>();

		Record total = (Record)record.get("total");
		if (null == total) {
			return null;
		}
		pub.put("CIS", inter.getChannelConfig("CIS"));
		pub.put("fSeqno", total.getStr("bank_serial_number"));
		
		@SuppressWarnings("unchecked")
		List<Record> detail = (List<Record>)record.get("detail");
		
		if (detail != null && detail.size() > 0) {
			for (Record r : detail) {
				Map<String, Object> rd = new HashMap<String, Object>(1<<6);
				// 指令顺序号
				rd.put("iSeqno", r.getStr("package_seq"));
				this.iSeqno = r.getStr("package_seq");
				rd.put("PayAccNo", r.getStr("pay_account_no"));
				rd.put("PayAccNameCN", r.getStr("pay_account_name"));
				rd.put("PayBranch", r.getStr("pay_account_bank"));
				rd.put("Portno", r.getStr("insure_bill_no"));//缴费编号
				rd.put("ContractNo", section.getProtocolNo());//协议编号
				rd.put("CurrType", "CNY");// 币种total.getStr("pay_account_cur")
				rd.put("PayAmt", r.getStr("amount"));// 金额
				rd.put("Summary", "批量收款");
				rdList.add(rd);
			}
		} else {
			return null;
		}

		// 银行支持1-99用于当天汇总记账，该字段相同的记为一笔；
		//实时收BusType取1
		//批量收BusType：取值范围[2,99)左闭右开，同一天的 从2开始递增，发一笔递增1，直到99停止，第二天又从2开始（一天只能组批97次）
		result.put("BusType", total.get("bus_type"));
		result.put("RecAccNo", total.get("recv_account_no"));//收方帐号
		result.put("RecAccNameCN", total.get("recv_account_name"));//收方账户名称
		result.put("TotalNum", String.valueOf(detail.size()));
		result.put("TotalAmt", total.getStr("total_amount"));
		result.put("pub", pub);
		result.put("rd", rdList);
		return result;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.PERDISCOL;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject json = JSONObject.parseObject(jsonStr);
        JSONArray ebArray = json.getJSONArray("eb");
        JSONObject eb0 = ebArray.getJSONObject(0);
        JSONArray pubArray = eb0.getJSONArray("pub");
        JSONObject pub0 = pubArray.getJSONObject(0);
        String retCode = pub0.getString("RetCode");
        String retMsg = pub0.getString("RetMsg");
        
        if ("0".equals(retCode)) {
        	this.pub0 = pub0;
        	log.debug("工行成功受理批收指令！");
		}else {
			log.error(ERROR_MSG, retCode, retMsg);
		}
		return 1;
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		JSONObject jsonStr = JSONObject.parseObject(json);
		JSONArray ebArray = jsonStr.getJSONArray("eb");
		JSONObject eb0 = ebArray.getJSONObject(0);
		Record rs = new Record();
		if ("0".equals(this.pub0.getString("RetCode"))) {
			JSONArray out = eb0.getJSONArray("out");
			if(!OminiUtils.isNullOrEmpty(out)){
				JSONObject out0 = out.getJSONObject(0);
				JSONArray rd = out0.getJSONArray("rd");
				JSONObject rd0 = rd.getJSONObject(0);
				rs.set("iSeqno",rd0.get("iSeqno"));
				rs.set("bankErrMsg",rd0.get("iRetMsg"));
			} else {
				rs.set("iSeqno",this.iSeqno);
			}
			rs.set("status", "S");
			return rs;
		}
		return rs.set("status", "F")
				.set("bankErrMsg", this.pub0.getString("RetMsg"))
				.set("bankErrCode", this.pub0.getString("RetCode"));
	}

	/**
	 * 99-150
	 */
	@Override
	public int getBatchSize() {
		return 100;
	}

}
