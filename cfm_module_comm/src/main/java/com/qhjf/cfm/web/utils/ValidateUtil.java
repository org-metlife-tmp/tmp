package com.qhjf.cfm.web.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class ValidateUtil {
    public static boolean checkNumber(String str){
        if(StringUtils.isEmpty(str)){
            return false;
        }
        String pattern = "(^[1-9](\\d+)?(\\.\\d{1,2})?$)|(^\\d\\.\\d{1,2}$)";
        return Pattern.matches(pattern, str);
    }
}
