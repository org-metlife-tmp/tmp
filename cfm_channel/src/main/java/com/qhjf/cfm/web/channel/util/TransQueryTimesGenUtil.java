package com.qhjf.cfm.web.channel.util;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史交易查询工具类
 *
 */
public class TransQueryTimesGenUtil {
	private TransQueryTimesGenUtil(){
		if (null != TransQueryInner.INSTANCE) {
			throw new RuntimeException("试图破坏单例！");
		}
	}
	public static final TransQueryTimesGenUtil getInstance(){
		return TransQueryInner.INSTANCE;
	}

	/**
	 * 获取历史交易查询的开始、结束日期
	 * @param accId	银行账号ID
	 * @param preDay	银行服务器与前置机服务器相差得天数，银行服务器比前置机服务器时间小则为正，否则为负
	 * @return
	 */
	public Map<String, String> getTransQueryTime(String accId, String preDay){
		int preDayInt = preDayTransToInt(preDay);//配置文件中的天数转int
		
		Map<String, String> result = new HashMap<>();
		Date jobExtLatestTime = getJobExtLatestTime(accId);
		if (null != jobExtLatestTime) {
			getDateMap(result, preDayInt, jobExtLatestTime);
		}else {
			getDateMap(result, preDayInt);
		}
		return result;
	}
	/**
	 * 获取accId历史交易查询的最近成功执行日期
	 * @param accId
	 * @return
	 */
	private Date getJobExtLatestTime(String accId){
		String sql = "select latest_date from acc_his_transaction_jobext where acc_id=?";
		Object[] params = new Object[]{accId};
		List<Record> find = Db.find(sql, params);
		if (null != find && find.size() > 0) {
			return find.get(0).getDate("latest_date");
		}else {
			return null;
		}
	}
	/**
	 * 生成历史交易查询的开始结束日期
	 * @param map
	 * @param preDay
	 */
	private void getDateMap(Map<String, String> map, int preDay, Date startDate){
		String start = DateUtil.getSpecifiedDayAfter(startDate, 1, "yyyyMMdd");
		String preDate = getPreDate(preDay);
		map.put("start", start);
		map.put("end", preDate);
	}
	private void getDateMap(Map<String, String> map, int preDay){
		String preDate = getPreDate(preDay);
		map.put("start", preDate);
		map.put("end", preDate);
	}
	/**
	 * 生成当前时间前一天的日期（针对于银行服务器的日期而言）
	 * @param preDay
	 * @return
	 */
	private String getPreDate(int preDay){
		Date date = new Date();
		String preDate = DateUtil.getSpecifiedDayBefore(date, preDay + 1, "yyyyMMdd");
		return preDate;
	}
	
	private static class TransQueryInner{
		private static final TransQueryTimesGenUtil INSTANCE = new TransQueryTimesGenUtil();
	}
	
	private int preDayTransToInt(String preDay) {
		int r;
		try {
			r = Integer.parseInt(preDay);
		} catch (Exception e) {
			r = 0;
		}
		return r;
	}
	
}
