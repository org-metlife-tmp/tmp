package com.qhjf.cfm.utils;

import java.util.Arrays;

/**
 * Created by zhangsq on 2018/7/11.
 */
public class ArrayUtil {

    public static boolean checkDbResult(int[] result) {
        String resultStr = Arrays.toString(result);
        if ("null".equals(resultStr) || "[]".equals(resultStr) || resultStr.contains("0")) {
            return false;
        }
        return true;
    }

    public static boolean checkDbResult(int[]... result) {
        if (null == result || result.length == 0) {
            return false;
        }
        Boolean[] flag = new Boolean[result.length];
        for (int i = 0; i < result.length; i++) {
            flag[i] = checkDbResult(result[i]);
        }
        return !Arrays.asList(flag).contains(false);
    }
}
