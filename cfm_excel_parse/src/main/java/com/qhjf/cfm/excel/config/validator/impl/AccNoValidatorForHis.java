package com.qhjf.cfm.excel.config.validator.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.qhjf.cfm.excel.config.validator.IValidator;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Map;

/**
 * 校验银行账户号是否在系统中
 * @author CHT
 *
 */
public class AccNoValidatorForHis implements IValidator {
		//错误信息
		private String errorMessage;
		private static final int INTERACTIVE_MODE = 1;
		@Override
		public boolean doValidat(Object data) {
//			List<Object> query = Db.query(String.format("SELECT * FROM account WHERE acc_no='%s'", data.toString()));
			Map<String, Object> aRowData = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", data.toString());
			int interativecode = TypeUtils.castToInt(aRowData.get("interactive_mode"));		
			if (null == aRowData) {
				this.errorMessage = String.format("银行账号【%s】在系统中不存在，或者没有启用", data);
				return false;
			}else {	
				if(interativecode == INTERACTIVE_MODE) 
				{
					this.errorMessage = String.format("银行账号【%s】直连账号，不能进行导入", data);
					return false;
				}
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
