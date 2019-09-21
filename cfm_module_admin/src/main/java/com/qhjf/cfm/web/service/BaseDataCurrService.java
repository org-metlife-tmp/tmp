package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;

/**
 * 基础数据 - 币种
 *
 * @auther zhangyuanyuan
 * @create 2018/5/22
 */

public class BaseDataCurrService {

    /**
     * 获取币种列表
     *
     * @param record
     * @return
     */
    public Page<Record> findCurrencyPage(int pageNum,int pageSize ,final Record record) {
        SqlPara sqlPara = Db.getSqlPara("currency.getCurrencyList", Kv.by("cond", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 设置默认币种
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record currSetDefault(final Record record) throws BusinessException {

        boolean flag = Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {
                //币种默认全部设置为否：0
                String sql = Db.getSql("currency.currencyDefaultReset");
                Db.update(sql, 0);

                record.set("is_default", WebConstant.YesOrNo.YES.getKey());
                //根据币种id修改默认币种
                boolean flag = Db.update("currency", "id", record);
                if (flag) {
                    return true;
                }
                return false;
            }
        });
        if (flag) {
            return findCurrById(TypeUtils.castToLong(record.get("id")));
        }
        throw new DbProcessException("修改默认币种失败!");
    }

    public Record findCurrById(Long id) {
        String sql = Db.getSql("currency.findCurrencyById");
        return Db.findFirst(sql, id);
    }
}
