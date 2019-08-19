package com.qhjf.cfm.excel.config.validator.impl;

import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Map;

/**
 * 供应商账户信息表的账号校验
 * 
 * @author CHT
 *
 */
public class SupplierAccNoValidator implements IValidator {

	// 错误信息
	private String errorMessage;

	@Override
	public boolean doValidat(Object data) {
		Map<String, Object> aRowData = TableDataCacheUtil.getInstance().getARowData("supplier_acc_info", "acc_no", data.toString());
		if (null == aRowData) {
			this.errorMessage = String.format("银行账号【%s】在系统中不存在", data);
			return false;
		}else {
			return true;
		}
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
	@Override
	public boolean doValidat(Object data, Cell cell) {
		return doValidat(data);
	}
}
