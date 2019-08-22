package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.utils.Query4PinYinTool;

import java.util.List;

public class BankQueryService {

    private final static Log logger = LogbackLog.getLog(BankQueryService.class);


    public Record getBankType(Record record) {
        logger.debug("Enter into getBankType()");
        Record finalRec = new Record();
        String query_key = "";
        if (record != null) {
            query_key = TypeUtils.castToString(record.get("query_key"));
        }
        Kv cond = Kv.create();
        Query4PinYinTool.processPinyinQuery(cond, query_key);
        SqlPara sql = Db.getSqlPara("bank.allBankType", Kv.by("map", cond));
        List<Record> banktypequeryList = Db.find(sql);

        sql = Db.getSqlPara("bank.findConstBankType");
        List<Record> banktypeList = Db.find(sql);


        finalRec.set("alias", banktypequeryList)
                .set("standard", banktypeList);

        return finalRec;
    }

    public List<Record> getBankList(Record record) {
        logger.debug("Enter into getBankType()");
        String query_key = TypeUtils.castToString(record.get("query_key"));
        String area_code = TypeUtils.castToString(record.get("area_code"));
        String bank_type = TypeUtils.castToString(record.get("bank_type"));
        String province = TypeUtils.castToString(record.get("province"));
        String city = TypeUtils.castToString(record.get("city"));
        Kv cond = Kv.create();
        if (area_code != null && !"".equals(area_code)) {
            cond.put("area_code", area_code);
        }
        if (bank_type != null && !"".equals(bank_type)) {
            cond.put("bank_type", bank_type);
        }
        if (province != null && !"".equals(province)) {
            cond.put("province", province);
        }
        if (city != null && !"".equals(city)) {
            cond.put("city", city);
        }
        Query4PinYinTool.processPinyinQuery(cond, query_key);
        SqlPara sql = Db.getSqlPara("bank.bankList", Kv.by("map", cond));
        return Db.find(sql);
    }
}
