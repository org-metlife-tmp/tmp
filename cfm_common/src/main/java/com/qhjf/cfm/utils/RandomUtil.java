package com.qhjf.cfm.utils;

import org.apache.commons.lang3.RandomUtils;

import java.util.Date;
/**
 * 随机串
 * @author CHT
 *
 */
public class RandomUtil {
	/**
	 * 当前时间毫秒数
	 * @return
	 */
	public static String currentTimeStamp(){
		long time = new Date().getTime();
		return String.valueOf(time);
	}
	public static String currentTimeStamp(Date date){
		return String.valueOf(date.getTime());
	}
	/**
	 * 10000~99999之间的随机数
	 * @return
	 */
	public static String randomNumber(){
		int nextInt = RandomUtils.nextInt(10000, 99999);
		return String.valueOf(nextInt);
	}
	public static String randomNumber(int start, int end){
		int nextInt = RandomUtils.nextInt(start, end);
		return String.valueOf(nextInt);
	}
	/**
	 * 当前时间毫秒数 + 10000~99999之间的随机数
	 * @return
	 */
	public static String randomTimeAndNumber(){
		return currentTimeStamp().concat(randomNumber());
	}
}
