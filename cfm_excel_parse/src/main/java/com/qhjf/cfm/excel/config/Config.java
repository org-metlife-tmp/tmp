package com.qhjf.cfm.excel.config;

import java.util.ArrayList;
import java.util.List;

import com.qhjf.cfm.excel.config.convertor.IConvertor;
import com.qhjf.cfm.excel.config.statistic.IStatistics;
import com.qhjf.cfm.excel.config.validator.IValidator;

/**
 * Excel列配置/单元格配置
 */
public abstract class Config implements Cloneable {

	/*是否必须*/
	protected boolean required;
	/*excel列名、Excel单元格名*/
	protected String columnName;
	/*列类型,必须是java的基本类型或者是String、Date*/
	protected Class<?> columnClass;
	//数据库列明
	protected String dbColumnName;
	/*错误提示信息,将写回到excel文档*/
	protected String tips;
	/*验证器列表*/
	protected List<IValidator> validatorList;
	/*单元格值转换器*/
	protected IConvertor convertor;
	/*统计处理器*/
	protected IStatistics statistics;
	//日期格式处理的格式化对象
	protected String dateFormat;
	
	
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public Class<?> getColumnClass() {
		return columnClass;
	}
	public void setColumnClass(Class<?> columnClass) {
		this.columnClass = columnClass;
	}
	public String getDbColumnName() {
		return dbColumnName;
	}
	public void setDbColumnName(String dbColumnName) {
		this.dbColumnName = dbColumnName;
	}
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
	public List<IValidator> getValidatorList() {
		return validatorList;
	}
	public void setValidatorList(List<IValidator> validatorList) {
		this.validatorList = validatorList;
	}
	public IConvertor getConvertor() {
		return convertor;
	}
	public void setConvertor(IConvertor convertor) {
		this.convertor = convertor;
	}
	public IStatistics getStatistics() {
		return statistics;
	}
	public void setStatistics(IStatistics statistics) {
		this.statistics = statistics;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	/**
	 * 半深度拷贝
	 * validator对象共用，盛放validator的List拷贝，用于添加用户传入的自定义validator，以保证配置文件中的validator列表不变
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Config o = null;
		o = (Config)super.clone();
		
		List<IValidator> validatorList = new ArrayList<>();
		for (IValidator iValidator : o.validatorList) {
			validatorList.add(iValidator);
		}
		o.validatorList = validatorList;
		return o;
	}
}
