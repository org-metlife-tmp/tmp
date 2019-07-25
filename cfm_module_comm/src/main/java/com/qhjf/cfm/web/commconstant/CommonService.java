package com.qhjf.cfm.web.commconstant;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/11
 * @Description:
 */
public class CommonService {
    private final static Log logger = LogbackLog.getLog(com.qhjf.cfm.utils.CommonService.class);

    /*private static SimpleDateFormat standFormat = new SimpleDateFormat("yyyy-MM");
    private static SimpleDateFormat format = new SimpleDateFormat("0MMyyyy");//入账区间规则
    private static SimpleDateFormat formatTrans = new SimpleDateFormat("ddMMyyyy");//交易时间规则
    private static SimpleDateFormat formatTransRefer = new SimpleDateFormat("YYMM");//交易标识规则
    private static SimpleDateFormat formatdsfTransRefer = new SimpleDateFormat("yyMMdd");//交易标识规则*/


    public static String getBatchno(WebConstant.MajorBizType bizType) {
        RedisCacheConfigSection redisCacheConfigSection = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);
        String redisCacheName = redisCacheConfigSection.getCacheName();
        String bizTypeCode = bizType.getPrefix();
        String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = String.format("serial:%s:%s", bizTypeCode, now);
        Long sequence = Redis.use(redisCacheName).incr(key);
        return String.format("%s%s%07d", bizTypeCode, now, sequence);
    }

    public static boolean saveFileRef(int biz_type, long bill_id, List<Object> files) {
        if (null == files || files.isEmpty()) {
            return true;
        }
        List<Record> list = new ArrayList<>();
        for (Object id : files) {
            list.add(new Record().set("biz_type", biz_type)
                    .set("bill_id", bill_id)
                    .set("attachment_id", id));
        }
        int[] result = Db.batchSave("common_attachment_info_ref", list, 1000);
        return ArrayUtil.checkDbResult(result);
    }

    public static void delFileRef(int biz_type, long bill_id) {
        Db.deleteById("common_attachment_info_ref",
                "biz_type,bill_id",
                biz_type, bill_id);
    }

    public static List<Record> findFiles(int biz_type, long bill_id) {
        return Db.find(Db.getSql("common.findFiles"), biz_type, bill_id);
    }

    public static List<Record> displayPossibleWf(int biz_type, long org_id, String biz_type_setting) throws BusinessException {
        Record record = new Record();
        record.set("biz_exp", "@" + biz_type + "@");
        record.set("org_exp", "@" + org_id + "@");
        if (biz_type_setting != null) {
            record.set("biz_setting_exp", "@" + biz_type_setting + "@");
        }
        record.set("is_activity", 1);
        SqlPara sqlPara = Db.getSqlPara("common.displayPossibleWf", Kv.by("map", record.getColumns()));

        return Db.find(sqlPara);
    }

    //修改任务状态
    public static boolean updateQuartz(WebConstant.YesOrNo yesOrNo, WebConstant.CronTaskGroup cronTaskGroup, long id) {
        return Db.update(Db.getSql("common.updateQuartz"), yesOrNo.getKey(), cronTaskGroup.getPrefix() + id) > 0;
    }


    public static int updateRows(String tableName, Record set, Record where) {
        Kv kv = Kv.create();
        kv.set("set", set.getColumns());
        kv.set("where", where.getColumns());
        kv.set("table_name", tableName);
        SqlPara sqlPara = Db.getSqlPara("common.update", Kv.by("map", kv));
        return Db.update(sqlPara);
    }

    public static boolean update(String tableName, Record set, Record where) {
        return updateRows(tableName, set, where) > 0;
    }

    /**
     * 判断单据是否可修改
     *
     * @param record
     * @param service_status
     * @throws BusinessException
     */
    public static void checkBillStatus(Record record, String service_status) throws BusinessException {
        if (null == record) {
            throw new ReqDataException("此单据不存在，请刷新重试！");
        }
        Integer status = TypeUtils.castToInt(record.get(service_status));
        if (null == status) {
            throw new ReqDataException("此单据状态错误，请联系管理员！");
        }
        if (status != WebConstant.BillStatus.SAVED.getKey()
                && status != WebConstant.BillStatus.REJECT.getKey()) {
            throw new ReqDataException("此单据状态错误，请刷新重试！");
        }
    }

    public static void checkBillStatus(Record record) throws BusinessException {
        checkBillStatus(record, "service_status");
    }


    /**
     * 生成确认的Record
     *
     * @param tradingId
     * @param billId
     * @param userInfo
     * @return
     */
    public static List<Record> genConfirmRecords(final List<Integer> tradingId, Long billId, UserInfo userInfo) {
        List<Record> records = null;
        if (tradingId != null && tradingId.size() > 0) {
            records = new ArrayList<>();
            for (Integer s : tradingId) {
                Record rc = new Record();
                rc.set("bill_id", billId);
                rc.set("trans_id", s);
                rc.set("create_on", new Date());
                rc.set("create_by", userInfo.getUsr_id());
                rc.set("delete_flag", 0);
                records.add(rc);
            }
        }
        return records;
    }

    /**
     * 收付费生成Record
     *
     * @param batchNo
     * @param tradingNo
     * @param userInfo
     * @return
     */
    public static List<Record> genPayConfirmRecords(final List<Integer> batchNo, List<Integer> tradingNo, UserInfo userInfo, String checkSerialSeqNo) {
        List<Record> records = null;
        if (batchNo != null && batchNo.size() > 0) {
            records = new ArrayList<>();
            for (Integer batch : batchNo) {
                if (tradingNo != null && tradingNo.size() > 0) {
                    for (Integer trad : tradingNo) {
                        Record rc = new Record();
                        rc.set("batch_id", batch);
                        rc.set("trans_id", trad);
                        rc.set("create_on", new Date());
                        rc.set("create_by", userInfo.getUsr_id());
                        rc.set("check_service_number", checkSerialSeqNo);
                        rc.set("delete_flag", 0);
                        records.add(rc);
                    }
                }
            }
        }
        return records;
    }

    /**
     * 柜面付生成Record
     *
     * @param billId
     * @param tradingNo
     * @param userInfo
     * @param checkSerialSeqNo
     * @return
     */
    public static List<Record> genPayCounterConfirmRecords(final Integer billId, List<Integer> tradingNo, UserInfo userInfo, String checkSerialSeqNo) {
        List<Record> records = null;
        if (tradingNo != null && tradingNo.size() > 0) {
            records = new ArrayList<>();
            for (Integer s : tradingNo) {
                Record rc = new Record();
                rc.set("bill_id", billId);
                rc.set("trans_id", s);
                rc.set("create_on", new Date());
                rc.set("create_by", userInfo.getUsr_id());
                rc.set("check_service_number", checkSerialSeqNo);
                rc.set("delete_flag", 0);
                records.add(rc);
            }
        }
        return records;
    }


    /**
     * @param origin_recode 原始请求对象
     * @param source_key    原始的key值， 根据原始key值，获取原始值。
     * @param dest_name_key 原始值中含有中文，把原始值设置到dest_name_key下
     * @param dest_no_key   否则，把原始值设置到dest_no_key下
     *                      eg: processQueryKey(record,"pay_query_key","pay_account_name","pay_account_no")
     * @return
     */
    public static Record processQueryKey(Record origin_recode, String source_key, String dest_name_key, String dest_no_key) {
        if (origin_recode != null && source_key != null && !"".equals(source_key)
                && dest_name_key != null && !"".equals(dest_name_key)
                && dest_no_key != null && !"".equals(dest_no_key)) {
            String source_value = origin_recode.get(source_key);
            origin_recode.remove(source_key);

            //是否包含中文
            boolean isName = StringKit.isContainChina(source_value);
            if (isName) {
                origin_recode.set(dest_name_key, source_value);
            } else {
                origin_recode.set(dest_no_key, source_value);
            }
        }
        return origin_recode;
    }


    /**
     * @param origin_recode 原始请求对象
     * @param dest          处理完的Kv对象
     * @param source_key    原始的key值， 根据原始key值，获取原始值。
     * @param dest_name_key 原始值中含有中文，把原始值设置到dest_name_key下
     * @param dest_no_key   否则，把原始值设置到dest_no_key下
     *                      eg: processQueryKey4Kv(record,kv,"pay_query_key","pay_account_name","pay_account_no")
     * @return
     */
    public static Kv processQueryKey4Kv(Record origin_recode, Kv dest, String source_key, String dest_name_key, String dest_no_key) {
        if (origin_recode != null && source_key != null && !"".equals(source_key)
                && dest_name_key != null && !"".equals(dest_name_key)
                && dest_no_key != null && !"".equals(dest_no_key)) {
            String source_value = origin_recode.get(source_key);
            //是否包含中文
            boolean isName = StringKit.isContainChina(source_value);
            if (isName) {
                dest.set(dest_name_key, source_value);
            } else {
                dest.set(dest_no_key, source_value);
            }
        }
        return dest;
    }

    /**
     * 调拨通 付方向生成凭证1、2
     *
     * @param payRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record dbtPayVorcher(Record payRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, WebConstant.MajorBizType bizType, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String batchno = "";
        String serviceSerialNumber = "";
        String curr = "";
        String description = "";
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = "8350000000";
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 3) {
            subjectCode = "6999999999";
            debitCredit = "D";
        } else if (iden == 4) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 5) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 6) {
            subjectCode = "6999999999";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        if (bizType == WebConstant.MajorBizType.INNERDB) {
            serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
            //描述改为单据的摘要信息
            description = TypeUtils.castToString(billRec.get("payment_summary"));
            curr = TypeUtils.castToString(billRec.get("pay_account_cur"));
        } else {
            batchno = TypeUtils.castToString(billRec.get("batchno"));
            serviceSerialNumber = batchno;
            //描述改为单据的摘要信息
            description = TypeUtils.castToString(billRec.get("memo"));
            //根据批次号查询批次信息
            Record baseRec = Db.findFirst(Db.getSql("dbtbatchtrad.findInnerBatchpayBaseinfoByBathno"), batchno);
            curr = TypeUtils.castToString(baseRec.get("pay_account_cur"));
        }

        int tranId = payRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
//                .set("file_name", payRec.get(""))
//                .set("export_count", payRec.get(""));
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", bizType.getKey());
        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    public static Record dbtPlfPayVorcher(Record payRec, Record billRec,
                                          String seqnoOrstatmentCode, int iden,
                                          WebConstant.MajorBizType bizType,
                                          Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {

        String description = null, code6 = null, transactionReference = null, curr = null, orgcode = null;

        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));
        //根据机构id查询机构信息
        curr = TypeUtils.castToString(
                Db.findById("currency", "id", TypeUtils.castToLong(accRec.get("curr_id")))
                        .get("iso_code"));
        orgcode = TypeUtils.castToString(
                Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")))
                        .get("code"));
        Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);
        code6 = "SYS";

        String subjectCode = null;
        String debitCredit = null;
        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        transactionReference = "SS" + sftcheck.getStr("code_abbre") + DateFormatThreadLocal.format("YYMM", payTransDate) + seqnoOrstatmentCode;
        description = DateFormatThreadLocal.format("yyMMdd", payTransDate) + accRec.getStr("bankcode") + "拨付保险资金";

        if (iden == 1) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 2) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 3) {
            subjectCode = "8350000000";
            debitCredit = "D";
        } else if (iden == 4) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        int tranId = payRec.get("id");
        String newStatmentCode = DateFormatThreadLocal.format("yyyyMMddhhmmss", new Date()) + seqnoOrstatmentCode;
        String serviceSerialNumber = "";

        if (bizType == WebConstant.MajorBizType.INNERDB) {
            serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        } else {
            serviceSerialNumber = TypeUtils.castToString(billRec.get("batchno"));
        }

        Record record = new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", transactionReference)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", bizType.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * 调拨通 收方向生成凭证1、2
     *
     * @param recvRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record dbtRecvVorcher(Record recvRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, WebConstant.MajorBizType bizType, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(recvRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Date recvTransDate = TypeUtils.castToDate(recvRec.get("trans_date"));

        String subjectCode = null;
        String debitCredit = null;

        if (iden == 1) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = "8350000000";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        String batchno = "";
        String serviceSerialNumber = "";

        if (bizType == WebConstant.MajorBizType.INNERDB) {
            serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        } else {
            batchno = TypeUtils.castToString(billRec.get("batchno"));
            serviceSerialNumber = batchno;
        }

        //描述改为单据的摘要信息
        String description = getDesc(billRec, seqnoOrstatmentCode, bizType);
        int tranId = recvRec.get("id");

        Record record = new Record()
                .set("trans_id", recvRec.get("id"))
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", TypeUtils.castToString(billRec.get("recv_account_cur")))
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", recvTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
//                .set("file_name", payRec.get(""))
//                .set("export_count", payRec.get(""));
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", recvTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", bizType.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * 支付通 付方向生成凭证
     *
     * @param payRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @return
     */
    public static Record zftPayVorcher(Record payRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, WebConstant.MajorBizType bizType, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String batchno = "";
        String serviceSerialNumber = "";
        String curr = "";
        String description = "";
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));
        String a_code6 = "";
        String a_code7 = "";
        String payBankCnaps = TypeUtils.castToString(billRec.get("pay_bank_cnaps"));
        //根据cnaps查询银行大类信息
        Record payBankTypeTec = Db.findById("all_bank_info", "cnaps_code", payBankCnaps);
        String bank_type = TypeUtils.castToString(payBankTypeTec.get("bank_type"));


        String transactionReference = "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode;

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        ///根据bank_type 查询银行大类
        Record bankRec = Db.findById("oa_bank_mapping", "bank_type", bank_type);

        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {//其他-借
            subjectCode = "8120000040";
            debitCredit = "D";
            a_code6 = "SYS";
            a_code7 = "SYS";
        } else if (iden == 2) {//其他-贷
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
            a_code6 = "SYS";
            a_code7 = "SYS";
        } else if (iden == 3) {//保单业务结算费-借
            subjectCode = "5220200075";
            debitCredit = "D";
            a_code6 = bankRec == null ? bank_type : TypeUtils.castToString(bankRec.get("analysis_code"));
            a_code7 = "1190";
        } else if (iden == 4) {//保单业务结算费-贷
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
            a_code6 = "SYS";
            a_code7 = "SYS";
        } else if (iden == 5) {//银行手续费结算-借
            subjectCode = "5220300010";
            debitCredit = "D";
            a_code6 = bankRec == null ? bank_type : TypeUtils.castToString(bankRec.get("analysis_code"));
            a_code7 = "1020";
        } else if (iden == 6) {//银行手续费结算-贷
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
            a_code6 = "SYS";
            a_code7 = "SYS";
        } else if (iden == 7) {//资金支付-批量付/第三方-借
            subjectCode = "7110000012";
            debitCredit = "D";
            a_code6 = billRec.getStr("channel_code") + billRec.getStr("bankcode");
            a_code7 = "SYS";

        } else if (iden == 8) {//资金支付-批量付/第三方-贷
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
            a_code6 = "SYS";
            a_code7 = "SYS";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        if (bizType == WebConstant.MajorBizType.ZFT) {
            serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
            //描述改为单据的摘要信息
            description = TypeUtils.castToString(billRec.get("payment_summary"));
            curr = TypeUtils.castToString(billRec.get("pay_account_cur"));
        } else {
            batchno = TypeUtils.castToString(billRec.get("batchno"));
            serviceSerialNumber = batchno;
            //描述改为单据的摘要信息
            description = TypeUtils.castToString(billRec.get("memo"));
            //根据批次号查询批次信息
            Record baseRec = Db.findFirst(Db.getSql("zftbatch.findBillByBatchno"), batchno);
            curr = TypeUtils.castToString(baseRec.get("pay_account_cur"));
        }


        transactionReference = "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode;
        //如果是第三方的，个别字段需要修改
        if (iden == 7 || iden == 8) {
            Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", TypeUtils.castToString(orgRec.get("code")));
            description = DateFormatThreadLocal.format("yyMMdd", payTransDate) + billRec.getStr("bankcode") + "第三方结算-批量付款-拨付保险金";
            transactionReference = "SS" + sftcheck.getStr("code_abbre") + DateFormatThreadLocal.format("YYMM", payTransDate) + seqnoOrstatmentCode;
        }

        int tranId = payRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", a_code6)
                .set("a_code7", a_code7)
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", transactionReference)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", bizType.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * 批量付LA/EBS回显
     *
     * @return
     * @throws BusinessException
     */
    public static Record plfLaBackCheckVoucher(Record payLegalRecord, int iden, String seqnoOrstatmentCode) throws BusinessException {
        /**
         * 会记日及transactionDate取回盘日期，description及transactionReference取la确认日期
         */
        BigDecimal amount = TypeUtils.castToBigDecimal(payLegalRecord.get("amount"));
        String newStatmentCode = DateFormatThreadLocal.format("yyyyMMddhhmmss", new Date()) + seqnoOrstatmentCode;
        String subjectCode = null;
        String debitCredit = null;
        String description = null, code6 = null, transactionReference = null;
        //根据通道找对应的bankcode和channel_code，然后根据channel_code找对应的账号
        Date backDate = payLegalRecord.getDate("resp_time");
        Date backOn = payLegalRecord.getDate("back_on");
        Record chann = Db.findById("channel_setting", "id", payLegalRecord.getInt("channel_id"));
        int inner = chann.getInt("is_inner");
        int netMode = chann.getInt("net_mode");


        if (inner == 0) {
            description = DateFormatThreadLocal.format("yyMMdd", backOn) + chann.getStr("bankcode") + "第三方结算-批量付款回盘";
        } else if (inner == 1) {
            if (netMode == 0) {
                description = DateFormatThreadLocal.format("yyMMdd", backOn) + chann.getStr("bankcode") + "批量付款回盘-银行净额模式";
            } else if (netMode == 1) {
                description = DateFormatThreadLocal.format("yyMMdd", backOn) + chann.getStr("bankcode") + "批量付款回盘-银行全额模式";
            }
        }
        Record acc = Db.findFirst(Db.getSql("paycheck.findaccount"), chann.getStr("bankcode"));
        String curr = TypeUtils.castToString(
                Db.findById("currency", "id", TypeUtils.castToLong(acc.get("curr_id")))
                        .get("iso_code"));
        //根据机构id查询机构信息
        String orgcode = TypeUtils.castToString(
                Db.findById("organization", "org_id", TypeUtils.castToLong(acc.get("org_id")))
                        .get("code"));
        Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);

        transactionReference = "SS" + sftcheck.getStr("code_abbre") + DateFormatThreadLocal.format("YYMM", backOn) + seqnoOrstatmentCode;
        if (iden == 1) {        //LA
            subjectCode = "8360000007";
            debitCredit = "D";
            code6 = "SYS";
        } else if (iden == 2) {
            subjectCode = "8120000013";
            debitCredit = "C";
            code6 = chann.getStr("channel_code") + chann.getStr("bankcode");
        } else if (iden == 3) {     //EBS
            subjectCode = "8360000008";
            debitCredit = "D";
            code6 = "SYS";
        } else if (iden == 4) {
            subjectCode = "8120000013";
            debitCredit = "C";
            code6 = chann.getStr("channel_code") + chann.getStr("bankcode");
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",backDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",backDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("docking_status", 0)
                .set("statement_code", newStatmentCode)
                .set("business_ref_no", TypeUtils.castToString(payLegalRecord.get("insure_bill_no")))
                .set("biz_type", payLegalRecord.get("xx_biz_type"))
                .set("ref_bill_id", payLegalRecord.get("xx_ref_bill_id"))
                .set("ref_bill", payLegalRecord.get("xx_ref_bill"))
                .set("transaction_reference", transactionReference);
    }

    /**
     * 柜面付LA/EBS回显
     *
     * @return
     * @throws BusinessException
     */
    public static Record gmfLaBackCheckVoucher(Record payLegalRecord, int iden, String seqnoOrstatmentCode) throws BusinessException {
        BigDecimal amount = TypeUtils.castToBigDecimal(payLegalRecord.get("amount"));
        String newStatmentCode = DateFormatThreadLocal.format("yyyyMMddhhmmss", new Date()) + seqnoOrstatmentCode;
        String subjectCode = null;
        String debitCredit = null;
        String description = null, code6 = null, transactionReference = null;
        //根据付款账号去找对应的bankcode
        Date backDate = new Date();
        Date backOn = payLegalRecord.getDate("actual_payment_date");
        logger.info(String.format("EBS回盘付款方%s,", TypeUtils.castToString(payLegalRecord.get("pay_account_no"))));
        Record acc = Db.findById("account", "acc_id", TypeUtils.castToLong(
                Db.findFirst(Db.getSql("acc.findAccountByAccNo"), TypeUtils.castToString(payLegalRecord.get("pay_account_no"))).get("acc_id")));
        String curr = TypeUtils.castToString(
                Db.findById("currency", "id", TypeUtils.castToLong(acc.get("curr_id")))
                        .get("iso_code"));
        Record chann = Db.findFirst(Db.getSql("channel_setting.getchannelbybankcode"), acc.get("bankcode"));
        //根据机构id查询机构信息
        String orgcode = TypeUtils.castToString(
                Db.findById("organization", "org_id", TypeUtils.castToLong(acc.get("org_id")))
                        .get("code"));
        Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);


        description = DateFormatThreadLocal.format("yyMMdd", backOn) + acc.getStr("bankcode") + "柜面付费确认";
        transactionReference = "SS" + sftcheck.getStr("code_abbre") + DateFormatThreadLocal.format("YYMM", backOn) + seqnoOrstatmentCode;
        if (iden == 1) {        //LA
            subjectCode = "8360000007";
            debitCredit = "D";
            code6 = "SYS";
        } else if (iden == 2) {
            subjectCode = "8120000015";
            debitCredit = "C";
            code6 = chann.getStr("channel_code") + acc.getStr("bankcode");
        } else if (iden == 3) {     //EBS
            subjectCode = "8360000008";
            debitCredit = "D";
            code6 = "SYS";
        } else if (iden == 4) {
            subjectCode = "8120000015";
            debitCredit = "C";
            code6 = chann.getStr("channel_code") + acc.getStr("bankcode");
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }


        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",backDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",backDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("docking_status", 0)
                .set("statement_code", newStatmentCode)
                .set("business_ref_no", TypeUtils.castToString(payLegalRecord.get("insure_bill_no")))
                .set("biz_type", payLegalRecord.get("xx_biz_type"))
                .set("ref_bill_id", payLegalRecord.get("xx_ref_bill_id"))
                .set("ref_bill", payLegalRecord.get("xx_ref_bill"))
                .set("transaction_reference", transactionReference);
    }

    /**
     * 批量收LA回显
     *
     * @return
     * @throws BusinessException
     */
    public static Record plsLaBackCheckVoucher(Record recvLegalRecord, int iden, String seqnoOrstatmentCode) throws BusinessException {
        BigDecimal amount = TypeUtils.castToBigDecimal(recvLegalRecord.get("amount"));
        String newStatmentCode = DateFormatThreadLocal.format("yyyyMMddhhmmss", new Date()) + seqnoOrstatmentCode;
        String subjectCode = null;
        String debitCredit = null;
        String description = null, code6 = null, transactionReference = null;
        //根据通道找对应的bankcode和channel_code，然后根据channel_code找对应的账号
        Date backDate = recvLegalRecord.getDate("resp_time");
        Date backOn = recvLegalRecord.getDate("back_on");
        Record chann = Db.findById("channel_setting", "id", recvLegalRecord.getInt("channel_id"));

        description = DateFormatThreadLocal.format("yyMMdd", backOn) + chann.getStr("bankcode") + "批量收款回盘";

        Record acc = Db.findFirst(Db.getSql("paycheck.findaccount"), chann.getStr("bankcode"));
        String curr = TypeUtils.castToString(
                Db.findById("currency", "id", TypeUtils.castToLong(acc.get("curr_id")))
                        .get("iso_code"));
        //根据机构id查询机构信息
        String orgcode = TypeUtils.castToString(
                Db.findById("organization", "org_id", TypeUtils.castToLong(acc.get("org_id")))
                        .get("code"));
        Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);

        transactionReference = "SS" + sftcheck.getStr("code_abbre") + DateFormatThreadLocal.format("YYMM", backOn) + seqnoOrstatmentCode;
        if (iden == 1) {        //LA
            subjectCode = "7110000014";
            debitCredit = "D";
            code6 = chann.getStr("channel_code") + chann.getStr("bankcode");
        } else if (iden == 2) {
            subjectCode = "8360000002";
            debitCredit = "C";
            code6 = "SYS";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",backDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",backDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("docking_status", 0)
                .set("statement_code", newStatmentCode)
                .set("business_ref_no", TypeUtils.castToString(recvLegalRecord.get("insure_bill_no")))
                .set("biz_type", recvLegalRecord.get("xx_biz_type"))
                .set("ref_bill_id", recvLegalRecord.get("xx_ref_bill_id"))
                .set("ref_bill", recvLegalRecord.get("xx_ref_bill"))
                .set("transaction_reference", transactionReference);
    }

    /**
     * 批量付 付方向生成凭证
     *
     * @param accRec
     * @param iden
     * @param periodDate
     * @param transDate
     * @param detailRecord
     * @param curr
     * @param orgcode
     * @return
     * @throws BusinessException
     */
    public static Record plfPayVorcher(Record accRec,
                                       String description,
                                       String code6,
                                       String transactionReference,
                                       int iden,
                                       Date periodDate,
                                       Date transDate,
                                       Record detailRecord,
                                       String curr,
                                       String orgcode
    ) throws BusinessException {


        BigDecimal amount = TypeUtils.castToBigDecimal(detailRecord.get("amount"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {//净额模式---借
            subjectCode = "8120000013";
            debitCredit = "D";
        } else if (iden == 2) {//净额模式---贷
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 3) {//全额模式付款---借
            subjectCode = "8120000013";
            debitCredit = "D";
        } else if (iden == 4) {//全额模式付款---贷
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 5) {//全额模式收款---借
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 6) {//全额模式收款---贷
            subjectCode = "8120000013";
            debitCredit = "C";
        } else if (iden == 7) {//第三方成功---借
            subjectCode = "8120000013";
            debitCredit = "D";
        } else if (iden == 8) {//第三方成功---贷
            subjectCode = "7110000012";
            debitCredit = "C";
        } else if (iden == 9) {//第三方失败---借
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 10) {//第三方失败---贷
            subjectCode = "7110000012";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }


        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", periodDate))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",transDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",transDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM",periodDate))
                .set("docking_status", 0)
                .set("operator", TypeUtils.castToLong(detailRecord.get("xx_operator")))
                .set("operator_org", TypeUtils.castToLong(detailRecord.get("xx_operator_org")))
                .set("statement_code", TypeUtils.castToString(detailRecord.get("xx_seqnoOrstatmentCode")))
                .set("business_ref_no", TypeUtils.castToString(detailRecord.get("xx_insure_bill_no")))
                .set("biz_type", TypeUtils.castToString(detailRecord.get("xx_biz_type")))
                .set("transaction_reference", transactionReference);
    }

    /**
     * 批量收 付方向生成凭证
     * @param accRec
     * @param iden
     * @param periodDate
     * @param transDate
     * @param detailRecord
     * @param curr
     * @param orgcode
     * @return
     * @throws BusinessException
     */
    public static Record plsPayVorcher(Record accRec,
                                       String description,
                                       String code6,
                                       String transactionReference,
                                       int iden,
                                       Date periodDate,
                                       Date transDate,
                                       Record detailRecord,
                                       String curr,
                                       String orgcode
    ) throws BusinessException {

        BigDecimal amount = TypeUtils.castToBigDecimal(detailRecord.get("amount"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = "7110000014";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }
        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", periodDate))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",transDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",transDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM",periodDate))
                .set("docking_status", 0)
                .set("operator", TypeUtils.castToLong(detailRecord.get("xx_operator")))
                .set("operator_org", TypeUtils.castToLong(detailRecord.get("xx_operator_org")))
                .set("statement_code", TypeUtils.castToString(detailRecord.get("xx_seqnoOrstatmentCode")))
                .set("business_ref_no", TypeUtils.castToString(detailRecord.get("xx_insure_bill_no")))
                .set("biz_type", TypeUtils.castToString(detailRecord.get("xx_biz_type")))
                .set("transaction_reference", transactionReference);
    }

    /**
     * 财务预提冲销凭证
     *
     * @param accRec
     * @param iden
     * @param periodDate
     * @param transDate
     * @param detailRecord
     * @param curr
     * @param orgcode
     * @return
     * @throws BusinessException
     */
    public static Record presubmitchargeoffVorcher(Record accRec,
                                                   String description,
                                                   String code6,
                                                   String transactionReference,
                                                   int iden,
                                                   Date periodDate,
                                                   Date transDate,
                                                   Record detailRecord,
                                                   String curr,
                                                   String orgcode
    ) throws BusinessException {

        BigDecimal amount = TypeUtils.castToBigDecimal(detailRecord.get("amount"));
        String subjectCode = null;
        String debitCredit = null;
        int voucher_type;
        if (iden == 1) {
            //月度统一计提银行未达账项凭证 付
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
            voucher_type = 1;
        } else if (iden == 2) {
            //月度统一计提银行未达账项凭证 付
            subjectCode = "7110000013";
            debitCredit = "D";
            voucher_type = 1;
        } else if (iden == 3) {
            //月度统一计提银行未达账项凭证 收
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
            voucher_type = 1;
        } else if (iden == 4) {
            //月度统一计提银行未达账项凭证 收
            subjectCode = "8120000012";
            debitCredit = "C";
            voucher_type = 1;
        } else if (iden == 5) {
            //TMP自动冲销该类凭证 付
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
            voucher_type = 2;
        } else if (iden == 6) {
            //TMP自动冲销该类凭证 付
            subjectCode = "7110000013";
            debitCredit = "D";
            voucher_type = 2;
        } else if (iden == 7) {
            //TMP自动冲销该类凭证 收
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
            voucher_type = 2;
        } else if (iden == 8) {
            //TMP自动冲销该类凭证 收
            subjectCode = "8120000012";
            debitCredit = "C";
            voucher_type = 2;
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }
        return new Record()
                .set("trans_id", TypeUtils.castToLong(detailRecord.get("id")))
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", periodDate))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", transDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",transDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM",periodDate))
                .set("docking_status", 0)
                .set("operator", TypeUtils.castToLong(detailRecord.get("xx_operator")))
                .set("operator_org", TypeUtils.castToLong(detailRecord.get("xx_operator_org")))
                .set("statement_code", TypeUtils.castToString(detailRecord.get("xx_seqnoOrstatmentCode")))
                .set("business_ref_no", TypeUtils.castToString(detailRecord.get("xx_service_serial_number")))
                .set("biz_type", TypeUtils.castToString(detailRecord.get("xx_biz_type")))
                .set("transaction_reference", transactionReference)
                .set("voucher_type", voucher_type);
    }

    /**
     * 柜面付 付方向生成凭证
     *
     * @param accRec
     * @param iden
     * @param periodDate
     * @param transDate
     * @param detailRecord
     * @param curr
     * @param orgcode
     * @return
     * @throws BusinessException
     */
    public static Record gmfPayVorcher(Record accRec,
                                       String description,
                                       String code6,
                                       String transactionReference,
                                       int iden,
                                       Date periodDate,
                                       Date transDate,
                                       Record detailRecord,
                                       String curr,
                                       String orgcode
    ) throws BusinessException {


        BigDecimal amount = TypeUtils.castToBigDecimal(detailRecord.get("amount"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 2) {
            subjectCode = "8120000015";
            debitCredit = "D";
        } else if (iden == 3) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 4) {
            subjectCode = "8120000012";
            debitCredit = "D";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }
        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", periodDate))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",transDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",transDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM",periodDate))
                .set("docking_status", 0)
                .set("operator", TypeUtils.castToLong(detailRecord.get("xx_operator")))
                .set("operator_org", TypeUtils.castToLong(detailRecord.get("xx_operator_org")))
                .set("statement_code", TypeUtils.castToString(detailRecord.get("xx_seqnoOrstatmentCode")))
                .set("business_ref_no", TypeUtils.castToString(detailRecord.get("xx_insure_bill_no")))
                .set("biz_type", TypeUtils.castToString(detailRecord.get("xx_biz_type")))
                .set("transaction_reference", transactionReference);
    }

    /**
     * 归集通 付方向生成凭证1、2
     *
     * @param payRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record gjtPayVorcher(Record payRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String serviceSerialNumber = "";
        String curr = "";
        String description = "";
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("collect_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = "8350000000";
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        //描述改为单据的摘要信息
        description = TypeUtils.castToString(billRec.get("memo"));
        curr = TypeUtils.castToString(billRec.get("pay_account_cur"));

        int tranId = payRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
//                .set("biz_id", null)                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
//                .set("biz_name", null)                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.GJT.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * 归集通 收方向生成凭证3、4
     *
     * @param recvRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record gjtRecvVorcher(Record recvRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {

        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("collect_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(recvRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Date recvTransDate = TypeUtils.castToDate(recvRec.get("trans_date"));

        String subjectCode = null;
        String debitCredit = null;

        if (iden == 3) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 4) {
            subjectCode = "8350000000";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        String serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        //描述改为单据的摘要信息
        String description = TypeUtils.castToString(billRec.get("memo"));

        int tranId = recvRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", TypeUtils.castToString(billRec.get("recv_account_cur")))
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", recvTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", recvTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.GJT.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * 非直连归集通 付方向生成凭证1、2
     *
     * @param payRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record nonGjtPayVorcher(Record payRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String curr = "";
        String description = "";
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = "8350000000";
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        String batchno = TypeUtils.castToString(billRec.get("batchno"));
        //描述改为单据的摘要信息
        description = TypeUtils.castToString(billRec.get("memo"));
        curr = TypeUtils.castToString(billRec.get("pay_account_cur"));

        int tranId = payRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("yyMMdd", new Date()) + seqnoOrstatmentCode)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
//                .set("biz_id", null)                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
//                .set("biz_name", null)                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", batchno)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.CBB.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * 非直连归集通 收方向生成凭证3、4
     *
     * @param recvRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record nonGjtRecvVorcher(Record recvRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String curr = "";
        //描述改为单据的摘要信息
        String description = TypeUtils.castToString(billRec.get("memo"));
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));
        String batchno = TypeUtils.castToString(billRec.get("batchno"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(recvRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        //根据批次号查询主单据信息
        billRec = Db.findFirst(Db.getSql("collect_batch_check.selectInfoByBatchno"), batchno);


        Date payTransDate = TypeUtils.castToDate(recvRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 3) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 4) {
            subjectCode = "8350000000";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }


        curr = TypeUtils.castToString(billRec.get("recv_account_cur"));

        int tranId = recvRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", batchno)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.CBB.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * OA总公司付款 付方向生成凭证
     *
     * @param payRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @return
     */
    public static Record oaHeadPayVorcher(Record payRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        String curr = "";
        String description = "";
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Long originDataId = TypeUtils.castToLong(billRec.get("ref_id"));
        //根据原始数据id查询原始数据信息
        Record originDataRec = Db.findById("oa_origin_data", "id", originDataId);

        if (originDataRec == null) {
            throw new ReqDataException("未找到原始数据信息！");
        }


        String a_code10 = TypeUtils.castToString(orgRec.get("code"));

        Long billOrgId = TypeUtils.castToLong(billRec.get("org_id"));// oa_head_payment org_id

        boolean is_beanch_pay = false;
        Record applyOrg = Db.findById("organization", "org_id", billOrgId);
        if (applyOrg != null && applyOrg.getInt("level_num") > 1) { // 总公司帐户代分公司对外付款(如果申请机构的level_num > 1 ,即不为总公司)
            is_beanch_pay = true;
        }


        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = "8350000004";
            debitCredit = "D";
            if (is_beanch_pay) {
                a_code10 = TypeUtils.castToString(applyOrg.get("code"));
            }
        } else if (iden == 2) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else if (iden == 3) {
            subjectCode = "8350000000";
            debitCredit = "D";
        } else if (iden == 4) {
            subjectCode = "8350000000";
            debitCredit = "C";
            if (is_beanch_pay) {
                a_code10 = TypeUtils.castToString(applyOrg.get("code"));
            }
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        //描述改为原始单据的OA报销单号
        description = TypeUtils.castToString(originDataRec.get("bill_no"));
        curr = TypeUtils.castToString(billRec.get("pay_account_cur"));

        int tranId = payRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", a_code10)
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
//                .set("biz_id", null)                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
//                .set("biz_name", null)                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.OA_HEAD_PAY.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * OA分公司付款-调拨 付方向生成凭证1、2
     *
     * @param payRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record oaBrHeadPayVorcher(Record payRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String serviceSerialNumber = "";
        String curr = "";
        String description = "";
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Long originDataId = TypeUtils.castToLong(billRec.get("ref_id"));
        //根据原始数据id查询原始数据信息
        Record originDataRec = Db.findById("oa_origin_data", "id", originDataId);

        if (originDataRec == null) {
            throw new ReqDataException("未找到原始数据信息！");
        }

        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = "8350000004";
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        //描述改为原始单据的OA报销单号
        description = TypeUtils.castToString(originDataRec.get("bill_no"));
        curr = TypeUtils.castToString(billRec.get("pay_account_cur"));

        int tranId = payRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
//                .set("file_name", payRec.get(""))
//                .set("export_count", payRec.get(""));
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
//                .set("biz_id", null)                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
//                .set("biz_name", null)                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.OA_BRANCH_PAY.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * OA分公司付款-调拨 收方向生成凭证3、4
     *
     * @param recvRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record oaBrHeadRecvVorcher(Record recvRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(recvRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Date recvTransDate = TypeUtils.castToDate(recvRec.get("trans_date"));

        Long originDataId = TypeUtils.castToLong(billRec.get("ref_id"));
        //根据原始数据id查询原始数据信息
        Record originDataRec = Db.findById("oa_origin_data", "id", originDataId);

        if (originDataRec == null) {
            throw new ReqDataException("未找到原始数据信息！");
        }

        String subjectCode = null;
        String debitCredit = null;

        if (iden == 3) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 4) {
            subjectCode = "8350000000";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        String serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        //描述改为原始单据的OA报销单号
        String description = TypeUtils.castToString(originDataRec.get("bill_no"));
        String curr = TypeUtils.castToString(billRec.get("recv_account_cur"));

        int tranId = recvRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", recvTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
//                .set("file_name", payRec.get(""))
//                .set("export_count", payRec.get(""));
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", recvTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
//                .set("biz_id", null)                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
//                .set("biz_name", null)                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.OA_BRANCH_PAY.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * OA分公司付款-支付 付方向生成凭证5、6
     *
     * @param payRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record oaBranchPayVorcher(Record payRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        String serviceSerialNumber = "";
        String curr = "";
        String description = "";
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("payment_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(payRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Long originDataId = TypeUtils.castToLong(billRec.get("ref_id"));
        //根据原始数据id查询原始数据信息
        Record originDataRec = Db.findById("oa_origin_data", "id", originDataId);

        if (originDataRec == null) {
            throw new ReqDataException("未找到原始数据信息！");
        }

        Date payTransDate = TypeUtils.castToDate(payRec.get("trans_date"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 5) {
            subjectCode = "8350000000";
            debitCredit = "D";
        } else if (iden == 6) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        //描述改为原始单据的OA报销单号
        description = TypeUtils.castToString(originDataRec.get("bill_no"));
        curr = TypeUtils.castToString(billRec.get("pay_account_cur"));

        int tranId = payRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", payTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", payTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)          //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.OA_BRANCH_PAY.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    /**
     * 收款通 收方向生成凭证3、4
     *
     * @param recvRec
     * @param billRec
     * @param seqnoOrstatmentCode
     * @param iden
     * @return
     */
    public static Record sktRecvVorcher(Record recvRec, Record billRec, String seqnoOrstatmentCode, String newStatmentCode, int iden, Map<Integer, Date> transDateMap, UserInfo userInfo) throws BusinessException {
        BigDecimal paymentAmount = TypeUtils.castToBigDecimal(billRec.get("receipts_amount"));

        //根据账户id查询账户所属机构
        Record accRec = Db.findById("account", "acc_id", TypeUtils.castToLong(recvRec.get("acc_id")));

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));

        Date recvTransDate = TypeUtils.castToDate(recvRec.get("trans_date"));

        String subjectCode = null;
        String debitCredit = null;

        if (iden == 1) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = "8120000040";
            debitCredit = "C";
        } else if (iden == 3) {
            subjectCode = "4110000000";
            debitCredit = "C";
        } else if (iden == 4) {
            subjectCode = "4119000000";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        String serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
        //描述改为单据的摘要信息
        String description = TypeUtils.castToString(billRec.get("receipts_summary"));
        String curr = TypeUtils.castToString(billRec.get("recv_account_cur"));

        int tranId = recvRec.get("id");

        Record record = new Record()
                .set("trans_id", tranId)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", transDateMap.get(tranId)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", "SYS")
                .set("a_code7", "SYS")
                .set("a_code10", TypeUtils.castToString(orgRec.get("code")))
                .set("base_amount", paymentAmount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", paymentAmount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy", recvTransDate))
                .set("transaction_reference", "SSAL" + DateFormatThreadLocal.format("YYMM", new Date()) + seqnoOrstatmentCode)
//                .set("file_name", payRec.get(""))
//                .set("export_count", payRec.get(""));
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd", recvTransDate))    //本地交易时间(yyyy-MM-dd)
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", transDateMap.get(tranId)))         //记账区间(yyyy-MM)
                .set("docking_status", 0)            //是否已生成xml文件 0:否 1:是
                .set("biz_id", TypeUtils.castToString(billRec.get("biz_id")))                    // 业务类型id 关联 cfm_biz_type_setting.biz_id
                .set("biz_name", TypeUtils.castToString(billRec.get("biz_name")))                  //业务类型名称
                .set("statement_code", newStatmentCode)            //对账码
                .set("business_ref_no", serviceSerialNumber)         //业务编码,内部调拨,支付同关联service_serial_number, LA, EAS 业务关联 保单号
                .set("biz_type", WebConstant.MajorBizType.SKT.getKey());

        if (userInfo != null) {
            record.set("operator", userInfo.getUsr_id())                  //操作人
                    .set("operator_org", userInfo.getCurUodp().getOrg_id());              //操作人所在机构
        }
        return record;
    }

    public static Map<Integer, Date> getPeriod(List<Integer> tradingNo) throws ReqDataException {
        Map<Integer, Date> tradMap = new HashMap<>();
        //根据交易id查询对应交易日期
        for (Integer transId : tradingNo) {
            Record tradRec = Db.findById("acc_his_transaction", "id", transId);
            Date transDate = TypeUtils.castToDate(tradRec.get("trans_date"));

            Calendar c = Calendar.getInstance();
            c.setTime(transDate);

            //获取该月有多少天
            final int day = com.qhjf.cfm.utils.CommonService.getDayOfMonth(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
            String yearMonth = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1);
            String beginDate = yearMonth + "-" + 1;
            String endDate = yearMonth + "-" + day;
            //查询(交易日期)月初至月末间的结账日
            Record calRec = Db.findFirst(Db.getSql("workcal.findWorkingCalInterval"), beginDate, endDate);
            if (calRec == null) {
                throw new ReqDataException("该月未设置结账日，核对失败!");
            }


            /**
             * 如果交易日期小于结账日，则结算区间算本月
             * 如果交易日期大于结账日，则结算区间算下一个月
             * */
            Date calDate = TypeUtils.castToDate(calRec.get("cdate"));
            try {
                if (transDate.after(calDate)) {//大于结账日
                    if ((c.get(Calendar.MONTH) + 1) == 12) {
                        //查询下一年的一月是否有结账日，没有则抛出异常

                        Record nextCalRec = Db.findFirst(Db.getSql("workcal.findWorkingCalInterval"), (c.get(Calendar.YEAR) + 1) + "-1-1", (c.get(Calendar.YEAR) + 1) + "-1-31");
                        if (nextCalRec == null) {
                            throw new ReqDataException((c.get(Calendar.YEAR) + 1) + "未设置结账日，请联系管理员!");
                        }
                        tradMap.put(transId, DateFormatThreadLocal.convert("yyyy-MM", (c.get(Calendar.YEAR) + 1) + "-1"));
                    } else {
                        tradMap.put(transId, DateFormatThreadLocal.convert("yyyy-MM", c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 2)));
                    }
                } else {//小于结账日
                    tradMap.put(transId, DateFormatThreadLocal.convert("yyyy-MM", yearMonth));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                throw new ReqDataException("日期转换失败!");
            }
        }
        return tradMap;
    }

    /**
     * 获取当前日期所属会计区间
     *
     * @return
     * @throws ReqDataException
     */
    public static Date getPeriodByCurrentDay(Date date) throws ReqDataException {

        Calendar c = Calendar.getInstance();
        try {
            SimpleDateFormat sdfD = new SimpleDateFormat("yyyy-MM-dd");
            date = sdfD.parse(sdfD.format(date));
            c.setTime(date);
        } catch (ParseException e) {
            logger.error("日期转换失败!");
        }

        //获取该月有多少天
        final int day = com.qhjf.cfm.utils.CommonService.getDayOfMonth(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
        String yearMonth = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1);
        String beginDate = yearMonth + "-" + 1;
        String endDate = yearMonth + "-" + day;
        //查询(交易日期)月初至月末间的结账日
        Record calRec = Db.findFirst(Db.getSql("workcal.findWorkingCalInterval"), beginDate, endDate);
        if (calRec == null) {
            throw new ReqDataException("该月未设置结账日，核对失败!");
        }


        /**
         * 如果交易日期小于结账日，则结算区间算本月
         * 如果交易日期大于结账日，则结算区间算下一个月
         * */
        Date calDate = TypeUtils.castToDate(calRec.get("cdate"));
        try {
            if (date.after(calDate)) {//大于结账日
                if ((c.get(Calendar.MONTH) + 1) == 12) {
                    //查询下一年的一月是否有结账日，没有则抛出异常

                    Record nextCalRec = Db.findFirst(Db.getSql("workcal.findWorkingCalInterval"), (c.get(Calendar.YEAR) + 1) + "-1-1", (c.get(Calendar.YEAR) + 1) + "-1-31");
                    if (nextCalRec == null) {
                        throw new ReqDataException((c.get(Calendar.YEAR) + 1) + "未设置结账日，请联系管理员!");
                    }
                    return DateFormatThreadLocal.convert("yyyy-MM", (c.get(Calendar.YEAR) + 1) + "-1");
                } else {
                    return DateFormatThreadLocal.convert("yyyy-MM", c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 2));
                }
            } else {//小于结账日
                return DateFormatThreadLocal.convert("yyyy-MM", yearMonth);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ReqDataException("日期转换失败!");
        }
    }

    private static String getDesc(Record billRec, String seqnoOrstatmentCode, WebConstant.MajorBizType bizType) {
        String serviceSerialNumber;
        //描述改为单据的摘要信息
        String description;
        String batchno;
        if (bizType == WebConstant.MajorBizType.INNERDB) {
            serviceSerialNumber = TypeUtils.castToString(billRec.get("service_serial_number"));
            description = TypeUtils.castToString(billRec.get("payment_summary"));
        } else {
            batchno = TypeUtils.castToString(billRec.get("batchno"));
            description = TypeUtils.castToString(billRec.get("memo"));
        }
        return description;
    }

    public static boolean saveCheckVoucher(List<Record> list) {
        int[] result = Db.batchSave("sun_voucher_data", list, 1000);
        return ArrayUtil.checkDbResult(result);
    }

    /**
     * 获取某年某月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, 0);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 根据用户的org_id 和单据的org_id 判断用户是否有权限查看单据
     *
     * @param use_org_id  用户org_id
     * @param bill_org_id 单据org_id
     * @throws BusinessException 无权限则抛出异常
     */
    public static boolean checkUseCanViewBill(Long use_org_id, Long bill_org_id) throws BusinessException {
        TableDataCacheUtil util = TableDataCacheUtil.getInstance();
        Map userOrg = util.getARowData("organization", "org_id", use_org_id + "");
        Map billOrg = util.getARowData("organization", "org_id", bill_org_id + "");
        if (!TypeUtils.castToString(billOrg.get("level_code")).startsWith(TypeUtils.castToString(userOrg.get("level_code")))) {
            throw new DbProcessException("无权查看此单据!");
        }
        return true;
    }

    /**
     * 收付通主批次 批次号  YYYYMMDDHHmmss_PriPKN_000001
     *
     * @return
     */
    public static String getSftMasterBatchno() {
        RedisCacheConfigSection redisCacheConfigSection = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);
        String redisCacheName = redisCacheConfigSection.getCacheName();
        String now_day = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String now_second = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String key = String.format("serial:%s", now_day + "_PriPKN_");
        Long sequence = Redis.use(redisCacheName).incr(key);
        return String.format("%s%06d", now_second + "_PriPKN_", sequence);

    }

    /**
     * 收付通子批次 批次号  YYYYMMDDHHmmss_SubPKN_000001
     *
     * @param
     * @return
     */
    public static String getSftSonBatchno() {
        RedisCacheConfigSection redisCacheConfigSection = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);
        String redisCacheName = redisCacheConfigSection.getCacheName();
        String now_day = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String now_second = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String key = String.format("serial:%s", now_day + "_SubPKN_");
        Long sequence = Redis.use(redisCacheName).incr(key);
        return String.format("%s%06d", now_second + "_SubPKN_", sequence);
    }


    /**
     * 判断用户是否在列表中
     *
     * @param userInfo 用户信息
     * @param records  用户列表
     * @return
     */
    public static boolean isUserInList(UserInfo userInfo, List<Record> records) {
        if (records != null && records.size() > 0) {
            for (Record record : records) {
                if (record.getLong("usr_id").longValue() == userInfo.getUsr_id().longValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 柜面收个单团单确认接口
     * @param waitMatchFlag 0 常规 1 待匹配
     * @return
     * @throws BusinessException
     */
    public static Record gmsConfirmVoucher(Record payLegalRecord, int iden, String seqnoOrstatmentCode, int waitMatchFlag) throws BusinessException {
        BigDecimal amount = TypeUtils.castToBigDecimal(payLegalRecord.get("amount"));

        String subjectCode = null;
        String debitCredit = null;
        String description=null, code6=null, transactionReference=null;
        //根据通道找对应的bankcode和channel_code，然后根据channel_code找对应的账号
        Date backDate = new Date();
        Record chann = Db.findFirst(Db.getSql("channel_setting.getchannelbybankcode"), payLegalRecord.get("recv_bank_name"));
        Record acc = Db.findFirst(Db.getSql("paycheck.findaccount"), chann.getStr("bankcode"));
        String curr = TypeUtils.castToString(
                Db.findById("currency","id", TypeUtils.castToLong(acc.get("curr_id")))
                        .get("iso_code"));
        //根据机构id查询机构信息
        String orgcode = TypeUtils.castToString(
                Db.findById("organization","org_id", TypeUtils.castToLong(acc.get("org_id")))
                        .get("code"));
        Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);
        if(waitMatchFlag == 0){
            description = "系统确认收费日期"+payLegalRecord.get("recv_bank_name")+"柜面收费确认-常规";
        }else if(waitMatchFlag == 1){
            description = "系统转保单核销收费日期"+payLegalRecord.get("recv_bank_name")+"柜面收费确认-待匹配-转保单收费";
        }
        transactionReference = "SS" + sftcheck.getStr("code_abbre") + DateFormatThreadLocal.format("YYMM",backDate) + seqnoOrstatmentCode;
        if (iden == 1) {        //常规确认 LA
            subjectCode = "8360000002";
            debitCredit = "C";
            code6 = "SYS";
        } else if (iden == 2) {  //常规确认 EBS
            subjectCode = "8360000003";
            debitCredit = "C";
            code6 = "SYS";
        } else if (iden == 3) {   //常规确认 D方向
            if(waitMatchFlag == 0){
                subjectCode = "7110000016";
            }else if(waitMatchFlag == 1){
                subjectCode = "8120000012";
            }
            debitCredit = "D";
            code6 = chann.getStr("channel_code")+chann.getStr("bankcode");
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }

        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",backDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",backDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM", com.qhjf.cfm.utils.CommonService.getPeriodByCurrentDay(backDate)))
                .set("docking_status", 0)
                .set("statement_code", seqnoOrstatmentCode)
                .set("business_ref_no", TypeUtils.castToString(payLegalRecord.get("insure_bill_no")))
                .set("biz_type", TypeUtils.castToString(payLegalRecord.get("xx_biz_type")))
                .set("ref_bill_id", "recv_counter_bill")
                .set("ref_bill", payLegalRecord.get("id"))
                .set("transaction_reference", transactionReference);

    }

    /**
     * 柜面收 个单团单对账
     * @param accRec
     * @param iden
     * @param periodDate
     * @param transDate
     * @param detailRecord
     * @param curr
     * @param orgcode
     * @return
     * @throws BusinessException
     */
    public static Record gmsCheckVoucher(Record accRec,
                                       String description,
                                       String code6,
                                       String transactionReference,
                                       int iden,
                                       Date periodDate,
                                       Date transDate,
                                       Record detailRecord,
                                       String curr,
                                       String orgcode
    ) throws BusinessException {

        BigDecimal amount = TypeUtils.castToBigDecimal(detailRecord.get("amount"));
        String subjectCode = null;
        String debitCredit = null;
        if (iden == 1) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 2) {
            subjectCode = "7110000016";
            debitCredit = "C";
        } else if (iden == 3) {
            subjectCode = TypeUtils.castToString(accRec.get("subject_code"));
            debitCredit = "D";
        } else if (iden == 4) {
            subjectCode = "8120000012";
            debitCredit = "C";
        } else {
            throw new ReqDataException("借贷标识未定义!");
        }
        return new Record()
                .set("trans_id", 0)
                .set("account_code", subjectCode)
                .set("account_period", DateFormatThreadLocal.format("0MMyyyy",periodDate))
                .set("a_code1", "G")
                .set("a_code2", "CO")
                .set("a_code3", "S")
                .set("a_code5", "SYS")
                .set("a_code6", code6)
                .set("a_code7", "SYS")
                .set("a_code10", orgcode)
                .set("base_amount", amount)
                .set("currency_code", curr)
                .set("debit_credit", debitCredit)
                .set("description", description)
                .set("journal_source", "SST")
                .set("transaction_amount", amount)
                .set("transaction_date", DateFormatThreadLocal.format("ddMMyyyy",transDate))
                .set("local_transaction_date", DateFormatThreadLocal.format("yyyy-MM-dd",transDate))
                .set("accounting_period", DateFormatThreadLocal.format("yyyy-MM",periodDate))
                .set("docking_status", 0)
                .set("operator", TypeUtils.castToLong(detailRecord.get("xx_operator")))
                .set("operator_org", TypeUtils.castToLong(detailRecord.get("xx_operator_org")))
                .set("statement_code", TypeUtils.castToString(detailRecord.get("xx_seqnoOrstatmentCode")))
                .set("business_ref_no", TypeUtils.castToString(detailRecord.get("insure_bill_no")))
                .set("biz_type", TypeUtils.castToString(detailRecord.get("xx_biz_type")))
                .set("transaction_reference", transactionReference);
    }

    public static void main(String[] args) {
//        DruidPlugin dp = new DruidPlugin("jdbc:sqlserver://10.1.1.157:1433;DatabaseName=demo", "sa", ".");
//        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
//        arp.setDevMode(true);
//        arp.setDialect(new SqlServerDialect());
//        arp.setShowSql(true);
//        arp.addSqlTemplate("sql/sqlserver/common_cfm.sql");
//        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
//        dp.start();
//        arp.start();
//
//        //Db.update("create table test2(totalnum int,totalAmount decimal(16,2),batchno nvarchar(100))");
//
//        boolean update = update("test", new Record().set("name","").set("date_d", new Date()).set("time_t",new Date()).set("time_s",new Date()), new Record().set("id", 1));
//        System.out.println(update);
//
//        dp.stop();
//        arp.stop();   
    }
}
