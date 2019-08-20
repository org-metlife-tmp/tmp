package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillQryRespBean;

/**
 * NB系统保单查询结果解析工具
 * @author CHT
 *
 */
public class NbCounterRecvRespResolveTool implements ResponseResolveTool {
	private NbCounterRecvRespResolveTool(){}
	public static NbCounterRecvRespResolveTool getInstance(){
		return NbCounterRecvRespResolveToolInner.INSTANCE;
	}
	private static class NbCounterRecvRespResolveToolInner{
		private static final NbCounterRecvRespResolveTool INSTANCE = new NbCounterRecvRespResolveTool();
	}
	@Override
	public Object parseResponse(JSONObject jo) {
		
		JSONObject paramJo = jo.getJSONObject("Response").getJSONObject("Parameters");
		
		if (paramJo == null || paramJo.isEmpty()) {
			return null;
		}
		
		String ownname = paramJo.getString("ownname");//投保人
		String ownsel = paramJo.getString("ownsel");//投保人客户号
		String ownid = paramJo.getString("ownid");//投保人证件号
		String branch = paramJo.getString("branch");//保单机构
		
		String standard = paramJo.getString("standard");//当前保单缴费标准
		String ispay = paramJo.getString("ispay");//是否在垫交中
		String srcebus = paramJo.getString("srcebus");//销售渠道
		String campaign = paramJo.getString("campaign");//专案代码
		String agntnum = paramJo.getString("agntnum");//代理人号码
//		String isbank = jo.getString("isbank");//是否银行转账中的保单缴费
		PersonBillQryRespBean bean = new PersonBillQryRespBean(ownname, ownsel, ownid, branch);
		bean.setPremiumStandard(standard);
		bean.setIsPadPayment(ispay);
		bean.setSourceSys("2");
		
		//请资金平台默认NB保单暂记余额为空，是否银行转账中为否。
		bean.setSuspenseBalance("");
		bean.setIsTransAccount("0");
		bean.setSrceBus(srcebus);
		bean.setCampAign(campaign);
		bean.setAgntNum(agntnum);
		bean.setBankcode(paramJo.getString("bankcode"));
		return bean;
	}

}
