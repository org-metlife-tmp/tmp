package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;

import java.util.List;

/**
 *OA报表
 *
 * @author wy
 * @date 2019年7月3日
 */
public class OaService {
    /**
     * OA报表查询
     *
     * @param record
     * @throws ReqDataException
     * @throws DbProcessException
     */
   /* public List<Record> OaReportList( Record record, UodpInfo uodpInfo) {
        //record.set("flow_id", "102");//那我这里怎么获取前端传的？这是什么意思 写错了好像 这是个对象啊 你传到sql里怎么认识 你传个 1 啥的都行
        SqlPara sqlPara = Db.getSqlPara("oabb.oabbList", Kv.by("map", record.getColumns()));
        List<Record> rlist = Db.find(sqlPara);
        return rlist;
    }*/
    public Page<Record> OaReportList(int pageNum, int pageSize,Record record, UodpInfo uodpInfo) {
        //record.set("flow_id", "102");//那我这里怎么获取前端传的？这是什么意思 写错了好像 这是个对象啊 你传到sql里怎么认识 你传个 1 啥的都行
        SqlPara sqlPara = Db.getSqlPara("oabb.oabbList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }
}
