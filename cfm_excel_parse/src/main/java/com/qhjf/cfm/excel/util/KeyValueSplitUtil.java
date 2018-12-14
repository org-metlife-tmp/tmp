package com.qhjf.cfm.excel.util;

import java.util.HashMap;
import java.util.Map;

import com.qhjf.cfm.excel.exception.ExcelConfigParseException;

public class KeyValueSplitUtil {
	
	/**
	 * @param source	类似“k=v”字符串
	 * @return
	 */
	public static String[] splitEqualSign(String source){
		String[] result = new String[2];
		String[] split = source.split("=");
		result[0] = split[0].trim();
		if (split.length == 2) {
			int index = split[1].indexOf("##");
			if (index != -1) {//含有注释内容，去掉注释
				result[1] = split[1].substring(0, index).trim();
			}else {
				result[1] = split[1].trim();
			}
		}
		return result;
	}
	/**
	 * @param source	类似 “键:值”的字符串
	 * @return String[2]	第一个元素为key，第二个元素为value
	 * @throws Exception	key/value都不能为空
	 */
	public static String[] splitColon(String source) throws ExcelConfigParseException{
		String[] result = new String[2];
		String[] split = null;
		if (source.indexOf(":") != -1) {
			split = source.split(":");
		}else if (source.indexOf("：") != -1) {
			split = source.split("：");
		}else {
			throw new ExcelConfigParseException(String.format("配置项【%s】配置错误（键值必须以冒号分隔）", source));
		}
		result[0] = split[0].trim();
		if (split.length == 2) {
			result[1] = split[1].trim();
		}else {
			throw new ExcelConfigParseException(String.format("配置项【%s】配置错误（键值都不能为空）", source));
		}
		return result;
	}
	/**
	 * @param source	类似“k1:v1,k2:v2,K3:v3”字符串
	 * @return
	 * @throws Exception 
	 */
	public static Map<String, String> splitToMap(String source) throws ExcelConfigParseException {
		Map<String, String> result = new HashMap<>();
		//中文逗号转英文逗号
		if (source.indexOf("，") != -1) {
			source = source.replaceAll("，", ",");
		}
		//得到元素为k1:v1的数组
		String[] props = source.split(",");
		for (String prop : props) {
			String[] kv = splitColon(prop);
			result.put(kv[0], kv[1]);
		}
		return result;
	}
}
