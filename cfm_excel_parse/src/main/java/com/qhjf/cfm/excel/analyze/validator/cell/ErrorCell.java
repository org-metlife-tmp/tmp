package com.qhjf.cfm.excel.analyze.validator.cell;

import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;
import org.apache.poi.ss.usermodel.Cell;

public class ErrorCell extends CellValueGetterStrategy {

	@Override
	public Object getCellValue(Config item, Cell cell) throws CellValueUndesirableException {
		Object cellValue = null;
		if (item.isRequired()) {
			throw new CellValueUndesirableException(CELL_FORMAT_ERROR);
		} else {
			cellValue = null;
		}
		return cellValue;
	}

}
