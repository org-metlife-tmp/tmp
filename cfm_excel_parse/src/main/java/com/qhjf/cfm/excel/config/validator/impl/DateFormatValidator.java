package com.qhjf.cfm.excel.config.validator.impl;

import org.apache.poi.ss.usermodel.Cell;

import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.utils.RegexUtils;

/**
 * 日期格式校验器，接收字符串格式的时间，格式如下：
 * <p>
 * yyyy-MM-dd 或者
 * HH:mm:ss 或者
 * yyyy-MM-dd HH:mm:ss可以通过校验
 * </p>
 * @author CHT
 *
 */
public class DateFormatValidator implements IValidator {

	private String errorMessage;
	@Override
	public boolean doValidat(Object data) {
		String str = data.toString();
		if (RegexUtils.isDate(str) || RegexUtils.isTime(str) || RegexUtils.isDateTime(str)) {
			return true;
		}
		this.errorMessage = String.format("单元格数据【%s】需要满足格式：‘年-月-日’或者‘时:分:秒’或者‘年-月-日  时:分:秒’，且为有效时间", str);
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
