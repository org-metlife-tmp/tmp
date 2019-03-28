package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysSinglePayInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ZftBatchService {

    private static Logger log = LoggerFactory.getLogger(ZftBatchService.class);

    private static final RedisCacheConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

    ZftService zftService = new ZftService();


    public Record detaillisttotal(Record record) {
        SqlPara sqlPara = Db.getSqlPara("zftbatch.findAllAmount", Kv.by("map", record.getColumns()));
        return Db.findFirst(sqlPara);
    }

    public SqlPara getlistparam(final Record record, String sql) {
        CommonService.processQueryKey(record, "pay_query_key", "pay_account_name", "pay_account_no");
        CommonService.processQueryKey(record, "recv_query_key", "recv_account_name", "recv_account_no");
        record.set("delete_flag", 0);
        return Db.getSqlPara(sql, Kv.by("map", record.getColumns()));
    }

    public Page<Record> detallist(int pageNum, int pageSize, Record record) {
        SqlPara sqlPara = getlistparam(record, "zftbatch.findMoreInfo");
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public void payOneOff(final Record record, final long usr_id)
            throws BusinessException {

        List<Long> idsList = record.get("ids");
        if (idsList == null || idsList.size() == 0) {
            throw new ReqDataException("请选择要作废的明细！");
        }
        final Long[] ids = new Long[idsList.size()];

        for (int i = 0; i < idsList.size(); i++) {
            ids[i] = TypeUtils.castToLong(idsList.get(i));
        }

        String batchno = TypeUtils.castToString(record.get("batchno"));
        //检查单据状态
        final Record bill = Db.findById("outer_batchpay_baseinfo", "batchno", batchno);
        if (null == bill) {
            throw new ReqDataException("此单据不存在请刷新重试！");
        }

        //单据版本号
        final int version = TypeUtils.castToInt(bill.get("persist_version"));
        final long id = TypeUtils.castToLong(bill.get("id"));

        int service_status = TypeUtils.castToInt(bill.get("service_status"));
        if (service_status != WebConstant.BillStatus.PASS.getKey()
                && service_status != WebConstant.BillStatus.FAILED.getKey()
                && service_status != WebConstant.BillStatus.NOCOMPLETION.getKey()) {
            throw new ReqDataException("此单据状态错误，请刷新重试！");
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                Record set = new Record();
                Record where = new Record();
                for (Long detailId : ids) {
                    //修改详细单据作废原因以及支付状态
                    String feed_back = TypeUtils.castToString(record.get("feed_back"));
                    boolean flag = CommonService.update("outer_batchpay_bus_attach_detail",
                            new Record().set("feed_back", feed_back).set("pay_status", WebConstant.PayStatus.CANCEL.getKey())
                                    .set("update_by", usr_id).set("update_on", new Date()),
                            new Record().set("detail_id", detailId));
                    if (!flag) {
                        return false;
                    }
                }

                //修改主表单据状态
                set.set("persist_version", (version + 1))
                        .set("update_by", usr_id).set("update_on", new Date());
                where.set("id", id).set("persist_version", version);

                //根据批次号查询该单据是否未完结的单据
                SqlPara detailPara = Db.getSqlPara("zftbatch.findBatchAttachDetailByBatchnoAndPayStatus",
                        Ret.by("map", Kv.create().set("batchno", bill.get("batchno")).set("pay_status", new Integer[]{
                                WebConstant.PayStatus.INIT.getKey(),
                                WebConstant.PayStatus.HANDLE.getKey(),
                                WebConstant.PayStatus.FAILD.getKey()

                        })));
                List<Record> detailRecList = Db.find(detailPara);
                if (detailRecList == null || detailRecList.size() == 0) {
                    //修改主表信息
                    set.set("service_status", WebConstant.BillStatus.COMPLETION.getKey());
                } else {
                    set.set("service_status", WebConstant.BillStatus.NOCOMPLETION.getKey());
                }
                return CommonService.update("outer_batchpay_baseinfo", set, where);

            }
        });
        if (!flag) {
            throw new DbProcessException("批量支付明细单笔作废失败！");
        }
    }

    public void payOff(final Record paramsToRecord, final long usr_id)
            throws BusinessException {
        final long id = TypeUtils.castToLong(paramsToRecord.get("id"));
        final int version = TypeUtils.castToInt(paramsToRecord.get("persist_version"));
        //检查单据状态
        final Record bill = Db.findById("outer_batchpay_baseinfo", "id", id);
        if (null == bill) {
            throw new ReqDataException("此单据不存在请刷新重试！");
        }
        int service_status = TypeUtils.castToInt(bill.get("service_status"));

        if (service_status != WebConstant.BillStatus.PASS.getKey()
                && service_status != WebConstant.BillStatus.FAILED.getKey()
                && service_status != WebConstant.BillStatus.NOCOMPLETION.getKey()) {
            throw new ReqDataException("此单据状态错误，请刷新重试！");
        }

        //查询是否有未完结的单据
        SqlPara detailPara = Db.getSqlPara("zftbatch.findBatchAttachDetailByBatchnoAndPayStatus",
                Ret.by("map", Kv.create().set("batchno", bill.get("batchno")).set("pay_status", new Integer[]{
                        WebConstant.PayStatus.HANDLE.getKey(),
                        WebConstant.PayStatus.FAILD.getKey()

                })));
        List<Record> detailRecList = Db.find(detailPara);
        if (detailRecList != null && detailRecList.size() > 0) {
            throw new ReqDataException("此单据还有未完结的单据，无法作废！");
        }

        final String batchno = TypeUtils.castToString(bill.get("batchno"));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean b = CommonService.update("outer_batchpay_baseinfo",
                        new Record().set("persist_version", version + 1)
                                .set("service_status", WebConstant.BillStatus.COMPLETION.getKey())
                                .set("update_by", usr_id)
                                .set("update_on", new Date()),
                        new Record().set("persist_version", version).set("id", id));
                if (!b) return false;
                boolean s = CommonService.update("outer_batchpay_bus_attach_detail",
                        new Record().set("pay_status", WebConstant.PayStatus.CANCEL.getKey())
                                .set("feed_back", paramsToRecord.get("feed_back"))
                                .set("update_on", new Date())
                                .set("update_by", usr_id),
                        new Record().set("batchno", batchno).set("pay_status", WebConstant.PayStatus.INIT.getKey()));
                if (!s) return false;
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("批量支付作废失败！");
        }
    }

    public Record morebillsum(Record record) {
        return Db.findFirst(Db.getSqlPara("zftbatch.morebillsum", Kv.by("map", record.getColumns())));
    }

    public Record billdetailsum(Record record) {
        return Db.findFirst(Db.getSqlPara("zftbatch.billdetailsum", Kv.by("map", record.getColumns())));
    }

    public Page<Record> billdetaillist(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("zftbatch.billdetaillist", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record billdetail(Record record, UserInfo userInfo) throws BusinessException {
        long bill_id = TypeUtils.castToLong(record.get("id"));
        Record bill = Db.findFirst(Db.getSql("zftbatch.billdetail"),
                WebConstant.PayStatus.INIT.getKey(),
                WebConstant.PayStatus.INIT.getKey(),
                WebConstant.PayStatus.FAILD.getKey(),
                WebConstant.PayStatus.FAILD.getKey(),
                WebConstant.PayStatus.SUCCESS.getKey(),
                WebConstant.PayStatus.SUCCESS.getKey(),
                WebConstant.PayStatus.CANCEL.getKey(),
                WebConstant.PayStatus.CANCEL.getKey(),
                WebConstant.PayStatus.HANDLE.getKey(),
                WebConstant.PayStatus.HANDLE.getKey(),
                bill_id);

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), bill.getLong("org_id"));
        }
        return bill;
    }

