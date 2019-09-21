package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * @Auther: zhangyuan
 * @Date: 2018/7/27 14:11
 * @Description: 审批中业务跟踪管理
 */
public class WorkflowTraceService {

    /**
     * 列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("query.findrunexecuteinstList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

}
