package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/7/22
 * @Description: 支付通-退票重复
 */
public class ZftRefundService {

    /**
     * 退票重复列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        Kv kv = Kv.create();
        kv.set(record.getColumns());
        kv.set("is_checked", 0);
        kv.set("direction",WebConstant.PayOrRecv.PAYMENT.getKey());
        kv.set("checked_ref", new String[]{"outer_zf_payment", "oa_head_payment", "oa_branch_payment_item"});//支付通、OA付款
        SqlPara sqlPara = Db.getSqlPara("zftrefund.findZftRefundList", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public Record confirm(final Record record, final UserInfo userInfo, UodpInfo uodpInfo) throws BusinessException {
        final Long transId = TypeUtils.castToLong(record.get("trans_id"));//交易id
        final String bizId = TypeUtils.castToString(record.get("biz_id"));//业务类型id
        final Long oppAccId = TypeUtils.castToLong(record.get("opp_acc_id"));//对方帐号ID
        final String oppAccNo = TypeUtils.castToString(record.get("opp_acc_no"));//对方帐号
        final String oppAccName = TypeUtils.castToString(record.get("opp_acc_name"));//对方帐号名称
        final String cnapsCode = TypeUtils.castToString(record.get("opp_cnaps_code"));//对方银行
        final String memo = TypeUtils.castToString(record.get("summary"));//摘要
        final Record finalRec = new Record();

        //根据transid查询交易信息
        Record transRec = Db.findById("acc_his_transaction", "id", transId);
        if (transRec == null) {
            throw new ReqDataException("未找到有效的交易信息！");
        }

        Long accId = TypeUtils.castToLong(transRec.get("acc_id"));
        //根据付款方id查询帐号信息
        Record accRec = Db.findById("account", "acc_id", accId);
        if (accRec == null) {
            throw new ReqDataException("未找到有效的付款方信息！");
        }

        //根据收款方cnaps查询银行大类
        Record payBankTypeRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(accRec.get("bank_cnaps_code")));
        if (payBankTypeRec == null) {
            throw new ReqDataException("未找到有效的银行大类！");
        }

        //根据付款方币种id查询币种信息
        final Record curRec = Db.findById("currency", "id", TypeUtils.castToLong(accRec.get("curr_id")));
        if (curRec == null) {
            throw new ReqDataException("未找到有效的币种信息！");
        }

        finalRec.set("org_id", uodpInfo.getOrg_id());
        finalRec.set("dept_id", uodpInfo.getDept_id());

        finalRec.set("pay_account_id", TypeUtils.castToLong(accRec.get("acc_id")));
        finalRec.set("pay_account_no", TypeUtils.castToString(accRec.get("acc_no")));
        finalRec.set("pay_account_name", TypeUtils.castToString(accRec.get("acc_name")));
        finalRec.set("pay_account_cur", TypeUtils.castToString(curRec.get("iso_code")));
        finalRec.set("pay_account_bank", TypeUtils.castToString(payBankTypeRec.get("name")));
        finalRec.set("pay_bank_cnaps", TypeUtils.castToString(accRec.get("bank_cnaps_code")));
        finalRec.set("pay_bank_prov", TypeUtils.castToString(payBankTypeRec.get("province")));
        finalRec.set("pay_bank_city", TypeUtils.castToString(payBankTypeRec.get("city")));
        finalRec.set("payment_amount", TypeUtils.castToBigDecimal(transRec.get("amount")));
        finalRec.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ZFT);
        finalRec.set("service_serial_number", serviceSerialNumber);
        finalRec.set("pay_mode", WebConstant.PayMode.DIRECTCONN.getKey());
        finalRec.set("create_by", userInfo.getUsr_id()); //用户id
        finalRec.set("create_on", new Date()); //创建时间
        finalRec.set("persist_version", 0); //版本号

        if (memo != null && !memo.equals("")) {//摘要
            finalRec.set("payment_summary", memo);
        }

        if (bizId != null && !bizId.equals("")) {
            //根据bizid查询业务类型
            Record bizRec = Db.findById("cfm_biz_type_setting", "biz_id", bizId);
            if (bizRec == null) {
                throw new ReqDataException("为找到有效的业务类型！");
            }
            finalRec.set("biz_id", bizId);
            finalRec.set("biz_name", TypeUtils.castToString(bizRec.get("biz_name")));
        }

        if (oppAccId != null) {
            //根据id查询收款方帐号信息
            Record oppRec = Db.findById("supplier_acc_info", "id", oppAccId);
            if (oppRec == null) {
                throw new ReqDataException("未找到有效的对方账户号！");
            }

            //根据币种id查询币种信息
            Record oppCurRec = Db.findById("currency", "id", TypeUtils.castToLong(oppRec.get("curr_id")));
            if (oppCurRec == null) {
                throw new ReqDataException("未找到有效的币种信息！");
            }

            finalRec.set("recv_account_id", oppAccId);
            finalRec.set("recv_account_no", TypeUtils.castToString(oppRec.get("acc_no")));
            finalRec.set("recv_account_name", TypeUtils.castToString(oppRec.get("acc_name")));
            finalRec.set("recv_account_cur", TypeUtils.castToString(curRec.get("iso_code")));
            finalRec.set("recv_account_bank", TypeUtils.castToString(oppRec.get("bank_name")));
            finalRec.set("recv_bank_cnaps", TypeUtils.castToString(oppRec.get("cnaps_code")));
            finalRec.set("recv_bank_prov", TypeUtils.castToString(oppRec.get("province")));
            finalRec.set("recv_bank_city", TypeUtils.castToString(oppRec.get("city")));
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean flag = false;
                /**
                 * 对方帐号ID为空，往收款方信息表中插入一条数据
                 * 对方帐号ID不为空，则使用收款方帐号信息表
                 */
                if (oppAccId == null) {
                    //根据cnaps查询银行大类
                    Record bankRec = Db.findById("all_bank_info", "cnaps_code", cnapsCode);
                    if (bankRec == null) {
                        return false;
                    }

                    Record supplierRec = new Record();
                    //新增收款方信息
                    supplierRec.set("acc_no", oppAccNo);
                    supplierRec.set("acc_name", oppAccName);
                    supplierRec.set("cnaps_code", cnapsCode);
                    supplierRec.set("bank_name", TypeUtils.castToString(bankRec.get("name")));
                    supplierRec.set("province", TypeUtils.castToString(bankRec.get("province")));
                    supplierRec.set("city", TypeUtils.castToString(bankRec.get("city")));
                    supplierRec.set("address", TypeUtils.castToString(bankRec.get("address")));
                    supplierRec.set("curr_id", 1);//默认人民币
                    supplierRec.set("create_by", userInfo.getUsr_id());
                    supplierRec.set("create_on", new Date());
                    supplierRec.set("type", 2);//默认个人

                    flag = Db.save("supplier_acc_info", supplierRec);
                    if (flag) {
                        finalRec.set("recv_account_id", TypeUtils.castToLong(supplierRec.get("id")));
                        finalRec.set("recv_account_no", oppAccNo);
                        finalRec.set("recv_account_name", oppAccName);
                        finalRec.set("recv_account_cur", TypeUtils.castToString(curRec.get("iso_code")));
                        finalRec.set("recv_account_bank", TypeUtils.castToString(bankRec.get("name")));
                        finalRec.set("recv_bank_cnaps", cnapsCode);
                        finalRec.set("recv_bank_prov", TypeUtils.castToString(bankRec.get("province")));
                        finalRec.set("recv_bank_city", TypeUtils.castToString(bankRec.get("city")));

                        return Db.save("outer_zf_payment", finalRec);
                    }
                    return false;
                } else {
                    return Db.save("outer_zf_payment", finalRec);
                }
            }
        });

        if (!flag) {
            throw new DbProcessException("保存失败！");
        }

        return finalRec;
    }
}
