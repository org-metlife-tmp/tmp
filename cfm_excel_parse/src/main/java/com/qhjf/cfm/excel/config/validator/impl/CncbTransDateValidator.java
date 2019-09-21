package com.qhjf.cfm.excel.config.validator.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;

import com.qhjf.cfm.excel.config.validator.IValidator;

/**
 * 中信交易日期校验
 * @author CHT
 *
 */
public class CncbTransDateValidator implements IValidator {
	private static final String format = "yyyy-MM-dd";
	private static final String text = "日期格式错误(yyyy-MM-dd)";
	private String errorMessage;
	@Override
	public boolean doValidat(Object data) {
		String date = (String)data;
		try {
			new SimpleDateFormat(format).parse(date);
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		errorMessage = text;
		return false;
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
