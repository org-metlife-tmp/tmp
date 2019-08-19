package com.qhjf.cfm.utils;

import java.text.NumberFormat;
import java.util.Random;
import java.util.UUID;

public class RandomStrGenerator {

	/**
	 * 随机码生成字典1
	 */
	private static final String[] DICT_FOR_CAPTCHA = { "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F",
			"G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y" };

	/**
	 * 随机码生成字典2
	 */
	private static final String[] DICT_WITH_ALLCHAR = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C",
			"D", "E", "F", "G", "H", "I", "J", "K", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y",
			"Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z" };
	
	/**
	 * 随机码生成字典1
	 */
	private static final String[] DICT_FOR_DIGIT = {  "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	public static String generateRandomCode_for_cpatcha(int length) {
		// 生成随机类
		Random random = new Random();
		StringBuffer sRand = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String rand = String.valueOf(DICT_FOR_CAPTCHA[random.nextInt(DICT_FOR_CAPTCHA.length)]);
			sRand.append(rand);
		}
		return sRand.toString();
	}

	public static String generateRandomCode_with_ALLCHAR(int length) {
		// 生成随机类
		Random random = new Random();
		StringBuffer sRand = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String rand = String.valueOf(DICT_WITH_ALLCHAR[random.nextInt(DICT_WITH_ALLCHAR.length)]);
			sRand.append(rand);
		}
		return sRand.toString();
	}
	
	public static String generateRandomCode_with_digit(int length) {
		// 生成随机类
		Random random = new Random();
		StringBuffer sRand = new StringBuffer();
		for (int i = 0; i < length; i++) {
			String rand = String.valueOf(DICT_FOR_DIGIT[random.nextInt(DICT_FOR_DIGIT.length)]);
			sRand.append(rand);
		}
		return sRand.toString();
	}
	
	public static int generateIntRandomCode(int minValue,int maxValue){
		Random random = new Random();
		return random.nextInt(maxValue - minValue + 1) + minValue;
	}
	

	
	public static void main(String[] args){
		String parent_levelCode = "00001";
		Long parent_levelNum = 1L;
		String maxChildren_levleCode = "0000199999";	
		
		String childreSeg_code = maxChildren_levleCode.substring(parent_levelCode.length());
		System.out.println(childreSeg_code);
		Long preLevelLength = parent_levelCode.length()/parent_levelNum;
		
		NumberFormat nf = NumberFormat.getInstance();  
        // 设置是否使用分组  
        nf.setGroupingUsed(false);  
        // 设置最大整数位数  
        nf.setMaximumIntegerDigits(preLevelLength.intValue());  
        // 设置最小整数位数  
        nf.setMinimumIntegerDigits(preLevelLength.intValue());  
        
        String currenSeg = nf.format(Long.parseLong(childreSeg_code)+1);
        System.out.println(currenSeg);
        System.out.println(UUID.nameUUIDFromBytes(("283"+"INNERDB"+"0").getBytes()));
		System.out.println(UUID.nameUUIDFromBytes(("283"+"INNERDB"+"0").getBytes()));
		System.out.println(UUID.nameUUIDFromBytes(("283"+"INNERDB"+"0").getBytes()));
		System.out.println(UUID.nameUUIDFromBytes(("283"+"INNERDB"+"0").getBytes()));
		
	}

}
