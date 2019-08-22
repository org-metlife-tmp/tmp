package com.qhjf.cfm.web.utils;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 码表工具类
 */
public class CategoryUtil {

    private final static CategoryUtil instance = new CategoryUtil();

    private Map<String, Map<String, String>> allCate ;


    private CategoryUtil(){
        init();

    }

    public static CategoryUtil getInstance() {
        return instance;
    }

    private void init(){
        SqlPara sql = Db.getSqlPara("catg.allCatgList",Kv.by("map", null));
        List<Record> li =  Db.find(sql);
        List<CategoryInfo> infos  = CategoryInfo.convertRecodrToInfo(li);
        if(infos != null && infos.size() > 0){
            this.allCate = new HashMap<>();
            for (CategoryInfo info : infos) {
                this.allCate.put(info.getCode(),info.getItems());
            }
        }
    }


    public static boolean validate(String catg_code, String value){
        Map<String,String> map= instance.allCate.get(catg_code);
        if(map != null&& map.size() > 0){
            return map.containsKey(value);
        } else{
            return false;
        }
    }
}
