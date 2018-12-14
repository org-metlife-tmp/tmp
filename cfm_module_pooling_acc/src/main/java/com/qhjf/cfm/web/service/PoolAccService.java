package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UserInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 资金池账户设置
 *
 * @author GJF
 */
public class PoolAccService {

    /**
     * 资金池账户列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> acclist(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("poolAcc.acclist", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 新增一条账户信息
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record add(final Record record) throws BusinessException {
        final String bankType = TypeUtils.castToString(record.get("bank_type"));
        int accId = TypeUtils.castToInt(record.get("acc_id"));
        //检验该账户是否存在
        Record oldAcc = Db.findFirst(Db.getSqlPara("poolAcc.findPoolByCodeAccid",
                Kv.by("map", new Record().set("bank_type", bankType).set("acc_id", accId).getColumns())));

        if (oldAcc != null) {
            throw new ReqDataException("该账户已存在!");
        }
        final int defaultFlag = TypeUtils.castToInt(record.get("default_flag"));

        //根据code查询银行信息
        Record bankRec = Db.findById("const_bank_type", "code", bankType);
        if (bankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (defaultFlag == 1) {
                    //查询当前银行下是否有默认的账户存在
                    Record rc = Db.findFirst(Db.getSqlPara("poolAcc.findPoolByCodeAccid",
                            Kv.by("map", new Record().set("default_flag", 1).getColumns())));
                    if (rc != null) {
                        //存在更新当前银行大类的所有账户为非默认
                        updateDefault(bankType);
                    }
                }
                return Db.save("pooling_acc_setting", record);
            }
        });
        if (flag) {
            return Db.findById("pooling_acc_setting", "id", TypeUtils.castToLong(record.get("id")));
        }

        throw new DbProcessException("保存供应商信息失败!");
    }

    public boolean updateDefault(String code) {
        return Db.update("update pooling_acc_setting set default_flag = 0") >= 0;
    }

    /**
     * 删除一条账户信息
     *
     * @param record
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public void delete(final Record record) throws BusinessException {
        final Integer id = TypeUtils.castToInt(record.get("id"));
        Record rc = Db.findById("pooling_acc_setting", "id", id);
        if (rc == null) {
            throw new ReqDataException("该账户不存在!");
        }
        if (rc.getInt("default_flag") == 1) {
            throw new ReqDataException("该账户为默认账户，不允许删除!");
        }

        //进行数据删除操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("pooling_acc_setting", "id", new Record().set("id", id).set("delete_flag", 1));
            }
        });
        if (!flag) {
            throw new DbProcessException("删除账户失败！");
        }
    }

    /**
     * 设为默认
     *
     * @param record
     * @return
     */
    public void defaultset(final Record record) throws BusinessException {
        final Integer id = TypeUtils.castToInt(record.get("id"));
        final String bank_type = TypeUtils.castToString(record.get("bank_type"));
        Record rc = Db.findFirst("select * from pooling_acc_setting where delete_flag = 0 and id = ?", id);
        if (rc == null) {
            throw new ReqDataException("该账户不存在!");
        }

        //进行数据更新操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //查询当前银行下是否有默认的账户存在
                Record rc = Db.findFirst(Db.getSqlPara("poolAcc.findPoolByCodeAccid",
                        Kv.by("map", new Record().set("default_flag", 1).getColumns())));
                if (rc != null) {
                    //存在更新当前银行大类的所有账户为非默认
                    updateDefault(bank_type);
                }
                return Db.update("pooling_acc_setting", "id",
                        new Record().set("id", id).set("default_flag", 1));
            }
        });
        if (!flag) {
            throw new DbProcessException("设为默认账户失败！");
        }
    }

    /**
     * 根据银行大类id查询账户
     *
     * @param record
     * @return
     */
    public List<Record> getaccbybank(final Record record) throws BusinessException {
        return Db.find(Db.getSql("poolAcc.getaccbybank"), record.get("bank_type"));
    }

    /**
     * 根据银行大类bank_type or 默认资金池账户查询(文哥专用)
     *
     * @param record
     * @return
     */
    public List<Record> getpoolaccinfo(final Record record) {
        SqlPara sqlPara = Db.getSqlPara("poolAcc.getDefaultAcc", Ret.by("map", record.getColumns()));
        //先根据banktype查询
        List<Record> list = Db.find(sqlPara);
        //如果没有查到数据则查询默认账户查询
        if (list.size() == 0) {
            record.set("default_flag", 1);
            record.remove("bank_type");
            sqlPara = Db.getSqlPara("poolAcc.getDefaultAcc", Ret.by("map", record.getColumns()));
            list = Db.find(sqlPara);
        }
        return list;
    }

    /**
     * @选取默认账户
     * @param record
     * @return
     */
	public List<Record> getDefaultpoolacc(Record record) {
		record.set("default_flag", 1);
		SqlPara sqlPara = Db.getSqlPara("poolAcc.getDefaultAcc", Ret.by("map", record.getColumns()));
		List<Record> list = Db.find(sqlPara);
		return list ;
	}

}
