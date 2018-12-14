package com.qhjf.cfm.excel.config.parse.impl;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.qhjf.cfm.excel.config.CellConfig;
import com.qhjf.cfm.excel.config.parse.ConfigParseUtil;
import com.qhjf.cfm.excel.config.parse.ICellConfigParseUtil;
import com.qhjf.cfm.excel.exception.ExcelConfigParseException;
import com.qhjf.cfm.excel.util.KeyValueSplitUtil;

public class CellConfigParseUtil implements ICellConfigParseUtil {
	//个性配置数组：position单元格位置信息
	private static final String[] props = {"position"};
	
	@Override
	public CellConfig parse(String configStr) throws ExcelConfigParseException {
		CellConfig item = new CellConfig();
		parseLine(configStr, item);
		return item;
	}
	/**
	 * 样例：等号后面的内容
	 *[cellConfigs 单元格配置]=dataType:单元格数据类型, position:位置（A1）, required:是否必须, tips:提示信息, 
	 *		columnName:excel单元格标题,dbColumnName:对应数据库列, validator:校验器类路径(多个校验器以竖线分隔), convertor:转换器类路径, statistics:统计器类路径
	 * @throws ExcelConfigParseException 
	*/
	private void parseLine(String configStr, CellConfig item) throws ExcelConfigParseException{
		if (StringUtils.isBlank(configStr)) {
			throw new ExcelConfigParseException("Excel列配置信息为空");
		}
		Map<String, String> propsMap = KeyValueSplitUtil.splitToMap(configStr);
		//解析公共配置
		ConfigParseUtil.parseLineComm(propsMap, item);

		//解析个性配置
		Set<String> propsKeySet = propsMap.keySet();
		for (String prop : props) {
			for (String propsKey : propsKeySet) {
				if ("position".equals(propsKey)) {
					if (prop.toLowerCase().equals(propsKey.toLowerCase())) {
						item.setPosition(propsMap.get(propsKey));
						break;
					}
				}
			}
		}
	}
	
}
