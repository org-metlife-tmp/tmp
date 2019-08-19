package com.qhjf.cfm.excel.config.parse;

import com.qhjf.cfm.excel.config.ColumnConfig;
import com.qhjf.cfm.excel.exception.ExcelConfigParseException;
/**
 * excel配置文件，列配置解析工具
 */
public interface IColumnConfigParseUtil {

	/**
	 * 解析列配置
	 * @param configStr 列配置字符串
	 * @return
	 */
	public ColumnConfig parse(String configStr) throws ExcelConfigParseException;
}
