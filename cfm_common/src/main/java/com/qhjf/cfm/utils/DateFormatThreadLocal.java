package com.qhjf.cfm.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 线程安全的日期格式化工具
 */
public class DateFormatThreadLocal {

    private final static Map<String, ThreadLocal<DateFormat>> threadLocalPool = new HashMap<>();
    private static final Object object = new Object();

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";


    private static DateFormat getDateFormat(final String pattern)
            throws RuntimeException {
        ThreadLocal<DateFormat>  curThreadLocal  = threadLocalPool.get(pattern);
        if (curThreadLocal == null) {
            synchronized (object) {
                if (curThreadLocal == null) {
                    curThreadLocal = new ThreadLocal<DateFormat>(){
                        @Override
                        protected synchronized DateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    threadLocalPool.put(pattern,curThreadLocal);
                }
            }
        }
        return curThreadLocal.get();
    }

    public static Date convert(String source) throws ParseException {
        return getDateFormat(DEFAULT_PATTERN).parse(source);
    }

    public static Date convert(String pattern , String source) throws ParseException {
        return getDateFormat(pattern).parse(source);
    }

    public static String format(Date date) {
        return getDateFormat(DEFAULT_PATTERN).format(date);
    }

    public static String format(String pattern , Date date) {
        return getDateFormat(pattern).format(date);
    }
}