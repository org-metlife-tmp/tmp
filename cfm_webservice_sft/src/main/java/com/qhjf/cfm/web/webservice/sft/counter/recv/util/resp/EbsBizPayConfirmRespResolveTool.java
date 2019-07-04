package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupBizPayConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccCancelRespBean;

public class EbsBizPayConfirmRespResolveTool implements ResponseResolveTool {
	private static final String FAIL = "FAIL";

	private EbsBizPayConfirmRespResolveTool() {
	}

	public static EbsBizPayConfirmRespResolveTool getInstance() {
		return EbsBizPayConfirmRespResolveToolInner.INSTANCE;
	}

	private static class EbsBizPayConfirmRespResolveToolInner {
		private static final EbsBizPayConfirmRespResolveTool INSTANCE = new EbsBizPayConfirmRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		GroupBizPayConfirmRespBean result = null;

		String resultCode = jo.getString("ResultCode");
		String resultMsg = jo.getString("ResultMsg");
		String payNo = jo.getString("PayNo");
		result = new GroupBizPayConfirmRespBean(resultCode, resultMsg, payNo);
		return result;
	}
}
