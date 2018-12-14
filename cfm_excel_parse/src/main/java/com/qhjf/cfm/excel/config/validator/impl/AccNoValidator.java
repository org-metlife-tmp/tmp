package com.qhjf.cfm.excel.config.validator.impl;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;

import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.excel.config.validator.IValidator;

/**
 * 校验银行账户号是否在系统中
 * @author CHT
 *
 */
public class AccNoValidator implements IValidator {
	//错误信息
	private String errorMessage;

	@Override
	public boolean doValidat(Object data) {
//		List<Object> query = Db.query(String.format("SELECT * FROM account WHERE acc_no='%s'", data.toString()));
		Map<String, Object> aRowData = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", data.toString());
		if (null == aRowData) {
			this.errorMessage = String.format("银行账号【%s】在系统中不存在，或者没有启用", data);
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
