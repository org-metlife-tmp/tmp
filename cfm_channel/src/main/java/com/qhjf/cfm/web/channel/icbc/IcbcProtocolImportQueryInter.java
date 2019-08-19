package com.qhjf.cfm.web.channel.icbc;

import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;

/**
 * 批收协议导入查询指令
 * 
 * @author CHT
 *
 */
public class IcbcProtocolImportQueryInter implements IMoreResultChannelInter {
	private JSONArray rdArray;
	private String qryfSeqno;

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
		result.put("QryfSeqno", record.getStr("bank_seriral_no"));
		return result;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.QENDIIMPT;
	}

	@Override
	public int getResultCount(String respStr) throws Exception {
		JSONObject out0 = IcbcResultParseUtil.parseResult(respStr);
		this.qryfSeqno = out0.getString("QryfSeqno");
		JSONArray rdArray = out0.getJSONArray("rd");
		this.rdArray = rdArray;
		return rdArray.size();
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record result = new Record();
		JSONObject rd = this.rdArray.getJSONObject(index);
		result.set("bank_seriral_no", this.qryfSeqno);
		result.set("package_seq", rd.getInteger("iSeqno"));
		result.set("dead_line", rd.getString("DeadLine"));
		result.set("bank_err_code", rd.getString("iRetCode"));
		result.set("bank_err_msg", rd.getString("iRetMsg"));
		// 0 提交成功,等待银行处理1 授权成功, 等待银行处理2 等待授权3 等待二次授权4 等待内管抽查5 主机返回待处理6
		// 被银行拒绝,拒绝原因写入备注栏7 处理成功8 指令被拒绝授权9
		// 银行正在处理10预约11预约取消;20：待内管柜员上传抽查文件;21：内管抽查审核同意;22：内管抽查审核拒绝;
		String status = rd.getString("Result");
		if ("7".equals(status)) {
			result.set("status", "1");
		} else if ("6".equals(status) || "8".equals(status) || "22".equals(status)) {
			result.set("status", "2");
		} else {
			result.set("status", "3");
		}

		return result;
	}

}
