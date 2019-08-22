package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import java.util.List;

public class AccountQueryService {

    public List<Record> normallist(Record record) {
        SqlPara sqlPara = Db.getSqlPara("acc_comm.normallist", record.getColumns());
        List<Record> list = Db.find(sqlPara);

        return list;
    }
}
