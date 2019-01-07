package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/11
 * @Description: 内部调部-批量制单（非直连归集）
 */
public class DbtBatchService {

    private static Logger log = LoggerFactory.getLogger(DbtBatchService.class);


    private static final RedisCacheConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

    /**
     * 插入临时表信息
     *
     * @param record
     * @param userInfo
     */
    public Record imports(final Record record, final UserInfo userInfo) throws DbProcessException {
        final String documentId = TypeUtils.castToString(record.get("document_id"));
        final String objectId = TypeUtils.castToString(record.get("object_id"));
        ExcelResultBean excelBean = Redis.use(config.getCacheName()).get(objectId);
        //获取导入数据
        final List<Map<String, Object>> datas = excelBean.getRowData();
        //生成uuid
        final String uuid = TypeUtils.castToString(record.get("uuid"));

        String oldBatchno = TypeUtils.castToString(record.get("batchno"));
        if (oldBatchno == null || "".equals(oldBatchno)) {
            oldBatchno = CommonService.getBatchno(WebConstant.MajorBizType.INNERDB_BATCH);
        }
        final String batchno = oldBatchno;
        final Record countRec = new Record();


        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                BigDecimal currTotalAmount = new BigDecimal(0);
                BigDecimal totalAmount = new BigDecimal(0);

                Integer totalQuantity = datas.size();
                Integer currTotalQuantity = datas.size();
                boolean flag = false;

                //根据batchno查询baseinfo信息
                Kv kv = Kv.create();
                kv.set("batchno", batchno);
                SqlPara sqlPara = Db.getSqlPara("batch.findTempBatchBaseInfo", Kv.by("map", kv));
                Record baseinfo = Db.findFirst(sqlPara);
                if (baseinfo == null) {
                    baseinfo = new Record();
                    //插入baseinfotemp表初始数据
                    baseinfo.set("batchno", batchno);
                    baseinfo.set("uuid", uuid);
                    baseinfo.set("usr_id", userInfo.getUsr_id());
                    baseinfo.set("total_num", 0);
                    baseinfo.set("total_amount", 0);
                    flag = Db.save("inner_batchpay_baseinfo_temp", baseinfo);
                    if (!flag) {
                        return false;
                    }
                }

                // 插入新的数据，临时业务附件信息表（temp_inner_batchpay_bus_attach_info）
                String originFileName = TypeUtils.castToString(record.get("origin_name"));
                String fileExtensionSuffix = originFileName.lastIndexOf(".") > 0 ? originFileName.substring(originFileName.indexOf(".") + 1) : "";
                Record batchPayBusAttacRecord = new Record();
                batchPayBusAttacRecord.set("id", documentId);//散列值
                batchPayBusAttacRecord.set("uuid", uuid);
                batchPayBusAttacRecord.set("batchno", batchno);
                batchPayBusAttacRecord.set("origin_name", originFileName);
                batchPayBusAttacRecord.set("file_extension_suffix", fileExtensionSuffix);
                batchPayBusAttacRecord.set("number", datas.size());
                batchPayBusAttacRecord.set("amount", 0);
                batchPayBusAttacRecord.set("status", WebConstant.TempTableStatus.NEW.getKey());

                flag = Db.save("inner_batchpay_bus_attach_info_temp", batchPayBusAttacRecord);
                if (flag) {


                    // 循环计算总笔数和总金额
                    for (Map<String, Object> data : datas) {
                        BigDecimal target = new BigDecimal(String.valueOf(data.get("payment_amount")));

                        totalAmount = totalAmount.add(target);
                        currTotalAmount = currTotalAmount.add(target);

                        //查询收款方帐号是否存在
                        Map<String, Object> accountMap = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", TypeUtils.castToString(data.get("recv_account_no")));
                        if (accountMap.size() > 0) {
                            Record detalRec = new Record();
                            //根据cnaps号查询银行信息
                            Record bankRec = Db.findById("all_bank_info", "cnaps_code", accountMap.get("bank_cnaps_code"));
                            if (bankRec != null) {
                                detalRec.set("recv_bank_prov", TypeUtils.castToString(bankRec.get("province")));//
                                detalRec.set("recv_bank_city", TypeUtils.castToString(bankRec.get("city")));//

                                //根据bank_type查询信息
//                                Record banktypeRec = Db.findById("const_bank_type", "code", TypeUtils.castToString(bankRec.get("bank_name")));
                                detalRec.set("recv_account_bank", TypeUtils.castToString(bankRec.get("name")));//
                            }


                            Map<String, Object> curMap = TableDataCacheUtil.getInstance().getARowData("currency", "id", TypeUtils.castToString(accountMap.get("curr_id")));

                            detalRec.set("batchno", batchno);
                            detalRec.set("info_id", TypeUtils.castToString(batchPayBusAttacRecord.get("id")));
                            detalRec.set("recv_account_id", accountMap.get("acc_id"));
                            detalRec.set("recv_account_no", accountMap.get("acc_no"));
                            detalRec.set("recv_account_name", accountMap.get("acc_name"));
                            detalRec.set("recv_account_cur", curMap.get("iso_code"));
                            detalRec.set("recv_bank_cnaps", accountMap.get("bank_cnaps_code"));
                            detalRec.set("payment_amount", TypeUtils.castToBigDecimal(data.get("payment_amount")));
                            detalRec.set("pay_status", WebConstant.PayStatus.INIT.getKey());
                            detalRec.set("memo", TypeUtils.castToString(data.get("memo")));

                            flag = Db.save("inner_batchpay_bus_attach_detail_temp", detalRec);
                            if (!flag) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }

                    totalQuantity += TypeUtils.castToInt(baseinfo.get("total_num"));//总笔数累加计算
                    baseinfo.set("total_num", totalQuantity);

                    Record setRec = new Record();
                    setRec.set("number", currTotalQuantity);
                    setRec.set("amount", currTotalAmount);

                    //更新临时attchmentinfo附件表统计信息
                    CommonService.update("inner_batchpay_bus_attach_info_temp", setRec, new Record().set("id", TypeUtils.castToString(batchPayBusAttacRecord.get("id"))).set("batchno", batchno).set("uuid", uuid));

                    countRec.set("files", batchPayBusAttacRecord.set("number", currTotalQuantity).set("amount", currTotalAmount));


                    // 总金额累加计算
                    totalAmount = totalAmount.add(TypeUtils.castToBigDecimal(baseinfo.get("total_amount")));

                    Record setBaseRec = new Record();
                    setBaseRec.set("total_num", totalQuantity);
                    setBaseRec.set("total_amount", totalAmount);

                    //更新临时baseinfo表信息
                    boolean result = CommonService.update("inner_batchpay_baseinfo_temp",
                            setBaseRec,
                            new Record().set("batchno", batchno).set("uuid", uuid));
                    if (result) {
                        countRec.set("total_num", totalQuantity).set("total_amount", totalAmount).set("batchno", batchno);
                        return true;
                    }
                    return false;

                }
                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("导入数据失败!");
        }

        return countRec;
    }

