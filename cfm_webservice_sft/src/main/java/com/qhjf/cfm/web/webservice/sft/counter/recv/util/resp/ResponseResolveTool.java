package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONObject;

public interface ResponseResolveTool {

	/**
	 * 解析核心返回的结果
	 * @param jo
	 * @return
	 */
	public Object parseResponse(JSONObject jo);
}
