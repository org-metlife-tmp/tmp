package com.qhjf.cfm.excel.analyze.validator.cell;

import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;
import org.apache.poi.ss.usermodel.Cell;

public class BlankCell extends CellValueGetterStrategy {

	@Override
	public Object getCellValue(Config item, Cell cell) throws CellValueUndesirableException {
		Object cellValue = null;
		if (item.isRequired()) {
			throw new CellValueUndesirableException(String.format(FORMAT_ERROR_TIPS, item.getColumnName(),
					TIPS_MAP.get(item.getColumnClass().getName())));
		} else {
			cellValue = null;
		}
		return cellValue;
	}

}
