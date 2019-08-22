package com.qhjf.cfm.excel.config.validator.impl;

import com.qhjf.cfm.excel.config.validator.IValidator;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 收支方向校验器
 * 收付方向1：付、2：收 
 * @author CHT
 *
 */
public class DirectionValidator implements IValidator {
	private String errorMessage;
	
	@Override
	public boolean doValidat(Object data) {
		Integer direction = null;
		if (data instanceof Double) {
			Double dataD = (Double)data;
			direction = dataD.intValue();
		}else if(data instanceof Integer){
			direction = (Integer)data;
		}else {
			direction = Integer.parseInt(data.toString());
		}
		
		if (direction != 1 && direction != 2) {
			this.errorMessage = String.format("收付方向【%s】不存在(1：付、2：收 )", data);
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	@Override
	public boolean doValidat(Object data, Cell cell) {
		return doValidat(data);
	}
}
