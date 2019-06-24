package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/5/31
 * @Description: 凭证查询
 */
public class VoucherQueryService {

    /**
     * 凭证查询列表(分页)
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */

    public Page<Record> page(int pageNum, int pageSize, Record record) throws BusinessException {
        SqlPara sqlPara = findByListParam(record);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 凭证查询列表
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public List<Record> list(Record record) throws BusinessException {
        SqlPara sqlPara = findByListParam(record);
        return Db.find(sqlPara);
    }

    private SqlPara findByListParam(Record record) throws ReqDataException {
        long orgId = TypeUtils.castToLong(record.get("org_id"));
        record.remove("org_id");
        //根据orgid查询机构信息
        Record orgRec = Db.findById("organization", "org_id", orgId);
        if (orgRec == null) {
            throw new ReqDataException("未找到有效的机构信息!");
        }

        record.set("level_num", TypeUtils.castToInt(orgRec.get("level_num")));
        record.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));

        return Db.getSqlPara("vquery.findSunVoucherDataList", Kv.by("map", record.getColumns()));
    }
}
