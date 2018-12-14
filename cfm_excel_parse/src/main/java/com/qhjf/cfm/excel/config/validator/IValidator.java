package com.qhjf.cfm.excel.config.validator;

import org.apache.poi.ss.usermodel.Cell;

/**
 * 校验器
 * Excell单元格校验器接口
 */
public interface IValidator {
	/**
	 * 校验单元格数据
	 * @param data 待校验数据
	 * @return 
	 */
	public boolean doValidat(Object data);
	public boolean doValidat(Object data, Cell cell);
	/**
	 * 返回错误信息
	 * @return
	 */
	public String getErrorMessage();
}