//    public Page<Record> morebill(int page_num, int page_size, Record record) {
//        SqlPara sqlPara = Db.getSqlPara("zftbatch.morebill", Kv.by("map", record.getColumns()));
//        return Db.paginate(page_num, page_size, sqlPara);
//    }

    /**
     * 更多列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param userInfo
     * @return
     */
    public Page<Record> morelist(int pageNum, int pageSize, final Record record, final UserInfo userInfo) {
        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("zftbatch.getCanBatchnoByUser"), userInfo.getUsr_id());
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "zftbatch.getBatchBaseInfoByBatchNo", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 更多列表汇总
     *
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     */
    public Record morelisttotal(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) {
        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("zftbatch.getCanBatchnoByUser"), userInfo.getUsr_id());
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "zftbatch.findmorelisttotal", kv);
        return Db.findFirst(sqlPara);
    }

    public Page<Record> viewlist(int pageNum, int pageSize, final Record record, final UodpInfo uodpInfo, final UserInfo userInfo) {

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("zftbatch.getAllCanBatchno"), orgRec.get("level_code"), orgRec.get("level_num"));
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "zftbatch.getBatchBaseInfoByBatchNo", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public Record viewlisttotal(final Record record, final UodpInfo uodpInfo) {
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("zftbatch.getAllCanBatchno"), orgRec.get("level_code"), orgRec.get("level_num"));
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "zftbatch.findmorelisttotal", kv);
        return Db.findFirst(sqlPara);
    }

    public void delbill(final Record record, final UserInfo userInfo) throws BusinessException {
        final long bill_id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("version"));
        Record bill = Db.findById("outer_batchpay_baseinfo", "id", bill_id);

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(bill.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("outer_batchpay_baseinfo",
                        new Record().set("persist_version", version + 1).set("delete_flag", WebConstant.YesOrNo.YES.getKey()),
                        new Record().set("id", bill_id).set("persist_version", version));
            }
        });

        if (!flag) {
            throw new DbProcessException("删除批量支付单据失败！");
        }
    }

    public Page<Record> attclist(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("zftbatch.attclist", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record chgbill(final Record record, UserInfo userInfo) throws BusinessException {
        //单据ID
        final long bill_id = TypeUtils.castToLong(record.get("id"));
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final long version = TypeUtils.castToLong(record.get("version"));

        final List<Object> fileList = record.get("files");

        final Record obb = Db.findById("outer_batchpay_baseinfo", "id", bill_id);

        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(obb.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        obb.set("persist_version",
                version + 1)
                .set("attachment_count", (fileList == null || fileList.isEmpty()) ? 0 : fileList.size())
                .set("payment_summary", record.get("memo"))
                .set("update_by", userInfo.getUsr_id())
                .set("update_on", new Date())
                .set("apply_on", TypeUtils.castToDate(record.get("apply_on")))
                .set("pay_mode", record.get("pay_mode"))
                .set("biz_id", record.get("biz_id"))
                .set("biz_name", record.get("biz_name"))
                .set("service_status", WebConstant.BillStatus.SAVED.getKey());

        //如果当前付款账户号发生变化，则修改一下内容
        if (TypeUtils.castToInt(record.get("pay_acc_id")).intValue()
                != TypeUtils.castToInt(obb.get("pay_account_id")).intValue()) {


            Record payAccount = Db.findFirst(Db.getSql("acc.getAccByAccId"), record.get("pay_acc_id"));
            if (null == payAccount
                    || TypeUtils.castToInt(payAccount.get("is_activity")) == WebConstant.YesOrNo.NO.getKey()
                    || !(TypeUtils.castToInt(payAccount.get("status")) == WebConstant.AccountStatus.NORAMAL.getKey())) {
                throw new ReqDataException("付款方账户不存在！");
            }
            obb.set("pay_account_id", payAccount.getLong("acc_id"))
                    .set("pay_account_no", payAccount.getStr("acc_no"))
                    .set("pay_account_name", payAccount.getStr("acc_name"))
                    .set("pay_account_bank", payAccount.getStr("bank_name"))
                    .set("pay_account_cur", payAccount.getStr("iso_code"))
                    .set("pay_bank_cnaps", payAccount.getStr("bank_cnaps_code"))
                    .set("pay_bank_prov", payAccount.getStr("bank_pro"))
                    .set("pay_bank_city", payAccount.getStr("bank_city"))
                    .set("process_bank_type", payAccount.getStr("bank_type"));

        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空正式表数据，保留单据信息
                Db.update(Db.getSql("zftbatch.deleteOBBAD"), batchno);
                Db.update(Db.getSql("zftbatch.deleteOBBAI"), batchno);

                //将数据从临时表插入到正式表
                int insertOBBAIFlag = Db.update(Db.getSql("zftbatch.insertOBBAI"), uuid, batchno);
                if (insertOBBAIFlag == 0) {
                    return false;
                }
                int insertOBBADFlag = Db.update(Db.getSql("zftbatch.insertOBBAD"), uuid, batchno);
                if (insertOBBADFlag == 0) {
                    return false;
                }
                //先删除原有关系
                CommonService.delFileRef(WebConstant.MajorBizType.OBP.getKey(), bill_id);
                //保存附件信息
                boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.OBP.getKey(), bill_id, fileList);
                if (!saveFileRef) {
                    return saveFileRef;
                }
                //修复单据数据
                setObbTemp(obb, getAllCollect(uuid, batchno));
                obb.remove("id");
                boolean update = CommonService.update("outer_batchpay_baseinfo", obb,
                        new Record().set("id", bill_id).set("persist_version", version));
                if (!update) {
                    return false;
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改批量支付单据失败！");
        }
        //此时主表与临时表数据一致。
        return Db.findById("outer_batchpay_baseinfo", "id", bill_id);

    }


    public Record prechgbill(Record record, UserInfo userInfo) throws BusinessException {
        long bill_id = TypeUtils.castToLong(record.get("id"));
        Record bill = Db.findById("outer_batchpay_baseinfo", "id", bill_id);
        if (bill != null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), bill.getLong("org_id"));
        } else {
            throw new ReqDataException("单据不存在！");
        }
        return prechgbill(record, userInfo.getUsr_id());
    }


    private Record prechgbill(Record record, final long usr_id) throws BusinessException {
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final long bill_id = TypeUtils.castToLong(record.get("id"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final Record bill = Db.findById("outer_batchpay_baseinfo", "id", bill_id);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空临时表,预防删除数据未保存状况发生
                //删除临时表数据，先删除详情，后删除excel
                Db.update(Db.getSql("zftbatch.deleteOBBADTemp"), uuid, batchno);
                Db.update(Db.getSql("zftbatch.deleteOBBAITemp"), uuid, batchno);
                Db.update(Db.getSql("zftbatch.deleteOBBTemp"), uuid, batchno);

                Record obbTemp = new Record()
                        .set("batchno", batchno)
                        .set("uuid", uuid)
                        .set("usr_id", usr_id)
                        .set("total_num", bill.get("total_num"))
                        .set("total_amount", bill.get("total_amount"));
                boolean obbTempFlag = Db.save("outer_batchpay_baseinfo_temp", obbTemp);
                if (!obbTempFlag) {
                    return obbTempFlag;
                }
                Db.update(Db.getSql("zftbatch.insertOBBAITemp"), uuid, batchno);
                Db.update(Db.getSql("zftbatch.insertOBBADTemp"), uuid, batchno);
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("同步数据错误！");
        }
        //此时主表与临时表数据一致。
        //批量操作excel数据
        bill.set("obbaiTempList", Db.find(Db.getSql("zftbatch.getCurrentOBBAITemp"), uuid, batchno));
        return bill;
    }

    public Record addbill(Record record, final long usr_id, long org_id) throws BusinessException {
        final List<Object> fileList = record.get("files");
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final Record obb = new Record()
                .set("batchno", batchno)
                .set("delete_flag", 0)
                .set("org_id", org_id)
                .set("attachment_count", (fileList == null || fileList.isEmpty()) ? 0 : fileList.size())
                .set("pay_mode", record.get("pay_mode"))
                .set("create_by", usr_id)
                .set("create_on", new Date())
                .set("apply_on", TypeUtils.castToDate(record.get("apply_on")))
                .set("update_by", usr_id)
                .set("update_on", new Date())
                .set("persist_version", 0)
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("payment_summary", record.get("memo"))
                .set("biz_id", record.get("biz_id"))
                .set("biz_name", record.get("biz_name"));
        Record payAccount = Db.findFirst(Db.getSql("acc.getAccByAccId"), record.get("pay_acc_id"));
        if (null == payAccount
                || TypeUtils.castToInt(payAccount.get("is_activity")) == WebConstant.YesOrNo.NO.getKey()
                || !(TypeUtils.castToInt(payAccount.get("status")) == WebConstant.AccountStatus.NORAMAL.getKey())) {
            throw new ReqDataException("付款方账户不存在！");
        }
        obb.set("pay_account_id", payAccount.getLong("acc_id"))
                .set("pay_account_no", payAccount.getStr("acc_no"))
                .set("pay_account_name", payAccount.getStr("acc_name"))
                .set("pay_account_bank", payAccount.getStr("bank_name"))
                .set("pay_account_cur", payAccount.getStr("iso_code"))
                .set("pay_bank_cnaps", payAccount.getStr("bank_cnaps_code"))
                .set("pay_bank_prov", payAccount.getStr("bank_pro"))
                .set("pay_bank_city", payAccount.getStr("bank_city"))
                .set("process_bank_type", payAccount.getStr("bank_type"));
        //设置当前单据所有数据汇总信息
        setObbTemp(obb, getAllCollect(uuid, batchno));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //插入单据信息
                boolean save = Db.save("outer_batchpay_baseinfo", "id", obb);
                if (!save) {
                    return save;
                }
                //保存附件信息
                boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.OBP.getKey(), TypeUtils.castToLong(obb.get("id")), fileList);
                if (!saveFileRef) {
                    return saveFileRef;
                }
                //将数据从临时表插入到正式表
                int insertOBBAIFlag = Db.update(Db.getSql("zftbatch.insertOBBAI"), uuid, batchno);
                if (insertOBBAIFlag == 0) {
                    return false;
                }
                int insertOBBADFlag = Db.update(Db.getSql("zftbatch.insertOBBAD"), uuid, batchno);
                if (insertOBBADFlag == 0) {
                    return false;
                }
                //删除临时表数据，先删除详情，后删除excel
                int deleteOBBADFlag = Db.update(Db.getSql("zftbatch.deleteOBBADTemp"), uuid, batchno);
                if (deleteOBBADFlag == 0) {
                    return false;
                }
                int deleteOBBAIFlag = Db.update(Db.getSql("zftbatch.deleteOBBAITemp"), uuid, batchno);
                if (deleteOBBAIFlag == 0) {
                    return false;
                }
                int deleteOBBFlag = Db.update(Db.getSql("zftbatch.deleteOBBTemp"), uuid, batchno);
                if (deleteOBBFlag == 0) {
                    return false;
                }
                try {
                    prechgbill(new Record().set("uuid", uuid).set("id", obb.get("id")).set("batchno", batchno), usr_id);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });

        if (!flag) {
            throw new DbProcessException("新增批量支付单据失败!");
        }

        return obb;

    }

    /**
     * 删除excel
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record delbillexcel(Record record) throws BusinessException {
        Record result = new Record();
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        //获取汇总数据信息。
        final Record obbTemp = getObbTemp(uuid, batchno);
        final String info_id = TypeUtils.castToString(record.get("info_id"));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //删除数据
                Db.update(Db.getSql("zftbatch.deleteOBBADTempByInfoId"), info_id);
                Db.update(Db.getSql("zftbatch.deleteOBBAITempByInfoId"), info_id);
                //更新临时表汇总数据
                return updateOBBTemp(uuid, batchno, obbTemp);
            }
        });
        if (!flag) {
            throw new DbProcessException("删除批量支付文件失败！");
        }
        result.setColumns(obbTemp);
        return result;
    }

    /**
     * 上传-新增excel
     */
    public Record addbillexcel(Record params, final long usr_id) throws BusinessException {
        final String documentId = TypeUtils.castToString(params.get("document_id"));
        String originName = TypeUtils.castToString(params.get("origin_name"));
        ExcelResultBean excelBean = Redis.use(config.getCacheName()).get(params.get("object_id"));
        Record result = new Record();
        //当前登录用户操作唯一标识
        final String uuid = TypeUtils.castToString(params.get("uuid"));
        //批次号
        final String batchno;
        //根据批次号获取是否第一次添加excel.少一次DB查询
        final boolean isFirst;
        //首次生成批次号
        if (StrKit.isBlank(TypeUtils.castToString(params.get("batchno")))) {
            //获取批次号
            batchno = CommonService.getBatchno(WebConstant.MajorBizType.OBP);
            isFirst = true;
        } else {
            batchno = TypeUtils.castToString(params.get("batchno"));
            isFirst = false;
        }
        //构建excel所有列数据
        final List<Record> obbadTempList = new ArrayList<>(excelBean.getRowData().size());
        genBatchpayBusAttacDetailList(excelBean, obbadTempList, usr_id, batchno, uuid, documentId);
        //构建excel基础信息.
        final Record obbaiTemp = buildBatchpayBusAttacInfoRecord(batchno, uuid, documentId, originName);
        final Record[] obbTemp = new Record[1];
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                int[] res = Db.batchSave("outer_batchpay_bus_attach_detail_temp", obbadTempList, 1000);
                boolean checkDbResult = ArrayUtil.checkDbResult(res);
                //如果插入失败，直接返回false，不往下执行。
                if (!checkDbResult) {
                    return checkDbResult;
                }
                //设置excel汇总数据
                setObbaiTemp(obbaiTemp, getCollect(documentId, batchno, uuid));
                boolean obbaiTempFlag = Db.save("outer_batchpay_bus_attach_info_temp", obbaiTemp);
                //如果插入失败，直接返回false
                if (!obbaiTempFlag) {
                    return obbaiTempFlag;
                }
                if (isFirst) {
                    obbTemp[0] = new Record()
                            .set("uuid", uuid)
                            .set("batchno", batchno)
                            .set("usr_id", usr_id)
                            .set("total_num", 0)
                            .set("total_amount", 0.00);
                    setObbTemp(obbTemp[0], getAllCollect(uuid, batchno));
                    return Db.save("outer_batchpay_baseinfo_temp", "batchno,uuid", obbTemp[0]);
                } else {
                    //通过 uuid 与 batchno 获取汇总数据信息
                    obbTemp[0] = getObbTemp(uuid, batchno);
                    return updateOBBTemp(uuid, batchno, obbTemp[0]);
                }
            }
        });
        if (!flag) {
            throw new DbProcessException("上传批量支付数据失败！");
        }
        result.setColumns(obbaiTemp);
        result.setColumns(obbTemp[0]);
        return result;
    }

    /**
     * 获取 outer_batchpay_baseinfo_temp 汇总数据信息
     *
     * @param uuid
     * @param batchno
     * @return
     */
    private Record getObbTemp(String uuid, String batchno) {
        return Db.findById("outer_batchpay_baseinfo_temp", "uuid,batchno", uuid, batchno);
    }

    /**
     * 设置总汇总数据值
     */

    private void setObbTemp(Record obbTemp, Record allCollect) {
        obbTemp.set("total_num", allCollect.get("total_num")).set("total_amount", allCollect.get("total_amount"));
    }

    /**
     * 设置excel汇总数据值
     */
    private void setObbaiTemp(Record obbaiTemp, Record collect) {
        obbaiTemp.set("number", collect.get("total_num")).set("amount", collect.get("total_amount"));
    }

    /**
     * 获取汇总信息
     *
     * @param uuid
     * @param batchno
     * @return
     */
    private Record getAllCollect(String uuid, String batchno) {
        return Db.findFirst(Db.getSql("zftbatch.getAllCollectRecordTemp"), uuid, batchno);
    }

    /**
     * 获取excel汇总信息
     *
     * @param info_id
     * @param uuid
     * @param batchno
     * @return
     */
    private Record getCollect(String info_id, String batchno, String uuid) {
        return Db.findFirst(Db.getSql("zftbatch.getCollectRecordTemp"), info_id, batchno, uuid);
    }

    public Record getAllCollect(Record record) {
        return Db.findFirst(Db.getSql("zftbatch.getAllCollectRecord"),
                TypeUtils.castToString(record.get("batchno")));
    }

    public Record getCollect(Record record) {
        return Db.findFirst(Db.getSql("zftbatch.getCollectRecord"),
                TypeUtils.castToString(record.get("info_id")),
                TypeUtils.castToString(record.get("batchno")));
    }

    private boolean updateOBBTemp(String uuid, String batchno, Record obbTemp) {
        setObbTemp(obbTemp, getAllCollect(uuid, batchno));
        return Db.update("outer_batchpay_baseinfo_temp", "uuid,batchno", obbTemp);
    }

    /**
     * 目前只存在针对于excel操作，不存在对excel单挑数据操作。此方法暂时无用。
     *
     * @param obbaiTemp
     * @param batchno
     * @param uuid
     * @return
     */
    private boolean updateOBBAITemp(Record obbaiTemp, String batchno, String uuid) {
        setObbaiTemp(obbaiTemp, getCollect(obbaiTemp.getStr("id"), batchno, uuid));
        return Db.update("outer_batchpay_bus_attach_info_temp", "id,batchno,uuid", obbaiTemp);
    }

    private void genBatchpayBusAttacDetailList(ExcelResultBean excelBean, List<Record> list, long usr_id, String batchno, String uuid, String documentId) throws BusinessException {
        //取第一条数据，获取收款方账户信息。
        for (Map<String, Object> map : excelBean.getRowData()) {
            Record _r = new Record();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                _r.set(entry.getKey(), entry.getValue());
            }
            //获取收款方账户信息 TODO 校验可放置在解析 excel
            String recv_account_no = TypeUtils.castToString(_r.get("recv_account_no"));

            Map<String, Object> accountMap = TableDataCacheUtil.getInstance().getARowData("supplier_acc_info", "acc_no", recv_account_no);

            if (accountMap == null) {
                throw new ReqDataException("收款方账户信息不存在！");
            }
            _r.set("info_id", documentId);//TODO 待替换
            _r.set("uuid", uuid);
            _r.set("batchno", batchno);
            _r.set("update_on", new Date());
            _r.set("update_by", usr_id);
            _r.set("recv_account_id", accountMap.get("id"));
            _r.set("recv_account_name", accountMap.get("acc_name"));
            _r.set("recv_account_bank", accountMap.get("bank_name"));
            _r.set("recv_account_cur", accountMap.get("iso_code"));
            _r.set("recv_bank_prov", accountMap.get("province"));
            _r.set("recv_bank_city", accountMap.get("city"));
            _r.set("recv_bank_cnaps", accountMap.get("cnaps_code"));
            _r.set("pay_status", WebConstant.PayStatus.INIT.getKey());
            list.add(_r);
        }
    }

    private Record buildBatchpayBusAttacInfoRecord(String batchno, String uuid, String documentId, String originName) {
        return new Record()
                .set("batchno", batchno)
                .set("uuid", uuid)
                .set("id", documentId)
                .set("origin_name", originName) //从解析对象中获取
                .set("file_extension_suffix",
                        (originName.lastIndexOf('.') == -1) ? "" : originName.substring(originName.lastIndexOf('.') + 1))//从解析对象中获取
                .set("number", 0)//从解析对象中获取
                .set("amount", 0);
    }


    public void sendPayList(List<Long> ids, long id) {
        if (ids == null || ids.size() == 0) {
            return;
        }
        final Long[] idStrs = new Long[ids.size()];

        for (int i = 0; i < ids.size(); i++) {
            idStrs[i] = TypeUtils.castToLong(ids.get(i));
        }
        for (final Long idStr : idStrs) {
            try {
                sendPayDetail(idStr, id);
            } catch (Exception e) {
                e.printStackTrace();
                String errMsg = null;
                if (e.getMessage() == null || e.getMessage().length() > 1000) {
                    errMsg = "发送银行失败！";
                } else {
                    errMsg = e.getMessage();
                }

                final Record outerRec = Db.findFirst(Db.getSql("zftbatch.findBatchSendInfoByDetailId"), idStr);
                final Integer status = outerRec.getInt("service_status");
                final int persist_version = TypeUtils.castToInt(outerRec.get("persist_version"));
                final String feedBack = errMsg;
                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {

                        if (WebConstant.PayStatus.INIT.getKey() != status && WebConstant.PayStatus.FAILD.getKey() != status) {
                            log.error("单据状态有误!");
                            return false;
                        }
                        Record setRecord = new Record();
                        Record whereRecord = new Record();

                        setRecord.set("pay_status", WebConstant.BillStatus.FAILED.getKey()).set("feed_back", feedBack)
                                .set("persist_version", persist_version + 1);
                        whereRecord.set("detail_id", idStr).set("pay_status", status).set("persist_version", persist_version);
                        return CommonService.updateRows("outer_batchpay_bus_attach_detail", setRecord, whereRecord) == 1;
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
     * 发送单笔明细
     *
     * @param detailId 明细id
     * @param bill_id  单据id
     * @throws Exception
     */
    private void sendPayDetail(final Long detailId, final long bill_id) throws Exception {

        final Record outerRec = Db.findFirst(Db.getSql("zftbatch.findBatchSendInfoByDetailId"), detailId);
        if (outerRec == null) {
            throw new Exception();
        }
        final Integer status = outerRec.getInt("pay_status");
        if (status != WebConstant.PayStatus.FAILD.getKey() && status != WebConstant.PayStatus.INIT.getKey()) {
            throw new Exception("单据状态有误!:" + detailId);
        }
        String payCnaps = outerRec.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
        outerRec.set("id", detailId);
        outerRec.set("source_ref", "outer_batchpay_bus_attach_detail");
        final int oldRepearCount = outerRec.getInt("repeat_count");
        outerRec.set("repeat_count", oldRepearCount + 1);
        outerRec.set("bank_serial_number", ChannelManager.getSerianlNo(payBankCode));
        SysSinglePayInter sysInter = new SysSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(outerRec);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("single_pay_instr_queue", instr);
                if (save) {
                    save = Db.update(Db.getSql("zftbatch.updateDetailById"), instr.getStr("bank_serial_number"),
                            instr.getInt("repeat_count"), WebConstant.PayStatus.HANDLE.getKey(), instr.getStr("instruct_code"), detailId,
                            oldRepearCount, status) == 1;

                    if (save) {
                        //修改批次状态为未完结
                        Record set = new Record();
                        Record where = new Record();

                        set.set("service_status", WebConstant.BillStatus.NOCOMPLETION.getKey());

                        where.set("id", bill_id);
                        return CommonService.update("outer_batchpay_baseinfo", set, where);
                    }
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

    public void payok(Record record, final long usr_id) throws BusinessException {
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));
        final long id = TypeUtils.castToLong(record.get("id"));

        Record bill = Db.findById("outer_batchpay_baseinfo", "id", id);
        if (TypeUtils.castToInt(bill.get("pay_mode")) != WebConstant.PayMode.NETSILVER.getKey()) {
            throw new ReqDataException("非网联的单据不可做支付确认操作！");
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean b = CommonService.update("outer_batchpay_baseinfo",
                        new Record()
                                .set("persist_version", version + 1)
                                .set("update_on", new Date())
                                .set("update_by", usr_id)
                                .set("service_status", WebConstant.BillStatus.COMPLETION.getKey()),
                        new Record().set("id", id).set("persist_version", version));
                if (!b) return false;

                return CommonService.update("outer_batchpay_bus_attach_detail",
                        new Record().set("pay_status", WebConstant.PayStatus.SUCCESS.getKey()),
                        new Record().set("batchno", batchno));
            }
        });

        if (!flag) {
            throw new DbProcessException("支付确认操作失败，请刷新重试！");
        }
    }

    public void payokbyids(Record record, final long usr_id) throws BusinessException {
        final List<Object> ids = record.get("ids");//所有明细ID
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));
        Record bill = Db.findById("outer_batchpay_baseinfo", "batchno", batchno);
        if (TypeUtils.castToInt(bill.get("pay_mode")) != WebConstant.PayMode.NETSILVER.getKey()) {
            throw new ReqDataException("非网联的单据不可做支付确认操作！");
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean b = CommonService.update("outer_batchpay_baseinfo",
                        new Record()
                                .set("persist_version", version + 1)
                                .set("service_status", WebConstant.BillStatus.NOCOMPLETION.getKey())
                                .set("update_on", new Date())
                                .set("update_by", usr_id),
                        new Record().set("batchno", batchno).set("persist_version", version));
                if (!b) return false;
                Kv u = Kv.create();
                u.set("ids", ids);
                u.set("update_by", usr_id);
                u.set("update_on", new Date());
                u.set("pay_status", WebConstant.PayStatus.SUCCESS.getKey());
                boolean s = Db.update(Db.getSqlPara("zftbatch.updateBillDetail2", u)) > 0;
                if (!s) return false;
                Kv cond = Kv.create();
                cond.set("ids", ids);
                cond.set("batchno", batchno);
                cond.set("pay_status", WebConstant.PayStatus.SUCCESS.getKey());
                SqlPara sqlPara = Db.getSqlPara("zftbatch.billDetailCancelSum", cond);
                long l = Db.queryLong(sqlPara.getSql(), sqlPara.getPara());
                //如果所有明细都成功，则更改单据为已成功状态
                if (l == 0) {
                    return CommonService.update("outer_batchpay_baseinfo",
                            new Record()
                                    .set("service_status", WebConstant.BillStatus.COMPLETION.getKey()),
                            new Record().set("batchno", batchno));
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("支付确认操作失败，请刷新重试！");
        }
    }

    public boolean hookPass(Record record) {
        final long id = TypeUtils.castToLong(record.get("id"));
        Record bill = Db.findById("outer_batchpay_baseinfo", "id", id);
        int pay_mode = TypeUtils.castToInt(bill.get("pay_mode"));
        int version = TypeUtils.castToInt(bill.get("persist_version"));
        String batchno = TypeUtils.castToString(bill.get("batchno"));
        if (pay_mode == WebConstant.PayMode.ADDITIONAL.getKey()) {
            //单据状态变成已成功
            return CommonService.update("outer_batchpay_baseinfo",
                    new Record().set("service_status", WebConstant.BillStatus.COMPLETION.getKey()).set("persist_version", version + 1),
                    new Record().set("id", id)) &&
                    CommonService.update("outer_batchpay_bus_attach_detail",
                            new Record().set("pay_status", WebConstant.PayStatus.SUCCESS.getKey()),
                            new Record().set("batchno", batchno));
        }
        return true;
    }

    /**
     * 指定附件详细列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> batchBillAttachPage(int pageNum, int pageSize, final Record record) {
        Kv kv = Kv.create();
        kv.set("batchno", TypeUtils.castToString(record.get("batchno")));
        kv.set("info_id", TypeUtils.castToString(record.get("id")));
        SqlPara sqlPara = Db.getSqlPara("zjzf.findAttachDetailToPage", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 指定附件详细汇总
     *
     * @param record
     * @return
     */
    public Record batchBillAttachTotal(final Record record) {
        String id = TypeUtils.castToString(record.get("id"));
        String batchno = TypeUtils.castToString(record.get("batchno"));

        Kv kv = Kv.create();
        kv.set("batchno", batchno);
        kv.set("id", id);
        SqlPara sqlPara = Db.getSqlPara("zjzf.findAttachInfoByIDBatchno", Kv.by("map", kv));
        return Db.findFirst(sqlPara);
    }

    public SqlPara getListParam(final Record record, String sql, final Kv kv) {
        String pay_query_key = record.get("pay_query_key");
//        record.remove("pay_query_key");

        String recv_query_key = record.get("recv_query_key");
//        record.remove("recv_query_key");

        //付款方
        //是否包含中文
        boolean payFlag = StringKit.isContainChina(pay_query_key);
        if (payFlag) {
            //名称
            kv.set("pay_account_name", pay_query_key);
        } else {
            //帐号
            kv.set("pay_account_no", pay_query_key);
        }

        //收款方
        //是否包含中文
        boolean recvFlag = StringKit.isContainChina(recv_query_key);
        if (recvFlag) {
            //名称
            kv.set("recv_account_name", recv_query_key);
        } else {
            //帐号
            kv.set("recv_account_no", recv_query_key);
        }

        BigDecimal min = TypeUtils.castToBigDecimal(record.get("min"));
        if (min != null) {
            kv.set("min", min);
        }

        BigDecimal max = TypeUtils.castToBigDecimal(record.get("max"));
        if (max != null) {
            kv.set("max", max);
        }

        String startDate = TypeUtils.castToString(record.get("start_date"));
        if (startDate != null && !"".equals(startDate)) {
            kv.set("start_date", startDate);
        }

        String endDate = TypeUtils.castToString(record.get("end_date"));
        if (endDate != null && !"".equals(endDate)) {
            kv.set("end_date", endDate);
        }

        Integer payMode = TypeUtils.castToInt(record.get("pay_mode"));
        if (payMode != null) {
            kv.set("pay_mode", payMode);
        }

        kv.set("service_status", record.get("service_status"));
        return Db.getSqlPara(sql, Kv.by("map", kv));
    }
}
