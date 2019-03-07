package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.util.CommonUtil;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkCalService {
	
	/**
	 * 初始化日历
	 *
	 * @param record
	 * @return
	 * @throws ReqDataException
	 */
	public HashMap<String,Object> initWorkCalList(final Record record) throws BusinessException {
		final String year = record.getStr("year");
		try {
			if(Long.valueOf(year) > Long.valueOf(CommonUtil.getSysYear())+1) {
				throw new ReqDataException("请求年份不能超过明年!");
			}
		}catch(NumberFormatException e) {
			throw new ReqDataException("请求参数格式不对!");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Long end = 0l;
		try {
			cal.setTime(sdf.parse(year+"-01-01"));
			end = sdf.parse(year+"-12-31").getTime();
		} catch (ParseException e) {
		}
		
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		
		List<Record> li  = Db.find(Db.getSql("workcal.list"),year);
		if(li.size() > 0) {
			resultMap.put("detail", getReturnList(li,0));
			resultMap.put("is_active", Db.findFirst(Db.getSql("workcal.findactive"),"day",year)
					.getStr("is_active"));
			return resultMap;
		}
		
		final ArrayList<Record> list = new ArrayList<Record>();
		final int totalDay = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
		int[] weekArray = new int[] {7,1,2,3,4,5,6};
		while(cal.getTimeInMillis() <= end) {
			Record map = new Record();
			map.set("cdate", cal.getTime());
			int we = cal.get(Calendar.DAY_OF_WEEK);
			map.set("day_of_week", weekArray[we-1]);
			map.set("is_holiday", (we==7||we==1) ? 1:0);
			map.set("is_checkout", 0);
			list.add(map);
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		
        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //批量新增
            	int[] result = Db.batch(Db.getSql("workcal.init"),"cdate, day_of_week, is_holiday, is_checkout",
            			list,500);
            	Record recordYear = new Record();
            	recordYear.set("cdate", CommonUtil.getCurrentYear(year));
            	recordYear.set("is_active", 0);
            	recordYear.set("type", "day");
            	boolean saveYear = Db.save("working_year_setting", "cdate", recordYear);
            	return (result.length==totalDay) && saveYear;
            }
        });
        if (!flag) {
            throw new DbProcessException("初始化日历失败！");
        }else {
        	resultMap.put("detail", getReturnList(list,1));
			resultMap.put("is_active", Db.findFirst(Db.getSql("workcal.findactive"),"day",year)
					.getStr("is_active"));
        	return resultMap;
        }
	}
	
    /**
     * 日历列表查询
     *
     * @param record
     * @return
     * @throws ReqDataException 
     */
    public HashMap<String,Object> findWorkCalList(final Record record) throws BusinessException {
    	String year = record.getStr("year");
    	
    	List<Record> rc = Db.find(Db.getSql("workcal.findisactive"),"day",year);
    	if(rc.size() <= 0) {
    		throw new ReqDataException("该时间日历未启用!");
    	}
    	
    	List<Record> li  = Db.find(Db.getSql("workcal.list"),year);
    	
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("is_active", Db.findFirst(Db.getSql("workcal.findactive"),"day",year)
				.getStr("is_active"));
		resultMap.put("detail", getReturnList(li,0));
    	return resultMap;
    }
    
    /**
     * 更新节假日非节假日
     *
     * @param record
     * @return
     * @throws ReqDataException 
     */
    public void updateWorkCalHoliday(final Record record) throws BusinessException {
    	final List<String> holiday = record.get("holiday");
    	final List<String> workingday = record.get("workingday");
    	if(CommonUtil.isNullHoliday(holiday) && CommonUtil.isNullHoliday(workingday)) {
    		throw new ReqDataException("未有更新的数据!");
    	}
    	String year = record.getStr("year");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		Calendar cal = Calendar.getInstance();
		try {
			if(Long.valueOf(year) > Long.valueOf(CommonUtil.getSysYear())+1) {
				throw new ReqDataException("请求年份不能超过明年!");
			}			
			cal.setTime(sdf.parse(year));
		} catch (ParseException e) {
			throw new ReqDataException("请求参数年份格式不对!");
		}  
    	boolean exist = Db.find(Db.getSql("workcal.findisactive"),"day",year).size()>0;
    	if(exist) {
    		throw new ReqDataException("当前年份已经启用！");
    	}
        //进行数据更新操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清除原有关联信息
            	if(!CommonUtil.isNullHoliday(holiday) && !CommonUtil.isNullHoliday(workingday)) {
            		boolean y = Db.update(Db.getSqlPara("workcal.upholiday", 
            				Kv.by("holiday", holiday))) > 0;
    				boolean n = Db.update(Db.getSqlPara("workcal.upnotholiday", 
    						Kv.by("holiday", workingday))) > 0;
            		return y && n;
            	}else if(!CommonUtil.isNullHoliday(holiday)) {
            		boolean y = Db.update(Db.getSqlPara("workcal.upholiday", 
            				Kv.by("holiday", holiday))) > 0;
            		return y;
            	}else {
    				boolean n = Db.update(Db.getSqlPara("workcal.upnotholiday", 
    						Kv.by("holiday", workingday))) > 0;
            		return n;
            	}
            }
        });    	
    	if(!flag) {
    		throw new DbProcessException("更新节假日非节假日失败！");
    	}
    }
    
    /**
     * 启用停用
     *
     * @param record
     * @return
     * @throws  
     * @throws ReqDataException 
     */
    public Record updateWorkCalActivity(final Record record) throws BusinessException {
    	final String year = record.getStr("year");
    	List<Record> liy  = Db.find(Db.getSql("workcal.findactive"),"day",year);
    	if(liy.size() <=0) {
    		throw new ReqDataException("该年未被初始化！");
    	}
    	
    	//进行数据更新操作
    	boolean flag = Db.tx(new IAtom() {
    		@Override
    		public boolean run() throws SQLException {
    			//清除原有关联信息
    			boolean y = Db.update(Db.getSql("workcal.upactive"),"day",year) > 0;
    			return y;
    		}
    	});    	
    	Record rc = new Record();
    	if(!flag) {
    		throw new DbProcessException("更新节假日非节假日失败！");
    	}else {
    		rc.set("is_active", 1);
    		return rc;
    	}
    }
    
    /**
     * 结账日设定
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public void updateWorkCalCheckout(final Record record) throws BusinessException {
    	final Kv kv = Kv.create();
    	final List<String> dates = record.get("dates");
    	final String year = record.getStr("year");
    	kv.set("year", year);
    	kv.set("dates", dates);
    	if(CommonUtil.isNullHoliday(dates)) {
    		throw new ReqDataException("更新的日期为空!");
    	}
    	//判断结账日是否一月只设置了一次
    	Set<String> dateSet = new HashSet<String>();
    	for(String s : dates) {
    		dateSet.add(s.substring(0, 7));
    	}
    	if(dateSet.size() != dates.size()) {
    		throw new ReqDataException("一月只能有一天为结账日!");
    	}
    	
    	final boolean exist = Db.find(Db.getSql("workcal.findisactive"),"checkout",year).size()>0;
    	if(exist) {
    		throw new ReqDataException("当前银行报盘日已经启用！");
    	}  		
        //进行数据更新操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清除原有关联信息
            	Db.update(Db.getSql("workcal.checkoutsetn"), year);
            	boolean n = Db.update(Db.getSqlPara("workcal.checkoutsety", 
            			Kv.by("map", kv))) > 0;
            			
            	boolean existCheck = Db.find(Db.getSql("workcal.findactive"),"checkout",year).size()>0;
            	boolean saveCheck = true;
            	if(!existCheck) {
            		Record recordCheck = new Record();
            		recordCheck.set("cdate", CommonUtil.getCurrentYear(year));
            		recordCheck.set("is_active", 0);
            		recordCheck.set("type", "checkout");
            		saveCheck = Db.save("working_year_setting", "cdate", recordCheck);       	
            	}              			
            			
            	return n && saveCheck;
            }
        });    	
    	if(!flag) {
    		throw new DbProcessException("更新结账日失败！");
    	}
    } 
    
    /**
     * 结账日列表查询
     *
     * @param record
     * @return
     * @throws ReqDataException 
     */
    public Map<String,Object> findCheckoutList(final Record record) throws BusinessException {
        List<Record> rcList = Db.find(Db.getSql("workcal.checkoutlist"), record.getStr("year"));
        
        if((rcList==null) || rcList.size()==0) {
        	throw new ReqDataException("暂未有结账日设置!");
        }
        
        String[] cdates = new String[rcList.size()];
        for(int i=0; i<rcList.size(); i++) {
        	cdates[i] = rcList.get(i).getStr("cdate");
        }
        
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("year", record.getStr("year"));
        map.put("dates", cdates);
        map.put("is_active", Db.findFirst(Db.getSql("workcal.findactive"),
        							"checkout",
        							record.getStr("year")).get("is_active"));
    	return map;
    } 
    
    /**
     * 启用停用 结账日
     *
     * @param record
     * @return
     * @throws  
     * @throws ReqDataException 
     */
    public Record updateCheckoutActivity(final Record record) throws BusinessException {
    	final String year = record.getStr("year");
    	List<Record> liy  = Db.find(Db.getSql("workcal.findactive"),"checkout",year);
    	if(liy.size() <=0) {
    		throw new ReqDataException("该银行未设置结账日！");
    	}
    	
    	//进行数据更新操作
    	boolean flag = Db.tx(new IAtom() {
    		@Override
    		public boolean run() throws SQLException {
    			//清除原有关联信息
    			boolean y = Db.update(Db.getSql("workcal.upactive"),"checkout",year) > 0;
    			return y;
    		}
    	});    	
    	Record rc = new Record();
    	if(!flag) {
    		throw new DbProcessException("更新结账日失败！");
    	}else {
    		rc.set("is_active", 1);
    		return rc;
    	}
    }        
    
    /**
     * 拼接返回值列表
     * @param li
     * @param flag
     * @return
     */
    public static ArrayList<HashMap<String,Object>> getReturnList(List<Record> li, int flag){
    	ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>>();
    	HashMap<String,Object> map = new HashMap<String,Object>();
    	ArrayList<Record> recordList = new ArrayList<Record>();
		for(Record r : li) {
			if(flag == 1) {
				r = transRecord(r);
			}
			String month = TypeUtils.castToString(r.get("month"));
			if(map.size()>0 && !map.containsValue(month)) {
				map.put("value", recordList);
				list.add(map);
				map = new HashMap<String,Object>();
				recordList = new ArrayList<Record>();
			}
			map.put("month", month);
			recordList.add(r);
		}
		if(li.size() > 0) {
			map.put("value", recordList);
			list.add(map);
		}
		return list;
    }
    
    /**
     * record转换
     * @param r
     * @return
     */
    public static Record transRecord(Record r) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Record record = new Record();
    	record.set("cdate", sdf.format(r.get("cdate")).substring(8, 10));
    	record.set("month", sdf.format(r.get("cdate")).substring(5, 7));
    	record.set("day_of_week", r.get("day_of_week"));
    	record.set("is_holiday", r.get("is_holiday"));
    	record.set("is_checkout", r.get("is_checkout"));
    	return record;
    }
    
}
