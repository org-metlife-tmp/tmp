package com.qhjf.cfm.web.channel.icbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 工行批量支付
 * @author CHT
 *
 */
public class IcbcBatchPayInter implements IChannelBatchInter {
//	private static final Logger log = LoggerFactory.getLogger(IcbcBatchPayInter.class);
	public static final String BATCH_ERROR_MSG = "批量支付回写，银行处理失败,失败原因:%s-%s";
	/**
	 * 批量指令包标识
	 */
	private String fSeqno;
	/**
	 * 银行返回结果列表
	 */
	private JSONArray rdArray;

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
				rd.put("PayAccNo", total.getStr("pay_account_no"));
				rd.put("PayAccNameCN", total.getStr("pay_account_name"));
				rd.put("RecAccNo", r.getStr("recv_account_no"));
				String recvAccName = r.getStr("recv_account_name");
				rd.put("RecAccNameCN", recvAccName);
				String sysIOFlg;
				// 0同行1异行
				String isCrossBank = r.getStr("is_cross_bank");
				if ("0".equals(isCrossBank)) {
					sysIOFlg = "1";
				} else {
					sysIOFlg = "2";
					// 收款方所在城市名称,跨行指令（非跨行快汇）此项必输
					rd.put("RecCityName", r.get("recv_bank_city"));
					// 对方行行号
					rd.put("RecBankNo", r.get("recv_bank_cnaps"));
					// 交易对方银行名称,跨行指令此项必输,中文，60位字符。
					rd.put("RecBankName", r.get("recv_account_bank"));
					// Prop对公对私标志:0：对公账户 1：个人账户
					rd.put("Prop", propJudge(recvAccName));
					
				}
				// 系统内外标志:1：系统内2：系统外
				rd.put("SysIOFlg", sysIOFlg);
				// 币种
				rd.put("CurrType", total.getStr("pay_account_cur"));
				// 金额
				rd.put("PayAmt", r.getStr("amount"));
				/*String summary = CommKit.isNullOrEmpty(r.getStr("summary")) ? "cfm" : r.getStr("summary");
				// 摘要里面加指令码 做自动核对用的
				String smr = r.getStr("instruct_code") + summary;

				// 摘要，工行要求上送20字节
				rd.put("Summary", ChannelStringUtil.getFixLenStr(smr, 20));
				// 用途中文描述
				rd.put("UseCN", ChannelStringUtil.getFixLenStr(summary, 10));*/
				rd.put("UseCN", "批量支付");
				rdList.add(rd);
			}
		} else {
			return null;
		}

		// 2：并笔记账 0：逐笔记账
		result.put("SettleMode", "0");
		result.put("TotalNum", String.valueOf(detail.size()));
		result.put("TotalAmt", total.getStr("total_amount"));
		result.put("pub", pub);
		result.put("rd", rdList);
		return result;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.PAYENT;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		return getBatchSize(jsonStr);
		
	}
	/**
	 * 指令包序列号：fSeqno
	 * 指令包-子指令顺序号：iSeqno
	 * 指令包-子指令序列号：无
	 */
	@Override
	public Record parseResult(String json, int index) throws Exception {
		JSONObject rd = this.rdArray.getJSONObject(index);
		
		Record record = new Record();
		record.set("bank_serial_number", this.fSeqno);
		record.set("package_seq", rd.getInteger("iSeqno"));
		
		String result = rd.getString("Result");
		String iRetMsg = rd.getString("iRetMsg");
		if ("7".equals(result)) {

			record.set("status", 1);
		} else if ("0".equals(result) || "1".equals(result) || "2".equals(result) || "3".equals(result)
				|| "4".equals(result) || "5".equals(result) || "9".equals(result) || "20".equals(result)
				|| "86".equals(result) || "95".equals(result)) {

			record.set("status", 3);
			record.set("message", iRetMsg);
			record.set("code", result);
		} else {// 6,8,98

			record.set("status", 2);
			record.set("message", iRetMsg);
			record.set("code", result);
		}
		return record;
	}

	@Override
	public int getBatchSize() {
		return 29;
	}
	/**
	 * 交易的收方名字符合以下三种情况则为共，否则为私
	 * 1.不为空2.长度大于4；3.包含公司或者机构  
	 * @param recvAccName
	 * @return
	 */
	private String propJudge(String recvAccName){
		if (null != recvAccName && recvAccName.length() >= 4) {
			
			if (recvAccName.indexOf("公司") != -1 || recvAccName.indexOf("机构") != -1) {
				return "0";
			}
		}
		return "1";
	}


	public void setfSeqno(String fSeqno) {
		this.fSeqno = fSeqno;
	}
	public void setRdArray(JSONArray rdArray) {
		this.rdArray = rdArray;
	}
	
	//获取返回结果条数
	private int getBatchSize(String jsonStr) throws Exception{
		JSONObject json = JSONObject.parseObject(jsonStr);
        JSONArray ebArray = json.getJSONArray("eb");
        JSONObject eb0 = ebArray.getJSONObject(0);
        JSONArray pubArray = eb0.getJSONArray("pub");
        JSONObject pub0 = pubArray.getJSONObject(0);
        String retCode = pub0.getString("RetCode");
        String retMsg = pub0.getString("RetMsg");
        this.fSeqno = pub0.getString("fSeqno");
        if ("0".equals(retCode)) {
        	JSONArray outArray = eb0.getJSONArray("out");
        	JSONArray rdArray =  outArray.getJSONObject(0).getJSONArray("rd");
        	this.rdArray = rdArray;
        	return rdArray.size();
		}else {
			throw new Exception(String.format(BATCH_ERROR_MSG, retCode, retMsg));
		}
	}

}
