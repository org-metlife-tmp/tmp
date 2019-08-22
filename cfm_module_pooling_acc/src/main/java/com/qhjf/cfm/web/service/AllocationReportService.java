package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.constant.WebConstant;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/19
 * @Description: 资金下拨报表
 */
public class AllocationReportService {


    public Record acctopchart(final Record record, final UodpInfo uodpInfo) {
        checkParam(record);
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(2);

        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());
        record.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));
        record.set("allocation_status", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());

        List<Record> accountList = new ArrayList<>();
        SqlPara sqlPara = Db.getSqlPara("allorep.getAccTotalAmount", Ret.by("map", record.getColumns()));
        Record totalAmountRec = Db.findFirst(sqlPara);
        BigDecimal totalAmount = TypeUtils.castToBigDecimal(totalAmountRec.get("sum_amount"));
        if (totalAmount != null && BigDecimal.ZERO.compareTo(totalAmount) != 0) {
            sqlPara = Db.getSqlPara("allorep.findAccAlloExcuteInsTopchar", Ret.by("map", record.getColumns()));
            List<Record> alloExcuteInsList = Db.find(sqlPara);

            if (alloExcuteInsList != null && alloExcuteInsList.size() > 0) {
                for (Record ins : alloExcuteInsList) {
                    BigDecimal amount = TypeUtils.castToBigDecimal(ins.get("amount"));
                    Double percentage = amount.divide(totalAmount, 4, BigDecimal.ROUND_UP).doubleValue();

                    if (null != percentage) {
                        ins.set("percentage", fmt.format(percentage));
                    } else {
                        ins.set("percentage", "0%");
                    }

                    accountList.add(ins);

                }
            }
        }
        Record resRec = new Record();
        resRec.set("total_amount", totalAmount);
        resRec.set("accounts", accountList);

        return resRec;
    }

    public Record acclist(final Record record, final UodpInfo uodpInfo) {
        checkParam(record);
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(2);

        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());
        record.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));
        record.set("allocation_status", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());

        List<Record> accountList = new ArrayList<>();
        SqlPara sqlPara = Db.getSqlPara("allorep.getAccTotalAmount", Ret.by("map", record.getColumns()));
        Record totalAmountRec = Db.findFirst(sqlPara);
        BigDecimal totalAmount = TypeUtils.castToBigDecimal(totalAmountRec.get("sum_amount"));
        if (totalAmount != null && BigDecimal.ZERO.compareTo(totalAmount) != 0) {
            sqlPara = Db.getSqlPara("allorep.findAccAlloExcuteInsList", Ret.by("map", record.getColumns()));
            List<Record> alloExcuteInsList = Db.find(sqlPara);

            if (alloExcuteInsList != null && alloExcuteInsList.size() > 0) {
                for (Record ins : alloExcuteInsList) {
                    BigDecimal amount = TypeUtils.castToBigDecimal(ins.get("amount"));
                    Double percentage = amount.divide(totalAmount, 4, BigDecimal.ROUND_UP).doubleValue();

                    if (null != percentage) {
                        ins.set("percentage", fmt.format(percentage));
                    } else {
                        ins.set("percentage", "0%");
                    }

                    accountList.add(ins);

                }
            }
        }
        Record resRec = new Record();
        resRec.set("total_amount", totalAmount);
        resRec.set("accounts", accountList);

        return resRec;
    }

    public Record orgtopchar(final Record record, final UodpInfo uodpInfo) {
        checkParam(record);
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(2);

        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());
        record.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));
        record.set("allocation_status", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());

        List<Record> accountList = new ArrayList<>();
        SqlPara sqlPara = Db.getSqlPara("allorep.getOrgTotalAmount", Ret.by("map", record.getColumns()));
        Record totalAmountRec = Db.findFirst(sqlPara);
        BigDecimal totalAmount = TypeUtils.castToBigDecimal(totalAmountRec.get("sum_amount"));
        if (totalAmount != null && BigDecimal.ZERO.compareTo(totalAmount) != 0) {
            sqlPara = Db.getSqlPara("allorep.findOrgAlloExcuteInsTopchar", Ret.by("map", record.getColumns()));
            List<Record> alloExcuteInsList = Db.find(sqlPara);

            if (alloExcuteInsList != null && alloExcuteInsList.size() > 0) {
                for (Record ins : alloExcuteInsList) {
                    BigDecimal amount = TypeUtils.castToBigDecimal(ins.get("amount"));
                    Double percentage = amount.divide(totalAmount, 4, BigDecimal.ROUND_UP).doubleValue();

                    if (null != percentage) {
                        ins.set("percentage", fmt.format(percentage));
                    } else {
                        ins.set("percentage", "0%");
                    }

                    accountList.add(ins);

                }
            }
        }
        Record resRec = new Record();
        resRec.set("total_amount", totalAmount);
        resRec.set("accounts", accountList);

        return resRec;
    }

    public Record orglist(final Record record, final UodpInfo uodpInfo) {
        checkParam(record);
        NumberFormat fmt = NumberFormat.getPercentInstance();
        fmt.setMaximumFractionDigits(2);

        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());
        record.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));
        record.set("allocation_status", WebConstant.CollOrPoolRunStatus.SUCCESS.getKey());

        List<Record> accountList = new ArrayList<>();
        SqlPara sqlPara = Db.getSqlPara("allorep.getOrgTotalAmount", Ret.by("map", record.getColumns()));
        Record totalAmountRec = Db.findFirst(sqlPara);
        BigDecimal totalAmount = TypeUtils.castToBigDecimal(totalAmountRec.get("sum_amount"));
        if (totalAmount != null && BigDecimal.ZERO.compareTo(totalAmount) != 0) {
            sqlPara = Db.getSqlPara("allorep.findOrgAlloExcuteInsList", Ret.by("map", record.getColumns()));
            List<Record> alloExcuteInsList = Db.find(sqlPara);

            if (alloExcuteInsList != null && alloExcuteInsList.size() > 0) {
                for (Record ins : alloExcuteInsList) {
                    BigDecimal amount = TypeUtils.castToBigDecimal(ins.get("amount"));
                    Double percentage = amount.divide(totalAmount, 4, BigDecimal.ROUND_UP).doubleValue();

                    if (null != percentage) {
                        ins.set("percentage", fmt.format(percentage));
                    } else {
                        ins.set("percentage", "0%");
                    }

//                    List<Record> accList = Db.find(Db.getSql("allorep.getAccListByOrgId"), uodpInfo.getOrg_id());
//                    ins.set("accounts", accList);

                    accountList.add(ins);

                }
            }
        }
        Record resRec = new Record();
        resRec.set("total_amount", totalAmount);
        resRec.set("accounts", accountList);

        return resRec;
    }

    /**
     * 查询条件处理
     *
     * @param record
     */
    public void checkParam(final Record record) {
        List<Record> orgList = record.get("pay_account_org_id");
        List<Record> bankList = record.get("pay_bank_cnaps");
        if (orgList == null || orgList.size() == 0) {
            record.remove("pay_account_org_id");
        }

        if (bankList == null || bankList.size() == 0) {
            record.remove("pay_bank_cnaps");
        }
    }
}
