package com.qhjf.cfm.excel.config;

import com.qhjf.cfm.excel.config.validator.IValidator;

import java.io.Serializable;

/**
 * Excel单元格配置
 */
public class CellConfig extends Config implements Serializable {
	private static final long serialVersionUID = -2291095253339020486L;
	//单元格位置
	private String position;
	
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ColumnItem:[\n");
		result.append(String.format("\t position:%s\n", position));
		result.append(String.format("\t required:%s\n", required));
		result.append(String.format("\t columnName:%s\n", columnName));
		result.append(String.format("\t columnClass:%s\n", columnClass));
		result.append(String.format("\t tips:%s\n", tips));
		if (null != validatorList) {
			for (IValidator iValidator : validatorList) {
				result.append(String.format("\t validator:%s\n", iValidator.getClass().getName()));
			}
		}
		result.append(String.format("\t convertor:%s\n", convertor.getClass().getName()));
		result.append(String.format("\t statistics:%s]\n", statistics.getClass().getName()));
		return result.toString();
	}
}
