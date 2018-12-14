package com.qhjf.cfm.web.utils;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryInfo {

    private String code ;
    private String desc ;
    private Map<String, String> items;

    public CategoryInfo(String code, String desc){
        this.code = code;
        this.desc = desc;
        this.items = new HashMap<>();
    }

    void addItem(String key, String value){
        this.items.put(key, value);

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


    public static List<CategoryInfo> convertRecodrToInfo(List<Record> li){
        List<CategoryInfo> result = null;
        if(li != null && li.size() > 0){
            result = new ArrayList<>();
            CategoryInfo curInfo = null;
            for (Record record : li) {
                String code = TypeUtils.castToString(record.get("code"));
                String desc = TypeUtils.castToString(record.get("desc"));
                String key = TypeUtils.castToString(record.get("key"));
                String value = TypeUtils.castToString(record.get("value"));
                if(curInfo == null || !code.equals(curInfo.getCode())){
                    curInfo = new CategoryInfo(code, desc);
                    result.add(curInfo);
                }
                curInfo.addItem(key, value);
            }
        }
        return result;
    }

    public static CategoryInfo convertRecodrToInfo(Map<String , AtomicInterfaceConfig> map){
        CategoryInfo info = null;
        if(map != null && map.size() > 0){
            for (String atomic_name : map.keySet()) {
                AtomicInterfaceConfig desc = map.get(atomic_name);
                if(info == null){
                    try{
                        info = new CategoryInfo(desc.getChannelInfo().getCode(), desc.getChannelInfo().getName());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                info.addItem(desc.getInterfaceName(),desc.getInterfaceDesc());
            }
        }
        return info;

    }


}
