package com.qhjf.cfm.web.channel.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常信息转换
 * @author CHT
 *
 */
public class ErrMsgTransfor {
	public static Map<String, String> CMBC_ERR_MSG = new HashMap<>();
	
	static{
		CMBC_ERR_MSG.put("S", "成功,银行支付成功");
		CMBC_ERR_MSG.put("F", "失败,银行支付失败");
		CMBC_ERR_MSG.put("B", "退票,银行支付被退票");
		CMBC_ERR_MSG.put("R", "否决,企业审批否决");
		CMBC_ERR_MSG.put("D", "过期,企业过期不审批");
		CMBC_ERR_MSG.put("C", "撤消,企业撤销");
		CMBC_ERR_MSG.put("M", "商户撤销订单,商务支付");
		CMBC_ERR_MSG.put("V", "拒绝,委托贷款被借款方拒绝");
	}
	
	public static String getCmbcErrMsg(String errCode){
		return CMBC_ERR_MSG.get(errCode);
	}
}
