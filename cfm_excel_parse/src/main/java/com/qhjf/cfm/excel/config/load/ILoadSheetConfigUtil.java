package com.qhjf.cfm.excel.config.load;

import com.qhjf.cfm.excel.config.parse.ICellConfigParseUtil;
import com.qhjf.cfm.excel.config.parse.IColumnConfigParseUtil;

/**
 * Excel配置文件加载工具
 * @author CHT
 *
 */
public interface ILoadSheetConfigUtil {
	/**
	 * 加载excel配置文件
	 * @param rootClassPath	excel配置文件夹根路径	
	 * @param columnParser	列配置解析工具
	 * @param cellParser	单元格配置解析工具
	 */
    public void loadconfig(String rootClassPath, IColumnConfigParseUtil columnParser, ICellConfigParseUtil cellParser);
}