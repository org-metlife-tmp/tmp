package com.qhjf.cfm.excel.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EXCEL97加密策略
 * 
 * @author CHT
 *
 */
public class HSSFEncryptStrategy extends ExcelEncryptStrategy {

	private static final Logger logger = LoggerFactory.getLogger(HSSFEncryptStrategy.class);

	@Override
	public byte[] encryptExcel(InputStream is, String password) {
		byte[] result = null;
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Excel开始加密，密码：[%s]", password));
		}

		// 创 建一个工作薄
		HSSFWorkbook wb = null;
		ByteArrayOutputStream bos = null;
		try {
			wb = new HSSFWorkbook(is);
			// 设置密 码 保 护
			wb.writeProtectWorkbook(password, "");
			bos = new ByteArrayOutputStream();
			wb.write(bos);
			result = bos.toByteArray();
		} catch (Exception e) {
			logger.error("给Excell加密异常！", e);
		} finally {
			if (null != bos) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != wb) {
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

}
