package com.qhjf.cfm.excel.config.validator.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;

import com.qhjf.cfm.excel.config.validator.IValidator;

/**
 * 建行交易时间校验
 * @author CHT
 *
 */
public class CbcTransTimeValidator implements IValidator {
	private static final String format = "yyyyMMdd HH:mm:ss";
	private static final String text = "日期格式错误(yyyyMMdd HH:mm:ss)";
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