    /**
     * 列表信息
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @param uodpInfo
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record, final UodpInfo uodpInfo, final UserInfo userInfo) {
        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("batch.getCanBatchnoByUser"), userInfo.getUsr_id());
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "batch.getBatchBaseInfoByBatchNo", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public Record listtotal(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) {
        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("batch.getCanBatchnoByUser"), userInfo.getUsr_id());
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "batch.findmorelisttotal", kv);
        return Db.findFirst(sqlPara);
    }

    public Page<Record> viewlist(int pageNum, int pageSize, final Record record, final UodpInfo uodpInfo, final UserInfo userInfo) {

        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("batch.getAllCanBatchno"), orgRec.get("level_code"), orgRec.get("level_num"));
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "batch.getBatchBaseInfoByBatchNo", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public Record viewlisttotal(final Record record, final UodpInfo uodpInfo) {
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("batch.getAllCanBatchno"), orgRec.get("level_code"), orgRec.get("level_num"));
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "batch.findmorelisttotal", kv);
        return Db.findFirst(sqlPara);
    }

    public Page<Record> paylist(int pageNum, int pageSize, final Record record, final UodpInfo uodpInfo) {
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        //获取可查batchno
        List<Record> uuidList = Db.find(Db.getSql("batch.getAllCanBatchno"), orgRec.get("level_code"), orgRec.get("level_num"));
        //查询单据
        Kv kv = Kv.create();
        if (uuidList.size() > 0) {
            Object[] batchnos = uuidList.toArray();
            kv.set("batchnos", batchnos);
        } else {
            kv.set("batchnos", new Object[]{new Record().set("batchno", "0")});
        }
        SqlPara sqlPara = getListParam(record, "batch.getBatchBaseInfoByBatchNoPayList", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    public Record add(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws BusinessException {
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final List<Object> list = record.get("files");

        //查询baseinfotemp 临时表信息
        final Record tempBaseRec = Db.findById("inner_batchpay_baseinfo_temp", "batchno,uuid", batchno, uuid);
        if (tempBaseRec == null) {
            throw new ReqDataException("导入信息查询失败!");
        }

        //根据付款方id查询付款方信息
        final Record payRec = Db.findById("account", "acc_id", TypeUtils.castToLong(record.get("pay_account_id")));

        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }

        //根据bank_cnaps_code查询银行信息
        final Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        if (bankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }

        final Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(payRec.get("curr_id")));
        if (currRec == null) {
            throw new ReqDataException("未找到有效的币种信息!");
        }

        final Record bankTypeRec = Db.findById("const_bank_type", "code", TypeUtils.castToString(bankRec.get("bank_type")));
        if (bankTypeRec == null) {
            throw new ReqDataException("未找到有效的银行大类信息!");
        }
        final Record baseRec = new Record();
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean flag = addTx(batchno, uuid, baseRec, uodpInfo, userInfo, record, payRec, currRec,
                        bankRec, bankTypeRec, tempBaseRec, list, null);
                if (flag) {
                    //保存附件
                    if (list != null && list.size() > 0) {
                        return CommonService.saveFileRef(WebConstant.MajorBizType.INNERDB_BATCH.getKey(),
                                TypeUtils.castToLong(baseRec.get("id")), list);
                    }
                    return true;
                }
                return false;

            }
        });


        if (flag) {
            //根据batchno查询附件信息
            List<Record> attInfo = Db.find(Db.getSql("batch.findAttachInfoByBatchno"), batchno);
            baseRec.set("attachment_info", attInfo);

            return baseRec;
        }
        throw new DbProcessException("调拨批量导入失败!");
    }

    public boolean addTx(final String batchno, final String uuid, final Record newBaseRec, final UodpInfo uodpInfo, final UserInfo userInfo,
                         final Record record, final Record payRec, final Record currRec, final Record bankRec,
                         final Record bankTypeRec, final Record tempBaseRec, final List<Object> list, final Record oldBaseRec) {
        int result = 0;

        //保存业务附件信息
        Db.update(Db.getSql("batch.insertBatchAttachInfo"), batchno, uuid, WebConstant.TempTableStatus.NEW.getKey());

        //保存业务附件详细信息
        Db.update(Db.getSql("batch.insertBatchAttachDetail"), batchno, WebConstant.TempTableStatus.NEW.getKey());

        //将业务临时表里新增的记录变为原有
        Db.update(Db.getSql("batch.updateTempAttachInfoByStatus"), WebConstant.TempTableStatus.ORIGINAL.getKey(), WebConstant.TempTableStatus.NEW.getKey(), uuid, batchno);

        newBaseRec.set("org_id", uodpInfo.getOrg_id());
        newBaseRec.set("dept_id", uodpInfo.getDept_id());
        newBaseRec.set("biz_id", TypeUtils.castToString(record.get("biz_id")));
        newBaseRec.set("biz_name", TypeUtils.castToString(record.get("biz_name")));
        newBaseRec.set("pay_account_id", TypeUtils.castToLong(payRec.get("acc_id")));
        newBaseRec.set("pay_account_no", TypeUtils.castToString(payRec.get("acc_no")));
        newBaseRec.set("pay_account_name", TypeUtils.castToString(payRec.get("acc_name")));
        newBaseRec.set("pay_account_cur", TypeUtils.castToString(currRec.get("iso_code")));
        newBaseRec.set("pay_account_bank", TypeUtils.castToString(bankRec.get("name")));
        newBaseRec.set("pay_bank_cnaps", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        newBaseRec.set("pay_bank_prov", TypeUtils.castToString(bankRec.get("province")));
        newBaseRec.set("pay_bank_city", TypeUtils.castToString(bankRec.get("city")));
        newBaseRec.set("process_bank_type", TypeUtils.castToString(bankTypeRec.get("name")));
        newBaseRec.set("total_num", TypeUtils.castToInt(tempBaseRec.get("total_num")));
        newBaseRec.set("total_amount", TypeUtils.castToBigDecimal(tempBaseRec.get("total_amount")));
        newBaseRec.set("delete_flag", 0);
        newBaseRec.set("create_by", userInfo.getUsr_id());
        newBaseRec.set("create_on", new Date());
        newBaseRec.set("batchno", batchno);
        newBaseRec.set("payment_summary", TypeUtils.castToString(record.get("payment_summary")));
        newBaseRec.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        newBaseRec.set("attachment_count", list != null ? list.size() : 0);
        newBaseRec.set("pay_mode", TypeUtils.castToInt(record.get("pay_mode")));
        if (oldBaseRec == null) {
            //保存baseinfo信息
            newBaseRec.set("persist_version", 0);
            boolean flag = Db.save("inner_batchpay_baseinfo", newBaseRec);
            if (!flag) {
                return false;
            }
        } else {
            //修改baseinfo主表信息
            newBaseRec.set("update_on", new Date());
            newBaseRec.set("persist_version", TypeUtils.castToInt(oldBaseRec.get("persist_version")) + 1);

            //要更新的列
            newBaseRec.remove("id");

            return CommonService.update("inner_batchpay_baseinfo",
                    newBaseRec,
                    new Record()
                            .set("id", TypeUtils.castToLong(oldBaseRec.get("id")))
                            .set("persist_version", TypeUtils.castToInt(oldBaseRec.get("persist_version"))));
        }

        return true;
    }

    /**
     * 调拨批量修改
     *
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     * @throws BusinessException
     */
    public Record chg(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));
        final List<Object> list = record.get("files");
        record.remove("files");

        //校验
        //查询baseinfotemp 临时表信息
        final Record tempBaseRec = Db.findById("inner_batchpay_baseinfo_temp", "batchno,uuid", batchno, uuid);
        if (tempBaseRec == null) {
            throw new ReqDataException("导入信息查询失败!");
        }

        //根据付款方id查询付款方信息
        final Record payRec = Db.findById("account", "acc_id", TypeUtils.castToLong(record.get("pay_account_id")));

        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }

        //根据bank_cnaps_code查询银行信息
        final Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        if (bankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }

        final Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(payRec.get("curr_id")));
        if (currRec == null) {
            throw new ReqDataException("未找到有效的币种信息!");
        }

        final Record bankTypeRec = Db.findById("const_bank_type", "code", TypeUtils.castToString(bankRec.get("bank_type")));
        if (bankTypeRec == null) {
            throw new ReqDataException("未找到有效的银行大类信息!");
        }
        final Record baseRec = new Record();

        //查询原主表数据
        final Record oldBaseRec = Db.findFirst(Db.getSql("batch.findBaseInfoByIdAndVersion"), id, version);
        if (oldBaseRec == null) {
            throw new ReqDataException("单据不存在或已过期!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(oldBaseRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //attInfo 根据批次号
                int resultAttInfo = Db.update(Db.getSql("batch.delAttachmentInfo"), batchno);
                //attDetal 根据批次号
                int resultAttDetail = Db.update(Db.getSql("batch.delAttachmentDetail"), batchno);

                if (resultAttInfo > 0 && resultAttDetail > 0) {

                    //插入主表数据
                    boolean flag = addTx(batchno, uuid, baseRec, uodpInfo, userInfo, record, payRec, currRec,
                            bankRec, bankTypeRec, tempBaseRec, list, oldBaseRec);
                    if (flag) {
                    	//删除附件
                    	CommonService.delFileRef(WebConstant.MajorBizType.INNERDB_BATCH.getKey(), id);
                        if (list != null && list.size() > 0) {
                            //保存附件
                            return CommonService.saveFileRef(WebConstant.MajorBizType.INNERDB_BATCH.getKey(),
                                    id, list);
                        }
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改调拨批量单据失败!");
        }

        return Db.findById("inner_batchpay_baseinfo", "id", id);
    }

    /**
     * 初始化临时信息
     *
     * @param record
     * @throws BusinessException
     */
    public void initchgtemp(final Record record, final UserInfo userInfo) throws BusinessException {
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final long id = TypeUtils.castToLong(record.get("id"));


        //根据正式表baseid查询base信息
        final Record baseRec = Db.findById("inner_batchpay_baseinfo", "id", id);
        if (baseRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //根据batchno&uuid删除临时表数据
                //1.临时主表信息baseinfo
                Db.update(Db.getSql("batch.delBaseInfoTemp"), batchno);
                //2.临时附件表信息attachmentinfo  attachmentdetail
                Db.update(Db.getSql("batch.delAttachmentInfoTemp"), batchno);
                Db.update(Db.getSql("batch.delAttachmentDetailTemp"), batchno);

                //正式表数据插入临时表
                //1.临时主表信息baseinfo
                Record baseTempRec = new Record();
                baseTempRec.set("batchno", TypeUtils.castToString(baseRec.get("batchno")));
                baseTempRec.set("uuid", uuid);
                baseTempRec.set("usr_id", userInfo.getUsr_id());
                baseTempRec.set("total_num", TypeUtils.castToBigDecimal(baseRec.get("total_num")));
                baseTempRec.set("total_amount", TypeUtils.castToBigDecimal(baseRec.get("total_amount")));

                Db.save("inner_batchpay_baseinfo_temp", baseTempRec);

                //2.临时附件表信息attachmentinfo  attachmentdetail
                Db.update(Db.getSql("batch.insertBatchAttachInfoTemp"), uuid, WebConstant.TempTableStatus.NEW.getKey(), batchno);
                Db.update(Db.getSql("batch.insertBatchAttachDetailTemp"), batchno);

                return true;
            }
        });
    }

    /**
     * 删除调拨批量单据
     *
     * @param record
     * @param userInfo
     * @throws BusinessException
     */
    public void del(final Record record, final UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        Record batchRec = Db.findById("inner_batchpay_baseinfo", "id", id);
        if (batchRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(batchRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.set("persist_version", old_version + 1);
        record.set("delete_flag", 1);

        //要更新的列
        record.remove("id");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("inner_batchpay_baseinfo",
                        record,
                        new Record().set("id", id).set("persist_version", old_version));
            }
        });

        if (!flag) {
            throw new DbProcessException("删除单据失败!");
        }

    }

    /**
     * 临时表修改操作
     *
     * @param record
     */
    public Record removetemp(final Record record, final UserInfo userInfo) throws BusinessException {
        final String infoid = TypeUtils.castToString(record.get("id"));
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final Record changeRec = new Record();

        final Record tempBaseRec = Db.findById("inner_batchpay_baseinfo_temp", "batchno,uuid", batchno, uuid);
        if (tempBaseRec == null) {
            throw new ReqDataException("删除数据失败!");
        }

        //查询临时附件信息
        final Record tempAttInfo = Db.findFirst(Db.getSql("batch.fingAttachInfoTemp"), infoid, uuid, batchno);
        if (tempAttInfo == null) {
            throw new ReqDataException("附件信息不存在!");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                int totalNum = TypeUtils.castToInt(tempBaseRec.get("total_num"));
                BigDecimal totalAmount = TypeUtils.castToBigDecimal(tempBaseRec.get("total_amount"));

                int tempNum = TypeUtils.castToInt(tempAttInfo.get("number"));
                BigDecimal tempAmount = TypeUtils.castToBigDecimal(tempAttInfo.get("amount"));


                //删除临时attacinfo   根据id
                boolean delFlag = Db.deleteById("inner_batchpay_bus_attach_info_temp", "id,uuid", infoid, uuid);
                //删除临时attadetail
                int result = Db.delete(Db.getSql("batch.delAttachDetalTempByInfoid"), infoid);
                if (delFlag && result > 0) {
                    //修改临时base表信息
                    totalNum -= tempNum;
                    totalAmount = totalAmount.subtract(tempAmount);

                    result = Db.update(Db.getSql("batch.updateTempBaseInfoBybatchUuid"), totalNum, totalAmount, userInfo.getUsr_id(), uuid, batchno);
                    if (result > 0) {
                        changeRec.set("total_num", totalNum);
                        changeRec.set("total_amount", totalAmount);
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        if (flag) {
            return changeRec;
        }
        throw new DbProcessException("删除模版数据失败!");
    }

    /**
     * 查看批次汇总
     *
     * @param record
     * @return
     */
    public Record viewbill(final Record record) {
        String batchno = TypeUtils.castToString(record.get("batchno"));
        return Db.findFirst(Db.getSql("batch.getBatchViewBillByBatchno"), batchno);
    }


    /**
     * 详细列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> detaillist(int pageNum, int pageSize, final Record record) {
        String batchno = TypeUtils.castToString(record.get("batchno"));
        Kv kv = Kv.create();
        kv.set("batchno", batchno);
        kv.set("pay_status", record.get("pay_status"));
        SqlPara sqlPara = getListParam(record, "batch.findAttachDetailToPage", kv);
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 详细列表汇总
     *
     * @param record
     * @return
     */
    public Record detaillisttotal(final Record record) {
        String batchno = TypeUtils.castToString(record.get("batchno"));
        Kv kv = Kv.create();
        kv.set("batchno", batchno);
        SqlPara sqlPara = getListParam(record, "batch.findAttachDetailTotal", kv);
        return Db.findFirst(sqlPara);
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
        SqlPara sqlPara = getListParam(record, "batch.findAttachDetailToPage", kv);
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
        SqlPara sqlPara = getListParam(record, "batch.findAttachInfoByIDBatchno", kv);
        return Db.findFirst(sqlPara);
    }

    public SqlPara getListParam(final Record record, String sql, final Kv kv) {

        CommonService.processQueryKey4Kv(record,kv,"pay_query_key","pay_account_name","pay_account_no");
        CommonService.processQueryKey4Kv(record,kv,"recv_query_key","recv_account_name","recv_account_no");


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

    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        long baseid = TypeUtils.castToLong(record.get("id"));
        String batchno = TypeUtils.castToString(record.get("batchno"));

        //baseinfo
        Record baseRec = Db.findById("inner_batchpay_baseinfo", "id", baseid);
        if (baseRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if(record.get("wf_inst_id") == null || record.get("biz_type") == null){
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(),baseRec.getLong("org_id"));
        }


        //attachinfo
        List<Record> attInfoRec = Db.find(Db.getSql("batch.findAttachInfoByBatchno"), batchno);
        if (attInfoRec == null) {
            throw new ReqDataException("未找到有效的附件信息!");
        }

        baseRec.set("attach_info", attInfoRec);


        return baseRec;
    }

    /**
     * 调拨通批量 - 批次 - 单据作废
     *
     * @param record
     * @param userInfo
     */
    public void cancel(final Record record, final UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int oldversion = TypeUtils.castToInt(record.get("persist_version"));

        Record innerRec = Db.findFirst(Db.getSql("batch.findBaseInfoByIdAndVersion"), id, oldversion);
        if (innerRec == null) {
            throw new ReqDataException("单据不存在或已过期!");
        }

        final String batchno = TypeUtils.castToString(innerRec.get("batchno"));
        int service_status = TypeUtils.castToInt(innerRec.get("service_status"));
        if (service_status != WebConstant.BillStatus.PASS.getKey()
                && service_status != WebConstant.BillStatus.FAILED.getKey()
                && service_status != WebConstant.BillStatus.NOCOMPLETION.getKey()) {
            throw new ReqDataException("此单据状态错误，请刷新重试！");
        }


        //查询是否有未完结的单据
        SqlPara detailPara = Db.getSqlPara("batch.findBatchAttachDetailByBatchnoAndPayStatus",
                Ret.by("map",Kv.create().set("batchno",innerRec.get("batchno")).set("pay_status",new Integer[]{
                        WebConstant.PayStatus.HANDLE.getKey(),
                        WebConstant.PayStatus.FAILD.getKey()

                })));
        List<Record> detailRecList = Db.find(detailPara);
        if(detailRecList != null && detailRecList.size() > 0){
            throw new ReqDataException("此单据还有未完结的单据，无法作废！");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                String feed_back = TypeUtils.castToString(record.get("feed_back"));
                //修改详细表支付状态以及作废原因
                int flag = CommonService.updateRows("inner_batchpay_bus_attach_detail",
                        new Record().set("feed_back", feed_back).set("pay_status", WebConstant.PayStatus.CANCEL.getKey())
                                .set("update_by", userInfo.getUsr_id()).set("update_on", new Date()),
                        new Record().set("batchno", batchno).set("pay_status",WebConstant.PayStatus.INIT.getKey()));
                //修改主表单据状态为已作废
                if (flag>=0) {
                    return CommonService.update("inner_batchpay_baseinfo",
                            new Record().set("service_status",
                                    WebConstant.BillStatus.COMPLETION.getKey()).set("persist_version", (oldversion + 1))
                                    .set("update_by", userInfo.getUsr_id()).set("update_on", new Date()),
                            new Record().set("id", id).set("persist_version", oldversion)
                    );
                }
                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("批次作废失败！");
        }
    }

    /**
     * 调拨通批量 - 详细 - 单据作废
     *
     * @param record
     * @param userInfo
     */
    public Record cancelids(final Record record, final UserInfo userInfo) throws BusinessException {
        List<Long> idsList = record.get("detail_ids");
        record.remove("detail_ids");

        final Long[] ids = new Long[idsList.size()];

        for (int i = 0; i < idsList.size(); i++) {
            ids[i] = TypeUtils.castToLong(idsList.get(i));
        }

        final long id = TypeUtils.castToLong(record.get("id"));
        final int oldversion = TypeUtils.castToInt(record.get("persist_version"));

        final Record innerRec = Db.findFirst(Db.getSql("batch.findBaseInfoByIdAndVersion"), id, oldversion);
        if (innerRec == null) {
            throw new ReqDataException("单据不存在或已过期!");
        }
        int service_status = TypeUtils.castToInt(innerRec.get("service_status"));
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
                    boolean flag = CommonService.update("inner_batchpay_bus_attach_detail",
                            new Record().set("feed_back", feed_back).set("pay_status", WebConstant.PayStatus.CANCEL.getKey())
                                    .set("update_by", userInfo.getUsr_id()).set("update_on", new Date()),
                            new Record().set("detail_id", detailId));
                    if (!flag) {
                        return false;
                    }
                }
                //修改主表单据状态
                set.set("persist_version", (oldversion + 1))
                        .set("update_by", userInfo.getUsr_id()).set("update_on", new Date());
                where.set("id", id).set("persist_version", oldversion);

                //根据批次号查询该单据是否有已保存单据

                //根据批次号查询该单据是否未完结的单据
                SqlPara detailPara = Db.getSqlPara("batch.findBatchAttachDetailByBatchnoAndPayStatus",
                        Ret.by("map",Kv.create().set("batchno",innerRec.get("batchno")).set("pay_status",new Integer[]{
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
                return CommonService.update("inner_batchpay_baseinfo", set, where);
            }
        });

        if (!flag) {
            throw new DbProcessException("作废失败!");
        }

        return Db.findById("inner_batchpay_baseinfo", "id", id);
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

                final Record innerRec = Db.findFirst(Db.getSql("batch.findBatchSendInfoByDetailId"), idStr);
                final Integer status = innerRec.getInt("service_status");
                final int persist_version = TypeUtils.castToInt(innerRec.get("persist_version"));
                final String feedBack = errMsg;
                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {

                        if( WebConstant.PayStatus.INIT.getKey() != status && WebConstant.PayStatus.FAILD.getKey() != status ){
                            log.error("单据状态有误!");
                            return false;
                        }
                        Record setRecord = new Record();
                        Record whereRecord  = new Record();

                        setRecord.set("pay_status",WebConstant.BillStatus.FAILED.getKey()).set("feed_back",feedBack)
                                .set("persist_version",persist_version+1);
                        whereRecord.set("detail_id",idStr).set("pay_status",status).set("persist_version",persist_version);
                        return CommonService.updateRows("inner_batchpay_bus_attach_detail",setRecord,whereRecord) == 1;
                    }
                });
                if(!flag){
                    log.error("数据过期！");
                }
                continue;
            }
        }
    }

    /**
     * 详细支付确认
     * 支付方式为网银，支付确认后单据状态改为  已成功
     */
    public Record payconfirm(final Record record, final UserInfo userInfo) throws BusinessException {
        List<Long> idsList = record.get("detail_ids");
        record.remove("detail_ids");

        final long id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));

        final Record innerRec = Db.findById("inner_batchpay_baseinfo", "id", id);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        final Long[] ids = new Long[idsList.size()];

        for (int i = 0; i < idsList.size(); i++) {
            ids[i] = TypeUtils.castToLong(idsList.get(i));
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                Record where = new Record();

                for (Long detailId : ids) {
                    //修改详细单据支付状态
                    boolean flag = CommonService.update("inner_batchpay_bus_attach_detail",
                            new Record().set("pay_status", WebConstant.PayStatus.SUCCESS.getKey())
                                    .set("update_by", userInfo.getUsr_id()).set("update_on", new Date()),
                            new Record().set("detail_id", detailId));
                    if (!flag) {
                        return false;
                    }
                }
                //修改主表单据状态
                set.set("persist_version", (version + 1))
                        .set("update_by", userInfo.getUsr_id()).set("update_on", new Date());
                where.set("id", id).set("persist_version", version);

                //根据批次号查询该单据是否有已保存单据和已失败
                SqlPara detailPara = Db.getSqlPara("batch.findBatchAttachDetailByBatchnoAndPayStatus",
                        Ret.by("map",Kv.create().set("batchno",innerRec.get("batchno")).set("pay_status",new Integer[]{
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
                return CommonService.update("inner_batchpay_baseinfo", set, where);
            }
        });

        if (!flag) {
            throw new DbProcessException("支付确认失败!");
        }

        return Db.findById("inner_batchpay_baseinfo", "id", id);

    }

    /**
     * 批次支付确认
     * 支付方式为网银，支付确认后单据状态改为  已成功
     */
    public Record paybatchconfirm(final Record record, final UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));

        Record innerRec = Db.findFirst(Db.getSql("batch.findBaseInfoByIdAndVersion"), id, version);
        if (innerRec == null) {
            throw new ReqDataException("单据不存在或已过期!");
        }

        final String batchno = TypeUtils.castToString(innerRec.get("batchno"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //修改详细表支付状态
                boolean flag = CommonService.update("inner_batchpay_bus_attach_detail",
                        new Record().set("pay_status", WebConstant.PayStatus.SUCCESS.getKey())
                                .set("update_by", userInfo.getUsr_id()).set("update_on", new Date()),
                        new Record().set("batchno", batchno));
                //修改主表单据状态
                if (flag) {
                    return CommonService.update("inner_batchpay_baseinfo",
                            new Record().set("service_status",
                                    WebConstant.BillStatus.COMPLETION.getKey()).set("persist_version", (version + 1))
                                    .set("update_by", userInfo.getUsr_id()).set("update_on", new Date()),
                            new Record().set("id", id).set("persist_version", version)
                    );
                }
                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("批次支付确认失败！");
        }

        return Db.findById("inner_batchpay_baseinfo", "id", id);
    }

    /**
     * 发送单笔明细
     *
     * @param detailId
     * @throws Exception
     */
    private void sendPayDetail(final Long detailId, final long id) throws Exception {

        final Record innerRec = Db.findFirst(Db.getSql("batch.findBatchSendInfoByDetailId"), detailId);
        if (innerRec == null) {
            throw new Exception();
        }
        final Integer status = innerRec.getInt("pay_status");
        if (status != WebConstant.PayStatus.FAILD.getKey() && status != WebConstant.PayStatus.INIT.getKey()) {
            throw new Exception("单据状态有误!:" + detailId);
        }
        String payCnaps = innerRec.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
        innerRec.set("id", detailId);
        innerRec.set("source_ref", "inner_batchpay_bus_attach_detail");
        final int oldRepearCount = innerRec.getInt("repeat_count");
        innerRec.set("repeat_count", innerRec.getInt("repeat_count") + 1);
        innerRec.set("bank_serial_number", ChannelManager.getSerianlNo(payBankCode));
        SysSinglePayInter sysInter = new SysSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(innerRec);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("single_pay_instr_queue", instr);
                if (save) {
                    save = Db.update(Db.getSql("batch.updateDetailById"), instr.getStr("bank_serial_number"),
                            instr.getInt("repeat_count"), WebConstant.PayStatus.HANDLE.getKey(),instr.getStr("instruct_code"), detailId,
                            oldRepearCount, status) == 1;
                    if (save) {
                        //修改批次状态为未完结
                        Record set = new Record();
                        Record where = new Record();

                        set.set("service_status", WebConstant.BillStatus.NOCOMPLETION.getKey());

                        where.set("id", id);
                        return CommonService.update("inner_batchpay_baseinfo", set, where);
                    }
                }
                return save;
            }
        });
        if (flag) {
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr),payBankCode);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
            throw new DbProcessException("发送失败，请联系管理员！");
        }
    }
}
