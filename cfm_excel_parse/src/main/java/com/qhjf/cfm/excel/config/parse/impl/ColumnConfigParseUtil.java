package com.qhjf.cfm.excel.config.parse.impl;

import com.qhjf.cfm.excel.config.ColumnConfig;
import com.qhjf.cfm.excel.config.parse.ConfigParseUtil;
import com.qhjf.cfm.excel.config.parse.IColumnConfigParseUtil;
import com.qhjf.cfm.excel.exception.ExcelConfigParseException;
import com.qhjf.cfm.excel.util.KeyValueSplitUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * excel配置文件，默认列配置解析工具
 * 
 * @author CHT
 *
 */
public class ColumnConfigParseUtil implements IColumnConfigParseUtil {

	@Override
	public ColumnConfig parse(String configStr) throws ExcelConfigParseException {
		ColumnConfig item = new ColumnConfig();
		parseLine(configStr, item);
		return item;
	}
	/**
	 * 样例：等号后面的内容
	 *[columnConfigs 列配置]=dataType:列数据类型,required:是否必须, tips:提示信息, columnName:excel列标题,
	 *		dbColumnName:对应数据库列, validator:校验器类路径(多个校验器以竖线分隔), convertor:转换器类路径, statistics:统计器类路径
	 * @throws ExcelConfigParseException 
	*/
	private void parseLine(String configStr, ColumnConfig item) throws ExcelConfigParseException{
		if (StringUtils.isBlank(configStr)) {
			throw new ExcelConfigParseException("Excel列配置信息为空");
		}
		Map<String, String> propsMap = KeyValueSplitUtil.splitToMap(configStr);
		//解析公共配置
		ConfigParseUtil.parseLineComm(propsMap, item);
	}
	
}
