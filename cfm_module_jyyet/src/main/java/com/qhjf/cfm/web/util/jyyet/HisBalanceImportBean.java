package com.qhjf.cfm.web.util.jyyet;

import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.Map;

/**
 * 历史余额导入javaBean
 * 用途：1.同一账号同一天只保留一条余额；2.取出同一账号导入的余额数据的开始日期与结束日期
 * @author CHT
 *
 */
public class HisBalanceImportBean {
	
	private String accNo;
	/**
	 * 开始日期
	 */
	private String start;
	/**
	 * 结束日期
	 */
	private String end;
	/**
	 * 同一账号的余额数据
	 */
	private Map<String, Record> map;
	
	public HisBalanceImportBean(){
		map = new HashMap<>();
	}
	
	/**
	 * 1.添加当前账户的一条余额数据
	 * 2.更新start/end：比start小更新start，比end大更新end；
	 * 3.如果当前账户，当前日期已经有数据了，则覆盖；
	 * @param r
	 */
	public void addRecord(Record r){
		
		String balDate = r.get("bal_date").toString();
		
		if (null == start) {
			start = balDate;
			end = balDate;
			accNo = r.getStr("acc_no");
			map.put(balDate, r);
		}else {
			if (end.compareTo(balDate) < 0) {
				end = balDate;
			}
			
			if (start.compareTo(balDate) > 0) {
				start = balDate;
			}
			
			map.put(balDate, r);//具有覆盖同一天余额数据的功能
			
			/*//同一账号同一天含有多天余额数据
			if (end.compareTo(balDate) == 0 || start.compareTo(balDate) == 0) {
				
			}*/
		}
	}
	
	
	
	
	public String getAccNo(){
		return accNo;
	}
	public String getStart() {
		return start;
	}
	public String getEnd() {
		return end;
	}
	public Map<String, Record> getMap() {
		return map;
	}
	
}
