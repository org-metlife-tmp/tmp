package com.qhjf.cfm.excel.config.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.config.convertor.IConvertor;
import com.qhjf.cfm.excel.config.statistic.IStatistics;
import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.excel.exception.ExcelConfigParseException;

/**
 * 单元格/列  公共配置解析
 * @author CHT
 *
 */
public class ConfigParseUtil {
	//Excel单元格的数据类型字典
	private static final Map<String, String> ADAPTER_TABLE = new HashMap<>();
	//bool类型转换字典
	private static final Map<String, Boolean> STRING_BOOL_TABLE = new HashMap<>();
	//无配置项
	private static final String NONE_KEY = "none";
	
	static {
		ADAPTER_TABLE.put("int", "java.lang.Integer");
		ADAPTER_TABLE.put("integer", "java.lang.Integer");

		ADAPTER_TABLE.put("bool", "java.lang.Boolean");
		ADAPTER_TABLE.put("boolean", "java.lang.Boolean");

		ADAPTER_TABLE.put("long", "java.lang.Long");

		ADAPTER_TABLE.put("float", "java.lang.Float");

		ADAPTER_TABLE.put("double", "java.lang.Double");

		ADAPTER_TABLE.put("byte", "java.lang.Byte");

		ADAPTER_TABLE.put("short", "java.lang.Short");

		ADAPTER_TABLE.put("char", "java.lang.Character");
		ADAPTER_TABLE.put("character", "java.lang.Character");

		ADAPTER_TABLE.put("string", "java.lang.String");

		ADAPTER_TABLE.put("date", "java.util.Date");
		
		STRING_BOOL_TABLE.put("1", true);
		STRING_BOOL_TABLE.put("0", false);
	}
	/**
	 * dataType:单元格数据类型, required:是否必须, tips:提示信息, columnName:excel单元格标题, dbColumnName:对应数据库列, 
	 * 			validator:校验器类路径(多个校验器以竖线分隔), convertor:转换器类路径, statistics:统计器类路径
	 * @param propsMap
	 * @param item
	 * @throws ExcelConfigParseException
	 */
	public static void parseLineComm(Map<String, String> propsMap, Config item) throws ExcelConfigParseException {
		
		Set<String> propsKeySet = propsMap.keySet();
		String propValue = "";
		for (String propKey : propsKeySet) {
			propValue = propsMap.get(propKey);
			//dataType数据类型
			if ("datatype".equals(propKey.toLowerCase())) {
				propValue = propValue.toLowerCase();
				//判断Excel数据类型是否存在
				if (!ADAPTER_TABLE.containsKey(propValue)) {
					throw new ExcelConfigParseException(String.format("数据类型【%s】无效", propValue));
				}
				Class<?> clazz = null;
				try {
					clazz = Class.forName(ADAPTER_TABLE.get(propValue));
				} catch (ClassNotFoundException e) {
					throw new ExcelConfigParseException(String.format("数据类型类【%s】没找到", propValue));
				}
				item.setColumnClass(clazz);
				
				if ("date".equals(propValue)) {
					String format = propsMap.get("dateFormat");
					if (!StringUtils.isBlank(format)) {
						item.setDateFormat(format.replaceAll("_", ":"));
					}else {
						throw new ExcelConfigParseException(String.format("日期类型的列或单元格需要配置日期格式：【dateFormat】"));
					}
				}
				continue;
			}
			//required是否必须
			if ("required".equals(propKey.toLowerCase())) {
				Boolean required = false;
				required = STRING_BOOL_TABLE.get(propValue);
				if (null == required) {
					required = new Boolean(propValue);
				}
				item.setRequired(required);
				continue;
			}
			//tips提示信息
			if ("tips".equals(propKey.toLowerCase())) {
				item.setTips(propValue);
				continue;
			}
			//columnName	Excel列名
			if ("columnname".equals(propKey.toLowerCase())) {
				item.setColumnName(propValue);
				continue;
			}
			//dbColumnName Excel列对应的数据库列名
			if ("dbcolumnname".equals(propKey.toLowerCase())) {
				item.setDbColumnName(propValue);
				continue;
			}
			//validator 校验器，多个校验器以竖线分隔
			if ("validator".equals(propKey.toLowerCase())) {
				if(NONE_KEY.equals(propValue)){
					continue;
				}
				List<IValidator> validatorList = new ArrayList<>();
				String[] validators = propValue.trim().split("\\|");
				for (String validator : validators) {
					if (StringUtils.isBlank(validator)) {
						continue;
					}
					IValidator vldt = (IValidator) getInstance(validator, "校验器");
					validatorList.add(vldt);
				}
				item.setValidatorList(validatorList);
				continue;
			}
			//convertor 转换器
			if ("convertor".equals(propKey.toLowerCase())) {
				if(NONE_KEY.equals(propValue)){
					continue;
				}
				IConvertor c = (IConvertor) getInstance(propValue, "转换器");
				item.setConvertor(c);
				continue;
			}
			//statistics 统计器
			if ("statistics".equals(propKey.toLowerCase())) {
				if(NONE_KEY.equals(propValue)){
					continue;
				}
				IStatistics s = (IStatistics) getInstance(propValue, "转换器");
				item.setStatistics(s);
				continue;
			}
		}
	}
	/**
	 * 获取类实例
	 * @param classPath	类路径
	 * @param type	校验器/转换器/统计器
	 * @return
	 * @throws ExcelConfigParseException
	 */
	public static Object getInstance(String classPath, String type) throws ExcelConfigParseException{
		Class<?> clazz = null;
		try {
			clazz = Class.forName(classPath);
		} catch (ClassNotFoundException e) {
			throw new ExcelConfigParseException(String.format("%s类【%s】没找到", type, classPath));
		}
		Object obj = null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			throw new ExcelConfigParseException(String.format("创建%s对象【%s】非法，异常信息【%s】", type, classPath, e.getMessage()));
		}
		return obj;
	}
}
