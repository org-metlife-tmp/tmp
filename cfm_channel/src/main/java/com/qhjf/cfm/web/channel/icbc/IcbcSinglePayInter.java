package com.qhjf.cfm.web.channel.icbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.util.ChannelStringUtil;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;
import com.qhjf.cfm.web.config.ICBCTestConfigSection;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

/**
 * 单次支付
 */
public class IcbcSinglePayInter  implements ISingleResultChannelInter{

	private static final Logger log = LoggerFactory.getLogger(IcbcSinglePayInter.class);

	private static ICBCTestConfigSection configSection = ICBCTestConfigSection.getInstance();

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String, Object> result = new HashMap<>();
		
		AtomicInterfaceConfig inter = getInter();
		Map<String, Object> pub = new HashMap<>();
		List<Map<String, Object>> rdList = new ArrayList<Map<String, Object>>();
		Map<String, Object> rd = new HashMap<String, Object>();

		pub.put("CIS", inter.getChannelConfig("CIS"));
		pub.put("fSeqno", record.getStr("bank_serial_number"));

		rd.put("iSeqno", "1");//指令顺序号
		rd.put("PayAccNo", record.getStr("pay_account_no"));
		rd.put("PayAccNameCN", record.getStr("pay_account_name"));
		rd.put("RecAccNo", record.getStr("recv_account_no"));
		String recvAccName = record.getStr("recv_account_name");
		rd.put("RecAccNameCN", recvAccName);
		String SysIOFlg;
		String isCrossBank = record.getStr("is_cross_bank");//0同行1异行
		if ("0".equals(isCrossBank)) {
			SysIOFlg = "1";
		}else {
			SysIOFlg = "2";
			rd.put("RecCityName", record.get("recv_bank_city"));//收款方所在城市名称,跨行指令（非跨行快汇）此项必输
			rd.put("RecBankNo", record.get("recv_bank_cnaps"));//对方行行号
			rd.put("RecBankName", record.get("recv_account_bank"));//交易对方银行名称,跨行指令此项必输,中文，60位字符。
			//Prop对公对私标志:0：对公账户 1：个人账户
			if (null != recvAccName 
					&& recvAccName.length() >= 4 
					&& (recvAccName.indexOf("公司") != -1 || recvAccName.indexOf("机构") != -1)) {
				rd.put("Prop", "0");
			}else {
				rd.put("Prop", "1");
			}
		}
		rd.put("SysIOFlg", SysIOFlg);// 系统内外标志:1：系统内2：系统外
		rd.put("CurrType", record.getStr("pay_account_cur"));// 币种
		rd.put("PayAmt", record.getStr("payment_amount"));// 金额

		String summary = CommKit.isNullOrEmpty(record.getStr("summary")) ? "cfm" : record.getStr("summary");
		summary = StringKit.filterSpecialChar(summary);
		rd.put("Summary",ChannelStringUtil.getFixLenStr(summary,20)); //摘要，工行要求上送20字节
		rd.put("UseCN", ChannelStringUtil.getFixLenStr(summary,20)) ;//用途中文描述
		rd.put("PostScript", ChannelStringUtil.getFixLenStr(summary,60)) ;//用途中文描述


		//指令码放到工行接口的Ref字段，做自动核对用
		rd.put("Ref",record.getStr("instruct_code"));


		rdList.add(rd);

		result.put("SettleMode", "0");//2：并笔记账 0：逐笔记账
		result.put("TotalNum", "1");
		result.put("TotalAmt", record.getStr("payment_amount"));
//		result.put("SignTime", DateUtil.getSpecifiedDayBefore(new Date(), configSection.getPreDay(), "yyyyMMddHHmmssSSS"));
		result.put("pub", pub);
		result.put("rd", rdList);
		return result;
	}

	/**
	 * 该方法返回状态码：WebConstant.PayStatus == SUCCESS(1, "成功"), FAILD(2, "失败"), HANDLE(3, "处理中"), CANCEL(4, "已作废");
	 * 银行返回的状态码：
	 *  0：提交成功,等待银行处理
		1：授权成功, 等待银行处理
		2：等待授权
		3：等待二次授权
		4：等待银行答复
		5：主机返回待处理
		6：被银行拒绝
		7：处理成功
		8：指令被拒绝授权
		9：银行正在处理
		10：预约指令
		11：预约取消                               
		20:待提交人确认转账
		86：等待电话核实
		95:待核查
		98:区域中心通讯可疑
	 * @throws Exception 
	 */
	@Override
	public Record parseResult(String jsonStr) {
		
		JSONObject out0 = null;
		try {
			out0 = IcbcResultParseUtil.parseResult(jsonStr);
		} catch (Exception e) {
			String jl = StringKit.removeControlCharacter(jsonStr);
			log.info("单笔支付指令发送失败，需要人工核查！！！ bean is: {}",jl );
			JSONObject json = JSONObject.parseObject(jsonStr);
	        JSONArray ebArray = json.getJSONArray("eb");
	        JSONObject eb0 = ebArray.getJSONObject(0);
	        JSONArray pubArray = eb0.getJSONArray("pub");
	        JSONObject pub0 = pubArray.getJSONObject(0);
	        String retCode = pub0.getString("RetCode");
	        String retMsg = pub0.getString("RetMsg");
			/*<RetCode>B0012</RetCode><RetMsg>指令包序列号为空或重复InsertBatchInfo</RetMsg>*/
			if (retCode != null && "B0012".equals(retCode.trim())) {
				Record r = new Record();
				r.set("status", WebConstant.PayStatus.HANDLE.getKey());
				r.set("message", retMsg);
				return r;
			}else {
				Record r = new Record();
				r.set("status", WebConstant.PayStatus.FAILD.getKey());
				r.set("message", retMsg);
				return r;
			}
		}
		JSONArray rdArray = out0.getJSONArray("rd");
		JSONObject rd = rdArray.getJSONObject(0);
		
		return parseRsult(rd);
	}
	
	public static Record parseRsult(JSONObject rd){
		Record record = new Record();
		String result = rd.getString("Result");
		String iRetMsg = rd.getString("iRetMsg");
		
		if ("7".equals(result)) {

			record.set("status", 1);

		} else if ("0".equals(result) || "1".equals(result) || "2".equals(result) || "3".equals(result)
				|| "4".equals(result) || "5".equals(result) || "9".equals(result) || "20".equals(result)
				|| "86".equals(result) || "95".equals(result)) {

			record.set("status", 3);
			record.set("message", iRetMsg);
		} else {// 6,8,98

			record.set("status", 2);
			record.set("message", iRetMsg);
		}
		return record;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.PAYENT;
	}
}
