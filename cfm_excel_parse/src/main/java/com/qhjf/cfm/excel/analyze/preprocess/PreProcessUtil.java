package com.qhjf.cfm.excel.analyze.preprocess;

import static org.apache.poi.ss.usermodel.CellType.STRING;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.qhjf.cfm.excel.config.ExcelSheetConfig;

/**
 * 解析Excel之前，预处理工具
 * @author CHT
 *
 */
public class PreProcessUtil {

	/**
	 * 数据导入之前的清理步骤 首先把全部的null的cell补充为blank单元格 去掉所有的批注信息,并作样式清除操作
	 */
	public static final PreProcessLog proImport(ExcelSheetConfig config, Sheet sheet) {
		PreProcessLog result = new PreProcessLog();
		int colNumber = config.getStartColumn();// 开始列
		int rowNumber = config.getStartRow();// 开始行
		int sheetLastRowNum = sheet.getLastRowNum();// Excel的sheet页最后有数据的行号
		int sheetLastColNum = config.getColumnConfigs().size();// Excel的sheet页最后有数据的列号
		// 行遍历
		for (int rowIndex = rowNumber; rowIndex <= sheetLastRowNum; rowIndex++) {
			Row row = sheet.getRow(rowIndex);// 获取行
			if (null != row) {
				// 列遍历
				for (int colIndex = colNumber, itemIndex = 0; itemIndex < sheetLastColNum; itemIndex++, colIndex++) {// itemIndex控制结束循环
					Cell cell = row.getCell(colIndex);
					if (cell == null) { // 设置cell为blank类型
						cell = row.createCell(colIndex, STRING);
						cell.setCellValue(" ");
						result.blankCellIncrassation();// 清空计数
					} else {
						// 批注信息不为空，清空批注
						if (cell.getCellComment() != null) {
							result.cellCommentIncrassation();// 去批注计数
							cell.setCellComment(null);
						}
					}
					result.cellResetIncrassation();
				}
			}
		}
		return result;
	}
}
