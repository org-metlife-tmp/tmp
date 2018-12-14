package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

public class OriginDataOaService {

    public Page<Record> getPage(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("origin_data_oa.getPage", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }
}
