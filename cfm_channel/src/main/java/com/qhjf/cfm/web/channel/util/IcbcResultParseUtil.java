package com.qhjf.cfm.web.channel.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 工行返回数据解析
 */
public class IcbcResultParseUtil {
	
	public static final String ERROR_MSG = "银行处理失败,失败原因:%s-%s";
	
	public static JSONObject parseResult(String respStr) throws Exception{
		JSONObject out0 = null;
				
		JSONObject json = JSONObject.parseObject(respStr);
        JSONArray ebArray = json.getJSONArray("eb");
        JSONObject eb0 = ebArray.getJSONObject(0);
        JSONArray pubArray = eb0.getJSONArray("pub");
        JSONObject pub0 = pubArray.getJSONObject(0);
        String retCode = pub0.getString("RetCode");
        String retMsg = pub0.getString("RetMsg");
        if ("0".equals(retCode)) {
        	JSONArray outArray = eb0.getJSONArray("out");
        	out0 = outArray.getJSONObject(0);
		}else {
			throw new Exception(String.format(ERROR_MSG, retCode, retMsg));
		}
        return out0;
	}
	
}
