package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.utils.CategoryInfo;

import java.util.List;

/**
 * 码表
 *
 * @auther zhangyuanyuan
 * @create 2018/7/2
 */
public class CategoryService {

    /**
     * 码表信息列表
     *
     * @param record
     * @return
     */
    public List<Record> list(final Record record) {
        SqlPara sqlPara = Db.getSqlPara("cate.list", Kv.by("map", record.getColumns()));
        return Db.find(sqlPara);
    }



    public List<CategoryInfo> listN(final  Record record){
        SqlPara sqlPara = Db.getSqlPara("cate.list", Kv.by("map", record.getColumns()));
        List<Record> li =  Db.find(sqlPara);
        return CategoryInfo.convertRecodrToInfo(li);
    }

}
