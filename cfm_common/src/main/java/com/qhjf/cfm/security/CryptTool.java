package com.qhjf.cfm.security;

import com.qhjf.cfm.exceptions.EncryAndDecryException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptTool {
	
	public static final String DEFAULTKEY = "h@xl(1cfM:_Bj%12!)UCfCfm"; //默认密钥
	
	public static byte[] encryptMode(byte[] src) throws EncryAndDecryException {
		
		return encryptMode(src,DEFAULTKEY);
	}
	
	public static byte[] encryptMode(byte[] src, String key) throws EncryAndDecryException {
		try {
			SecretKey deskey = new SecretKeySpec(build3DesKey(key), "DESede"); // 生成密钥
			Cipher cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			return cipher.doFinal(src);
		} catch (Exception e) {
			e.printStackTrace();
			throw new EncryAndDecryException("加密失败！",e);
		}
	}
	
	
	public static byte[] decrypttMode(byte[] src) throws EncryAndDecryException{
		return decrypttMode(src,DEFAULTKEY);
	}
	
	public static byte[] decrypttMode(byte[] src, String key) throws EncryAndDecryException{
		try {
			SecretKey deskey = new SecretKeySpec(build3DesKey(key), "DESede");
			Cipher cipher = Cipher.getInstance("DESede");

			cipher.init(Cipher.DECRYPT_MODE, deskey);
			return cipher.doFinal(src);
		}catch(Exception e){
			e.printStackTrace();
			throw new EncryAndDecryException("解密失败！",e);
		}
	}
	
	

	/**
	 * 根据参数生成密钥
	 * @param keyStr
	 * @return
	 * @throws Exception
	 */
	private static byte[] build3DesKey(String keyStr) throws Exception  {
		byte[] key = new byte[24]; // 声明一个24位的字节数组，默认里面都是0
		try
		{
			byte[] temp = keyStr.getBytes("UTF-8"); // 将字符串转成字节数组

			/*
			 * 执行数组拷贝 System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
			 */
			if (key.length > temp.length) {
				// 如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
				System.arraycopy(temp, 0, key, 0, temp.length);
			} else {
				// 如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
				System.arraycopy(temp, 0, key, 0, key.length);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("生成密钥失败！");
		}
		
		return key;
	}

}
