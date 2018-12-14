package com.qhjf.cfm.excel.analyze.validator.cell;

import org.apache.poi.ss.usermodel.Cell;
import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;

public class FormulaCell extends CellValueGetterStrategy {

	@Override
	public Object getCellValue(Config item, Cell cell) throws CellValueUndesirableException {
		String formulaCellName = item.getColumnName();
		throw new CellValueUndesirableException(String.format(NOT_ALLOWED_CELL_FORMAT_TIPS, formulaCellName));
	}

}
