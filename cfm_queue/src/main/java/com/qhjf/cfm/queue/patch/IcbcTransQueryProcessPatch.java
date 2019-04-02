package com.qhjf.cfm.queue.patch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.api.ProcessEntrance;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.bankinterface.api.exceptions.DataParseException;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;

import java.util.HashMap;
import java.util.Map;

/**
 * 工行交易查询指令发送补丁
 */
public class IcbcTransQueryProcessPatch {
	public static final String ERROR_MSG = "银行处理失败,失败原因:%s";
	/**
	 * 发送银行指令
	 * @param inter	原子接口配置信息
	 * @param reqParams	指令请求参数
	 * @return
	 * @throws BankInterfaceException 
	 */
	public static String process(AtomicInterfaceConfig inter, Map<String, Object> reqParams) throws BankInterfaceException{
		//向银行发送指令
		String resp = ProcessEntrance.getInstance().process(inter, reqParams);
		JSONObject resultJO = JSON.parseObject(resp);
		judgeException(resultJO);
		
		String nextTag = judgeNextTag(resultJO);
		if (null != nextTag) {
			reqParams.put("NextTag", nextTag);
			Map<String, Object> pub = (Map<String, Object>) reqParams.get("pub");
			
			pub.put("fSeqno", RedisSericalnoGenTool.genBankSeqNo());
			reqParams.put("pub", pub);
			
			JSONArray ebs = resultJO.getJSONArray("eb");
			JSONObject eb = ebs.getJSONObject(0);
			JSONArray outs = eb.getJSONArray("out");
			JSONObject out = outs.getJSONObject(0);
			JSONArray rds = out.getJSONArray("rd");
			
			queryPage(rds, inter, reqParams);
			
		}
		return resultJO.toJSONString();
	}
	
	//判断银行返回结果是否异常
	private static void judgeException(JSONObject jo) throws DataParseException{
		JSONArray ebs = jo.getJSONArray("eb");
		JSONObject eb = ebs.getJSONObject(0);
		JSONArray pubArray = eb.getJSONArray("pub");
        JSONObject pub0 = pubArray.getJSONObject(0);
        String retCode = pub0.getString("RetCode");
        if (!"0".equals(retCode)) {
			throw new DataParseException(String.format(ERROR_MSG, pub0.getString("RetMsg")));
		}
	}
	
	private static void queryPage(JSONArray rds, AtomicInterfaceConfig inter, Map<String, Object> reqParams) throws BankInterfaceException{
		String respPage = ProcessEntrance.getInstance().process(inter, reqParams);
		judgeException(JSON.parseObject(respPage));
		
		rds.addAll(getRds(respPage));
		String nextTag = judgeNextTag(JSON.parseObject(respPage));
		if (null != nextTag) {
			reqParams.put("NextTag", nextTag);
			Map<String, Object> pub = (Map<String, Object>) reqParams.get("pub");
			
			// 指令包序列号
			pub.put("fSeqno", RedisSericalnoGenTool.genBankSeqNo());
			reqParams.put("pub", pub);
			
			queryPage(rds, inter, reqParams);
		}else {
			return;
		}
	}
	
	//判断是否有分页
	private static String judgeNextTag(JSONObject resultJO){
		JSONArray ebs = resultJO.getJSONArray("eb");
		JSONObject eb = ebs.getJSONObject(0);
		
		JSONArray outs = eb.getJSONArray("out");
		JSONObject out = outs.getJSONObject(0);
		
		String nextTag = out.getString("NextTag");
		if (null != nextTag && !"".equals(nextTag.trim())) {
			return nextTag;
		}else {
			return null;
		}
	}
	
	//获取银行返回数据的数组
	private static JSONArray getRds(String resp){
		JSONObject jo = JSON.parseObject(resp);
		JSONArray ebs = jo.getJSONArray("eb");
		JSONObject eb = ebs.getJSONObject(0);
		
		JSONArray outs = eb.getJSONArray("out");
		JSONObject out = outs.getJSONObject(0);
		
		JSONArray rds = out.getJSONArray("rd");
		return rds;
	}
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> pubMap = new HashMap<>();

		pubMap.put("CIS", IcbcConstant.QHISD.getChannelConfig("CIS"));
		pubMap.put("fSeqno", "20181114222002000001");

		map.put("TotalNum", "1");
		map.put("AccNo", "1001190719004665588");
		map.put("BeginDate", "20181201");
		map.put("EndDate", "20181201");
		map.put("MinAmt", "0");
		map.put("MaxAmt", String.valueOf(Integer.MAX_VALUE));
		map.put("pub", pubMap);
		try {
			String r = IcbcTransQueryProcessPatch.process(IcbcConstant.QHISD, map);
			System.out.println(r);
		} catch (BankInterfaceException e) {
			e.printStackTrace();
		}
	}
}
