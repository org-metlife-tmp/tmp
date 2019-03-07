package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/21
 * @Description: 电子回单
 */
public class EleService {

    /**
     * 电子回单类型
     *
     * @param record
     */
    public List<Record> type(final Record record) {
        SqlPara sqlPara = Db.getSqlPara("ele.findElectronicType", Ret.by("map", record.getColumns()));
        return Db.find(sqlPara);
    }

    /**
     * 电子回单模板
     *
     * @param record
     * @return
     */
    public Record template(final Record record) {
        String uuid = TypeUtils.castToString(record.get("uuid"));
        SqlPara sqlPara = Db.getSqlPara("ele.findElectronicBillTemplate", Ret.by("map", record.getColumns()));
        List<Record> templateList = Db.find(sqlPara);

        Record finalRec = new Record();
        for (Record rec : templateList) {
            String ref_fd = rec.get("ref_fd");
            rec.remove("ref_fd");
            rec.remove("uuid");

            finalRec.set(ref_fd, rec);
        }

        return new Record().set(uuid, finalRec);
    }

    /**
     * 电子回单列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @returnv
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("ele.findElectronicBillList", Ret.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }
}
