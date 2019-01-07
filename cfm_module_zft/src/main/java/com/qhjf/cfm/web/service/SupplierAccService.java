package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.UserInfo;

import java.sql.SQLException;
import java.util.Date;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9 10:37
 * @Description: 供应商信息管理
 */
public class SupplierAccService {

    /**
     * 查询供应商列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        String query_key = record.get("query_key");
        record.remove("query_key");

        Kv kv = Kv.create();

        //是否包含中文
        boolean flag = StringKit.isContainChina(query_key);
        if (flag) {
            //名称
            kv.set("acc_name", query_key);
        } else {
            //帐号
            kv.set("acc_no", query_key);
        }

        kv.set("delete_flag", 0);
        SqlPara sqlPara = Db.getSqlPara("supplier.findSupplierAccInfoList", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 添加供应商信息
     *
     * @param record
     * @return
     */
    public Record add(final Record record, UserInfo userInfo) throws BusinessException {
        String accNo = TypeUtils.castToString(record.get("acc_no"));
        //校验帐号是否存在
        supplierAccNoValidate(accNo);

        String cnapsCode = TypeUtils.castToString(record.get("cnaps_code"));
        //根据cnaps查询银行信息
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", cnapsCode);
        if (bankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }

        long currId = TypeUtils.castToLong(record.get("curr_id"));
        //根据币种id查询币种信息
        Record currRec = Db.findById("currency", "id", currId);
        if (currRec == null) {
            throw new ReqDataException("未找到有效的币种信息!");
        }

        record.set("bank_name", TypeUtils.castToString(bankRec.get("name")));
        record.set("province", TypeUtils.castToString(bankRec.get("province")));
        record.set("city", TypeUtils.castToString(bankRec.get("city")));
        record.set("address", TypeUtils.castToString(bankRec.get("address")));

        record.set("create_by", userInfo.getUsr_id());
        record.set("create_on", new Date());

        record.set("persist_version", 0);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.save("supplier_acc_info", record);
            }
        });
        if (flag) {
            return Db.findById("supplier_acc_info", "id", TypeUtils.castToLong(record.get("id")));
        }

        throw new DbProcessException("保存供应商信息失败!");
    }

    /**
     * 修改供应商信息
     *
     * @param record
     * @return
     */
    public Record chg(final Record record, UserInfo userInfo) throws BusinessException {

        long id = TypeUtils.castToLong(record.get("id"));
        String accNo = TypeUtils.castToString(record.get("acc_no")).trim();

        //根据id查询供应商信息
        Record supplierRec = Db.findById("supplier_acc_info", "id", id);
        if (supplierRec == null) {
            throw new ReqDataException("未找到有效的供应商信息!");
        }

        //如果修改前帐号与修改后帐号不一致则校验帐号是否存在
        if (!TypeUtils.castToString(supplierRec.get("acc_no")).equals(accNo)) {
            supplierAccNoValidate(accNo);
        }

        long currId = TypeUtils.castToLong(record.get("curr_id"));
        //根据币种id查询币种信息
        Record currRec = Db.findById("currency", "id", currId);
        if (currRec == null) {
            throw new ReqDataException("未找到有效的币种信息!");
        }

        String cnapsCode = TypeUtils.castToString(record.get("cnaps_code"));
        //根据cnaps查询银行信息
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", cnapsCode);
        if (bankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }

        record.set("bank_name", TypeUtils.castToString(bankRec.get("name")));
        record.set("province", TypeUtils.castToString(bankRec.get("province")));
        record.set("city", TypeUtils.castToString(bankRec.get("city")));
        record.set("address", TypeUtils.castToString(bankRec.get("address")));

        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.set("persist_version", TypeUtils.castToInt(supplierRec.get("persist_version")) + 1);


        int old_version = TypeUtils.castToInt(supplierRec.get("persist_version"));

        record.remove("id");

        boolean flag = chgSupplierAccInfoByIdAndVersion(record,
                new Record().set("id", id).set("persist_version", old_version));

        if (flag) {
            return Db.findById("supplier_acc_info", "id", id);
        }

        throw new DbProcessException("修改供应商信息失败!");
    }

    /**
     * 删除供应商信息
     *
     * @param record
     */
    public void del(final Record record) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        //根据id查询供应商信息
        Record supplierRec = Db.findById("supplier_acc_info", "id", id);
        if (supplierRec == null) {
            throw new ReqDataException("未找到有效的供应商信息!");
        }

        record.set("acc_no", TypeUtils.castToString(supplierRec.get("acc_no")) + "_" + id);
        record.set("persist_version", TypeUtils.castToInt(supplierRec.get("persist_version")) + 1);
        record.set("delete_flag", 1);

        int old_version = TypeUtils.castToInt(supplierRec.get("persist_version"));

        record.remove("id");

        boolean flag = chgSupplierAccInfoByIdAndVersion(record,
                new Record().set("id", id).set("persist_version", old_version));

        if (!flag) {
            throw new DbProcessException("删除供应商信息失败!");
        }
    }

    public boolean chgSupplierAccInfoByIdAndVersion(final Record set, final Record where) {
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("supplier_acc_info", set, where);
            }
        });
    }

    /**
     * 查询该帐号是否已存在
     *
     * @param accNo
     * @return
     */
    public void supplierAccNoValidate(String accNo) throws BusinessException {
        Record supplierRec = Db.findFirst(Db.getSql("supplier.findSupplierByAccNo"), 0, accNo);
        if (supplierRec != null) {
            throw new ReqDataException("该收款方帐号已存在!");
        }
    }
}
