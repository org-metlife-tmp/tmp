package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;

import java.sql.SQLException;

public class HandlechannelService {

    public Page<Record> getHandlechannelPage(int pageNum, int pageSize, Record record) {
        SqlPara sqlPara = Db.getSqlPara("channel.getChannelPage", Ret.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public void add(final Record record) throws BusinessException {
        Record thisRecord = Db.findById("handle_channel_setting", "code", record.get("code"));
        if (thisRecord != null) {
            throw new ReqDataException("处理渠道信息已存在！[code不可重复选择！]");
        }
        record.set("is_activate", 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.save("handle_channel_setting", "code", record);
            }
        });
        if (!flag) {
            throw new DbProcessException("新增渠道信息失败！");
        }
    }

    public void setstatus(final Record record) throws BusinessException {
        Record thisRecord = Db.findById("handle_channel_setting", "code", record.get("code"));
        if (thisRecord == null) {
            throw new ReqDataException("处理渠道信息不存在！");
        }
        int is_activate = TypeUtils.castToInt(thisRecord.get("is_activate"));
        record.set("is_activate", is_activate == 1 ? 0 : 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("handle_channel_setting", "code", record);
            }
        });
        if (!flag) {
            throw new DbProcessException("修改处理渠道状态失败！");
        }
    }
}
