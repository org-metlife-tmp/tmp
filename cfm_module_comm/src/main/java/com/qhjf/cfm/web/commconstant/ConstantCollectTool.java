package com.qhjf.cfm.web.commconstant;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.ConstantAnnotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ConstantCollectTool {

    /**
     * 所有的可提供给前端的WebConstant常量，key:WebConstant的类名， value: code:desc的键值对
     * 能否提供给前端的依据：类上是否有ConstantAnnotation注解，有则可以提供给前端
     */
    public static final Map<String,Map<String,String>> ALL_WEB_CONSTANT;

    /**
     * 所有的可提供给前端的WebConstant常量描述信息， key:WebConstant的类名, value：类名描述
     */
    private static final Map<String, String> WEB_CONSTANT_DESC ;


    public static final Map<String, DbConstant> DB_CONSTANT;


    public static class DbConstant{
        private String code;
        private String desc;
        private Map<String, String> items;

        DbConstant(String code, String desc){
            this.code = code;
            this.desc = desc;
            this.items = new HashMap<>();
        }

        void addItem(String code , String desc){
            items.put(code, desc);
        }


        public String getCode() {
            return code;
        }


        public String getDesc() {
            return desc;
        }



        public Map<String, String> getItems() {
            return items;
        }

    }


    static{
        ALL_WEB_CONSTANT = new HashMap<>();
        WEB_CONSTANT_DESC = new HashMap<>();
        DB_CONSTANT = processDbConstant();
        for (Class cls : WebConstant.class.getClasses()) {
            String key = cls.getSimpleName();
            ConstantAnnotation ca = (ConstantAnnotation) cls.getAnnotation(ConstantAnnotation.class);
            if(ca != null){
                WEB_CONSTANT_DESC.put(key,ca.value());
                if(cls.isEnum() && WebConstant.class.isAssignableFrom(cls)){
                    ALL_WEB_CONSTANT.put(key,processEnum(cls));
                }
            }
        }

    }

    private static Map<String, DbConstant> processDbConstant(){
        Map<String, DbConstant> result  = null;
//        String sql = "select ct.code , ct.desc , cv.`key` as item_code , cv.value as item_desc " +
//                " from category_type ct , category_value cv where cv.cat_code = ct.code  order by ct.code ";
        List<Record> li  = Db.find(Db.getSql("common.category"));
        if(li != null){
            result = new HashMap<>();
            DbConstant inner = null;
            String curCode = "";
            for (Record record : li) {
                String code = TypeUtils.castToString(record.get("code"));
                String desc = TypeUtils.castToString(record.get("desc"));
                String item_code = TypeUtils.castToString(record.get("item_code"));
                String item_desc = TypeUtils.castToString(record.get("item_desc"));
                if(!curCode.equals(code)){
                    inner = new DbConstant(code , desc);
                    curCode = code;
                    result.put(code, inner);
                }
                inner.addItem(item_code, item_desc);
            }
        }
        return result;
    }


    private static Map<String,String> processEnum(Class cls){
        Map<String,String> result = new HashMap<>();
        try {
            Method method = cls.getMethod("values");
            WebConstant[] constants = (WebConstant[])method.invoke(null, null);
            for (WebConstant constant : constants) {
                result.put(String.valueOf(constant.getKey()), constant.getDesc());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(ConstantCollectTool.ALL_WEB_CONSTANT);
        System.out.println(ConstantCollectTool.WEB_CONSTANT_DESC);
    }



}
