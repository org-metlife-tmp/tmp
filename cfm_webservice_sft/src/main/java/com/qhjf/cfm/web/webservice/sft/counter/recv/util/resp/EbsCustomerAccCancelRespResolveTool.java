package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccCancelRespBean;

public class EbsCustomerAccCancelRespResolveTool implements ResponseResolveTool {

	private EbsCustomerAccCancelRespResolveTool() {
	}

	public static EbsCustomerAccCancelRespResolveTool getInstance() {
		return EbsCustomerAccCancelRespResolveToolInner.INSTANCE;
	}

	private static class EbsCustomerAccCancelRespResolveToolInner {
		private static final EbsCustomerAccCancelRespResolveTool INSTANCE = new EbsCustomerAccCancelRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		GroupCustomerAccCancelRespBean result = null;

		String resultCode = jo.getString("ResultCode");
		String resultMsg = jo.getString("ResultMsg");
		String payNo = jo.getString("PayNo");
		result = new GroupCustomerAccCancelRespBean(resultCode, resultMsg, payNo);
		return result;
	}
}
