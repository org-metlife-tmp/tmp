package com.qhjf.cfm.excel.analyze.validator.cell;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;

/**
 * String类型的Excel单元格
 * @author CHT
 *
 */
public class StringCell extends CellValueGetterStrategy {

	@Override
	public Object getCellValue(Config item, Cell cell) throws CellValueUndesirableException {
		String data = cell.getStringCellValue().trim();
		//必须项判断
		if (item.isRequired() && StringUtils.isBlank(data)) {
			throw new CellValueUndesirableException(NOT_BLANK);
		}
		return data;
	}

}
