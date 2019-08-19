package com.qhjf.cfm.web.utils;

import com.jfinal.kit.Kv;
import com.qhjf.cfm.utils.RegexUtils;

public abstract  class Query4PinYinTool {

    public static void processPinyinQuery(Kv cond, String query_key) {
        if(query_key != null  && !"".equals(query_key)){
            if(RegexUtils.isPinYin(query_key)){
                cond.set("pinyin",query_key);
            }else if(RegexUtils.isJianPin(query_key)){
                cond.set("jianpin",query_key);
            }else{
                cond.set("name",query_key);
            }
        }
    }
}
