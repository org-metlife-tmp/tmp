package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.constant.WebConstant;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 余额调节表
 */
public class DztAdjustService {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat standFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 余额调节表列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        String queryKey = TypeUtils.castToString(record.get("query_key"));
        record.remove("query_key");

        //是否包含中文
        boolean isName = StringKit.isContainChina(queryKey);
        if (isName) {
            record.set("acc_name", queryKey);
        } else {
            record.set("acc_no", queryKey);
        }

        record.set("is_confirm", 1);
        SqlPara sqlPara = Db.getSqlPara("dztadjust.findCheckVoucherBalAdjust", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);

    }

    /**
     * 查询余额调节表
     *
     * @param record
     * @throws BusinessException
     * @throws ParseException
     */
    public Record build(final Record record) throws BusinessException, ParseException {

        String cdate = TypeUtils.castToString(record.get("cdate"));
        final Long accId = TypeUtils.castToLong(record.get("acc_id"));

        Record nowCalRec = null;
        Record preCalRec = null;
        BigDecimal voucherBal = null;//日记账余额(已核对数据计算后余额)

        //获取本月日期
        Calendar c = Calendar.getInstance();
        c.setTime(format.parse(cdate));
        Date now = c.getTime();

        c.add(Calendar.MONTH, -1); //月份减1
        Date preMonth = c.getTime(); //结果

        //根据年、月查询此月结账日期
        Kv kv = Kv.create();
        kv.set("is_checkout", 1);
        c.setTime(now);
        kv.set("year", c.get(Calendar.YEAR));
        kv.set("month", c.get(Calendar.MONTH) + 1);//月份从0开始 所以+1
        SqlPara sqlPara = Db.getSqlPara("dztadjust.findWorkingCalSetting", Kv.by("map", kv));
        nowCalRec = Db.findFirst(sqlPara);
        if (nowCalRec == null) {
            throw new ReqDataException(format.format(now) + "未设置结账日！");
        }


        c.setTime(preMonth);
        //上一月
        kv.set("year", c.get(Calendar.YEAR));
        kv.set("month", c.get(Calendar.MONTH) + 1);
        sqlPara = Db.getSqlPara("dztadjust.findWorkingCalSetting", Kv.by("map", kv));
        preCalRec = Db.findFirst(sqlPara);

        //根据帐号ID查询期初余额（对账账户日记账余额）

        Record balRec = new Record();
        balRec.set("acc_id", accId);
        balRec.set("year", c.get(Calendar.YEAR));
        if (preCalRec != null) {
            c.setTime(preMonth);
            balRec.set("month", c.get(Calendar.MONTH) + 1);
        } else {
            c.setTime(now);
            balRec.set("month", c.get(Calendar.MONTH));
        }
        sqlPara = Db.getSqlPara("dztadjust.findCheckVoucherBal", Kv.by("map", balRec.getColumns()));
        Record initDataRec = Db.findFirst(sqlPara);
        if (initDataRec == null) {
            throw new ReqDataException("此账号未设置期初数据！");
        }

        voucherBal = TypeUtils.castToBigDecimal(initDataRec.get("balance"));

        //查询本月结账日前所有历史交易信息（已核对）- 企业
        kv = Kv.create();
        if (preCalRec != null) {
            kv.set("pre", TypeUtils.castToString(preCalRec.get("cdate")));
        }
        kv.set("now", TypeUtils.castToString(nowCalRec.get("cdate")));
        kv.set("acc_id", accId);
        kv.set("is_checked", 1);
        kv.set("statement_code", 1);
        sqlPara = Db.getSqlPara("dztadjust.findAccHisTrans", Kv.by("map", kv));
        List<Record> checkList = Db.find(sqlPara);

        for (Record rec : checkList) {
            int direction = TypeUtils.castToInt(rec.get("direction"));
            BigDecimal amount = TypeUtils.castToBigDecimal(rec.get("amount"));

            if (WebConstant.CredirOrDebit.CREDIT.getKey() == direction) {
                voucherBal = voucherBal.add(amount);//贷+
            } else if (WebConstant.CredirOrDebit.DEBIT.getKey() == direction) {
                voucherBal = voucherBal.subtract(amount);//借-
            } else {
                throw new ReqDataException("未定义的付款方式！");
            }
        }

        BigDecimal voucherAdjustBal = new BigDecimal(0);//企业调整后余额
        BigDecimal recvAmount = new BigDecimal(0);
        BigDecimal payAmount = new BigDecimal(0);
        //查询本月结账日前所有历史交易信息（未核对）- 企业
        kv = Kv.create();
//        if (preCalRec != null) {
//            kv.set("pre", TypeUtils.castToString(preCalRec.get("cdate")));
//        }
        kv.set("now", TypeUtils.castToString(nowCalRec.get("cdate")));
        kv.set("acc_id", accId);
        kv.set("is_checked", 0);
        kv.set("direction", WebConstant.CredirOrDebit.CREDIT.getKey());//贷
        sqlPara = Db.getSqlPara("dztadjust.findAccHisTrans", Kv.by("map", kv));
        final List<Record> noCheckCreditList = Db.find(sqlPara);

        kv = Kv.create();
//        if (preCalRec != null) {
//            kv.set("pre", TypeUtils.castToString(preCalRec.get("cdate")));
//        }
        kv.set("now", TypeUtils.castToString(nowCalRec.get("cdate")));
        kv.set("acc_id", accId);
        kv.set("is_checked", 0);
        kv.set("direction", WebConstant.CredirOrDebit.DEBIT.getKey());//借
        sqlPara = Db.getSqlPara("dztadjust.findAccHisTrans", Kv.by("map", kv));
        final List<Record> noCheckDebitList = Db.find(sqlPara);

        for (Record recvRec : noCheckCreditList) {//收
            recvAmount = recvAmount.add(TypeUtils.castToBigDecimal(recvRec.get("amount")));
        }

        for (Record payRec : noCheckDebitList) {//付
            payAmount = payAmount.add(TypeUtils.castToBigDecimal(payRec.get("amount")));
        }

        voucherAdjustBal = voucherBal.add(recvAmount).subtract(payAmount);//调整后余额 = 期初+已核对单据金额（贷+借-）+未核对单据（贷）-未核对单据（借）

        Record finalRec = new Record();
        finalRec.set("ysqws", noCheckCreditList);//贷
        finalRec.set("yfqwf", noCheckDebitList);//借
        finalRec.set("voucher_bal", voucherBal);
        finalRec.set("voucher_adjust_bal", voucherAdjustBal);


        //根据帐号ID查询本月结账日银行对账余额（如果没有，则取查询当天余额）
        BigDecimal accBal = new BigDecimal(0);//银行对账单余额
        BigDecimal accAdjustBal = new BigDecimal(0);//调节后的存款余额
        BigDecimal payInitAmount = new BigDecimal(0);
        BigDecimal recvInitAmount = new BigDecimal(0);

        Date currDate = new Date();
        Record accBalRec = Db.findFirst(Db.getSql("dztadjust.findAccHisBalByAccId"), accId, TypeUtils.castToString(nowCalRec.get("cdate")));
        if (accBalRec == null) {
            accBalRec = Db.findFirst(Db.getSql("dztadjust.findAccCurrBalByAccId"), accId, standFormat.format(currDate));
            if (accBalRec == null) {
                throw new ReqDataException("未找到当日银行对账单余额！");
            }
        }
        accBal = TypeUtils.castToBigDecimal(accBalRec.get("bal"));

        //查询期初数据银行未达数据
        kv = Kv.create();
        kv.set("acc_id", accId);
        kv.set("credit_or_debit", WebConstant.CredirOrDebit.CREDIT.getKey());//贷-
        kv.set("data_type", WebConstant.InitDataType.YHWD.getKey());
        if (preCalRec != null) {
            kv.set("is_checked", 0);
        }
        sqlPara = Db.getSqlPara("dztadjust.findInitdataItem", Kv.by("map", kv));
        List<Record> initPayList = Db.find(sqlPara);

        kv = Kv.create();
        kv.set("acc_id", accId);
        kv.set("credit_or_debit", WebConstant.CredirOrDebit.DEBIT.getKey());//借+
        kv.set("data_type", WebConstant.InitDataType.YHWD.getKey());
        if (preCalRec != null) {
            kv.set("is_checked", 0);
        }
        sqlPara = Db.getSqlPara("dztadjust.findInitdataItem", Kv.by("map", kv));
        List<Record> initRecvList = Db.find(sqlPara);

        for (Record recvRec : initRecvList) {
            recvInitAmount = recvInitAmount.add(TypeUtils.castToBigDecimal(recvRec.get("amount")));
        }

        for (Record payRec : initPayList) {
            payInitAmount = payInitAmount.add(TypeUtils.castToBigDecimal(payRec.get("amount")));
        }

        accAdjustBal = accBal.add(recvInitAmount).subtract(payInitAmount);//调节后的存款余额 = 银行对账余额+期初未核对借-期初未核对贷

        //根据帐号ID查询期初数据银行未达项（未核对）
        kv.set("acc_id", accId);
        kv.set("credit_or_debit", WebConstant.CredirOrDebit.CREDIT.getKey());//贷-
        kv.set("data_type", WebConstant.InitDataType.YHWD.getKey());
        kv.set("is_checked", 0);
        sqlPara = Db.getSqlPara("dztadjust.findInitdataItem", Kv.by("map", kv));
        final List<Record> noInitPayList = Db.find(sqlPara);

        kv = Kv.create();
        kv.set("acc_id", accId);
        kv.set("credit_or_debit", WebConstant.CredirOrDebit.DEBIT.getKey());//借+
        kv.set("data_type", WebConstant.InitDataType.YHWD.getKey());
        kv.set("is_checked", 0);
        sqlPara = Db.getSqlPara("dztadjust.findInitdataItem", Kv.by("map", kv));
        final List<Record> noInitRecvList = Db.find(sqlPara);

        finalRec.set("acc_bal", accBal);
        finalRec.set("acc_adjust_bal", accAdjustBal);
        finalRec.set("qsyws", noInitRecvList);
        finalRec.set("qfywf", noInitPayList);


        //结果存入余额调节表
        //根据accid查询账户信息
        final Record accRec = Db.findById("account", "acc_id", accId);
        if (accRec == null) {
            throw new ReqDataException("未找到有效的账户信息！");
        }

        final Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));
        if (orgRec == null) {
            throw new ReqDataException("未找到有效的机构信息！");
        }

        c = Calendar.getInstance();
        //查询余额表信息是否存在
        c.setTime(format.parse(cdate));
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH) + 1;
        kv = Kv.create();
        kv.set("year", year);
        kv.set("month", month);
        kv.set("acc_id", accId);
        sqlPara = Db.getSqlPara("dztadjust.findCheckVoucherBalAdjust", Kv.by("map", kv));
        final Record adjustRec = Db.findFirst(sqlPara);

        if (adjustRec != null) {
            int isconfirm = TypeUtils.castToInt(adjustRec.get("is_confirm"));
            if (isconfirm == 1) {
                throw new ReqDataException("该帐号已生成余额调节表，请勿重复生成！");
            }
        }

        final Record check_voucher_bal_adjustRec = new Record();
        check_voucher_bal_adjustRec.set("acc_id", accRec.get("acc_id"))
                .set("acc_no", accRec.get("acc_no"))
                .set("acc_name", accRec.get("acc_name"))
                .set("org_name", orgRec.get("name"))
                .set("year", year)
                .set("month", month)
                .set("checkout_date", nowCalRec.get("cdate"))
                .set("pre_voucher_bal", TypeUtils.castToBigDecimal(initDataRec.get("balance")))
                .set("voucher_bal", voucherBal)
                .set("acc_bal", accBal)
                .set("voucher_adjust_bal", voucherAdjustBal)
                .set("acc_adjust_bal", accAdjustBal)
                .set("is_confirm", 0)
                .set("persist_version", 0);


        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (adjustRec == null) {//add
                    //保存主表信息
                    boolean flag = Db.save("check_voucher_bal_adjust", check_voucher_bal_adjustRec);
                    if (flag) {

                        List<Record> recvHisTransList = handleItemList(noCheckDebitList, 1, TypeUtils.castToLong(check_voucher_bal_adjustRec.get("id")), accId);
                        List<Record> payHisTransList = handleItemList(noCheckCreditList, 1, TypeUtils.castToLong(check_voucher_bal_adjustRec.get("id")), accId);

                        List<Record> bankPayList = handleItemList(noInitPayList, 2, TypeUtils.castToLong(check_voucher_bal_adjustRec.get("id")), accId);
                        List<Record> bankRecvList = handleItemList(noInitRecvList, 2, TypeUtils.castToLong(check_voucher_bal_adjustRec.get("id")), accId);

                        //保存子表信息
                        int[] resultArr = Db.batchSave("check_voucher_bal_adjust_item", recvHisTransList, 1000);
                        int[] resultArr2 = Db.batchSave("check_voucher_bal_adjust_item", payHisTransList, 1000);
                        int[] resultArr3 = Db.batchSave("check_voucher_bal_adjust_item", bankPayList, 1000);
                        int[] resultArr4 = Db.batchSave("check_voucher_bal_adjust_item", bankRecvList, 1000);

                        return (ArrayUtil.checkDbResult(resultArr) || recvHisTransList.size() == 0) && (ArrayUtil.checkDbResult(resultArr2) || payHisTransList.size() == 0) &&
                                (ArrayUtil.checkDbResult(resultArr3) || bankPayList.size() == 0) && (ArrayUtil.checkDbResult(resultArr4) || bankRecvList.size() == 0);
                    }
                } else {//chg
                    int version = TypeUtils.castToInt(adjustRec.get("persist_version"));
                    Long id = TypeUtils.castToLong(adjustRec.get("id"));

                    //update
                    //修改主表信息
                    Record set = new Record();
                    Record where = new Record();

                    set.set("checkout_date", check_voucher_bal_adjustRec.get("checkout_date"))
                            .set("pre_voucher_bal", check_voucher_bal_adjustRec.get("pre_voucher_bal"))
                            .set("voucher_bal", check_voucher_bal_adjustRec.get("voucher_bal"))
                            .set("acc_bal", check_voucher_bal_adjustRec.get("acc_bal"))
                            .set("voucher_adjust_bal", check_voucher_bal_adjustRec.get("voucher_adjust_bal"))
                            .set("acc_adjust_bal", check_voucher_bal_adjustRec.get("acc_adjust_bal"))
                            .set("persist_version", (version + 1));

                    where.set("persist_version", version)
                            .set("id", id);

                    boolean flag = CommonService.update("check_voucher_bal_adjust", set, where);
                    if (!flag) {
                        return false;
                    }
                    check_voucher_bal_adjustRec.set("id", id);

                    //删除子表信息，重新插入子表信息
                    Db.update(Db.getSql("dztadjust.deleteAdjustItem"), id);

                    //保存子表信息
                    List<Record> recvHisTransList = handleItemList(noCheckDebitList, 1, TypeUtils.castToLong(adjustRec.get("id")), accId);
                    List<Record> payHisTransList = handleItemList(noCheckCreditList, 1, TypeUtils.castToLong(adjustRec.get("id")), accId);

                    List<Record> bankPayList = handleItemList(noInitPayList, 2, TypeUtils.castToLong(adjustRec.get("id")), accId);
                    List<Record> bankRecvList = handleItemList(noInitRecvList, 2, TypeUtils.castToLong(adjustRec.get("id")), accId);

                    int[] resultArr = Db.batchSave("check_voucher_bal_adjust_item", recvHisTransList, 1000);
                    int[] resultArr2 = Db.batchSave("check_voucher_bal_adjust_item", payHisTransList, 1000);
                    int[] resultArr3 = Db.batchSave("check_voucher_bal_adjust_item", bankPayList, 1000);
                    int[] resultArr4 = Db.batchSave("check_voucher_bal_adjust_item", bankRecvList, 1000);

                    return (ArrayUtil.checkDbResult(resultArr) || recvHisTransList.size() == 0) && (ArrayUtil.checkDbResult(resultArr2) || payHisTransList.size() == 0) &&
                            (ArrayUtil.checkDbResult(resultArr3) || bankPayList.size() == 0) && (ArrayUtil.checkDbResult(resultArr4) || bankRecvList.size() == 0);

                }

                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("生成余额调节表失败！");
        }


        //根据id查询余额调节表
        Record newAdjustRec = Db.findById("check_voucher_bal_adjust", "id", check_voucher_bal_adjustRec.get("id"));
        finalRec.set("id", newAdjustRec.get("id"));
        finalRec.set("persist_version", newAdjustRec.get("persist_version"));
        finalRec.set("acc_no", TypeUtils.castToString(newAdjustRec.get("acc_no")));
        finalRec.set("checkout_date", TypeUtils.castToString(newAdjustRec.get("year")) + "-" + TypeUtils.castToString(newAdjustRec.get("month")));

        return finalRec;
    }


    /**
     * itemlist处理
     *
     * @param list
     * @param dataType
     * @param baseId
     * @param accId
     * @return
     */
    public List<Record> handleItemList(List<Record> list, int dataType, Long baseId, Long accId) {
        List<Record> itemList = new ArrayList<>();
        Record rec = null;
        for (Record record : list) {
            rec = new Record();
            rec.set("base_id", baseId)
                    .set("data_type", dataType)//1:企业未达 2:银行未达
                    .set("amount", record.get("amount"));
            if (dataType == WebConstant.InitDataType.QYWD.getKey()) {
                rec.set("credit_or_debit", record.get("direction"));
//                rec.set("memo", TypeUtils.castToString(record.get("summary")));
            } else {
                rec.set("credit_or_debit", record.get("credit_or_debit"));
            }
            rec.set("memo", TypeUtils.castToString(record.get("memo")));

            itemList.add(rec);
        }
        return itemList;
    }

    /**
     * 启用
     *
     * @param record
     * @throws BusinessException
     */
    public Record confirm(final Record record) throws BusinessException {
        final Long id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));
        String cdate = TypeUtils.castToString(record.get("cdate"));

        Date nowDate = new Date();

        //根据id查询余额调节表
        final Record adjustRec = Db.findById("check_voucher_bal_adjust", "id", id);
        if (adjustRec == null) {
            throw new ReqDataException("未找到有效的余额调节表信息！");
        }

        //未到本月结账日不允许生成
        //获取查询月结账日
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(cdate));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ReqDataException("生成余额调节表失败！");
        }
        Date jzDate = c.getTime();


        //根据年、月查询此月结账日期
        Kv kv = Kv.create();
        kv.set("is_checkout", 1);
        c.setTime(jzDate);
        kv.set("year", c.get(Calendar.YEAR));
        kv.set("month", c.get(Calendar.MONTH) + 1);//月份从0开始 所以+1
        SqlPara sqlPara = Db.getSqlPara("dztadjust.findWorkingCalSetting", Kv.by("map", kv));
        Record nowCalRec = Db.findFirst(sqlPara);
        if (nowCalRec == null) {
            throw new ReqDataException(format.format(jzDate) + "未设置结账日！");
        }

        jzDate = TypeUtils.castToDate(nowCalRec.get("cdate"));
        if (nowDate.before(jzDate)) {
            throw new ReqDataException("未到结账日不允许生成！");
        }


        final BigDecimal voucher_adjust_bal = TypeUtils.castToBigDecimal(adjustRec.get("voucher_adjust_bal"));//日记账调整后余额
        BigDecimal acc_adjust_bal = TypeUtils.castToBigDecimal(adjustRec.get("acc_adjust_bal"));//账户调整后余额

        if (!voucher_adjust_bal.equals(acc_adjust_bal)) {
            throw new ReqDataException("金额不一致，生成余额调节表失败！");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                Record where = new Record();

                set.set("is_confirm", 1)
                        .set("persist_version", (version + 1));

                where.set("id", id).set("persist_version", version);

                boolean flag = CommonService.update("check_voucher_bal_adjust", set, where);
                if (flag) {
                    //插入余额表信息 check_voucher_acc_balance
                    Record accBalRec = new Record();

                    accBalRec.set("acc_id", TypeUtils.castToLong(adjustRec.get("acc_id")))
                            .set("acc_no", TypeUtils.castToString(adjustRec.get("acc_no")))
                            .set("acc_name", TypeUtils.castToString(adjustRec.get("acc_name")))
                            .set("year", TypeUtils.castToString(adjustRec.get("year")))
                            .set("month", TypeUtils.castToString(adjustRec.get("month")))
                            .set("balance", TypeUtils.castToBigDecimal(adjustRec.get("voucher_bal")));

                    return Db.save("check_voucher_acc_balance", accBalRec);
                }

                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("生成余额调节表失败！");
        }

        return detailRec(id);
    }

    public Record detail(final Record record) throws BusinessException {
        Long id = TypeUtils.castToLong(record.get("id"));
        return detailRec(id);
    }

    public Record detailRec(Long id) throws ReqDataException {
        //查询余额调节表主表信息
        Record adRec = Db.findById("check_voucher_bal_adjust", "id", id);
        if (adRec == null) {
            throw new ReqDataException("未找到有效的余额调节表数据！");
        }

        //根据baseid查询子表信息
        List<Record> adjustCreditRec = Db.find(Db.getSql("dztadjust.findCheckVoucherBalAdjustItem"), id, WebConstant.CredirOrDebit.DEBIT.getKey(), WebConstant.InitDataType.YHWD.getKey());
        List<Record> adjustDebititRec = Db.find(Db.getSql("dztadjust.findCheckVoucherBalAdjustItem"), id, WebConstant.CredirOrDebit.CREDIT.getKey(), WebConstant.InitDataType.YHWD.getKey());
        List<Record> bankAdjustCreditRec = Db.find(Db.getSql("dztadjust.findCheckVoucherBalAdjustItem"), id, WebConstant.CredirOrDebit.CREDIT.getKey(), WebConstant.InitDataType.QYWD.getKey());
        List<Record> bankAdjustDebititRec = Db.find(Db.getSql("dztadjust.findCheckVoucherBalAdjustItem"), id, WebConstant.CredirOrDebit.DEBIT.getKey(), WebConstant.InitDataType.QYWD.getKey());

        //企业已收，银行未收
        //企业已付，银行未付
        adRec.set("qsyws", adjustCreditRec);
        adRec.set("qfywf", adjustDebititRec);

        //银行已收，企业未收
        //银行未付，企业未付
        adRec.set("ysqws", bankAdjustCreditRec);
        adRec.set("yfqwf", bankAdjustDebititRec);

        return adRec;
    }

    public static void main(String[] args) throws ParseException {
//        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
////        ca.set(2018, 12,12);//月份是从0开始的，所以12表示11月
//
//        String date = "2018-5";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
//
//        ca.setTime(format.parse(date));
//
//        Date now = ca.getTime();
//        ca.add(Calendar.MONTH, -1); //月份减1
//        Date lastMonth = ca.getTime(); //结果
//        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println(sf.format(now));
//        System.out.println(sf.format(lastMonth));

    }

}
