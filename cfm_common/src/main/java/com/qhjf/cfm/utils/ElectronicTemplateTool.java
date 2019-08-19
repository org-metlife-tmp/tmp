package com.qhjf.cfm.utils;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 电子回单模版缓存
 */
public class ElectronicTemplateTool {

    public static Map<String, Map<String, String>> ELECTRONIC_TEMPLATE_MAP = new HashMap<>();


    private ElectronicTemplateTool() {
        init();
    }

    public static ElectronicTemplateTool INSTANCE;

    public static ElectronicTemplateTool getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ElectronicTemplateTool();
        }
        return INSTANCE;
    }

    private void init() {
        SqlPara sqlPara = Db.getSqlPara("ele.findElectronicBillTemplate");
        List<Record> templateList = Db.find(sqlPara);


        Map<String, String> map = new HashMap<>();
        for (Record rec : templateList) {
            String uuid = TypeUtils.castToString(rec.get("uuid"));

            if (!ELECTRONIC_TEMPLATE_MAP.containsKey(uuid)) {
                map = new HashMap<>();
            }

            String origin_fd = rec.get("origin_fd");
            String ref_fd = rec.get("ref_fd");

            map.put(origin_fd, ref_fd);


            ELECTRONIC_TEMPLATE_MAP.put(uuid, map);
        }
    }
}
