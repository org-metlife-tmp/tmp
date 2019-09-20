package com.qhjf.cfm.utils;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

public class SymmetricEncryptUtil {
	private static Logger log = LoggerFactory.getLogger(SymmetricEncryptUtil.class);
	private static DDHLAConfigSection section = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);

	private static final String encode = "utf-8";
	private static final String ALGORITHM = "AES";
	private Base64 base64 = new Base64();
	private static final String PREFIX = "dHk=<";
	private static final String SUFFIX = ">11111000101";
	private static final String password = PREFIX + section.getSecret() + SUFFIX;
	private static Cipher cipherEnc = null;
	private static Cipher cipherDec = null;
	private static final String errMsg = "[%s]:解密失败";
	private static final String DECR_ERR_MSG = "[%s]:解密失败：[%s]";
	private static final String ENCR_ERR_MSG = "[%s]:加密失败：[%s]";
	
	private static final String REGEX_G8 = "([A-Za-z0-9-]{4})([A-Za-z0-9-]{0,})([A-Za-z0-9-]{4})";
	private static final String REGEX_G4 = "([A-Za-z0-9-]{2})([A-Za-z0-9-]{0,})([A-Za-z0-9-]{2})";
	
	private SymmetricEncryptUtil(){}
	private static class SymmetricEncryptUtilInner{
		private static final SymmetricEncryptUtil instance = new SymmetricEncryptUtil();
	}
	
	public static SymmetricEncryptUtil getInstance(){
		return SymmetricEncryptUtilInner.instance;
	}
	
	static{
		KeyGenerator keyGenerator = null;
		SecureRandom random = null;
		try {
			keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			log.error("********************生成密钥失败，需要人工处理**********************");
		}
		random.setSeed(password.getBytes());
		keyGenerator.init(128, random);
		SecretKey key = keyGenerator.generateKey();
		byte[] encodeFormat = key.getEncoded();
		key = new SecretKeySpec(encodeFormat, ALGORITHM);
		
		try {
			cipherEnc = Cipher.getInstance(ALGORITHM);
			cipherDec = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e2) {
			e2.printStackTrace();
			log.error("********************Cipher.getInstance异常，需要人工处理**********");
		} catch (NoSuchPaddingException e3) {
			e3.printStackTrace();
			log.error("********************Cipher.getInstance异常，需要人工处理**********");
		}
		
		try {
			cipherEnc.init(Cipher.ENCRYPT_MODE, key);
			cipherDec.init(Cipher.DECRYPT_MODE, key);
		} catch (InvalidKeyException e4) {
			e4.printStackTrace();
			log.error("********************cipherEnc.init异常，需要人工处理**********");
		}
	}
	
	
	/**
	 * 对称加密
	 * @param content
	 * @return
	 * @throws BusinessException
	 */
	public String encrypt(String content) throws EncryAndDecryException {
		System.out.println("初始化加密=====密码=  "+password);
		if (content != null && !"".equals(content)) {
			byte[] result = new byte[0];
			try {
				result = cipherEnc.doFinal(content.getBytes());
			} catch (Exception e) {
				throw new EncryAndDecryException(String.format(ENCR_ERR_MSG, content, e.getMessage()));
			}
			String base64Result = base64.encodeToString(result);
			return base64Result.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
		} else {
			return content;
		}
	}
	/**
	 * 对称解密
	 * @param content 密文
	 * @return 明文byte数组
	 * @throws BusinessException
	 */
	public byte[] decrypt(String content) throws EncryAndDecryException {
		System.out.println("解密=====密码=  "+password);
		byte[] result = null;
		if (content != null && !"".equals(content)) {
			result = new byte[0];
			try {
				result = cipherDec.doFinal(base64.decode(content.getBytes()));
			} catch (Exception e) {
				throw new EncryAndDecryException(String.format(DECR_ERR_MSG, content, e.getMessage()));
			}
		}
		return result;
	}
	/**
	 * 对称解密
	 * @param content 密文
	 * @param encode 编码
	 * @return 明文
	 * @throws BusinessException
	 */
	public String decryptToStr(String content, String encode) throws EncryAndDecryException {
		System.out.println("解密=====密码=  "+password);
		String result = null;
		byte[] decrypt = decrypt(content);
		if (null != decrypt) {
			try {
				result = new String(decrypt, encode);
			} catch (UnsupportedEncodingException e) {
				log.error("解密[{}]时，字节数组解码为[{}]失败", content, encode);
				throw new EncryAndDecryException(String.format(DECR_ERR_MSG, content, e.getMessage()));
			}
		}else {
			log.error("解密[{}]时，字节数组为null", content);
			throw new EncryAndDecryException(String.format(errMsg, content));
		}
		return result;
	}
	/**
	 * 对称解密，默认以utf-8解码
	 * @param content 密文
	 * @return
	 * @throws BusinessException
	 */
	public String decryptToStr(String content) throws EncryAndDecryException {
		System.out.println("解密=====密码=  "+password);
		return decryptToStr(content, encode);
	}
	
	/**
	 * 银行账号明文加掩码
	 * @param accNo
	 * @return
	 */
	public static String accNoAddMask(String accNo){
		System.out.println("银行账号明文加掩码=====密码=  "+password);
		if (null == accNo) {
			return null;
		}
		if (accNo.length() > 8) {
			return accNo.replaceAll(REGEX_G8, "$1********$3");
		}else if(accNo.length() > 4) {
			return accNo.replaceAll(REGEX_G4, "$1********$3");
		}else {
			return accNo.concat("********");
		}
	}
	
	/**
	 * 
	 * @param lists
	 * @return
	 * @throws BusinessException
	 * @throws UnsupportedEncodingException 
	 */
	public void recvmask(List<Record> lists) throws BusinessException, UnsupportedEncodingException {
		for (int i = 0; i < lists.size(); i++) {
			String recv_acc_no = lists.get(i).getStr("recv_acc_no");
			if(StringUtils.isNoneBlank(recv_acc_no)) {
				byte[] decrypt = decrypt(recv_acc_no);
				String dec_recv_acc_no = new String(decrypt, "utf-8");
				//待匹配列表掩码转明文
				/*dec_recv_acc_no = dec_recv_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
						.replaceAll("\\*+", "********");*/
				lists.get(i).set("recv_acc_no", dec_recv_acc_no);
				lists.get(i).set("recv_account_no", dec_recv_acc_no);
			}
			
			String consumer_acc_no = lists.get(i).getStr("consumer_acc_no");
			if(StringUtils.isNoneBlank(consumer_acc_no)) {
				byte[] decrypt = decrypt(consumer_acc_no);
				String dec_consumer_acc_no = new String(decrypt, "utf-8");
				//待匹配列表掩码转明文
				/*dec_consumer_acc_no = dec_consumer_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
						.replaceAll("\\*+", "********");*/
				lists.get(i).set("consumer_acc_no", dec_consumer_acc_no);
			}
		}		
	}

	/**
	 *
	 * @param lists
	 * @return
	 * @throws BusinessException
	 * @throws UnsupportedEncodingException
	 */
	public void recvmaskforPage(Page<Record> lists) throws BusinessException, UnsupportedEncodingException {
		for (int i = 0; i < lists.getList().size(); i++) {
			String recv_acc_no = lists.getList().get(i).getStr("recv_acc_no");
			if(StringUtils.isNoneBlank(recv_acc_no)) {
				byte[] decrypt = decrypt(recv_acc_no);
				String dec_recv_acc_no = new String(decrypt, "utf-8");
				//待匹配列表掩码转明文
         /*dec_recv_acc_no = dec_recv_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
               .replaceAll("\\*+", "********");*/
				lists.getList().get(i).set("recv_acc_no", dec_recv_acc_no);
				lists.getList().get(i).set("recv_account_no", dec_recv_acc_no);
			}

			String consumer_acc_no = lists.getList().get(i).getStr("consumer_acc_no");
			if(StringUtils.isNoneBlank(consumer_acc_no)) {
				byte[] decrypt = decrypt(consumer_acc_no);
				String dec_consumer_acc_no = new String(decrypt, "utf-8");
				//待匹配列表掩码转明文
         /*dec_consumer_acc_no = dec_consumer_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
               .replaceAll("\\*+", "********");*/
				lists.getList().get(i).set("consumer_acc_no", dec_consumer_acc_no);
			}
		}
	}

	/**
	 * 
	 * @param lists
	 * @return
	 * @throws BusinessException
	 * @throws UnsupportedEncodingException 
	 */
	public void recvmaskforSingle(Record rec) throws BusinessException, UnsupportedEncodingException {
			String recv_acc_no = rec.getStr("recv_acc_no");
			if(StringUtils.isNoneBlank(recv_acc_no)) {
				byte[] decrypt = decrypt(recv_acc_no);
				String dec_recv_acc_no = new String(decrypt, "utf-8");
				/*dec_recv_acc_no = dec_recv_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
						.replaceAll("\\*+", "********");*/
				rec.set("recv_acc_no", dec_recv_acc_no);
				rec.set("recv_account_no", dec_recv_acc_no);
			}
			String consumer_acc_no = rec.getStr("consumer_acc_no");
			if(StringUtils.isNoneBlank(consumer_acc_no)) {
				byte[] decrypt = decrypt(consumer_acc_no);
				String dec_consumer_acc_no = new String(decrypt, "utf-8");
				/*dec_consumer_acc_no = dec_consumer_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
						.replaceAll("\\*+", "********");*/
				rec.set("consumer_acc_no", dec_consumer_acc_no);
			}
			String match_recv_acc_no = rec.getStr("match_recv_acc_no");
			if(StringUtils.isNoneBlank(match_recv_acc_no)) {
				byte[] decrypt = decrypt(match_recv_acc_no);
				String dec_match_recv_acc_no = new String(decrypt, "utf-8");
				/*dec_match_recv_acc_no = dec_match_recv_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
						.replaceAll("\\*+", "********");*/
				rec.set("match_recv_acc_no", dec_match_recv_acc_no);
			}
	}
	
	/**
	 * 
	 * @param lists
	 * @return
	 * @throws BusinessException
	 * @throws UnsupportedEncodingException 
	 */
	public void paymask(List<Record> lists) throws BusinessException, UnsupportedEncodingException {
		for (int i = 0; i < lists.size(); i++) {
			String pay_acc_no = lists.get(i).getStr("pay_acc_no");
			if(StringUtils.isNotBlank(pay_acc_no)) {
				byte[] decrypt = decrypt(pay_acc_no);
				String dec_recv_acc_no = new String(decrypt, "utf-8");
				dec_recv_acc_no = dec_recv_acc_no.replaceAll("(?<=\\d{4})\\d(?=\\d{4})", "*")
						.replaceAll("\\*+", "********");
				lists.get(i).set("pay_acc_no", dec_recv_acc_no);				
			}
		}		
	}
	
	public static void main(String[] args) throws Exception {
		/*String plain = "This is my Test text";
		SymmetricEncryptUtil util = SymmetricEncryptUtil.getInstance();
		
		System.out.println("明文："+plain);
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 20000; i++) {
			util.encrypt(plain);
		}
		System.out.println("加密时间："+ (System.currentTimeMillis() - t1));
		
		String encry = util.encrypt(plain);
		System.out.println("加密后密文："+encry);
		
		long t2 = System.currentTimeMillis();
		for (int i = 0; i < 20000; i++) {
			util.decrypt(encry);
		}
		System.out.println("解密时间："+ (System.currentTimeMillis() - t2));
		
		byte[] decry = util.decrypt(encry);
		System.out.println("解密后明文："+new String(decry, encode));*/
		
		String [] accList = {"21345","213456754321","345678765432","dfsfdh34565uytr","34567-9875hff3","-43524yfk","fdsfh56tgf0-09",""};
		for (String s : accList) {
			System.out.println(s + "=" +accNoAddMask(s));
		}
	}

}