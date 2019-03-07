package com.qhjf.cfm.excel.config;

import com.qhjf.cfm.excel.config.validator.ICustomValidator;
import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.excel.exception.ExcelConfigMergeException;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * Excel解析规则 与 校验规则 bean
 * @author CHT
 *
 */
public class ExcelSheetConfig implements Serializable, Cloneable {
	private static final long serialVersionUID = -3144434554548706376L;
	public ExcelSheetConfig(){}
	public ExcelSheetConfig(int sheetNumber){
		this.sheetNumber = sheetNumber;
	}
	//该Excel配置的主键，全局唯一
	private String pk;
	//存入mongoDb中的Excel模板的ObjectId
	private String templateId;
	//sheet号
	private Integer sheetNumber;
	//sheet名称
	private String sheetName;
	//excel中数据分布类型:1.按行列规律分布；2.单元格无规律分布；3.混合式
	private int excelDataType;
	
	/**
	 * excel中行列数据规则部分的属性
	 */
	//起始行:从0开始
	private int startRow;
	//每行的起始列:从0开始
	private int startColumn;
	//列配置
	private List<Config> columnConfigs;
	
	/**
	 * excel中无规律分布的单元格 的配置
	 * key: Excel的单元格位置，例：A1，B3
	 */
	private Map<String, Config> cellConfigs;

	
	
	public String getPk() {
		return pk;
	}
	public void setPk(String pk) {
		this.pk = pk;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public Integer getSheetNumber() {
		return sheetNumber;
	}
	public void setSheetNumber(Integer sheetNumber) {
		this.sheetNumber = sheetNumber;
	}
	public int getExcelDataType() {
		return excelDataType;
	}
	public void setExcelDataType(int excelDataType) {
		this.excelDataType = excelDataType;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getStartColumn() {
		return startColumn;
	}
	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}
	public List<Config> getColumnConfigs() {
		return columnConfigs;
	}
	public void setColumnConfigs(List<Config> columnConfigs) {
		this.columnConfigs = columnConfigs;
	}
	public Map<String, Config> getCellConfigs() {
		return cellConfigs;
	}
	public void setCellConfigs(Map<String, Config> cellConfigs) {
		this.cellConfigs = cellConfigs;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ExcelSheetConfig o = null;
		o = (ExcelSheetConfig)super.clone();
		
		List<Config> column = new ArrayList<>();
		for (Config iConfig : o.columnConfigs) {
			column.add((Config)iConfig.clone());
		}
		o.columnConfigs = column;
		
		Map<String, Config> cell = new HashMap<>();
		Set<Entry<String,Config>> entrySet = o.cellConfigs.entrySet();
		for (Entry<String, Config> entry : entrySet) {
			cell.put(entry.getKey(), (Config)entry.getValue().clone());
		}
		o.cellConfigs = cell;
		
		return o;
	}
	
	/**
	 * 校验器合并
	 * @param v 自定义校验器
	 * @throws ExcelConfigMergeException 
	 */
	public void mergeValidator(ICustomValidator v) throws ExcelConfigMergeException{
		Integer column = v.getColumn();
		if (null != column) {
			mergeColumnValidator(v, column);
		}
		String position = v.getPosition();
		if (null != position) {
			mergeCellValidator(v, position);
		}
	}
	
	/**
	 * 行列校验器合并
	 * @param v	自定义校验器
	 * @param column	第几列(从1开始)
	 * @throws ExcelConfigMergeException 
	 */
	private void mergeColumnValidator(ICustomValidator v, Integer column) throws ExcelConfigMergeException{
		column = column -1;
		if (this.columnConfigs.size() < column) {
			throw new ExcelConfigMergeException(String.format("Excel自定义校验器的列参数[%s]错误", column));
		}
		Config columnConfig = this.columnConfigs.get(column);
		List<IValidator> validatorList = columnConfig.getValidatorList();
		validatorList.add(v);
	}
	
	/**
	 * 列校验器合并
	 * @param v	自定义校验器
	 * @param position	单元格位置（Excel中的A1,A2,B1,B3...）
	 * @throws ExcelConfigMergeException 
	 */
	private void mergeCellValidator(ICustomValidator v, String position) throws ExcelConfigMergeException {
		if (this.cellConfigs.containsKey(position.toUpperCase())) {
			Config cellConfig = this.cellConfigs.get(position);
			List<IValidator> validatorList = cellConfig.getValidatorList();
			validatorList.add(v);
		} else {
			throw new ExcelConfigMergeException(String.format("Excel自定义校验器的单元格参数[%s]错误", position));
		}
	}
}
