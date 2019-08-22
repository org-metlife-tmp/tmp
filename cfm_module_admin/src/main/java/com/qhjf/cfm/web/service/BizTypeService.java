package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BizTypeService {

    public List<Record> getAllBizTypes() {
        return Db.find(Db.getSql("biztype.getAllBizTypes"));
    }

    public void add(final Record record) throws BusinessException {
        String uuid = UUID.randomUUID().toString().replace("-","");
        record.set("biz_id",uuid);
        record.set("is_delete", WebConstant.YesOrNo.NO.getKey());
        record.set("is_activity", WebConstant.YesOrNo.YES.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.save("biz_type", "biz_id", record);
            }
        });
        if (!flag) {
            throw new DbProcessException("添加业务类型失败！");
        }
    }

    public void chg(final Record record) throws BusinessException {
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("biz_type", "biz_id", record);
            }
        });
        if (!flag) {
            throw new DbProcessException("修改业务类型失败！");
        }
    }

    public void del(final Record record) throws BusinessException {
        record.set("is_delete", WebConstant.YesOrNo.YES.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("biz_type", "biz_id", record);
            }
        });
        if (!flag) {
            throw new DbProcessException("删除业务类型失败！");
        }
    }

    public void setstatus(final Record record) throws BusinessException {
        Record old = Db.findById("biz_type", "biz_id", record.get("biz_id"));
        int oldIsActtivity = TypeUtils.castToInt(old.get("is_activity"));
        record.set("is_activity", oldIsActtivity == WebConstant.YesOrNo.YES.getKey()
                ? WebConstant.YesOrNo.NO.getKey() : WebConstant.YesOrNo.YES.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("is_activity", "biz_id", record);
            }
        });

        if (!flag) {
            throw new DbProcessException("修改业务类型状态失败！");
        }
    }

}
