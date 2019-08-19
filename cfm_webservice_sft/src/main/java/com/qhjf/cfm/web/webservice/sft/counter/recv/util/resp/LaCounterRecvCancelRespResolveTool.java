package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillCancelRespBean;

/**
 * LA系统保单确认结果解析工具
 * 
 * @author CHT
 *
 */
public class LaCounterRecvCancelRespResolveTool implements ResponseResolveTool {
	private LaCounterRecvCancelRespResolveTool() {
	}

	public static LaCounterRecvCancelRespResolveTool getInstance() {
		return LaCounterRecvRespResolveToolInner.INSTANCE;
	}

	private static class LaCounterRecvRespResolveToolInner {
		private static final LaCounterRecvCancelRespResolveTool INSTANCE = new LaCounterRecvCancelRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		PersonBillCancelRespBean result = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = jo.getJSONArray("ESBEnvelopeResult").getJSONObject(0).getJSONArray("MsgBody").getJSONObject(0)
					.getJSONArray("DRNDELO_REC").getJSONObject(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if ("0".equals(jsonObject.getString("STATUS"))) {
			JSONObject json = jsonObject.getJSONArray("ADDITIONAL_FIELDS").getJSONObject(0);

			String status = json.getString("STATUS");
			String errField = json.getString("FLDNAM");
			String errCode = json.getString("ERRCODE");
			String errMessage = json.getString("ERRMESS");
			PersonBillCancelRespBean bean = new PersonBillCancelRespBean(status, errField, errCode, errMessage);
			result = bean;
		}
		return result;
	}

}
