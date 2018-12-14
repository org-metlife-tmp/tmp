package com.qhjf.cfm.excel.encrypt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Excel加密策略
 * @author CHT
 *
 */
public abstract class ExcelEncryptStrategy {
	private static final Logger logger = LoggerFactory.getLogger(ExcelEncryptStrategy.class);
	/**
	 * 加密
	 * @param is excel输入流
	 * @param password excel打开密码
	 * @return
	 */
	public abstract byte[] encryptExcel(InputStream is, String password);
	/**
	 * 加密
	 * @param content Excel字节数组
	 * @param password	excel打开密码
	 * @return
	 */
	public byte[] encryptExcel(byte[] content, String password){
		byte[] rs = null;
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(content);
			rs = encryptExcel(is, password);
		} catch (Exception e) {
			logger.error("未知异常", e);
		} finally{
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return rs;
	}
}
