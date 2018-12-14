package com.qhjf.cfm.excel.analyze.validator.cell;

import org.apache.poi.ss.usermodel.Cell;

import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;

/**
 * boolean类型的单元格
 * @author CHT
 *
 */
public class BooleanCell extends CellValueGetterStrategy {

	@Override
	public Object getCellValue(Config item, Cell cell) throws CellValueUndesirableException {
		boolean result = false;
		if (!item.getColumnClass().getName().equals("java.lang.Boolean")) {
			throw new CellValueUndesirableException(String.format(FORMAT_ERROR_TIPS, item.getColumnName(),
					TIPS_MAP.get(item.getColumnClass().getName())));
		} else {
			result = cell.getBooleanCellValue();
		}
		return result;
	}

}
