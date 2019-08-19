package com.qhjf.cfm.security;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 同IOS交互加密key
 * @author admin
 *
 */
public class OneTimekey {
	

	private  String getKey(){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		format.setTimeZone(TimeZone.getTimeZone("GMT-0:00"));
		String data = format.format(new Date());
		return data+"CFM";
	}
	private  String byteHEX(byte ib) {  
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',  
                'b', 'c', 'd', 'e', 'f' };  
        char[] ob = new char[2];  
        ob[0] = Digit[(ib >>> 4) & 0X0F];  
        ob[1] = Digit[ib & 0X0F];  
        String s = new String(ob);  
        return s;  
    }  
	
	public String encryptId(String sessionid){
		StringBuffer encry_key = new StringBuffer();
		byte[] source = sessionid.getBytes();
		byte[] result = new byte[source.length];
		byte[] key= getKey().getBytes();
		int klen=key.length;
		for(int i = 0 ; i<source.length; i++){
			int x = source[i] - key[i%klen]+48;
			int jsw = x&0xAA;
			int osw = x&0x55;
			jsw=jsw>>>1;
			osw=osw<<1;
			x=jsw|osw;
			result[i]=(byte) x;
		}
		if(result != null){
			for(byte b : result){
				encry_key.append(byteHEX(b));
			}
		}
		
		return encry_key.toString();
	}

	public static void main(String[] args) {
		String sessionId = "848257EB9308E471F68B29F878A26C9A";
		
		
		OneTimekey key = new OneTimekey();
		System.out.println(key.getKey());
		String encrypt = key.encryptId(sessionId);
		System.out.println(encrypt);
	}

}
