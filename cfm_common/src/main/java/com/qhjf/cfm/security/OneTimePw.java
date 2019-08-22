package com.qhjf.cfm.security;


/**
 * 基于ASCII加密算法的解密，加密方法称为「维吉尼亚密码」（Vigenere cipher）。严格说属于内存级的数据变形 是凯撒密码的变种  －－黄牁轩
 * @author huangkexuan
 *
 */
public class OneTimePw {

	private static  String byteHEX(byte ib) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',  
                'b', 'c', 'd', 'e', 'f' };  
        char[] ob = new char[2];  
        ob[0] = Digit[(ib >>> 4) & 0X0F];  
        ob[1] = Digit[ib & 0X0F];  
        String s = new String(ob); 
        System.out.println("转码："+s);
        return s;  
    }  
	
	private byte offsetForward(int cryptogram)
	{
	    int ret = cryptogram;
	    if(cryptogram < 0)
	    { 
	        ret =  32 + (128 + cryptogram)%94+2;
	        
	    }else if(cryptogram <= 32)
	    {
	        ret =  32 + (32 - cryptogram)%94+2;
	    }else if(cryptogram > 127)
	    {
	        ret =  32 + (cryptogram-127)%94+1;
	    }
	    else if(cryptogram == 127)
	    {
	        ret =  32+1;
	    }
	    byte[] barray=intToBytes(ret);
	    
	    return barray[3];
	}
	
	public static byte[] intToBytes(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) ((value >> 24) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}
	
	
	public String decryptOTP(String encrypt)throws Exception
	{

	  byte[] lenByte = encrypt.substring(0, 1).getBytes();
	  int len = lenByte[0] - 0X20;
	  String keyStr = encrypt.substring(0, len);
	  String encryptStr = encrypt.substring(len);
	  String retStr = decrypt(encryptStr,keyStr);
	  System.out.println(" 解码后："+retStr);
	  return retStr;
	  
	}
	
	private String decrypt(String encrypt,String keyStr) throws Exception{
		System.out.println(encrypt);
		System.out.println(keyStr);
		StringBuffer encry_key = new StringBuffer();
		byte[] source = encrypt.getBytes();
		byte[] result = new byte[source.length];
		byte[] key    = keyStr.getBytes();
		int klen=key.length;

		
		for(int i = 0 ; i<source.length; i++){
			System.out.print("解码前: "+source[i]);
			int x = source[i] +(key[i%klen]-96);
			System.out.print(" 中途X： "+x);
			result[i] = offsetForward(x);
			System.out.println(" 解码后："+result[i]);
		}
		if(result != null){
			for(byte b : result){
				encry_key.append(byteHEX(b));
			}
		}
		String encry_keyStr = new String(result,"UTF8");
		
		return encry_keyStr;
	}
	
	public static void main(String[] args) throws Exception{
		String sessionId= "-]qlFPpSNrLaQk7'&OG5OK!D7Tg: :PH2?K4L6Gt5%7SQ";
		
		OneTimePw key = new OneTimePw();
		String encrypt = key.decryptOTP(sessionId);
		System.out.println(encrypt);
	}
}
