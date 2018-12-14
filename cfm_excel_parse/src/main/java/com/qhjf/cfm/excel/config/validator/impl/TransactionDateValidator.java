package com.qhjf.cfm.excel.config.validator.impl;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;

import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.excel.util.PoiDateFomatUtil;

/**
 * 交易日期不能大于当前日期的前一天
 * @author CHT
 *
 */
public class TransactionDateValidator implements IValidator {
	private static final String format = "yyyy-MM-dd";
	private static final String text = "日期必须在当天之前";
	private static final String SPACE_DATE_MSG = "日期必须在当天之前";
	private static final String SPACE_DATE = "1899-12-31";//POI导入日期格式数据时，不输入年月日默认1899-12-31
	private String errorMessage;
	@Override
	public boolean doValidat(Object data) {
		String date = (String)data;
		
		Date now = new Date();
		String nowStr = PoiDateFomatUtil.dateToStr(now, format);
		if (date.compareTo(nowStr) < 0) {
			return true;
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
		if (SPACE_DATE.equals((String)data)) {
			errorMessage = SPACE_DATE_MSG;
			return false;
		}
		return doValidat(data);
	}
}
