package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupBizPayQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.list.GroupBizPayQryRespInfoListBean;

public class EbsBizPayQryRespResolveTool implements ResponseResolveTool {
	private static final String FAIL = "FAIL";

	private EbsBizPayQryRespResolveTool() {
	}

	public static EbsBizPayQryRespResolveTool getInstance() {
		return EbsBizPayQryRespResolveToolInner.INSTANCE;
	}

	private static class EbsBizPayQryRespResolveToolInner {
		private static final EbsBizPayQryRespResolveTool INSTANCE = new EbsBizPayQryRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		GroupBizPayQryRespBean result = null;

		String resultCode = jo.getString("ResultCode");
		String resultMsg = jo.getString("ResultMsg");
		if (FAIL.equals(resultCode)) {
			result = new GroupBizPayQryRespBean(resultCode, resultMsg, null);
			return result;
		}

		JSONArray infoList = jo.getJSONArray("InfoList");
		if (infoList != null && infoList.size() > 0) {
			List<GroupBizPayQryRespInfoListBean> list = new ArrayList<>();

			int size = infoList.size();
			for (int i = 0; i < size; i++) {
				JSONObject info = infoList.getJSONObject(i);
				
				Map<String, String> params = new HashMap<String, String>();
				params.put("preinsureBillNo", info.getString("PrtNo"));
				params.put("insureBillNo", info.getString("GrpContNo"));
				params.put("bussinessNo", info.getString("BussinessNo"));
				params.put("bussinessType", info.getString("BussinessType"));
				params.put("havePayMoney", info.getString("HavePayMoney"));
				params.put("needPayMoney", info.getString("NeedPayMoney"));
				
				params.put("agentCom", info.getString("AgentCom"));
				params.put("manageCom", info.getString("ManageCom"));
				params.put("customerNo", info.getString("CustomerNo"));
				params.put("customerName", info.getString("CustomerName"));
				params.put("appntNo", info.getString("AppntNo"));
				params.put("appntName", info.getString("AppntName"));
				
				list.add(new GroupBizPayQryRespInfoListBean(params));
			}

			result = new GroupBizPayQryRespBean(resultCode, resultMsg, list);
		}

		return result;
	}

}
