package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccConfirmRespBean;

public class EbsCustomerAccConfirmRespResolveTool implements ResponseResolveTool {

	private EbsCustomerAccConfirmRespResolveTool() {
	}

	public static EbsCustomerAccConfirmRespResolveTool getInstance() {
		return EbsCustomerAccConfirmRespResolveToolInner.INSTANCE;
	}

	private static class EbsCustomerAccConfirmRespResolveToolInner {
		private static final EbsCustomerAccConfirmRespResolveTool INSTANCE = new EbsCustomerAccConfirmRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		GroupCustomerAccConfirmRespBean result = null;

		String resultCode = jo.getString("ResultCode");
		String resultMsg = jo.getString("ResultMsg");
		String payNo = jo.getString("PayNo");
		result = new GroupCustomerAccConfirmRespBean(resultCode, resultMsg, payNo);
		return result;
	}
}
