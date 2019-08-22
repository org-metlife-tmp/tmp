package com.qhjf.cfm.excel.analyze.validator;

import com.qhjf.cfm.excel.bean.ResultBean;
import com.qhjf.cfm.excel.config.ExcelSheetConfig;
import com.qhjf.cfm.excel.exception.DisposeException;
import com.qhjf.cfm.excel.exception.StatisticsAssertionException;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;

/**
 * Excel校验服务接口
 * @author CHT
 *
 */
public interface IValidatorService {

	/**
	 * 校验Excel
	 */
	public ResultBean doValidate(UploadFileScaffold ufs, ExcelSheetConfig sheetConfig) throws StatisticsAssertionException, DisposeException;
}
