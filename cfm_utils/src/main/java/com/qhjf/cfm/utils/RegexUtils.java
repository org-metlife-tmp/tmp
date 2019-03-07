package com.qhjf.cfm.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 */
public class RegexUtils {

	/**
	 * 手机号的正则表达式
	 */
	public static final String PHONE_REG = "^(0|86|17951)?(-)?(12[0-9]|13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$";

	/**
	 * 邮箱的正则表达式
	 */
	public static final String EMIAL_REG = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

	/**
	 * ip地址的正则表达式
	 */

	public static final String IP4_REG = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))";

	/**
	 * 域名从的正则表达式
	 */
	public static final String DOMAIN_NAME_REG = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";

	/**
	 * 全批的正则表达式
	 */
	public static final String PINYIN_REG = "(a[io]?|ou?|e[inr]?|ang?|ng|[bmp](a[io]?|[aei]ng?|ei|ie?|ia[no]|o|u)|pou|me|m[io]u|"
			+ "[fw](a|[ae]ng?|ei|o|u)|fou|wai|[dt](a[io]?|an|e|[aeio]ng|ie?|ia[no]|ou|u[ino]?|uan)|dei|diu|[nl]"
			+ "(a[io]?|ei?|[eio]ng|i[eu]?|i?ang?|iao|in|ou|u[eo]?|ve?|uan)|nen|lia|lun|[ghk](a[io]?|[ae]ng?|e|ong|ou|u[aino]?|uai|uang?)|"
			+ "[gh]ei|[jqx](i(ao?|ang?|e|ng?|ong|u)?|u[en]?|uan)|([csz]h?|r)([ae]ng?|ao|e|i|ou|u[ino]?|uan)|[csz](ai?|ong)|[csz]h(ai?|uai|uang)|"
			+ "zei|[sz]hua|([cz]h|r)ong|y(ao?|[ai]ng?|e|i|ong|ou|u[en]?|uan))";

	/**
	 * 简拼的正则表达式
	 */
	public static final String JIANPIN_REG = "[a-z]+";

	// 日期的正则表达式
	public static final String DATE = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])";

	// 时间的正则表达式
	public static final String TIME = "^(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d";

	// 日期+时间的正则表达式
	public static final String DATE_TIME = "^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\s+(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d";

	/**
	 * 判断是否正整数
	 *
	 * @param number
	 *            数字
	 *
	 * @return boolean true,通过，false，没通过
	 */
	public static boolean isNumber(String number) {
		if (null == number || "".equals(number))
			return false;
		String regex = "[0-9]*";
		return number.matches(regex);
	}

	/**
	 * 判断几位小数(正数)
	 *
	 * @param decimal
	 *            数字
	 * @param count
	 *            小数位数
	 * @return boolean true,通过，false，没通过
	 */
	public static boolean isDecimal(String decimal, int count) {
		if (null == decimal || "".equals(decimal))
			return false;
		String regex = "^(-)?(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){" + count + "})?$";
		return decimal.matches(regex);
	}

	/**
	 * 判断是否为手机号
	 * 
	 * @param phoneNo
	 * @return
	 */
	public static boolean isPhoneNo(String phoneNo) {
		if (null == phoneNo || "".equals(phoneNo))
			return false;
		return Pattern.matches(PHONE_REG, phoneNo.trim());
	}

	/**
	 * 判断是否为邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		return Pattern.matches(EMIAL_REG, email.trim());
	}

	/**
	 * 判断是否是IP v4 的IP地址
	 * 
	 * @param ip4Addr
	 * @return
	 */
	public static boolean isIP4Addr(String ip4Addr) {
		if (null == ip4Addr || "".equals(ip4Addr))
			return false;
		return Pattern.matches(IP4_REG, ip4Addr.trim());
	}

	/**
	 * 判断是否是ip 或 域名
	 * 
	 * @param ipOrDomain
	 * @return
	 */
	public static boolean isIPOrDoamin(String ipOrDomain) {
		return isIP4Addr(ipOrDomain) || isDomainname(ipOrDomain);
	}

	/**
	 * 判断是否是域名
	 */
	public static boolean isDomainname(String domainName) {
		if (null == domainName || "".equals(domainName))
			return false;
		return Pattern.matches(DOMAIN_NAME_REG, domainName.trim());
	}

	/**
	 * 判断是否是合法的端口 1-65535
	 */
	public static boolean isValidatePort(String port) {
		if (isNumber(port)) {
			int intPort = Integer.parseInt(port);
			return intPort >= 1 && intPort <= 65535;
		} else {
			return false;
		}
	}

	public static boolean isValidatePort(Integer port) {
		return port >= 1 && port <= 65535;
	}

	/**
	 * 判断是否为简拼
	 */
	public static boolean isJianPin(String pinyin) {
		if (null == pinyin || "".equals(pinyin))
			return false;
		return Pattern.matches(JIANPIN_REG, pinyin.trim().toLowerCase());
	}

	/**
	 * 判断是否为全拼
	 */
	public static boolean isPinYin(String pinyin) {
		if (isJianPin(pinyin)) {
			Pattern pattern = Pattern.compile(PINYIN_REG);
			Matcher matcher = pattern.matcher(pinyin.trim().toLowerCase());
			if (matcher.find() && matcher.start() == 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断是否为日期 yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static boolean isDate(String date){
		if (StringUtils.isBlank(date)) {
			return false;
		}
		if (date.matches(DATE)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否为时间 HH:mm:ss
	 * @param time
	 * @return
	 */
	public static boolean isTime(String time){
		if (StringUtils.isBlank(time)) {
			return false;
		}
		if (time.matches(TIME)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否为日期时间  yyyy-MM-dd HH:mm:ss
	 * @param dateTime
	 * @return
	 */
	public static boolean isDateTime(String dateTime){
		if (StringUtils.isBlank(dateTime)) {
			return false;
		}
		if (dateTime.matches(DATE_TIME)) {
			return true;
		}
		return false;
	}
}
