package com.qhjf.cfm.excel.analyze.validator.cell;

import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * excel单元格取值策略抽象类
 * @author CHT
 *
 */
public abstract class CellValueGetterStrategy {
	
	protected static final Logger logger = LoggerFactory.getLogger(CellValueGetterStrategy.class);
	
	protected final static String EXCEL2007_VERSION_TEXT = "EXCEL2007";
	protected final static String EXCEL97_VERSION_TEXT = "EXCEL97";
	protected final static String INVALID_FORMAT = "文档格式错误";
	protected final static String TRANSFER_ERROR = "数据传输错误";
	protected final static String EMPTY_DATA = "excel文档无数据";
	protected final static String INVALID_CONTENT = "文档内容不符合要求";
	protected final static String ENCRYPTED_DOCUMENT = "文档已经被加密";
	protected final static String FORMAT_ERROR_TIPS = "格式错误,请在%s列填写%s数据";
	protected final static String CELL_FORMAT_ERROR = "单元格类型无法处理";
	protected final static String EXCEL_PARSE_SUCCESS = "文档解析完成";
	protected final static String NOT_ALLOWED_CELL_FORMAT_TIPS = "\"%s\" 列格式错误,系统不能使用公式类型";
	protected final static String NOT_BLANK = "单元格不能为空";
	
	protected final static Map<String, String> TIPS_MAP = new HashMap<String, String>();
	
	protected final static String BOOLEAN_CLASSNAME = java.lang.Boolean.class.getName();
	protected final static String STRING_CLASSANME = java.lang.String.class.getName();
	protected final static String INTEGER_CLASSANME = java.lang.Integer.class.getName();
	protected final static String DOUBLE_CLASSANME = java.lang.Double.class.getName();
	protected final static String FLOAT_CLASSANME = java.lang.Float.class.getName();
	protected final static String SHORT_CLASSANME = java.lang.Short.class.getName();
//	protected final static String BYTE_CLASSANME = java.lang.Byte.class.getName();
//	protected final static String CHAR_CLASSANME = java.lang.Character.class.getName();
	protected final static String LONG_CLASSANME = java.lang.Long.class.getName();
	protected final static String DATE_CLASSANME = java.util.Date.class.getName();
	static {
		TIPS_MAP.put(STRING_CLASSANME, "文本类型");
		TIPS_MAP.put(BOOLEAN_CLASSNAME, "逻辑类型");
		TIPS_MAP.put(DATE_CLASSANME, "日期类型");
		TIPS_MAP.put(INTEGER_CLASSANME, "数字类型");
		TIPS_MAP.put(DOUBLE_CLASSANME, "浮点数类型");
		TIPS_MAP.put(FLOAT_CLASSANME, "浮点数类型");
		TIPS_MAP.put(LONG_CLASSANME, "数字类型");
	}

	public abstract Object getCellValue(Config item, Cell cell) throws CellValueUndesirableException;
	
}
