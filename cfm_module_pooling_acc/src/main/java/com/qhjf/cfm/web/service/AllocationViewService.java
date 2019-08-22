package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.UodpInfo;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/19
 * @Description: 资金下拨查看
 */
public class AllocationViewService {

    public Page<Record> list(int pageNum, int pageSize, final Record record, final UodpInfo uodpInfo) throws ReqDataException {

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());
        if (orgRec == null) {
            throw new ReqDataException("机构信息不存在!");
        }

        Kv kv = Kv.create();
        kv.set("delete_flag", 0);
        kv.set("level_code", orgRec.get("level_code"));
        kv.set("level_num", orgRec.get("level_num"));
        SqlPara sqlPara = getListParam(record, "allocation.findViewList", kv);
        Page<Record> page = Db.paginate(pageNum, pageSize, sqlPara);
        for (Record rec : page.getList()) {
            //根据topicid查询下拨频率
            List<Record> timesetList = Db.find(Db.getSql("allocation.findTimesettingByAlloId"), TypeUtils.castToLong(rec.get("id")));
            rec.set("frequency_detail", timesetList);
        }
        return page;
    }

    /**
     * 查询参数组装
     *
     * @param record
     * @param sql
     * @param kv
     * @return
     */
    public SqlPara getListParam(final Record record, String sql, final Kv kv) {
        List<Record> mainAccountList = null;
        String mainacc = TypeUtils.castToString(record.get("main_account_query"));
        Record mainrec = new Record();
        //是否包含中文
        if (StringKit.isContainChina(mainacc)) {
            //名称
            mainrec.set("main_acc_name", mainacc);
        } else {
            //帐号
            mainrec.set("main_acc_no", mainacc);
        }


        if (mainacc != null && !"".equals(mainacc)) {
            //根据主账户号查询符号条件的主表账户id
            SqlPara sqlPara = Db.getSqlPara("allocation.findMainAccount", Ret.by("map", mainrec.getColumns()));
            mainAccountList = Db.find(sqlPara);
            Object[] poolids = null;
            if (mainAccountList.size() > 0) {
                poolids = mainAccountList.toArray();
            }
            kv.set("allocationids", poolids);
        }

        String queryKey = TypeUtils.castToString(record.get("query_key"));
        if (queryKey != null && !"".equals(queryKey)) {
            kv.set("query_key", queryKey);
        }

        kv.set("service_status", record.get("service_status"));
        return Db.getSqlPara(sql, Kv.by("map", kv));
    }
}
