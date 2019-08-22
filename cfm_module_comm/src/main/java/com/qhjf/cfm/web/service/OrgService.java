package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/7/4
 * @Description: 机构信息
 */
public class OrgService {

    public List<Record> lsit() {
        String sql = Db.getSql("org.getOrgList");
        return Db.find(sql);
    }

    public List<Record> curlist(long org_id) {
        return Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
    }
}
