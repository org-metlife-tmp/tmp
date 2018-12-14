package com.qhjf.cfm.excel.config.statistic.impl;

import java.util.HashSet;
import java.util.List;
import com.qhjf.cfm.excel.config.statistic.IStatistics;
/**
 * 银行账号重复性校验
 * @author CHT
 *
 */
public class AccountNoRepeatCheck implements IStatistics {
	
	private String errorMessage;
	private static final String template = "只支持单账号导入，包含账号：%s";

	@Override
	public Number doStatistic(List<Object> dataSource) {
		if (dataSource == null || dataSource.isEmpty() || dataSource.size() == 1) {
			return 0;
		}
		
		HashSet<Object> set = new HashSet<Object>(dataSource);
		if (set.size() != 1) {
			this.errorMessage = String.format(template, set.toString());
			return 1;
		}
		return 0;
	}

	@Override
	public Boolean compare(Number value) {
		if (value.intValue() == 1) {
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage() {
		return this.errorMessage;
	}

}
