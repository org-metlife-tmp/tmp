package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;

import java.sql.SQLException;

public class PositionService {

    private static final String POS = "position.";
    private static final String TABLE_NAME = "position_info";
    private static final String ID = "pos_id";
    private static final String STATUS = "status";
    private static final String NAME = "name";

    private static String installKey(String key) {
        return POS + key;
    }

    public Page<Record> getPositionPage(int pageNum, int pageSize, Record record) {
        SqlPara sqlPara = Db.getSqlPara(installKey("getPositionPage"), Ret.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }


    private void uniqueNameAdd(Record record) throws ReqDataException{
        long re = Db.queryLong(Db.getSql(installKey("getPosNumByName")),record.get(NAME));
        if(re > 0){
            throw new ReqDataException("此名称已使用！");
        }
    }

    private void uniqueNameChg(Record record) throws ReqDataException{
        long re = Db.queryLong(Db.getSql(installKey("getPosNumByNameExcludeId")),record.get(NAME),record.get(ID));
        if(re > 0){
            throw new ReqDataException("此名称已存在！");
        }
    }

    public void add(final Record record) throws BusinessException {
        record.remove("pos_id");
        uniqueNameAdd(record);
        record.set(STATUS, 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.save(TABLE_NAME, ID, record);
            }
        });
        if (!flag) {
            throw new DbProcessException("新增职业数据失败！");
        }
    }

    public void del(final Record record) throws BusinessException {
        checkUse(record.getLong(ID), "此职位已使用，禁止删除！");
        record.set(NAME, record.getStr(NAME) + "_" + record.get(ID));
        record.set(STATUS, 3);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update(TABLE_NAME, ID, record);
            }
        });
        if (!flag) {
            throw new DbProcessException("修改职业数据失败！");
        }
    }

    public void chg(final Record record) throws BusinessException {
        uniqueNameChg(record);
        checkUse(record.getLong(ID), "此职位已使用，禁止修改！");
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update(TABLE_NAME, ID, record);
            }
        });
        if (!flag) {
            throw new DbProcessException("修改职业数据失败！");
        }
    }

    public void setState(final Record record) throws BusinessException {
        Record thisRecord = getPosInfo(record.getLong(ID));
        if (thisRecord == null) {
            throw new ReqDataException("职位信息不存在！");
        }
        int status = TypeUtils.castToInt(thisRecord.get(STATUS));
        record.set(STATUS, status == 1 ? 2 : 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update(TABLE_NAME, ID, record);
            }
        });
        if (!flag)
            throw new DbProcessException("修改职位状态失败！");
    }

    private void checkUse(long pos_id, String errorMsg) throws BusinessException {
        if (checkUse(pos_id)) {
            throw new ReqDataException(errorMsg);
        }
    }

    private boolean checkUse(long pos_id) {
        String sql = Db.getSql(installKey("checkUse"));
        long counts = Db.queryLong(sql, pos_id);
        return counts > 0;
    }

    private Record getPosInfo(long pos_id) {
        String sql = Db.getSql(installKey("getPosInfo"));
        return Db.findFirst(sql, pos_id);
    }
}
