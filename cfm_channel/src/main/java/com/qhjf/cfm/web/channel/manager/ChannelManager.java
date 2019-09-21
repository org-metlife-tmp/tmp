package com.qhjf.cfm.web.channel.manager;

import com.qhjf.bankinterface.api.ChannelInfo;
import com.qhjf.bankinterface.ccb.CcbChannel;
import com.qhjf.bankinterface.cmbc.CmbcChannel;
import com.qhjf.bankinterface.fingard.api.channel.FingardTcpChannel;
import com.qhjf.bankinterface.icbc.IcbcChannel;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;

import java.util.HashMap;
import java.util.Map;

/**
 * 渠道管理
 * @author yunxw
 *
 */
public class ChannelManager {
	
	/**
	 * bankCode与channelCode对应关系
	 */
	private static final Map<String,String> bankMap = new HashMap<String,String>();
	
	/**
	 * bankCode与ChannelInfo对应关系
	 */
	@SuppressWarnings("rawtypes")
	private static final Map<String,ChannelInfo> channelMap = new HashMap<String,ChannelInfo>();

	/**
	 * 初始化map
	 */
	static{
		bankMap.put("308", "cmbc");
		bankMap.put("102", "icbc");
		bankMap.put("fingard", "fingard");
		bankMap.put("105","ccb");
		channelMap.put("308", CmbcChannel.getInstance());
		channelMap.put("102", IcbcChannel.getInstance());  //=======key:号 value:
		channelMap.put("fingard", FingardTcpChannel.getInstance());
		channelMap.put("105", CcbChannel.getInstance());
	}

	public static Map<String,String> getBankMap(){
		return bankMap;
	}

	/**
	 * 根据bankCode获取channelCode
	 * @param bankCode
	 * @return
	 * @throws Exception
	 */
	public static String getChannelCode(String bankCode) throws Exception{
		String channelCode = bankMap.get(bankCode);
		if(channelCode == null || channelCode.length() == 0){
			throw new Exception("银行"+bankCode+"未开通");
		}
		return channelCode;
	}
	
	/**
	 * 根据bankCode获取channelInfo
	 * @param bankCode
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static ChannelInfo getChannelInfo(String bankCode) throws Exception{
		ChannelInfo channelInfo = channelMap.get(bankCode);
		if(channelInfo == null){
			throw new Exception("银行"+bankCode+"未开通");
		}
		return channelInfo;
	}
	
	/**
	 * 根据bankCode生成流水号
	 * @param bankCode
	 * @return
	 * @throws Exception
	 */
	public static String getSerianlNo(String bankCode) throws Exception{
		if(bankCode.equals("308") || bankCode.equals("102") || bankCode.equals("fingard")|| bankCode.equals("105")){             //=====***建行加个或
			return RedisSericalnoGenTool.genBankSeqNo();
		}
		throw new Exception("银行"+bankCode+"未开通");
	}
	
	/**
	 * 获取channelInter
	 * @param bankCode
	 * @param jobCode
	 * @return
	 * @throws Exception
	 */
	public static IChannelInter getInter(String bankCode,String jobCode) throws Exception{
		String channelCode = getChannelCode(bankCode);
		StringBuffer fullClassName = new StringBuffer("com.qhjf.cfm.web.channel.");
		fullClassName.append(channelCode).append(".");
		fullClassName.append(channelCode.substring(0, 1).toUpperCase()).append(channelCode.substring(1)).append(jobCode).append("Inter");
		Class<?> cls = Class.forName(fullClassName.toString());
		return (IChannelInter) cls.newInstance();
	}

}
