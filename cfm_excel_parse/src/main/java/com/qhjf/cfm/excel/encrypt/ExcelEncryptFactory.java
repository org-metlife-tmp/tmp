package com.qhjf.cfm.excel.encrypt;

import com.qhjf.cfm.excel.adapter.POIWorkbookVersionAdapter;

/**
 * excel加密策略工厂
 * @author CHT
 *
 */
public class ExcelEncryptFactory {

	/**
	 * 构造excel加密策略
	 * @param clazz	excel的WorkSheet class
	 * @return
	 */
	public static ExcelEncryptStrategy createEncryptor(String clazz){
		ExcelEncryptStrategy strategy = null;
		if (POIWorkbookVersionAdapter.HSSF_WORK_BOOK_KEY.equals(clazz)) {
			strategy = new HSSFEncryptStrategy();
		}else if (POIWorkbookVersionAdapter.XSSF_WORK_BOOK_KEY.equals(clazz)) {
			strategy = new XSSFEncryptStrategy();
		}
		return strategy;
	}
}
