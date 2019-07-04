package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.list.GroupCustomerAccRespInfoListBean;

/**
 * 客户账户查询解析工具
 * 
 * @author CHT
 *
 */
public class EbsCustomerAccQryRespResolveTool implements ResponseResolveTool {
	private static final String FAIL = "FAIL";

	private EbsCustomerAccQryRespResolveTool() {
	}

	public static EbsCustomerAccQryRespResolveTool getInstance() {
		return EbsCustomerAccQryRespResolveToolInner.INSTANCE;
	}

	private static class EbsCustomerAccQryRespResolveToolInner {
		private static final EbsCustomerAccQryRespResolveTool INSTANCE = new EbsCustomerAccQryRespResolveTool();
	}

	@Override
	public Object parseResponse(JSONObject jo) {
		GroupCustomerAccQryRespBean result = null;

		String resultCode = jo.getString("ResultCode");
		String resultMsg = jo.getString("ResultMsg");
		if (FAIL.equals(resultCode)) {
			result = new GroupCustomerAccQryRespBean(resultCode, resultMsg, null);
			return result;
		}

		JSONArray infoList = jo.getJSONArray("InfoList");
		if (infoList != null && infoList.size() > 0) {
			List<GroupCustomerAccRespInfoListBean> list = new ArrayList<>();

			int size = infoList.size();
			for (int i = 0; i < size; i++) {
				JSONObject info = infoList.getJSONObject(i);
				String customerNo = info.getString("CustomerNo");
				String customerName = info.getString("CustomerName");
				String money = info.getString("Money");
				list.add(new GroupCustomerAccRespInfoListBean(customerNo, customerName, money));
			}

			result = new GroupCustomerAccQryRespBean(resultCode, resultMsg, list);
		}

		return result;
	}

}
