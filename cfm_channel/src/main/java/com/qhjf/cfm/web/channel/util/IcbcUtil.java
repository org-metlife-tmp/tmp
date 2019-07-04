package com.qhjf.cfm.web.channel.util;

public class IcbcUtil {

	/**
	 * 交易的收方名字符合以下三种情况则为公，否则为私
	 * 1.不为空2.长度大于4；3.包含公司或者机构  
	 * @param recvAccName
	 * @return
	 */
	public static String propGen(String recvAccName){
		if (null != recvAccName && recvAccName.length() >= 4) {
			
			if (recvAccName.indexOf("公司") != -1 || recvAccName.indexOf("机构") != -1) {
				return "0";
			}
		}
		return "1";
	}
}
