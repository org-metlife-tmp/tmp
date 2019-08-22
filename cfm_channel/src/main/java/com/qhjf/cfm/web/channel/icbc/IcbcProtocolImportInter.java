package com.qhjf.cfm.web.channel.icbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;
/**
 * 批收协议导入指令
 * @author CHT
 *
 */
public class IcbcProtocolImportInter implements IMoreResultChannelInter {

	private JSONArray rdArray;
	
	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String, Object> result = new HashMap<>();

		AtomicInterfaceConfig inter = getInter();
		Map<String, Object> pub = new HashMap<>();
		List<Map<String, Object>> rdList = new ArrayList<Map<String, Object>>();

		Record total = (Record)record.get("total");
		if (null == total) {
			return null;
		}
		pub.put("CIS", inter.getChannelConfig("CIS"));
		pub.put("fSeqno", total.getStr("bank_seriral_no"));
		
		@SuppressWarnings("unchecked")
		List<Record> detail = (List<Record>)record.get("detail");
		
		if (detail != null && detail.size() > 0) {
			for (Record r : detail) {
				Map<String, Object> rd = new HashMap<String, Object>();
				// 指令顺序号
				rd.put("iSeqno", r.getStr("package_seq"));
				rd.put("PayNo", r.getStr("pay_no"));//缴费编号
				rd.put("PayAcctName", r.getStr("pay_acc_name"));
				rd.put("PayAccNo", r.getStr("pay_acc_no"));
				rd.put("CertType", r.getStr("cert_type"));
				rd.put("CertNo", r.getStr("cert_no"));
				rd.put("DeadLine", "99991230");
				rdList.add(rd);
			}
		} else {
			return null;
		}

		result.put("TotalNum", TypeUtils.castToString(total.get("total_num")));
		result.put("ContractNo", total.getStr("protocol_no"));//协议编号
		result.put("PayType", total.getStr("pay_type"));//缴费种类
		result.put("AcctName", total.getStr("enterprise_name"));//企业名称
		result.put("AcctNo", total.getStr("enterprise_acc_no"));//企业账号
		result.put("pub", pub);
		result.put("rd", rdList);
		return result;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.ENDIIMPT;
	}

	@Override
	public int getResultCount(String respStr) throws Exception {
        JSONObject eb0 = IcbcResultParseUtil.getEB(respStr);
        JSONArray outArray = IcbcResultParseUtil.getOUT(eb0);
        if (outArray == null) {//文件级返回
        	this.rdArray = null;//重发时，这个字段需要重置
			return 1;
		}else {//指令集返回
			JSONArray rdArray = outArray.getJSONObject(0).getJSONArray("rd");
			this.rdArray = rdArray;
			return rdArray.size();
		}
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record result = new Record();
		if (this.rdArray == null) {//文件级返回
			JSONObject pub0 = IcbcResultParseUtil.getPUB(json);
			String retCode = pub0.getString("RetCode");
			result.set("isFileReturn", "0");
			if ("0".equals(retCode)) {
				result.set("status", "1");
			}else {
				result.set("status", "2");
				result.set("bank_err_code", pub0.getString("RetCode"));
				result.set("bank_err_msg", result.getStr("RetMsg"));
			}
		}else {//指令级返回
			JSONObject rd = this.rdArray.getJSONObject(index);
			result.set("isFileReturn", "0");//是否文件及返回：0-否；1-是
			result.set("package_seq", rd.getIntValue("iSeqno"));
			String rsCode = rd.getString("Result");
			if ("7".equals(rsCode)) {
				result.set("status", "1");
			}else {
				result.set("status", "3");
			}
			result.set("bank_err_code", rd.getString("iRetCode"));
			result.set("bank_err_msg", rd.getString("iRetMsg"));
		}
		return result;
	}

}
