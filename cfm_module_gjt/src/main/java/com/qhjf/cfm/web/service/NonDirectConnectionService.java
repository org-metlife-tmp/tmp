package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.utils.LevelCodeGenerator;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NonDirectConnectionService {

    private static final RedisCacheConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

    public boolean hookPass(Record record) {
        final long id = TypeUtils.castToLong(record.get("id"));
        record = Db.findById("collect_batch_baseinfo", "id", id);
        final int version = TypeUtils.castToInt(record.get("persist_version"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean b = CommonService.update("collect_batch_baseinfo",
                        new Record().set("service_status", WebConstant.BillStatus.COMPLETION.getKey()).set("persist_version", version + 1),
                        new Record().set("id", id).set("persist_version", version));
                if (!b) return false;
                return CommonService.update("collect_batch_bus_attach_detail",
                        new Record().set("pay_status", WebConstant.PayStatus.SUCCESS.getKey()),
                        new Record().set("batchno", batchno));
            }
        });
    }

    /**
     * 获取当前机构本公司以及上一级账户列表
     *
     * @param org_id
     * @return
     */
    public List<Record> accs(long org_id) throws BusinessException {
        //根据机构id查询本机构信息
        Record orgRec = Db.findById("organization", "org_id", org_id);

        //获取本公司以及上级机构levelcode
        String levelCode = orgRec.get("level_code");
        int levelNum = orgRec.get("level_num");
        LevelCodeGenerator gen = new LevelCodeGenerator(5);
        Map<Integer, String> map = gen.getAncestorCodeByChild(levelCode, levelNum);
        map.put(levelNum, levelCode);
        Object[] values = map.values().toArray();

        Record record = new Record();
        record.set("is_activity", WebConstant.YesOrNo.YES.getKey());
        record.set("status", WebConstant.AccountStatus.NORAMAL.getKey());
        record.set("level_code", values);

        SqlPara sqlPara = Db.getSqlPara("collect_ndc.accs", Kv.by("map", record.getColumns()));

        return Db.find(sqlPara);
    }

    public Record morebillsum(Record record) {
        return Db.findFirst(Db.getSqlPara("collect_ndc.morebillsum", Kv.by("map", record.getColumns())));
    }

    public Record billdetail(Record record, UserInfo userInfo) throws BusinessException {
        long bill_id = TypeUtils.castToLong(record.get("id"));
        Record bill = Db.findFirst(Db.getSql("collect_ndc.billdetail"),
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

    public Page<Record> morebill(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("collect_ndc.morebill", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public void delbill(final Record record, UserInfo userInfo) throws BusinessException {
        final long bill_id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));

        Record bill = Db.findById("collect_batch_baseinfo", "id", bill_id);
        if (!TypeUtils.castToLong(bill.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("collect_batch_baseinfo",
                        new Record().set("persist_version", version + 1).set("delete_flag", WebConstant.YesOrNo.YES.getKey()),
                        new Record().set("id", bill_id).set("persist_version", version));
            }
        });

        if (!flag) {
            throw new DbProcessException("删除非直联批量单据失败！");
        }
    }

    public Page<Record> attclist(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("collect_ndc.attclist", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record chgbill(final Record record, UserInfo userInfo) throws BusinessException {
        //单据ID
        final long bill_id = TypeUtils.castToLong(record.get("id"));
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final long version = TypeUtils.castToLong(record.get("persist_version"));

        final List<Object> fileList = record.get("files");

        final Record obb = Db.findById("collect_batch_baseinfo", "id", bill_id);

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
                .set("biz_id", record.get("biz_id"))
                .set("biz_name", record.get("biz_name"))
                .set("service_status", WebConstant.BillStatus.SAVED.getKey());

        //如果当前付款账户号发生变化，则修改一下内容
        if (TypeUtils.castToInt(record.get("recv_account_id")).intValue()
                != TypeUtils.castToInt(obb.get("recv_account_id")).intValue()) {


            Record recvAccount = Db.findFirst(Db.getSql("acc.getAccByAccId"), record.get("recv_account_id"));
            if (null == recvAccount
                    || TypeUtils.castToInt(recvAccount.get("is_activity")) == WebConstant.YesOrNo.NO.getKey()
                    || !(TypeUtils.castToInt(recvAccount.get("status")) == WebConstant.AccountStatus.NORAMAL.getKey())) {
                throw new ReqDataException("收款方账户不存在！");
            }
            obb.set("recv_account_id", recvAccount.getLong("acc_id"))
                    .set("recv_account_no", recvAccount.getStr("acc_no"))
                    .set("recv_account_name", recvAccount.getStr("acc_name"))
                    .set("recv_account_bank", recvAccount.getStr("bank_name"))
                    .set("recv_account_cur", recvAccount.getStr("iso_code"))
                    .set("recv_bank_cnaps", recvAccount.getStr("bank_cnaps_code"))
                    .set("recv_bank_prov", recvAccount.getStr("bank_pro"))
                    .set("recv_bank_city", recvAccount.getStr("bank_city"))
                    .set("process_bank_type", recvAccount.getStr("bank_type"));

        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空正式表数据，保留单据信息
                Db.update(Db.getSql("collect_ndc.deleteCBBAD"), batchno);
                Db.update(Db.getSql("collect_ndc.deleteCBBAI"), batchno);

                //将数据从临时表插入到正式表
                int insertOBBAIFlag = Db.update(Db.getSql("collect_ndc.insertCBBAI"), uuid, batchno);
                if (insertOBBAIFlag == 0) {
                    return false;
                }
                int insertOBBADFlag = Db.update(Db.getSql("collect_ndc.insertCBBAD"), uuid, batchno);
                if (insertOBBADFlag == 0) {
                    return false;
                }
                //先删除原有关系
                CommonService.delFileRef(WebConstant.MajorBizType.CBB.getKey(), bill_id);
                //保存附件信息
                boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.CBB.getKey(), bill_id, fileList);
                if (!saveFileRef) {
                    return saveFileRef;
                }
                //修复单据数据
                setObbTemp(obb, getAllCollect(uuid, batchno));
                obb.remove("id");
                boolean update = CommonService.update("collect_batch_baseinfo", obb,
                        new Record().set("id", bill_id).set("persist_version", version));
                if (!update) {
                    return false;
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改非直联归集批量单据失败！");
        }
        //此时主表与临时表数据一致。
        return Db.findById("collect_batch_baseinfo", "id", bill_id);

    }

    public Record prechgbill(Record record, UserInfo userInfo) throws BusinessException {
        long bill_id = TypeUtils.castToLong(record.get("id"));
        Record bill = Db.findById("collect_batch_baseinfo", "id", bill_id);
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
        final Record bill = Db.findById("collect_batch_baseinfo", "id", bill_id);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空临时表,预防删除数据未保存状况发生
                //删除临时表数据，先删除详情，后删除excel
                Db.update(Db.getSql("collect_ndc.deleteCBBADTemp"), uuid, batchno);
                Db.update(Db.getSql("collect_ndc.deleteCBBAITemp"), uuid, batchno);
                Db.update(Db.getSql("collect_ndc.deleteCBBTemp"), uuid, batchno);

                Record obbTemp = new Record()
                        .set("batchno", batchno)
                        .set("uuid", uuid)
                        .set("usr_id", usr_id)
                        .set("total_num", bill.get("total_num"))
                        .set("total_amount", bill.get("total_amount"));
                boolean obbTempFlag = Db.save("collect_batch_baseinfo_temp", obbTemp);
                if (!obbTempFlag) {
                    return obbTempFlag;
                }
                Db.update(Db.getSql("collect_ndc.insertCBBAITemp"), uuid, batchno);
                Db.update(Db.getSql("collect_ndc.insertCBBADTemp"), uuid, batchno);
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("同步数据错误！");
        }
        //此时主表与临时表数据一致。
        //批量操作excel数据
        bill.set("obbaiTempList", Db.find(Db.getSql("collect_ndc.getCurrentCBBAITemp"), uuid, batchno));
        return bill;
    }

    public Record addbill(Record record, final long usr_id, long org_id) throws BusinessException {
        final List<Object> fileList = record.get("files");
        final String uuid = TypeUtils.castToString(record.get("uuid"));
        final String batchno = TypeUtils.castToString(record.get("batchno"));
        final Record obb = new Record()
                .set("batchno", batchno)
                .set("delete_flag", WebConstant.YesOrNo.NO.getKey())
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

        //收款方信息
        Record payAccount = Db.findFirst(Db.getSql("acc.getAccByAccId"), record.get("recv_account_id"));
        if (null == payAccount
                || TypeUtils.castToInt(payAccount.get("is_activity")) == WebConstant.YesOrNo.NO.getKey()
                || !(TypeUtils.castToInt(payAccount.get("status")) == WebConstant.AccountStatus.NORAMAL.getKey())) {
            throw new ReqDataException("收款方账户不存在！");
        }
        obb.set("recv_account_id", payAccount.getLong("acc_id"))
                .set("recv_account_no", payAccount.getStr("acc_no"))
                .set("recv_account_name", payAccount.getStr("acc_name"))
                .set("recv_account_bank", payAccount.getStr("bank_name"))
                .set("recv_account_cur", payAccount.getStr("iso_code"))
                .set("recv_bank_cnaps", payAccount.getStr("bank_cnaps_code"))
                .set("recv_bank_prov", payAccount.getStr("bank_pro"))
                .set("recv_bank_city", payAccount.getStr("bank_city"))
                .set("process_bank_type", payAccount.getStr("bank_type"));

        //设置当前单据所有数据汇总信息
        setObbTemp(obb, getAllCollect(uuid, batchno));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //插入单据信息
                boolean save = Db.save("collect_batch_baseinfo", "id", obb);
                if (!save) {
                    return save;
                }
                //保存附件信息
                boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.CBB.getKey(), TypeUtils.castToLong(obb.get("id")), fileList);
                if (!saveFileRef) {
                    return saveFileRef;
                }
                //将数据从临时表插入到正式表
                int insertOBBAIFlag = Db.update(Db.getSql("collect_ndc.insertCBBAI"), uuid, batchno);
                if (insertOBBAIFlag == 0) {
                    return false;
                }
                int insertOBBADFlag = Db.update(Db.getSql("collect_ndc.insertCBBAD"), uuid, batchno);
                if (insertOBBADFlag == 0) {
                    return false;
                }
                //删除临时表数据，先删除详情，后删除excel
                int deleteOBBADFlag = Db.update(Db.getSql("collect_ndc.deleteCBBADTemp"), uuid, batchno);
                if (deleteOBBADFlag == 0) {
                    return false;
                }
                int deleteOBBAIFlag = Db.update(Db.getSql("collect_ndc.deleteCBBAITemp"), uuid, batchno);
                if (deleteOBBAIFlag == 0) {
                    return false;
                }
                int deleteOBBFlag = Db.update(Db.getSql("collect_ndc.deleteCBBTemp"), uuid, batchno);
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
            throw new DbProcessException("新增非直联归集批量单据失败!");
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
                Db.update(Db.getSql("collect_ndc.deleteCBBADTempByInfoId"), info_id);
                Db.update(Db.getSql("collect_ndc.deleteCBBAITempByInfoId"), info_id);
                //更新临时表汇总数据
                return updateOBBTemp(uuid, batchno, obbTemp);
            }
        });
        if (!flag) {
            throw new DbProcessException("删除非直连归集批量文件失败！");
        }
        result.setColumns(obbTemp);
        return result;
    }

    /**
     * 上传-新增excel
     */
    public Record addbillexcel(Record params, final long usr_id, final long org_id) throws BusinessException {
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
            batchno = CommonService.getBatchno(WebConstant.MajorBizType.CBB);
            isFirst = true;
        } else {
            batchno = TypeUtils.castToString(params.get("batchno"));
            isFirst = false;
        }
        //构建excel所有列数据
        final List<Record> obbadTempList = new ArrayList<>(excelBean.getRowData().size());
        genBatchpayBusAttacDetailList(excelBean, obbadTempList, usr_id, batchno, uuid, documentId, org_id);
        //构建excel基础信息.
        final Record obbaiTemp = buildBatchpayBusAttacInfoRecord(batchno, uuid, documentId, originName);
        final Record[] obbTemp = new Record[1];
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                int[] res = Db.batchSave("collect_batch_bus_attach_detail_temp", obbadTempList, 1000);
                boolean checkDbResult = ArrayUtil.checkDbResult(res);
                //如果插入失败，直接返回false，不往下执行。
                if (!checkDbResult) {
                    return checkDbResult;
                }
                //设置excel汇总数据
                setObbaiTemp(obbaiTemp, getCollect(documentId, batchno, uuid));
                boolean obbaiTempFlag = Db.save("collect_batch_bus_attach_info_temp", obbaiTemp);
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
                    return Db.save("collect_batch_baseinfo_temp", "batchno,uuid", obbTemp[0]);
                } else {
                    //通过 uuid 与 batchno 获取汇总数据信息
                    obbTemp[0] = getObbTemp(uuid, batchno);
                    return updateOBBTemp(uuid, batchno, obbTemp[0]);
                }
            }
        });
        if (!flag) {
            throw new DbProcessException("上传非直联归集批量数据失败！");
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
        return Db.findById("collect_batch_baseinfo_temp", "uuid,batchno", uuid, batchno);
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
        return Db.findFirst(Db.getSql("collect_ndc.getAllCollectRecordTemp"), uuid, batchno);
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
        return Db.findFirst(Db.getSql("collect_ndc.getCollectRecordTemp"), info_id, batchno, uuid);
    }

    public Record getAllCollect(Record record) {
        return Db.findFirst(Db.getSql("collect_ndc.getAllCollectRecord"),
                TypeUtils.castToString(record.get("batchno")));
    }

    public Record getCollect(Record record) {
        return Db.findFirst(Db.getSql("collect_ndc.getCollectRecord"),
                TypeUtils.castToString(record.get("info_id")),
                TypeUtils.castToString(record.get("batchno")));
    }

    private boolean updateOBBTemp(String uuid, String batchno, Record obbTemp) {
        setObbTemp(obbTemp, getAllCollect(uuid, batchno));
        return Db.update("collect_batch_baseinfo_temp", "uuid,batchno", obbTemp);
    }

    private void genBatchpayBusAttacDetailList(ExcelResultBean excelBean, List<Record> list, long usr_id, String batchno, String uuid, String documentId, long org_id) throws BusinessException {
        //取第一条数据，获取收款方账户信息。
        for (Map<String, Object> map : excelBean.getRowData()) {
            Record _r = new Record();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                _r.set(entry.getKey(), entry.getValue());
            }
            //获取收款方账户信息 TODO 校验可放置在解析 excel
            String pay_account_no = TypeUtils.castToString(_r.get("pay_account_no"));

            Map<String, Object> accountMap = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", pay_account_no);
            if (accountMap == null) {
                throw new ReqDataException("付款方账户信息不存在！");
            }

            //根据币种ID查询币种
            Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(accountMap.get("curr_id")));

            _r.set("info_id", documentId);//TODO 待替换
            _r.set("uuid", uuid);
            _r.set("batchno", batchno);
            _r.set("update_on", new Date());
            _r.set("update_by", usr_id);
            //填充付款方信息
            _r.set("pay_account_id", accountMap.get("acc_id"));
            _r.set("pay_account_name", accountMap.get("acc_name"));
            _r.set("pay_account_cur", TypeUtils.castToString(currRec.get("iso_code")));

            Object cnaps = accountMap.get("bank_cnaps_code");
            _r.set("pay_bank_cnaps", cnaps);
            Record bankRec = Db.findById("all_bank_info", "cnaps_code", cnaps);
            if (bankRec != null) {
                _r.set("pay_account_bank", bankRec.get("name"));
                _r.set("pay_bank_prov", bankRec.get("province"));
                _r.set("pay_bank_city", bankRec.get("city"));
            }
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

    public Record billdetailsum(Record record) {
        return Db.findFirst(Db.getSqlPara("collect_ndc.billdetailsum", Kv.by("map", record.getColumns())));
    }

    public Page<Record> billdetaillist(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("collect_ndc.billdetaillist", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
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
        SqlPara sqlPara = Db.getSqlPara("collect_ndc.findAttachDetailToPage", Kv.by("map", kv));
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
        SqlPara sqlPara = Db.getSqlPara("collect_ndc.findAttachInfoByIDBatchno", Kv.by("map", kv));
        return Db.findFirst(sqlPara);
    }
}
