package com.qhjf.cfm.utils;

import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class CommKit {

    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String) {
            return "".equals(obj);
        } else if (obj instanceof List) {
            return ((List<?>) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map<?,?>) obj).isEmpty();
        } else if (obj instanceof Set) {
            return ((Set<?>) obj).isEmpty();
        } else {
            return obj instanceof Queue ? ((Queue<?>) obj).isEmpty() : false;
        }
    }


    public static String convertPath2Pkg(String path) {
        if (!isNullOrEmpty(path)) {
            return path.replaceAll("/", "\\.");
        }
        return "";
    }

    public static String convertPkg2Path(String packageName) {
        if (!isNullOrEmpty(packageName)) {
            return packageName.replaceAll("\\.", "/");
        }
        return "";
    }


    public static Class<?> getClz(String clzname) {
        Class<?> clz = null;
        try {
            clz = Class.forName(clzname);
            return clz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clz;
    }

    public static void debugPrint(Logger log, String format, String arg){
        log.debug(format,StringKit.removeControlCharacter(arg));
    }

    public static void errorPrint(Logger log, String format, String arg){
        log.error(format,StringKit.removeControlCharacter(arg));
    }
}
