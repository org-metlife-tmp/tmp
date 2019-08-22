package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/30
 * @Description:
 */
public class BizTypeSettingService {

    public List<Record> biztypes(Record record) {
        return Db.find(Db.getSql("biztype.getBiztypesByPid"), record.get("p_id"));
    }
}
