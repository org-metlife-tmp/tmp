package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

/**
 * 币种
 *
 * @auther zhangyuanyuan
 * @create 2018/7/3
 */

public class CurrencyService {

    /**
     * 获取币种列表
     *
     * @param record
     * @return
     */
    public Page<Record> findCurrencyPage(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("currency.getCurrencyList", Kv.by("cond", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }
}
