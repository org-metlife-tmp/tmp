package com.qhjf.cfm.web.channel.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jfinal.template.stat.ParseException;

public class DateUtil {
	
public static final String DEFAULT_DATEPATTERN = "yyyy-MM-dd";
    
    public static final String DEFAULT_TIMEPATTERN = " hh:mm:ss";

    public static final String DEFAULT_DATETIMEPATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_DATEPATTERN_SIMP = "yyyyMMdd";

    public static final String DEFAULT_DATETIMEPATTERN_SIMP = "yyyyMMddHHmmss";

	public static Date getSpecifiedDayBeforeDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);
        return c.getTime();
    }
	
	/**
     * 根据日期字符串获取日期
     *
     * @param str_date
     * @param format
     * @return
     * @throws
     */
    public static Date formatDate(String str_date, String format) throws Exception {
        try {
            Date result = new SimpleDateFormat(format).parse(str_date);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception(str_date + "can't parse ");
        }

    }
    
    /**
     * 根据指定日期字符串。获取前几天（由dayNume指定）的日期字符串
     *
     * @param date 指定日期
     * @param dayNum       指定天数
     * @param format       返回日期字符串格式串
     * @return
     */
    public static String getSpecifiedDayBefore(Date date, int dayNum, String format) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - dayNum);

        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }
    
    public static String getSpecifiedDayBefore(String specifiedDay, int dayNum, String format) throws java.text.ParseException {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(DEFAULT_DATEPATTERN_SIMP).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - dayNum);

        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }
    
    public static String getSpecifiedDayAfter(Date date, int dayNum, String format) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + dayNum);

        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }
    
    public static String getSpecifiedDayAfter(String specifiedDay, int dayNum, String format) throws java.text.ParseException {
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat(DEFAULT_DATEPATTERN_SIMP).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + dayNum);

        String dayBefore = new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }
    
    public static Timestamp strToTimestap(String str_date, String format) throws Exception{
    	DateFormat df = new SimpleDateFormat(format);
    	df.setLenient(false);
    	Timestamp ts = new Timestamp(df.parse(str_date).getTime());
    	return ts;
    }
    
    public static boolean compareDate(String beginDate, String endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date beginDateD = format.parse(beginDate);
            Date endDateD = format.parse(endDate);
            if (beginDateD.getTime() >= endDateD.getTime()){
            	return false;
            }else{
            	return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
