package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupAdvanceReceiptStatusQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupBizPayConfirmRespBean;

public class EbsAdvanceReceiptStatusQryRespResolveTool implements ResponseResolveTool {
	private static final String FAIL = "FAIL";

	private EbsAdvanceReceiptStatusQryRespResolveTool() {
	}

	public static EbsAdvanceReceiptStatusQryRespResolveTool getInstance() {
		return EbsAdvanceReceiptStatusQryRespResolveToolInner.INSTANCE;
	}

	private static class EbsAdvanceReceiptStatusQryRespResolveToolInner {
		private static final EbsAdvanceReceiptStatusQryRespResolveTool INSTANCE = new EbsAdvanceReceiptStatusQryRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		GroupAdvanceReceiptStatusQryRespBean result = null;

		String resultCode = jo.getString("ResultCode");
		String resultMsg = jo.getString("ResultMsg");
		String payNo = jo.getString("PayNo");
		result = new GroupAdvanceReceiptStatusQryRespBean(resultCode, resultMsg, payNo);
		return result;
	}

}
