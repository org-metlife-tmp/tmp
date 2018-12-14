package com.qhjf.cfm.excel.analyze.validator.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import com.qhjf.cfm.excel.exception.DisposeException;

/**
 * 获取excel单元格值的具体策略
 * @author CHT
 *
 */
public class CellValueGetterFactory {
	
	private final static String CELL_FORMAT_ERROR = "单元格类型无法处理";

	/**
	 * 获取excel单元格值得绝体策略
	 * @param cell	excel单元格对象
	 * @throws DisposeException 
	 */
	public static CellValueGetterStrategy createStrategy(Cell cell) throws DisposeException{
		CellValueGetterStrategy strategy = null;
		
		CellType cellTypeEnum = cell.getCellTypeEnum();
		switch (cellTypeEnum) {
		case STRING:
			strategy = new StringCell();
			break;
		case BOOLEAN:
			strategy = new BooleanCell();
			break;
		case NUMERIC:
			strategy = new NumericCell();
			break;
		case ERROR:
			strategy = new ErrorCell();
			break;
		case FORMULA:
			strategy = new FormulaCell();
			break;
		case BLANK:
			strategy = new BlankCell();
			break;
		default:
			break;
		}
		
		if (null == strategy) {
			throw new DisposeException(CELL_FORMAT_ERROR);
		}
		return strategy;
	}
}
