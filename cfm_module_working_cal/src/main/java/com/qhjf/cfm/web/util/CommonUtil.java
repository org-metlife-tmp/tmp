package com.qhjf.cfm.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommonUtil {
	
    public static boolean isNullHoliday(List<String> s) {
		return s==null || s.size()==0;
    }
    
    /**
     * 获取当前年份
     * @return
     */
    public static String getSysYear() {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        return year;
    }    
    
    /**
     * 获取当前年一月一号
     * @param year
     * @return
     */
    public static Date getCurrentYear(String year) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(year+"-01-01");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
    }
}
