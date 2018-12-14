package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.RegexUtils;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.utils.Query4PinYinTool;

import java.util.List;

public class AreaQueryService {

    private final static Log logger = LogbackLog.getLog(AreaQueryService.class);

    public List<Record> getAllTopLevel(final Record record){
        logger.debug("Enter into getAllTopLevel()");
        String query_key = "";
        if(record != null){
            query_key = TypeUtils.castToString(record.get("query_key"));
        }
        Kv cond = Kv.create();
        Query4PinYinTool.processPinyinQuery(cond,query_key);
        SqlPara sql = Db.getSqlPara("area.allTopLevel",Kv.by("map", cond));
        return Db.find(sql);
    }


    public List<Record> getAreaList(final Record record){
        logger.debug("Enter into getAllTopLevel()");
        String top_super = "";
        String query_key = "";
        if(record != null){
            top_super = TypeUtils.castToString(record.get("top_super"));
            query_key = TypeUtils.castToString(record.get("query_key"));
        }
        Kv cond = Kv.create();
        if(top_super != null && !"".equals(top_super)){
            cond.set("top_super", top_super);
        }
        Query4PinYinTool.processPinyinQuery(cond,query_key);
        SqlPara sql = Db.getSqlPara("area.areaList",Kv.by("map", cond));
        return Db.find(sql);
    }



}
