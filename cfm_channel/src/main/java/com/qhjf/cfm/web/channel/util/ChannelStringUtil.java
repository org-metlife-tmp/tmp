package com.qhjf.cfm.web.channel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CHT
 */
public class ChannelStringUtil {
	private static final Logger log = LoggerFactory.getLogger(ChannelStringUtil.class);

	/**
	 * 场景：银行需要上传长度为20的摘要字段，一个中文占2个长度 功能：src是，
	 * 
	 * @param src
	 *            客户实际输入的摘要
	 * @param targetLen
	 *            最终需要的长度
	 * @return
	 */
	public static String getFixLenStr(String src, int targetLen) {
		int totalLen = 0;
		int srcLen = src.length();
		for (int i = 0; i < srcLen; i++) {
			int codePointAt = Character.codePointAt(src, i);
			if (codePointAt >= 0 && codePointAt <= 255) {
				totalLen++;
			} else {
				totalLen += 2;
			}

			if (totalLen > targetLen) {
				log.warn("ICBC字符串处理：原始字符串【{}】，最大长度【{}】, 截取到第【{}】位", src, targetLen, i - 1);
				return src.substring(0, i);
			}
		}
		log.debug("ICBC字符串处理：原始字符串【{}】，最大长度【{}】, 未发生截断", src, targetLen);
		return src;
	}

	/**
	 * 获取招行中文字符串的长度
	 * 
	 * 招行字符串规则如下：
	 * 
	 * 	包含中文的字符类型。注意如果字符长度为 N，最多可输入汉字数为(N- 银企直连 接口设计说明书2)/2。
	 * 
	 * 	如果中文中还夹杂英文字符或数字，则计算字符串长度时要分段 处理。
	 * 
	 * 	比如“中国 TCL 集团”，该字符串长度为 15，即对每段汉字的 长度都要作加 2处理
	 * 
	 * @param src
	 * @param targetLen
	 * @return
	 */
	public static String getCmbFixLenStr(String src, int targetLen) {
		int totalLen = 0;
		int srcLen = src.length();
		boolean hasChinese = true;
		for (int i = 0; i < srcLen; i++) {
			int codePointAt = Character.codePointAt(src, i);
			if (codePointAt >= 0 && codePointAt <= 255) {
				totalLen++;
			} else {
				totalLen += 2;
				if (hasChinese) {
					totalLen += 4;
					hasChinese = false;
				}
			}

			if (totalLen > targetLen) {
				log.warn("CMB字符串处理：原始字符串【{}】，最大长度【{}】, 截取到第【{}】位", src, targetLen, i - 1);
				return src.substring(0, i);
			}
		}
		log.debug("CMB字符串处理：原始字符串【{}】，最大长度【{}】, 未发生截断", src, targetLen);
		return src;
	}

	public static void main(String[] args) {
		String str = getFixLenStr("要了解这个服务发布要了解这个服务发布", 20);
		System.out.println(str);
	}
}
