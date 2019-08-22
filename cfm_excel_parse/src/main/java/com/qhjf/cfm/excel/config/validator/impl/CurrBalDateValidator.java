package com.qhjf.cfm.excel.config.validator.impl;

import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.excel.util.PoiDateFomatUtil;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;
/**
 * 当日余额不能导入历史余额的数据
 * @author CHT
 *
 */
public class CurrBalDateValidator implements IValidator {
	private static final String format = "yyyy-MM-dd";
	private static final String text = "余额时间必须为当天";
	private static final String SPACE_DATE = "1899-12-31";
	private String errorMessage;
	@Override
	public boolean doValidat(Object data) {
		return true;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public boolean doValidat(Object data, Cell cell) {
		String cellValue = PoiDateFomatUtil.dateToStr(cell.getDateCellValue(), format);
		
		Date now = new Date();
		String nowStr = PoiDateFomatUtil.dateToStr(now, format);
		if (!SPACE_DATE.equals(cellValue) && !nowStr.equals(cellValue)) {
			errorMessage = text;
			return false;
		}
		return doValidat(data);
	}

}
