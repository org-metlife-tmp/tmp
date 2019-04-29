package com.qhjf.cfm.excel.config.validator.impl;

import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.excel.util.PoiDateFomatUtil;
import org.apache.poi.ss.usermodel.Cell;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 中行交易日期校验
 * @author CHT
 *
 */
public class BocTransDateValidator implements IValidator {
	private static final String format = "yyyyMMdd";
	private static final String text = "日期格式错误(yyyyMMdd)";
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
