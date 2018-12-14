package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;

import java.sql.SQLException;
import java.util.Date;

public class SettaccService {

    private static final String TABLE_NAME = "settle_account_info";
    private static final String SQL_PREFIX = "settle.";
    private static final String ID = "id";

    private static String installKey(String key) {
        return SQL_PREFIX + key;
    }


    public Page<Record> getSettaccPage(int pageNum, int pageSize, Record record) {
        SqlPara sqlPara = Db.getSqlPara(installKey("settlePage"), Ret.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public void add(final Record record) throws BusinessException {
        record.remove("id");
        uniqueAccno(record, "add");
        checkAndbuild(record);
        record.set("status", 1);
        record.set("open_date", TypeUtils.castToDate(record.get("open_date")));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.save(TABLE_NAME, ID, record);
            }
        });
        if (!flag) {
            throw new DbProcessException("新增结算账户失败");
        }
    }

    private void checkAndbuild(Record record) throws ReqDataException {
        Record org = Db.findFirst(Db.getSqlPara("org.findOrgInfoById", record.get("org_id")));
        if (org == null) {
            throw new ReqDataException("机构信息不存在，请刷新重试！");
        }
        Record curr = Db.findById("currency", "id", record.get("curr_id"));
        if (curr == null) {
            throw new ReqDataException("币种不存在，请刷新重试！");
        }
        Record bank = Db.findById("all_bank_info", "cnaps_code", record.get("cnaps_code"));
        if (bank == null) {
            throw new ReqDataException("银行不存在，请刷新重试！");
        }
        record.set("bank_name", bank.get("name"));
        record.set("bank_type", bank.get("bank_type"));
        record.set("curr_name", curr.get("name"));
        record.set("org_name", org.get("name"));
    }

    public void del(final Record record) throws BusinessException {
        Record old = Db.findById(TABLE_NAME, ID, record.get(ID));
        record.set("acc_no", TypeUtils.castToString(old.get("acc_no")) + "_" + record.get(ID));
        record.set("status", 3);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update(TABLE_NAME, ID, record);
            }
        });
        if (!flag) {
            throw new DbProcessException("删除结算账户失败！");
        }
    }


    public void chg(final Record record) throws BusinessException {
        uniqueAccno(record, "chg");
        checkAndbuild(record);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() {
                return Db.update(TABLE_NAME, ID, record);

            }
        });
        if (!flag) {
            throw new DbProcessException("修改结算账户失败！");
        }
    }

    private long getAccnoNumByAccno(String accNo) {
        return Db.queryLong(Db.getSql(installKey("getAccnoNumByAccno")), accNo);
    }

    private void uniqueAccno(Record record, String methodName) throws ReqDataException {
        String acc_no = TypeUtils.castToString(record.get("acc_no"));
        if ("add".equals(methodName) && getAccnoNumByAccno(acc_no) > 0) {
            throw new ReqDataException("结算账户编号已存在！");
        } else if ("chg".equals(methodName)) {
            long re = Db.queryLong(Db.getSql(installKey("chgByAccno")), acc_no, record.get(ID));
            if (re > 0) {
                throw new ReqDataException("结算账户编号已存在！");
            }
        }
    }

    public void setstatus(final Record record) throws BusinessException {
        Record thisRecord = Db.findFirst(Db.getSql(installKey("getSettleById")), record.get(ID));
        if (thisRecord == null) {
            throw new ReqDataException("结算账户不存在！");
        }
        int status = TypeUtils.castToInt(thisRecord.get("status"));
        record.set("status", status == 1 ? 2 : 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update(TABLE_NAME, ID, record);
            }
        });
        if (!flag) {
            throw new DbProcessException("修改结算账户状态失败！");
        }
    }
}
