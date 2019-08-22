package com.qhjf.cfm.excel.config.parse;

import com.qhjf.cfm.excel.config.CellConfig;
import com.qhjf.cfm.excel.exception.ExcelConfigParseException;

/**
 * excel配置文件，单元格配置解析工具
 * @author CHT
 *
 */
public interface ICellConfigParseUtil {

	/**
	 * 解析列配置
	 * @param configStr 单元格配置字符串
	 * @return
	 */
	public CellConfig parse(String configStr) throws ExcelConfigParseException;
}
