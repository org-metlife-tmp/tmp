package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysSinglePayInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9
 * @Description: 调拨通
 */
public class DbtService {

    private static Logger log = LoggerFactory.getLogger(DbtService.class);

    /**
     * 调拨通 - 更多分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param userInfo
     * @return
     */
    public Page<Record> morelist(int pageNum, int pageSize, final Record record, UserInfo userInfo) {
        Kv kv = Kv.create();
        kv.set("create_by", userInfo.getUsr_id());
        //翻页数据查询丢失的问题在与sql的查询中存在相同权重的数据排序，修改方案为在排序中将唯一的标识（eg:id）添加到sql中。
        SqlPara sqlPara = getlistparam(record, "nbdb.findInnerDbPaymentMoreList", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 调拨通 - 更多列表汇总条数以及汇总金额
     *
     * @param record
     * @param userInfo
     * @return
     */
    public Record morelisttotal(final Record record, UserInfo userInfo) {
        Kv kv = Kv.create();
        kv.set("create_by", userInfo.getUsr_id());
        SqlPara sqlPara = getlistparam(record, "nbdb.findInnerDbPaymentAllListTotal", kv);
        return Db.findFirst(sqlPara);
    }

    /**
     * 调拨通 - 查看分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> detallist(int pageNum, int pageSize, final Record record, final UodpInfo uodpInfo) {
        Kv kv = Kv.create();
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        kv.set("level_code", orgRec.get("level_code"));
        kv.set("level_num", orgRec.get("level_num"));

        SqlPara sqlPara = getlistparam(record, "nbdb.findInnerDbPaymentDetailList", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 调拨通 - 查看列表汇总条数以及汇总金额
     *
     * @param record
     * @return
     */
    public Record detaillisttotal(final Record record, final UodpInfo uodpInfo) {
        Kv kv = Kv.create();

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        kv.set("level_code", orgRec.get("level_code"));
        kv.set("level_num", orgRec.get("level_num"));

        SqlPara sqlPara = getlistparam(record, "nbdb.findInnerDbPaymentAllListTotal", kv);
        return Db.findFirst(sqlPara);
    }

    /**
     * 调拨通 - 支付处理分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param uodpInfo
     * @return
     */
    public Page<Record> paylist(int pageNum, int pageSize, final Record record, UodpInfo uodpInfo) {
        Kv kv = Kv.create();
        kv.set("org_id", uodpInfo.getOrg_id());
        kv.set("interactive_mode", TypeUtils.castToInt(record.get("interactive_mode")));
        SqlPara sqlPara = getlistparam(record, "nbdb.findInnerDbPaymentPayList", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 调拨通 - 支付处理列表汇总条数以及汇总金额
     *
     * @param record
     * @param uodpInfo
     * @return
     */
    public Record paylisttotal(final Record record, UodpInfo uodpInfo) {
        Kv kv = Kv.create();
        kv.set("org_id", uodpInfo.getOrg_id());
        kv.set("interactive_mode", TypeUtils.castToInt(record.get("interactive_mode")));
        SqlPara sqlPara = getlistparam(record, "nbdb.findInnerDbPaymentAllListTotal", kv);
        return Db.findFirst(sqlPara);
    }

    /**
     * 获取更多、查看、支付处理列表查询参数
     *
     * @param record
     * @param sql
     * @param kv
     * @return
     */
    public SqlPara getlistparam(final Record record, String sql, final Kv kv) {

        CommonService.processQueryKey4Kv(record, kv, "pay_query_key", "pay_account_name", "pay_account_no");
        CommonService.processQueryKey4Kv(record, kv, "recv_query_key", "recv_account_name", "recv_account_no");

        Integer payment_type = TypeUtils.castToInt(record.get("payment_type"));
        if (payment_type != null) {
            kv.set("payment_type", payment_type);
        }

        BigDecimal min = TypeUtils.castToBigDecimal(record.get("min"));
        if (min != null) {
            kv.set("min", min);
        }

        BigDecimal max = TypeUtils.castToBigDecimal(record.get("max"));
        if (max != null) {
            kv.set("max", max);
        }

        Integer payMode = TypeUtils.castToInt(record.get("pay_mode"));
        if (payMode != null) {
            kv.set("pay_mode", payMode);
        }

        String startDate = TypeUtils.castToString(record.get("start_date"));
        if (startDate != null && !"".equals(startDate)) {
            kv.set("start_date", startDate);
        }

        String endDate = TypeUtils.castToString(record.get("end_date"));
        if (endDate != null && !"".equals(endDate)) {
            kv.set("end_date", endDate);
        }

        String bizId = TypeUtils.castToString(record.get("biz_id"));
        if (bizId != null && !"".equals(bizId)) {
            kv.set("biz_id", bizId);
        }

        kv.set("delete_flag", 0);
        kv.set("service_status", record.get("service_status"));
        return Db.getSqlPara(sql, Kv.by("map", kv));
    }

    /**
     * 调拨通 - 查看单据详情
     *
     * @param record
     * @param userInfo
     * @return
     */
    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));

        //根据单据id查询单据信息
        Record dbRec = Db.findById("inner_db_payment", "id", id);
        if (dbRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), dbRec.getLong("org_id"));
        }


