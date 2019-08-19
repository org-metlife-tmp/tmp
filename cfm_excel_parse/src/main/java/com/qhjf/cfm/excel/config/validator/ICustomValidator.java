package com.qhjf.cfm.excel.config.validator;

/**
 * 自定义校验器，可以同时作用与单独单元格与列
 * @author CHT
 *
 */
public abstract class ICustomValidator implements IValidator {
	/**
	 * Excel单元格位置，例：A1，B3
	 */
	public String position;
	/**
	 * 行列规律分布的Excel部分，的第几列
	 */
	public Integer column;
	
	
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Integer getColumn() {
		return column;
	}
	public void setColumn(Integer column) {
		this.column = column;
	}
	
}
