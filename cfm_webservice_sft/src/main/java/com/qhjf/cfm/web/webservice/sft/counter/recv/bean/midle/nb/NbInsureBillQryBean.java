package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.nb;

import com.alibaba.fastjson.JSONObject;

/**
 * NB保单查询bean
 * @author CHT
 *
 */
public class NbInsureBillQryBean {

	private String jsonStr;
	
	public NbInsureBillQryBean(String appno){
		JSONObject Parameters = new JSONObject();
		Parameters.put("chdrnum", appno);
		
		JSONObject Request = new JSONObject();
		Request.put("Parameters", Parameters);
		Request.put("Resource", "FP");
		
		JSONObject req = new JSONObject();
		req.put("Request", Request);
		
		this.jsonStr = req.toJSONString();
	}

	public String getJsonStr() {
		return jsonStr;
	}

	public static void main(String[] args) {
		NbInsureBillQryBean a = new NbInsureBillQryBean("123");
		System.out.println(a.getJsonStr());
	}
}
