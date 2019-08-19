package com.qhjf.cfm.excel.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * excel解析结果bean
 * @author CHT
 *
 */
public class ExcelResultBean implements Serializable {
	private static final long serialVersionUID = 7139989510460398756L;

	public ExcelResultBean(int excelDataType, List<Map<String, Object>> rowData, Map<String, Object> cellData){
		this.excelDataType = excelDataType;
		this.rowData = rowData;
		this.cellData = cellData;
		this.createTime = new Date().getTime();
	}
	
	/**
	 * 数据生成时间
	 * 用以控制存放内存的有效期，超过默认存放内存的毫秒数，就从内存中移除
	 */
	private long createTime;
	/**
	 * excel中数据分布类型
	 * 1.按行列规律分布（只有rowData有数据）
	 * 2.单元格无规律分布（只有cellData有数据）
	 * 3.1和2的混合型（rowData与cellData都有数据）
	 */
	private int excelDataType;
	/**
	 * excel中标准行列数据
	 * map的key为excel列对应的数据库表的列名
	 * map的value为excel单元格的值
	 */
	private List<Map<String, Object>> rowData;
	/**
	 * excel中无规律的单元格数据
	 * key：为excel中的坐标，如A1，A2，B1，B2
	 * value：为excel中单元格的值
	 */
	private Map<String, Object> cellData;

	public List<Map<String, Object>> getRowData() {
		return rowData;
	}
	public Map<String, Object> getCellData() {
		return cellData;
	}
	public int getExcelDataType() {
		return excelDataType;
	}
	public long getCreateTime() {
		return createTime;
	}
	@Override
	public String toString() {
		return "ExcelResultBean [createTime=" + createTime + ", excelDataType=" + excelDataType + ", rowData=" + rowData
				+ ", cellData=" + cellData + "]";
	}
	
}
