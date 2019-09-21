package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillQryRespBean;

/**
 * LA系统保单确认结果解析工具
 * 
 * @author CHT
 *
 */
public class LaCounterRecvComfirmRespResolveTool implements ResponseResolveTool {
	private LaCounterRecvComfirmRespResolveTool() {
	}

	public static LaCounterRecvComfirmRespResolveTool getInstance() {
		return LaCounterRecvRespResolveToolInner.INSTANCE;
	}

	private static class LaCounterRecvRespResolveToolInner {
		private static final LaCounterRecvComfirmRespResolveTool INSTANCE = new LaCounterRecvComfirmRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		List<PersonBillConfirmRespBean> result = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = jo.getJSONArray("ESBEnvelopeResult").getJSONObject(0).getJSONArray("MsgBody").getJSONObject(0)
					.getJSONArray("DRNADDO_REC").getJSONObject(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if ("0".equals(jsonObject.getString("STATUS"))) {
			JSONArray array = jsonObject.getJSONArray("ADDITIONAL_FIELDS").getJSONObject(0).getJSONArray("DRNOUTRECS");
			if (array != null && array.size() > 0) {
				result = new ArrayList<>();
				for (int i = 0; i < array.size(); i++) {
					JSONObject json = array.getJSONObject(i);
					String status = json.getString("STATUS");
					if (StringUtils.isBlank(status)) {
						continue;
					}
					String cownsel = json.getString("CHDRNUM");
					String receipt = json.getString("RECEIPT");
					String errField = json.getString("FLDNAM");
					String errCode = json.getString("ERRCODE");
					String errMessage = json.getString("ERRMESS");
					PersonBillConfirmRespBean bean = new PersonBillConfirmRespBean(status, cownsel, receipt, errField, errCode,
							errMessage);
					result.add(bean);
				}
			}
		}
		return result;
	}

}