        return dbRec;
    }

    /**
     * 调拨通 - 新增
     *
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     */
    public Record add(final Record record, UserInfo userInfo, UodpInfo uodpInfo) throws BusinessException {

        Long payOrgLevel = null, recvOrgLevel = null;
        long payAccountId = TypeUtils.castToLong(record.get("pay_account_id"));
        long recvAccountId = TypeUtils.castToLong(record.get("recv_account_id"));
        String biz_id = record.get("biz_id") == null ? "" : TypeUtils.castToString(record.get("biz_id"));
        String biz_name = record.get("biz_name");

        //根据付款方帐号id查询账户信息
        Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), payAccountId);
        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }

        payOrgLevel = TypeUtils.castToLong(payRec.get("level_num"));

        //根据付款方cnaps号查询银行信息
        Record payBankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        if (payBankRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }

        //根据收款方帐号id查询账户信息
        Record recvRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), recvAccountId);
        if (recvRec == null) {
            throw new ReqDataException("未找到有效的收款方帐号!");
        }

        recvOrgLevel = TypeUtils.castToLong(recvRec.get("level_num"));

        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.INNERDB);

        final List<Object> list = record.get("files");
        record.remove("files");

        if (!"".equals(biz_id) && biz_name != null && biz_name.length() > 0) {
            record.set("biz_id", biz_id);
            record.set("biz_name", biz_name);
        }

        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("service_serial_number", serviceSerialNumber);
        record.set("create_by", userInfo.getUsr_id());
        record.set("create_on", new Date());
        record.set("delete_flag", 0);
        record.set("org_id", uodpInfo.getOrg_id());
        record.set("dept_id", uodpInfo.getDept_id());
        record.set("pay_account_id", payAccountId);
        record.set("pay_account_no", TypeUtils.castToString(payRec.get("acc_no")));
        record.set("pay_account_name", TypeUtils.castToString(payRec.get("acc_name")));
        record.set("pay_account_bank", TypeUtils.castToString(payRec.get("bank_name")));
        record.set("recv_account_id", recvAccountId);
        record.set("recv_account_no", TypeUtils.castToString(recvRec.get("acc_no")));
        record.set("recv_account_name", TypeUtils.castToString(recvRec.get("acc_name")));
        record.set("recv_account_bank", TypeUtils.castToString(recvRec.get("bank_name")));
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("pay_account_cur", payRec.get("curr_code"));
        record.set("recv_account_cur", recvRec.get("curr_code"));
        record.set("pay_bank_cnaps", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        record.set("recv_bank_cnaps", TypeUtils.castToString(recvRec.get("bank_cnaps_code")));
        record.set("pay_bank_prov", TypeUtils.castToString(payBankRec.get("province")));
        record.set("recv_bank_prov", TypeUtils.castToString(recvRec.get("province")));
        record.set("pay_bank_city", TypeUtils.castToString(payBankRec.get("city")));
        record.set("recv_bank_city", TypeUtils.castToString(recvRec.get("city")));
        record.set("process_bank_type", TypeUtils.castToString(payBankRec.get("bank_type")));//付款方银行大类
        record.set("persist_version", 0);
        record.set("attachment_count", list != null ? list.size() : 0);

        int paymenType = getPayMentType(recvOrgLevel, payOrgLevel);
        record.set("payment_type", paymenType);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                boolean flag = Db.save("inner_db_payment", record);
                if (flag) {
                    //保存附件
                    if (list != null && list.size() > 0) {
                        return CommonService.saveFileRef(WebConstant.MajorBizType.INNERDB.getKey(),
                                TypeUtils.castToLong(record.get("id")), list);
                    }
                    return true;
                }
                return false;
            }
        });
        if (flag) {
            return Db.findById("inner_db_payment", "id", TypeUtils.castToLong(record.get("id")));
        }

        throw new DbProcessException("调拨单据保存失败!");
    }

    /**
     * 调拨通 - 修改
     *
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     */
    public Record chg(final Record record, UserInfo userInfo, UodpInfo uodpInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        //根据单据id查询单据信息
        Record innerRec = Db.findById("inner_db_payment", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(innerRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        Long payOrgLevel = null, recvOrgLevel = null;
        long payAccountId = TypeUtils.castToLong(record.get("pay_account_id"));
        long recvAccountId = TypeUtils.castToLong(record.get("recv_account_id"));

        String biz_id = record.get("biz_id") == null ? "" : TypeUtils.castToString(record.get("biz_id"));
        String biz_name = record.get("biz_name");

        //根据付款方帐号id查询账户信息
        Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), payAccountId);
        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }

        payOrgLevel = TypeUtils.castToLong(payRec.get("level_num"));

        //根据付款方cnaps号查询银行信息
        Record payBankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        if (payBankRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }

        //根据收款方帐号id查询账户信息
        Record recvRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), recvAccountId);
        if (recvRec == null) {
            throw new ReqDataException("未找到有效的收款方帐号!");
        }

        recvOrgLevel = TypeUtils.castToLong(recvRec.get("level_num"));

        final List<Object> list = record.get("files");
        record.remove("files");

        if (!"".equals(biz_id) && biz_name != null && biz_name.length() > 0) {
            record.set("biz_id", biz_id);
            record.set("biz_name", biz_name);
        }


        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.set("org_id", uodpInfo.getOrg_id());
        record.set("dept_id", uodpInfo.getDept_id());
        record.set("pay_account_id", payAccountId);
        record.set("pay_account_no", TypeUtils.castToString(payRec.get("acc_no")));
        record.set("pay_account_name", TypeUtils.castToString(payRec.get("acc_name")));
        record.set("pay_account_bank", TypeUtils.castToString(payRec.get("bank_name")));
        record.set("recv_account_id", recvAccountId);
        record.set("recv_account_no", TypeUtils.castToString(recvRec.get("acc_no")));
        record.set("recv_account_name", TypeUtils.castToString(recvRec.get("acc_name")));
        record.set("recv_account_bank", TypeUtils.castToString(recvRec.get("bank_name")));
        record.set("pay_account_cur", payRec.get("curr_code"));
        record.set("recv_account_cur", recvRec.get("curr_code"));
        record.set("pay_bank_cnaps", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        record.set("recv_bank_cnaps", TypeUtils.castToString(recvRec.get("bank_cnaps_code")));
        record.set("pay_bank_prov", TypeUtils.castToString(payBankRec.get("province")));
        record.set("recv_bank_prov", TypeUtils.castToString(recvRec.get("province")));
        record.set("pay_bank_city", TypeUtils.castToString(payBankRec.get("city")));
        record.set("recv_bank_city", TypeUtils.castToString(recvRec.get("city")));
        record.set("process_bank_type", TypeUtils.castToString(payBankRec.get("bank_type")));//付款方银行大类
        record.set("attachment_count", list != null ? list.size() : 0);

        int paymenType = getPayMentType(recvOrgLevel, payOrgLevel);
        record.set("payment_type", paymenType);

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("persist_version", old_version + 1);

        //要更新的列
        record.remove("id");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean result = CommonService.update("inner_db_payment",
                        record,
                        new Record()
                                .set("id", id)
                                .set("persist_version", old_version));
                if (result) {
                    //删除附件
                    CommonService.delFileRef(WebConstant.MajorBizType.INNERDB.getKey(), id);
                    if (list != null && list.size() > 0) {
                        //保存附件
                        return CommonService.saveFileRef(WebConstant.MajorBizType.INNERDB.getKey(),
                                id, list);
                    }
                    return true;
                }
                return false;
            }
        });

        if (flag) {
            return Db.findById("inner_db_payment", "id", id);
        }
        throw new DbProcessException("修改调拨单据失败!");
    }

    /**
     * 调拨通 - 删除
     *
     * @param record
     */
    public void del(final Record record, UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        Record innerRec = Db.findById("inner_db_payment", "id", id);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(innerRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.set("persist_version", old_version + 1);
        record.set("delete_flag", 1);

        //要更新的列
        record.remove("id");

        boolean flag = chgDbPaymentByIdAndVersion(record, new Record().set("id", id).set("persist_version", old_version));

        if (!flag) {
            throw new DbProcessException("删除调拨单据失败!");
        }
    }

    /**
     * 调拨通 - 支付单据发送
     *
     * @param record
     * @param userInfo
     */
    public void send(final Record record, UserInfo userInfo) throws BusinessException {
        List<Long> idsList = record.get("ids");
        record.remove("ids");

        Long[] ids = new Long[idsList.size()];

        for (int i = 0; i < idsList.size(); i++) {
            ids[i] = TypeUtils.castToLong(idsList.get(i));
        }

        for (Long id : ids) {
            Record innerRec = Db.findById("inner_db_payment", "id", id);
            if (innerRec == null) {
                throw new ReqDataException("未找到有效的单据!");
            }

            int service_status = TypeUtils.castToInt(innerRec.get("service_status"));

            if (service_status == WebConstant.BillStatus.PASS.getKey()
                    || service_status == WebConstant.BillStatus.FAILED.getKey()) {

                int old_version = TypeUtils.castToInt(innerRec.get("persist_version"));
                record.set("id", id);
                record.set("persist_version", old_version + 1);
                record.set("update_by", userInfo.getUsr_id());
                record.set("update_on", new Date());
                record.set("service_status", WebConstant.BillStatus.PROCESSING.getKey());

                //要更新的列
                record.remove("id");
                boolean flag = chgDbPaymentByIdAndVersion(record, new Record().set("id", id).set("persist_version", old_version));
                if (flag) {
                    //TODO 调用银行接口
                } else {
                    throw new DbProcessException("单据发送失败!");
                }

            } else {
                throw new ReqDataException("该单据状态不正确!");
            }
        }
    }

    /**
     * 调拨通 - 支付单据作废
     *
     * @param record
     * @param userInfo
     */
    public void cancel(final Record record, UserInfo userInfo) throws BusinessException {
        List<Long> idsList = record.get("ids");
        record.remove("ids");

        Long[] ids = new Long[idsList.size()];

        for (int i = 0; i < idsList.size(); i++) {
            ids[i] = TypeUtils.castToLong(idsList.get(i));
        }

        for (Long id : ids) {
            Record innerRec = Db.findById("inner_db_payment", "id", id);
            if (innerRec == null) {
                throw new ReqDataException("未找到有效的单据!");
            }

            int service_status = TypeUtils.castToInt(innerRec.get("service_status"));


            //判断单据状态为“审批通过”或“已失败”时可以发送，其他状态需抛出异常！
            if (service_status == WebConstant.BillStatus.PASS.getKey()
                    || service_status == WebConstant.BillStatus.FAILED.getKey()) {

                int old_version = TypeUtils.castToInt(innerRec.get("persist_version"));
                record.set("id", id);
                record.set("persist_version", old_version + 1);
                record.set("update_by", userInfo.getUsr_id());
                record.set("update_on", new Date());
                record.set("service_status", WebConstant.BillStatus.CANCEL.getKey());
                record.set("feed_back", TypeUtils.castToString(record.get("feed_back")));

                //要更新的列
                record.remove("id");

                boolean flag = chgDbPaymentByIdAndVersion(record, new Record().set("id", id).set("persist_version", old_version));
                if (!flag) {
                    throw new DbProcessException("单据作废失败!");
                }
            } else {
                throw new ReqDataException("该单据状态不正确!");
            }
        }


    }

    /**
     * 修改单据信息
     *
     * @return
     */
    public boolean chgDbPaymentByIdAndVersion(final Record set, final Record where) {
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("inner_db_payment", set, where);
            }
        });
    }

    /**
     * 获取调拨类型
     *
     * @param recvOrgLevel
     * @param payOrgLevel
     * @return
     */
    public int getPayMentType(long recvOrgLevel, long payOrgLevel) {
        if (recvOrgLevel == payOrgLevel) {//调拨类型，1：本公司内部账户调拨，2：本公司拨给下级公司，3：本公司拨给上级公司
            return WebConstant.ZjdbType.FORSELF.getKey();
        } else if (payOrgLevel > recvOrgLevel) {
            return WebConstant.ZjdbType.FORSUPPER.getKey();
        } else {
            return WebConstant.ZjdbType.FORJUNIOR.getKey();
        }
    }

    /**
     * 付款方账户列表（本级）
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param uodpInfo
     * @return
     * @throws BusinessException
     */
    public Page<Record> payacclist(int pageNum, int pageSize, final Record record, UodpInfo uodpInfo) throws BusinessException {
        long orgId = uodpInfo.getOrg_id();
        //根据orgid查询机构信息
        Record orgRec = Db.findById("organization", "org_id", orgId);
        if (orgRec == null) {
            throw new ReqDataException("获取付款方账户失败!");
        }

        Kv kv = Kv.create();
        kv.set("org_id", orgId);
//        kv.set("level_num", TypeUtils.castToInt(orgRec.get("level_num")));
//        kv.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));
        kv.set("status", WebConstant.AccountStatus.NORAMAL.getKey());
        kv.set("query_key", TypeUtils.castToString(record.get("query_key")));
        kv.set("exclude_ids", TypeUtils.castToLong(record.get("exclude_ids")));
        kv.set("is_activity", 1);
        kv.set("interactive_mode", TypeUtils.castToInt(record.get("interactive_mode")));

        SqlPara sqlPara = Db.getSqlPara("acc.findMainAccount", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 收款方账户查询（本级以及下级）
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param uodpInfo
     * @return
     * @throws BusinessException
     */
    public Page<Record> recvacclist(int pageNum, int pageSize, final Record record, UodpInfo uodpInfo) throws BusinessException {
        long orgId = uodpInfo.getOrg_id();
        //根据orgid查询机构信息
        Record orgRec = Db.findById("organization", "org_id", orgId);
        if (orgRec == null) {
            throw new ReqDataException("获取收款方账户失败!");
        }

        Kv kv = Kv.create();
//        kv.set("org_id",orgId);
        kv.set("level_num", TypeUtils.castToInt(orgRec.get("level_num")));
        kv.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));
        kv.set("status", WebConstant.AccountStatus.NORAMAL.getKey());
        kv.set("query_key", TypeUtils.castToString(record.get("query_key")));
        kv.set("exclude_ids", TypeUtils.castToLong(record.get("exclude_ids")));
        kv.set("is_activity", 1);

        SqlPara sqlPara = Db.getSqlPara("acc.findMainAccount", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public void sendPayList(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        for (final Long idStr : ids) {
            try {
                sendPayDetail(idStr);
            } catch (Exception e) {
                e.printStackTrace();
                String errMsg = null;
                if (e.getMessage() == null || e.getMessage().length() > 1000) {
                    errMsg = "发送银行失败！";
                } else {
                    errMsg = e.getMessage();
                }

                final Record innerRec = Db.findFirst(Db.getSql("nbdb.getBillById"), idStr);
                final Integer status = innerRec.getInt("service_status");
                final int persist_version = TypeUtils.castToInt(innerRec.get("persist_version"));
                final String feedBack = errMsg;
                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {

                        if (WebConstant.BillStatus.PASS.getKey() != status && WebConstant.BillStatus.FAILED.getKey() != status) {
                            log.error("单据状态有误!");
                            return false;
                        }
                        Record setRecord = new Record();
                        Record whereRecord = new Record();

                        setRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey()).set("feed_back", feedBack)
                                .set("persist_version", persist_version + 1);
                        whereRecord.set("id", idStr).set("service_status", status).set("persist_version", persist_version);
                        return CommonService.updateRows("inner_db_payment", setRecord, whereRecord) == 1;
                    }
                });
                if (!flag) {
                    log.error("数据过期！");
                }
                continue;
            }
        }

    }

    /**
     * 支付确认
     * 支付方式为网银，支付确认后单据状态改为  已成功
     */
    public void payconfirm(final Record record) throws BusinessException {
        List<Long> idsList = record.get("ids");
        record.remove("ids");

        final Long[] ids = new Long[idsList.size()];

        for (int i = 0; i < idsList.size(); i++) {
            ids[i] = TypeUtils.castToLong(idsList.get(i));
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                for (Long id : ids) {

                    Record innerRec = Db.findById("inner_db_payment", "id", id);
                    if (innerRec == null) {
                        return false;
                    }

                    int version = TypeUtils.castToInt(innerRec.get("persist_version"));

                    Record set = new Record();
                    Record where = new Record();

                    set.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
                    set.set("persist_version", (version + 1));

                    where.set("id", id);
                    where.set("persist_version", version);

                    return CommonService.update("inner_db_payment", set, where);
                }
                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("支付确认失败!");
        }
    }

    private void sendPayDetail(final Long id) throws Exception {
        final Record innerRec = Db.findFirst(Db.getSql("nbdb.getBillById"), id);
        if (innerRec == null) {
            throw new Exception();
        }
        final Integer status = innerRec.getInt("service_status");
        if (status != WebConstant.BillStatus.FAILED.getKey() && status != WebConstant.BillStatus.PASS.getKey()) {
            throw new Exception("单据状态有误!:" + id);
        }

        String payCnaps = innerRec.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
        innerRec.set("source_ref", "inner_db_payment");
        final int old_repeat_count = TypeUtils.castToInt(innerRec.get("repeat_count")); // 发送之前的repeat_count
        innerRec.set("repeat_count", old_repeat_count + 1);
        innerRec.set("bank_serial_number", ChannelManager.getSerianlNo(payBankCode));
        SysSinglePayInter sysInter = new SysSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(innerRec);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("single_pay_instr_queue", instr);
                if (save) {
                    return Db.update(Db.getSql("nbdb.updBillById"), instr.getStr("bank_serial_number"),
                            instr.getInt("repeat_count"), WebConstant.BillStatus.PROCESSING.getKey(),
                            instr.getStr("instruct_code"), id, old_repeat_count, status) == 1;
                }
                return save;
            }
        });
        if (flag) {
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
            throw new DbProcessException("发送失败，请联系管理员！");
        }
    }

}
