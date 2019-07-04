package com.qhjf.cfm.web.channel.util;

import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;

/**
 * 银行账号工具类
 * @author CHT
 *
 */
public class AccountUtil {

	/**
	 * 解密
	 * @param accNo
	 * @return
	 */
  	public static String decryptAccNo(String accNo){
  		String recvAccountNo = null;
  		try {
  			recvAccountNo = SymmetricEncryptUtil.getInstance().decryptToStr(accNo);
  		} catch (EncryAndDecryException e) {
  			e.printStackTrace();
  		}
  		return recvAccountNo;
  	}
}
