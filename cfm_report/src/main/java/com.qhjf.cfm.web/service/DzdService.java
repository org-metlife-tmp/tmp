package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;

/**
 *OA报表
 *
 * @author wy
 * @date 2019年7月3日
 */
public class DzdService {
    /**
     * 归集通报表查询
     *
     * @param record
     * @throws ReqDataException
     * @throws DbProcessException
     */

    public Page<Record> DzdReportList(int pageNum, int pageSize,Record record, UodpInfo uodpInfo) {
        //record.set("flow_id", "102");
        SqlPara sqlPara = Db.getSqlPara("dzdbb.dzdList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }
}
