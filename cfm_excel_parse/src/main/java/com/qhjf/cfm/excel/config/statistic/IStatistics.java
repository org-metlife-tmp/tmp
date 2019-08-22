package com.qhjf.cfm.excel.config.statistic;

import java.util.List;
/**
 * 统计校验器
 * 统计Excel一列数据，并进行校验
 * 需要设置标准值
 */
public interface IStatistics {
	/**
	 * 统计excel的一列数据
	 * @param dataSource excel的一列数据
	 * @return
	 */
	public Number doStatistic(List<Object> dataSource);
	/**
	 * 统计结果与标准值比较
	 * @param value
	 * @return
	 */
	public Boolean compare(Number value);
	/**
	 * 返回统计校验信息
	 * @return
	 */
	public String getErrorMessage();
}