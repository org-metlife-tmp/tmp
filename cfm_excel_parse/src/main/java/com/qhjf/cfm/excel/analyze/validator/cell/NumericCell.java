package com.qhjf.cfm.excel.analyze.validator.cell;

import com.qhjf.cfm.excel.config.Config;
import com.qhjf.cfm.excel.exception.CellValueUndesirableException;
import com.qhjf.cfm.excel.util.PoiDateFomatUtil;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;

public class NumericCell extends CellValueGetterStrategy {

	@Override
	public Object getCellValue(Config item, Cell cell) throws CellValueUndesirableException {
		Object cellValue = null;
		if (HSSFDateUtil.isCellDateFormatted(cell)) { // 日期适配
			if (!item.getColumnClass().getName().equals("java.util.Date")) {
				throw new CellValueUndesirableException(String.format(FORMAT_ERROR_TIPS, item.getColumnName(),
						TIPS_MAP.get(item.getColumnClass().getName())));
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("对单元格[%s]进行日期类型处，格式化为%s！", item.getColumnName(), item.getDateFormat()));
				}
				//提取日期数据时，统一转换为String
				cellValue = PoiDateFomatUtil.dateToStr(cell.getDateCellValue(), item.getDateFormat());
			}
		} else { // 数字适配
			Double doubleCellValue = cell.getNumericCellValue();
			if (item.getColumnClass().getName().equals(INTEGER_CLASSANME)) {
				cellValue = doubleCellValue.intValue();
			} else if (item.getColumnClass().getName().equals(LONG_CLASSANME)) {
				cellValue = doubleCellValue.longValue();
			} else if (item.getColumnClass().getName().equals(FLOAT_CLASSANME)) {
				cellValue = doubleCellValue.floatValue();
			} else if (item.getColumnClass().getName().equals(DOUBLE_CLASSANME)) {
				cellValue = doubleCellValue;
			} else if (item.getColumnClass().getName().equals(SHORT_CLASSANME)) {
				cellValue = doubleCellValue.shortValue();
			} else {
				cellValue = doubleCellValue;
			}
		}
		
		return cellValue;
	}

}
