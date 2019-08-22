package com.qhjf.cfm.excel.util;

import com.jfinal.ext.kit.DateKit;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * POI日期格式处理
 * 
 * @author CHT
 *
 */
public class PoiDateFomatUtil {
	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            日期java.util.Date
	 * @param formater
	 *            格式化对象
	 * @return
	 */
	public static String dateToStr(Date date, SimpleDateFormat formater) {
		if (null == date) {
			return null;
		}
		
		String format = formater.format(date);
		if (null == format) {
			return format;
		}
		
		if (format.indexOf(" ") != -1) {
			return format.replaceFirst("\\s+", " ");
		}
		return format;
	}
	/**
	 * 日期转字符串
	 * 
	 * @param date
	 *            日期java.util.Date
	 * @param formater
	 *            格式化字符串
	 * @return
	 */
	public static String dateToStr(Date date, String formater) {
		if (null == date) {
			return null;
		}
		
		String format = DateKit.toStr(date, formater);
		if (null == format) {
			return format;
		}
		
		if (format.indexOf(" ") != -1) {
			return format.replaceFirst("\\s+", " ");
		}
		return format;
	}
}
