package com.qhjf.cfm.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Kit {

	/***
	 * MD5加码 生成32位md5码
	 */
	public static String string2MD5(String inStr) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			return "";
		}
		char[] charArray = inStr.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	public static String byteArrayToMD5(byte[] target) {
		String result = "";
		int hex = 16;
		String DIGEST_KEY = "MD5";
		MessageDigest digestor = null;
		try {
			digestor = MessageDigest.getInstance(DIGEST_KEY);
			digestor.update(target);
			BigInteger encryptTarget = new BigInteger(1, digestor.digest());
			result = StringKit.flushZeroLeft4MD5(encryptTarget.toString(hex));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static String encryptPwd(String pwdPlain,String salt,int pcount){
		String cipher = string2MD5(pwdPlain);
		if(pcount == 1){
			return cipher;
		}
		cipher = cipher + salt;
		for(int i = 1;i<pcount;i++){
			cipher = string2MD5(cipher);
		}
		return cipher;
	}

	public static void main(String[] arg){
		System.out.println(MD5Kit.string2MD5("ASDF1234"));
	}

	
}
