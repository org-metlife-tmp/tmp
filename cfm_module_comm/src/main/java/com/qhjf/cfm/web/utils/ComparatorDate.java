package com.qhjf.cfm.web.utils;

import java.util.Comparator;
import java.util.Date;

public class ComparatorDate implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        Date begin = (Date) o1;
        Date end = (Date) o2;
        if(begin.before(end)){
            return -1;
        }else{
            return 1;
        }

    }
}
