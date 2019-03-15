package com.qhjf.cfm.web.quartzs.jobs.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.plugin.activerecord.Db;

/**
 * 大都会安全工具类
 * @author CHT
 *
 */
public class DDHSafeUtil {
	private static Logger log = LoggerFactory.getLogger(DDHSafeUtil.class);

	/**
	 * 大都会解密
	 * @param source 密文
	 * @return 明文
	 */
	public static String decrypt(String source) {
		String result = null;
		try {
			result = Db.queryStr(Db.getSql("quartzs_job_cfm.ddhDecrypt"), source);
		} catch (Exception e) {
			log.error("大都会数据库解密[{}]异常！", source);
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 大都会加密
	 * @param source 明文
	 * @return 密文
	 */
	public static String encrypt(String source) {
		String result = null;
		try {
			result = Db.queryStr(Db.getSql("quartzs_job_cfm.ddhEncrypt"), source);
		} catch (Exception e) {
			log.error("大都会数据库加密[{}]异常！", source);
			e.printStackTrace();
		}
		return result;
	}
}
