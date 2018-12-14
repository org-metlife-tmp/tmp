package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.constant.WebConstant;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AccConfirmService {

    public Page<Record> getPage(int page_num, int page_size, Record record) {
        Page<Record> page = Db.paginate(page_num, page_size, Db.getSqlPara("acc.accConfirmPage", Kv.by("map", record.getColumns())));
        for (Record rec : page.getList()) {
            //根据账户id查询账户拓展信息
            List<Record> extRecList = Db.find(Db.getSql("acc.findAccountExtInfo"), TypeUtils.castToLong(rec.get("acc_id")));
            for (Record ext : extRecList) {
                rec.set(TypeUtils.castToString(ext.get("type_code")), TypeUtils.castToString(ext.get("value")));
            }
        }
        return page;
    }

    public void setstatus(final Record record) throws BusinessException {
        Record thisRecord = Db.findById("account", "acc_id", record.get("acc_id"));
        if (thisRecord == null) {
            throw new ReqDataException("账户信息不存在！");
        }
        record.set("is_activity", WebConstant.YesOrNo.YES.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("account", "acc_id", record);
            }
        });
        if (!flag) {
            throw new DbProcessException("修改账户状态失败！");
        }
    }

    /**
     * 销户确认列表
     *
     * @param page_num
     * @param page_size
     * @param record
     * @return
     */
    public Page<Record> getCloseList(int page_num, int page_size, Record record) {
        Page<Record> page =  Db.paginate(page_num, page_size, Db.getSqlPara("acc.accCloseConfirmPage", Kv.by("map", record.getColumns())));
        for (Record rec : page.getList()) {
            //根据账户id查询账户拓展信息
            List<Record> extRecList = Db.find(Db.getSql("acc.findAccountExtInfo"), TypeUtils.castToLong(rec.get("acc_id")));
            for (Record ext : extRecList) {
                rec.set(TypeUtils.castToString(ext.get("type_code")), TypeUtils.castToString(ext.get("value")));
            }
        }
        return page;
    }

    /**
     * 销户确认修改
     *
     * @param record
     * @throws BusinessException
     */
    public void setCloseStatus(final Record record) throws BusinessException {
        Record thisRecord = Db.findById("account", "acc_id", record.get("acc_id"));
        if (thisRecord == null) {
            throw new ReqDataException("账户信息不存在！");
        }

        //销户确认
        record.set("is_close_confirm", 1);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("account", "acc_id", record);
            }
        });

        if (!flag) {
            throw new DbProcessException("销户确认失败!");
        }
    }

}
