package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import org.jsoup.helper.StringUtil;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 收款通
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class SktService {
    /**
     * 增加制单接口
     *
     * @param ""
     * @param userInfo
     * @param returnRecord
     * @param insertRecord
     * @param pay_info
     * @throws DbProcessException
     */
    public Record insert(UodpInfo uodpInfo, UserInfo userInfo, Record initrecord, Record rev_record,
                         final Record insertRecord, Record returnRecord, Record pay_info) throws DbProcessException {
        // 根据银行的cnaps码,查询银行名称
        final List<Object> list = initrecord.get("files");
        insertRecord.set("pay_account_id", pay_info.get("id")); // 付款账户id
        insertRecord.set("pay_account_no", pay_info.get("acc_no")); // 付款方账号
        insertRecord.set("pay_account_name", pay_info.get("acc_name")); // 付款方账号
        Record payBank = Db.findById("currency", "id", pay_info.get("curr_id"));
        insertRecord.set("pay_account_cur", payBank == null ? "CNY" : payBank.get("iso_code")); // 付款方币种编号
        insertRecord.set("pay_account_bank", pay_info.get("bank_name")); // 付款方银行名称
        insertRecord.set("pay_bank_cnaps", pay_info.get("cnaps_code")); // 付款方银行cnaps
        insertRecord.set("pay_bank_prov", pay_info.get("province")); // 付款方账户银行所在省
        insertRecord.set("pay_bank_city", pay_info.get("city")); // 付款方账户银行所在市

        insertRecord.set("recv_account_id", rev_record.get("acc_id")); // 收款方账号
        insertRecord.set("recv_account_no", rev_record.get("acc_no")); // 收款方账号
        insertRecord.set("recv_account_name", rev_record.get("acc_name")); // 收款方账户名
        insertRecord.set("recv_account_cur", rev_record.get("curr_code")); // 收款方账户币种编号
        insertRecord.set("recv_account_bank", rev_record.get("bank_name")); // 收款方银行名称
        insertRecord.set("recv_bank_cnaps", rev_record.get("bank_cnaps_code")); // 收款方账户银行cnaps
        insertRecord.set("recv_bank_prov", rev_record.get("province")); // 收款方账户银行所在省
        insertRecord.set("recv_bank_city", rev_record.get("city")); // 收款方账户银行所在市

        insertRecord.set("receipts_mode", initrecord.get("receipts_mode")); // 收款方式
        insertRecord.set("receipts_amount", initrecord.get("receipts_amount")); // 收款金额
        insertRecord.set("receipts_summary", initrecord.get("receipts_summary")); // 摘要信息
        insertRecord.set("attachment_count", list != null ? list.size() : 0); // 附件数量
        insertRecord.set("create_by", userInfo.getUsr_id()); // 用户id
        insertRecord.set("create_on", new Date()); // 创建时间
        insertRecord.set("apply_on", TypeUtils.castToDate(initrecord.get("apply_on")));//申请日期
        insertRecord.set("persist_version", 0); // 版本号
        insertRecord.set("org_id", uodpInfo.getOrg_id());
        insertRecord.set("dept_id", uodpInfo.getDept_id());
        insertRecord.set("delete_flag", 0); // 是否删除标志位
        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.SKT);
        insertRecord.set("service_serial_number", serviceSerialNumber);
        insertRecord.set("service_status", WebConstant.BillStatus.SAVED.getKey()); // 单据状态 1 : 已保存
        insertRecord.set("biz_id", initrecord.get("biz_id"));
        insertRecord.set("biz_name", initrecord.get("biz_name"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                boolean flag = Db.save("outer_sk_receipts", insertRecord);
                if (flag) {
                    //保存附件
                    if (list != null && list.size() > 0) {
                        return CommonService.saveFileRef(WebConstant.MajorBizType.SKT.getKey(),
                                TypeUtils.castToLong(insertRecord.get("id")), list);
                    }
                    return true;
                }
                return false;
            }
        });
        if (flag) {
            returnRecord = Db.findById("outer_sk_receipts", "id", TypeUtils.castToLong(insertRecord.get("id")));
            returnRecord.set("pay_persist_version", pay_info.get("persist_version"));
            return returnRecord;
        }

        throw new DbProcessException("收款单据保存失败!");

    }

    /**
     * 根据付款方id查询付款人信息
     *
     * @param record
     * @param userInfo
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record querySupplierInfo(String pay_acc_id, Record record, UserInfo userInfo)
            throws ReqDataException, DbProcessException {

        Record paramsToRecord = new Record();
        // 前台传输的版本号
        int persist_version = TypeUtils.castToInt(record.get("pay_persist_version"));
        String acc_name = TypeUtils.castToString(record.get("pay_account_name"));
        String cnaps_code = TypeUtils.castToString(record.get("pay_bank_cnaps"));
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", cnaps_code);
        paramsToRecord.set("acc_name", acc_name);
        paramsToRecord.set("cnaps_code", cnaps_code);
        paramsToRecord.set("bank_name", bankRec.get("name"));
        paramsToRecord.set("province", bankRec.get("province"));
        paramsToRecord.set("city", bankRec.get("city"));
        paramsToRecord.set("address", bankRec.get("address"));
        paramsToRecord.set("update_on", new Date());
        paramsToRecord.set("update_by", userInfo.getUsr_id());
        paramsToRecord.set("persist_version", persist_version + 1);
        final Kv kv = Kv.create();
        kv.set("set", paramsToRecord.getColumns());
        // where条件约束
        kv.set("where", new Record().set("id", pay_acc_id).set("persist_version", persist_version).getColumns());

        boolean flag = chgRevDbIdAndVersion(paramsToRecord,
                new Record().set("id", pay_acc_id).set("persist_version", persist_version));

        if (!flag) {
            throw new DbProcessException("更新收款人信息失败!");
        }
        // 获取收款人信息
        Record supplier_info = Db.findById("supplier_acc_info", "id", pay_acc_id);
        if (null == supplier_info) {
            throw new ReqDataException("根据付款人id" + pay_acc_id + "查询付款人信息不存在!!");
        }
        return supplier_info;
    }

    /**
     * 维护付款人表
     *
     * @param record
     * @param userInfo
     * @return
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record insertSupplier(Record record, UserInfo userInfo) throws ReqDataException {
        String pay_name = TypeUtils.castToString(record.get("pay_account_name"));
        String pay_no = TypeUtils.castToString(record.get("pay_account_no"));
        String cnaps_code = TypeUtils.castToString(record.get("pay_bank_cnaps"));
        Record supplier_info = new Record();
        if (StringUtil.isBlank(pay_name) || StringUtil.isBlank(pay_no) || StringUtil.isBlank(cnaps_code)) {
            throw new ReqDataException("请求参数错误_付款人信息传输不全");
        }
        String Supplier_sql = Db.getSql("supplier.querySupplier");
        List<Record> Supplier_infos = Db.find(Supplier_sql, pay_no);
        if (null != Supplier_infos && Supplier_infos.size() > 0) {
            return supplier_info = Supplier_infos.get(0);
        }
        //付款人信息都传输了,开始维护收款人信息表
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", cnaps_code);
        if (bankRec == null) {
            throw new ReqDataException("未根据银行cnapcode查询到银行信息!");
        }
        supplier_info.set("cnaps_code", cnaps_code);
        supplier_info.set("acc_no", pay_no);
        supplier_info.set("acc_name", pay_name);
        supplier_info.set("bank_name", bankRec.get("name"));
        supplier_info.set("curr_id", 1);
        supplier_info.set("create_on", new Date());
        supplier_info.set("province", bankRec.get("province"));
        supplier_info.set("city", bankRec.get("city"));
        supplier_info.set("address", bankRec.get("address"));
        supplier_info.set("persist_version", 0);
        supplier_info.set("delete_flag", 0);
        supplier_info.set("create_by", userInfo.getUsr_id());
        boolean flag = Db.save("supplier_acc_info", supplier_info);
        if (!flag) {
            throw new ReqDataException("维护付款人信息表错误!");
        }
        return supplier_info;
    }

    /**
     * 根据付款方id查询付款方信息
     *
     * @param payAccountId
     * @throws ReqDataException
     */
    public Record queryPayInfo(long payAccountId) throws ReqDataException {
        Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), payAccountId);
        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }
        return payRec;
    }

    public boolean saveFileRef(int biz_type, long bill_id, List<Object> files) {
        return CommonService.saveFileRef(biz_type, bill_id, files);
    }

    /**
     * 对单据进行删除,每进行一次操作,都要对version进行++
     *
     * @param paramsToRecord
     * @param userInfo
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public void deleteBill(Record paramsToRecord, UserInfo userInfo) throws ReqDataException, DbProcessException {
        long id = TypeUtils.castToLong(paramsToRecord.get("id"));
        Record innerRec = Db.findById("outer_sk_receipts", "id", id);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        if (!TypeUtils.castToLong(innerRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        int old_version = TypeUtils.castToInt(paramsToRecord.get("persist_version"));
        paramsToRecord.set("update_by", userInfo.getUsr_id());
        paramsToRecord.set("update_on", new Date());
        paramsToRecord.set("persist_version", old_version + 1);
        paramsToRecord.set("delete_flag", 1);
        paramsToRecord.remove("id");
        boolean flag = chgDbPaymentByIdAndVersion(paramsToRecord, new Record().set("id", id).set("persist_version", old_version));
        if (!flag) {
            throw new DbProcessException("删除收款通单据失败!");
        }
    }

    /**
     * 修改单据
     *
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record chg(final Record record, UserInfo userInfo, UodpInfo uodpInfo, Record payRec)
            throws ReqDataException, DbProcessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        // 根据单据id查询单据信息
        Record innerRec = Db.findById("outer_sk_receipts", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        if (!TypeUtils.castToLong(innerRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        long payAccountId = TypeUtils.castToLong(record.get("pay_account_id"));
        long recvAccountId = TypeUtils.castToLong(record.get("recv_account_id"));
        // 根据收款方帐号id查询账户信息
        Record recvRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), recvAccountId);
        if (recvRec == null) {
            throw new ReqDataException("未找到有效的收款方帐号!");
        }
        // 根据收款方cnaps号查询银行信息
        Record recvBankRec = Db.findById("all_bank_info", "cnaps_code",
                TypeUtils.castToString(recvRec.get("bank_cnaps_code")));
        if (recvBankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }
        // 根据付款方帐号id查询账户信息
        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }
        final List<Object> list = record.get("files");
        record.remove("files");
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.set("org_id", uodpInfo.getOrg_id());
        record.set("dept_id", uodpInfo.getDept_id());

        record.set("pay_account_id", payAccountId);
        record.set("pay_account_no", payRec.get("acc_no")); // 付款方账号
        record.set("pay_account_name", payRec.get("acc_name")); // 付款方账号

        Record payBank = Db.findById("currency", "id", payRec.get("curr_id"));
        record.set("pay_account_cur", payBank == null ? "CNY" : payBank.get("iso_code")); // 付款方币种编号
        record.set("pay_account_bank", payRec.get("bank_name")); // 付款方银行名称
        record.set("pay_bank_cnaps", payRec.get("cnaps_code")); // 付款方银行cnaps
        record.set("pay_bank_prov", payRec.get("province")); // 付款方账户银行所在省
        record.set("pay_bank_city", payRec.get("city")); // 付款方账户银行所在市

        record.set("recv_account_id", recvAccountId); // 收款方账号
        record.set("recv_account_no", recvRec.get("acc_no")); // 收款方账号
        record.set("recv_account_name", recvRec.get("acc_name")); // 收款方账户名
        record.set("recv_account_cur", recvRec.get("curr_code")); // 收款方账户币种编号
        record.set("recv_account_bank", recvRec.get("bank_name")); // 收款方银行名称
        record.set("recv_bank_cnaps", recvRec.get("bank_cnaps_code")); // 收款方账户银行cnaps
        record.set("recv_bank_prov", recvRec.get("province")); // 收款方账户银行所在省
        record.set("recv_bank_city", recvRec.get("city")); // 收款方账户银行所在市

        record.set("process_bank_type", TypeUtils.castToString(recvRec.get("bank_type")));// 收款方银行大类
        record.set("attachment_count", list != null ? list.size() : 0);
        record.remove("pay_persist_version");
        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("persist_version", old_version + 1);

        // 要更新的列
        record.remove("id");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                if (chgDbPaymentByIdAndVersion(record, new Record().set("id", id).set("persist_version", old_version))) {
                    // 删除附件
                    CommonService.delFileRef(WebConstant.MajorBizType.SKT.getKey(), id);
                    if (list != null && list.size() > 0) {
                        // 保存附件
                        return CommonService.saveFileRef(WebConstant.MajorBizType.SKT.getKey(), id, list);
                    }
                    return true;
                }
                return false;
            }
        });

        if (flag) {
            Record findById = Db.findById("outer_sk_receipts", "id", id);
            Record supplier_info = Db.findById("supplier_acc_info", "id", payAccountId);
            findById.set("pay_persist_version", supplier_info.get("persist_version"));
            return findById;
        }
        throw new DbProcessException("修改支付单据失败!");
    }

    /**
     * 更新单据信息sql拼装
     *
     * @return
     */
    public boolean chgDbPaymentByIdAndVersion(final Record set, final Record where) {
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("outer_sk_receipts", set, where);
            }
        });
    }

    /**
     * 更多列表查询单据
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param userInfo
     * @return
     */
    public Page<Record> morelist(int pageNum, int pageSize, Record record, UserInfo userInfo) {
        record.set("create_by", userInfo.getUsr_id());
        SqlPara sqlPara = getlistparam(record, "skzf.findMoreInfo");
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 查询所有单据总金额
     *
     * @param record
     * @param userInfo
     * @return
     */
    public Record morelisttotal(Record record, UserInfo userInfo) {
        record.set("create_by", userInfo.getUsr_id());
        SqlPara sqlPara = Db.getSqlPara("skzf.findAllAmount", Kv.by("map", record.getColumns()));
        return Db.findFirst(sqlPara);
    }

    /**
     * 获取更多、查看、支付处理列表查询参数
     *
     * @param record
     * @param sql
     * @return
     */
    public SqlPara getlistparam(final Record record, String sql) {
        CommonService.processQueryKey(record, "pay_query_key", "pay_account_name", "pay_account_no");
        CommonService.processQueryKey(record, "recv_query_key", "recv_account_name", "recv_account_no");
        record.set("delete_flag", 0);
        return Db.getSqlPara(sql, Kv.by("map", record.getColumns()));
    }

    /**
     * 单据列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> detallist(int pageNum, int pageSize, Record record, UodpInfo uodpInfo) {
        record.set("org_id", uodpInfo.getOrg_id());
        SqlPara sqlPara = getlistparam(record, "skzf.findMoreInfo");
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * @param record
     * @return
     */
    public Record detaillisttotal(Record record, UodpInfo uodpInfo) {
        record.set("org_id", uodpInfo.getOrg_id());
        SqlPara sqlPara = Db.getSqlPara("skzf.findAllAmount", Kv.by("map", record.getColumns()));
        return Db.findFirst(sqlPara);
    }

    /**
     * 查看单据详情
     *
     * @param record
     * @param userInfo
     * @return
     * @throws ReqDataException
     */
    public Record detail(Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        // 根据单据id查询单据信息
        Record dbRec = Db.findById("outer_sk_receipts", "id", id);
        if (dbRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), dbRec.getLong("org_id"));
        }


        Record rev_Rec = Db.findById("supplier_acc_info", "id", dbRec.get("pay_account_id"));
        dbRec.set("pay_persist_version", rev_Rec.get("persist_version"));
        return dbRec;
    }


    /**
     * 更新付款人账户信息
     *
     * @return
     */
    public boolean chgRevDbIdAndVersion(final Record set, final Record where) {
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("supplier_acc_info", set, where);
            }
        });

    }

    /**
     * 特殊处理：点击提交直接变成审批通过状态
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public boolean chgServiceStatus(final Record record) throws BusinessException {

        final Long id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("outer_sk_receipts",
                        new Record().set("service_status", WebConstant.BillStatus.SUCCESS.getKey()).set("persist_version", (version + 1)),
                        record);
            }
        });

        if (!flag) {
            throw new DbProcessException("提交失败!");
        }
        return flag;
    }

}
