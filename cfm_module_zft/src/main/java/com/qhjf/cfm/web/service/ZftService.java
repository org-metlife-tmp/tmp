package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.*;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysSinglePayInter;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zhangyuan
 * @Date: 2018/8/9
 * @Description: 支付通
 */
public class ZftService {

    private static Logger log = LoggerFactory.getLogger(ZftService.class);

    public boolean hookPass(Record record) {
        final long id = TypeUtils.castToLong(record.get("id"));
        Record bill = Db.findById("outer_zf_payment", "id", id);
        int pay_mode = TypeUtils.castToInt(bill.get("pay_mode"));
        int version = TypeUtils.castToInt(bill.get("persist_version"));
        if (pay_mode == WebConstant.PayMode.ADDITIONAL.getKey()) {
            //单据状态变成已成功
            return CommonService.update("outer_zf_payment",
                    new Record().set("service_status", WebConstant.BillStatus.SUCCESS.getKey()).set("persist_version", version + 1),
                    new Record().set("id", id));
        }
        return true;
    }

    /**
     * 增加制单接口
     *
     * @param ""
     * @param userInfo
     * @param returnRecord
     * @param insertRecord
     * @param pay_info
     * @throws DbProcessException
     * @throws ReqDataException 
     */
    public Record insert(UodpInfo uodpInfo, UserInfo userInfo, Record initrecord,
                         Record rev_record, final Record insertRecord, Record returnRecord, Record pay_info) throws DbProcessException, ReqDataException {
        //根据银行的cnaps码,查询银行名称
        final List<Object> list = initrecord.get("files");
        insertRecord.set("pay_account_id", pay_info.get("acc_id")); //付款账户id
        insertRecord.set("pay_account_no", pay_info.get("acc_no")); //付款方账号
        insertRecord.set("pay_account_name", pay_info.get("acc_name")); //付款方账号
        insertRecord.set("pay_account_bank", pay_info.get("bank_name")); //付款方银行名称
        insertRecord.set("pay_bank_cnaps", pay_info.get("bank_cnaps_code")); //付款方银行cnaps
        insertRecord.set("pay_account_cur", pay_info.get("curr_code")); //付款方币种编号
        insertRecord.set("pay_bank_prov", pay_info.get("province")); //付款方银行所在省
        insertRecord.set("pay_bank_city", pay_info.get("city")); //付款方银行所在市
        insertRecord.set("recv_account_no", rev_record.get("acc_no")); //收款方账号
        insertRecord.set("recv_account_name", rev_record.get("acc_name")); //收款方账户名
        insertRecord.set("recv_account_bank", rev_record.get("bank_name")); //收款方银行名称
        insertRecord.set("payment_amount", initrecord.get("payment_amount")); //支付金额
        insertRecord.set("payment_summary", initrecord.get("payment_summary")); //摘要信息
        insertRecord.set("attachment_count", list != null ? list.size() : 0); //附件数量
        insertRecord.set("recv_bank_cnaps", rev_record.get("cnaps_code")); //收款方银行cnaps
        insertRecord.set("recv_account_id", rev_record.get("id"));//收款方id
        Record currCode = Db.findById("currency", "id", rev_record.get("curr_id"));
        insertRecord.set("recv_account_cur", currCode == null ? "CNY" : currCode.get("iso_code")); //收款方账户币种
        insertRecord.set("recv_bank_prov", rev_record.get("province")); //收款方账户币种
        insertRecord.set("recv_bank_city", rev_record.get("city")); //收款方账户币种
        insertRecord.set("create_by", userInfo.getUsr_id()); //用户id
        insertRecord.set("create_on", new Date()); //创建时间
        insertRecord.set("persist_version", 0);  //版本号
        insertRecord.set("org_id", uodpInfo.getOrg_id());
        insertRecord.set("dept_id", uodpInfo.getDept_id());
        insertRecord.set("delete_flag", 0); // 是否删除标志位
        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ZFT);
        insertRecord.set("service_serial_number", serviceSerialNumber);
        insertRecord.set("service_status", WebConstant.BillStatus.SAVED.getKey()); // 单据状态
        // 1
        // :
        // 已保存
        insertRecord.set("biz_id", initrecord.get("biz_id"));
        insertRecord.set("biz_name", initrecord.get("biz_name"));
        insertRecord.set("pay_mode", initrecord.get("pay_mode"));
        
        //添加事物_outer_zf_payment单据表插入数据/保存附件
        boolean txFlag = Db.tx(new IAtom() {			
			@Override
			public boolean run() throws SQLException {
				boolean flag = Db.save("outer_zf_payment", insertRecord);
				if(flag){
					log.debug("=============支付通单据表插入成功");
					// 保存附件
					if (list != null && list.size() > 0){
						return CommonService.saveFileRef(WebConstant.MajorBizType.ZFT.getKey(), TypeUtils.castToLong(insertRecord.get("id")), list);
					}else{
						log.debug("=============未上传附件");
						return true ;						
					}
				}else{
					log.error("==================支付通单据表插入失败");
					return false;					
				}
			}
		});
        // 获取此条数据id
        log.debug("==================支付通制单插入,事物状态===="+txFlag);
        if(txFlag){
        	returnRecord = Db.findById("outer_zf_payment", "id", insertRecord.get("id"));
        	returnRecord.set("rev_persist_version", rev_record.get("persist_version"));
        }else{
        	throw new ReqDataException("支付通保存失败");
        }
        return returnRecord;
    }

    /**
     * 根据收款方id查询收款人信息
     *
     * @param supplier_acc_id
     * @param record
     * @param userInfo
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record querySupplierInfo(String supplier_acc_id, Record record, UserInfo userInfo)
            throws ReqDataException, DbProcessException {

        Record paramsToRecord = new Record();
        // 前台传输的版本号
        int persist_version = TypeUtils.castToInt(record.get("rev_persist_version"));
        String acc_name = TypeUtils.castToString(record.get("recv_account_name"));
        String cnaps_code = TypeUtils.castToString(record.get("recv_bank_cnaps"));
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
        boolean flag = chgRevDbIdAndVersion(paramsToRecord,
                new Record().set("id", supplier_acc_id).set("persist_version", persist_version));
        if (!flag) {
            throw new DbProcessException("更新收款人信息失败!");
        }
        // 获取收款人信息
        Record supplier_info = Db.findById("supplier_acc_info", "id", supplier_acc_id);
        if (null == supplier_info) {
            throw new ReqDataException("根据收款人id" + supplier_acc_id + "查询收款人信息不存在!!");
        }
        return supplier_info;
    }

    /**
     * 维护收款人表
     *
     * @param record
     * @param userInfo
     * @return
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record insertSupplier(Record record, UserInfo userInfo) throws ReqDataException {
        String acc_name = TypeUtils.castToString(record.get("recv_account_name"));
        String acc_no = TypeUtils.castToString(record.get("recv_account_no"));
        String cnaps_code = TypeUtils.castToString(record.get("recv_bank_cnaps"));
        Record supplier_info = new Record();
        if (StringUtil.isBlank(acc_name) || StringUtil.isBlank(acc_no) || StringUtil.isBlank(cnaps_code)) {
            throw new ReqDataException("请求参数错误_收款人信息传输不全");
        }
        String Supplier_sql = Db.getSql("supplier.querySupplier");
        List<Record> Supplier_infos = Db.find(Supplier_sql, acc_no);
        if (null != Supplier_infos && Supplier_infos.size() > 0) {
            return supplier_info = Supplier_infos.get(0);
        }
        // 收款人信息都传输了,开始维护收款人信息表
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", cnaps_code);
        if (bankRec == null) {
            throw new ReqDataException("未根据银行cnapcode查询到银行信息!");
        }
        supplier_info.set("cnaps_code", cnaps_code);
        supplier_info.set("acc_no", acc_no);
        supplier_info.set("acc_name", acc_name);
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
            throw new ReqDataException("维护收款人信息表错误!");
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

    /**
     * 对单据进行删除,没进行一次操作,都要对version进行++
     *
     * @param paramsToRecord
     * @param userInfo
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public void deleteBill(Record paramsToRecord, UserInfo userInfo) throws ReqDataException, DbProcessException {
        long id = TypeUtils.castToLong(paramsToRecord.get("id"));
        Record innerRec = Db.findById("outer_zf_payment", "id", id);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(innerRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        int old_version = TypeUtils.castToInt(paramsToRecord.get("persist_version"));
        paramsToRecord.set("update_by", userInfo.getUsr_id());
        paramsToRecord.set("update_on", new Date());
        paramsToRecord.set("persist_version", old_version + 1);
        paramsToRecord.set("delete_flag", 1);
        // 要更新的列
        paramsToRecord.remove("id");
        boolean flag = chgDbPaymentByIdAndVersion(paramsToRecord
                , new Record().set("id", id).set("persist_version", old_version));
        if (!flag) {
            throw new DbProcessException("删除支付通单据失败!");
        }
    }

    /**
     * @param record
     * @param userInfo
     * @param uodpInfo
     * @return
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record chg(final Record record, UserInfo userInfo, UodpInfo uodpInfo, Record recvRec)
            throws ReqDataException, DbProcessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        // 根据单据id查询单据信息
        Record innerRec = Db.findById("outer_zf_payment", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据信息!");
        }

        //申请单创建人id非登录用户，不允许进行操作
        if(!TypeUtils.castToLong(innerRec.get("create_by")).equals(userInfo.getUsr_id())){
            throw new ReqDataException("无权进行此操作！");
        }

        long payAccountId = TypeUtils.castToLong(record.get("pay_account_id"));
        long recvAccountId = TypeUtils.castToLong(record.get("recv_account_id"));
        // 根据付款方帐号id查询账户信息
        Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), payAccountId);
        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }
        // 根据付款方cnaps号查询银行信息
        Record payBankRec = Db.findById("all_bank_info", "cnaps_code",
                TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        if (payBankRec == null) {
            throw new ReqDataException("未找到有效的银行信息!");
        }
        // 根据收款方帐号id查询账户信息
        if (recvRec == null) {
            throw new ReqDataException("未找到有效的收款方帐号!");
        }
        final List<Object> list = record.get("files");
        record.remove("files");
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
        record.set("pay_account_cur", TypeUtils.castToString(payRec.get("curr_code")));
        Record currCode = Db.findById("currency", "id", TypeUtils.castToLong(recvRec.get("curr_id")));
        record.set("recv_account_cur", currCode == null ? "CNY" : currCode.get("iso_code"));
        record.set("pay_bank_cnaps", TypeUtils.castToString(payRec.get("bank_cnaps_code")));
        record.set("recv_bank_cnaps", TypeUtils.castToString(recvRec.get("cnaps_code")));
        record.set("pay_bank_prov", TypeUtils.castToString(payBankRec.get("province")));
        record.set("recv_bank_prov", TypeUtils.castToString(recvRec.get("province")));
        record.set("pay_bank_city", TypeUtils.castToString(payBankRec.get("city")));
        record.set("recv_bank_city", TypeUtils.castToString(recvRec.get("city")));
        record.set("process_bank_type", TypeUtils.castToString(payBankRec.get("bank_type")));// 付款方银行大类
        record.set("attachment_count", list != null ? list.size() : 0);
        record.set("service_status", WebConstant.BillStatus.SAVED.getKey());
        record.remove("rev_persist_version");
        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("persist_version", old_version + 1);

        record.remove("id");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean result = CommonService.update("outer_zf_payment",
                        record,
                        new Record().set("id", id).set("persist_version", old_version));
                if (result) {
                    // 删除附件
                    CommonService.delFileRef(WebConstant.MajorBizType.ZFT.getKey(), id);
                    if (list != null && list.size() > 0) {
                        // 保存附件
                        return CommonService.saveFileRef(WebConstant.MajorBizType.ZFT.getKey(), id, list);
                    }
                    return true;
                }
                return false;
            }
        });

        if (flag) {
            Record findById = Db.findById("outer_zf_payment", "id", id);
            Record supplier_info = Db.findById("supplier_acc_info", "id", recvAccountId);
            findById.set("rev_persist_version", supplier_info.get("persist_version"));
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
                return CommonService.update("outer_zf_payment", set, where);
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
        SqlPara sqlPara = getlistparam(record, "zjzf.findMoreInfo");
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
        SqlPara sqlPara = Db.getSqlPara("zjzf.findAllAmount", Kv.by("map", record.getColumns()));
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
     * @param uodpInfo
     * @param is_include_sub 是否包含下级
     * @return
     */
    public Page<Record> detallist(int pageNum, int pageSize, Record record, UodpInfo uodpInfo, boolean is_include_sub) {
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        record.set("level_code", orgRec.get("level_code"));
        record.set("level_num", orgRec.get("level_num"));
        /**
         * 如果不包含下级，则将org_id 传入，搜索的范围缩小，无法查到下级的机构
         * 如果包含下级，则不需要传org_id, 会根据上面的level_code 和level_num进行下级的搜索
         */
        if (!is_include_sub) {
            record.set("org_id", uodpInfo.getOrg_id());
        } else {
            record.remove("org_id");
        }

        SqlPara sqlPara = getlistparam(record, "zjzf.findMoreInfo");
        return Db.paginate(pageNum, pageSize, sqlPara);

    }

    /**
     * 单据列表, 默认包含下级机构
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> detallist(int pageNum, int pageSize, Record record, UodpInfo uodpInfo) {
        return detallist(pageNum, pageSize, record, uodpInfo, true);
    }


    public Record detaillisttotal(Record record, UodpInfo uodpInfo, boolean is_include_sub) {
        //根据机构id查询机构信息
        Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

        record.set("level_code", orgRec.get("level_code"));
        record.set("level_num", orgRec.get("level_num"));

        /**
         * 如果不包含下级，则将org_id 传入，搜索的范围缩小，无法查到下级的机构
         * 如果包含下级，则不需要传org_id, 会根据上面的level_code 和level_num进行下级的搜索
         */
        if (!is_include_sub) {
            record.set("org_id", uodpInfo.getOrg_id());
        } else {
            record.remove("org_id");
        }

        SqlPara sqlPara = Db.getSqlPara("zjzf.findAllAmount", Kv.by("map", record.getColumns()));
        return Db.findFirst(sqlPara);
    }

    /**
     * @param record
     * @return
     */
    public Record detaillisttotal(Record record, UodpInfo uodpInfo) {
        return detaillisttotal(record, uodpInfo, true);
    }

    /**
     * 查看单据详情
     *
     * @param record
     * @param userInfo
     * @return
     * @throws ReqDataException
     */
    public Record detail(Record record,UserInfo userInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        // 根据单据id查询单据信息
        Record dbRec = Db.findById("outer_zf_payment", "id", id);
        if (dbRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if(record.get("wf_inst_id") == null || record.get("biz_type") == null){
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(),dbRec.getLong("org_id"));
        }


        Record rev_Rec = Db.findById("supplier_acc_info", "id", dbRec.get("recv_account_id"));
        dbRec.set("rev_persist_version", rev_Rec.get("persist_version"));
        return dbRec;
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
        CommonService.processQueryKey(record, "pay_query_key", "pay_account_name", "pay_account_no");
        CommonService.processQueryKey(record, "recv_query_key", "recv_account_name", "recv_account_no");


        BigDecimal min = TypeUtils.castToBigDecimal(record.get("min"));
        if (min != null) {
            kv.set("min", min);
        }

        BigDecimal max = TypeUtils.castToBigDecimal(record.get("max"));
        if (max != null) {
            kv.set("max", max);
        }

        kv.set("delete_flag", 0);
        kv.set("service_status", record.get("service_status"));
        return Db.getSqlPara(sql, Kv.by("map", kv));
    }

    /**
     * 提供收款方账号/收款方户名列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> recvacclist(int pageNum, int pageSize, Record record) {
        Kv kv = Kv.create();
        CommonService.processQueryKey4Kv(record, kv, "query_key", "acc_name", "acc_no");


        kv.set("delete_flag", 0);
        SqlPara sqlPara = Db.getSqlPara("supplier.findSupplierInfoList", Kv.by("map", kv));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 支付作废功能
     *
     * @param paramsToRecord
     * @param userInfo
     * @param uodpInfo
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public void payOff(Record paramsToRecord, UserInfo userInfo, UodpInfo uodpInfo)
            throws DbProcessException, ReqDataException {
        final List<Long> ids = paramsToRecord.get("ids");
        final List<Integer> persist_versions = paramsToRecord.get("persist_version");
        for (int i = 0; i < ids.size(); i++) {
            long id = TypeUtils.castToLong(ids.get(i));
            Integer old_version = TypeUtils.castToInt(persist_versions.get(i));
            Record innerRec = Db.findById("outer_zf_payment", "id", id);
            if (innerRec == null) {
                throw new ReqDataException("未找到有效的单据!");
            }
            int service_status = TypeUtils.castToInt(innerRec.get("service_status"));
            // 判断单据状态为“审批通过”或“已失败”时可以发送，其他状态需抛出异常！
            if (service_status == WebConstant.BillStatus.PASS.getKey()
                    || service_status == WebConstant.BillStatus.FAILED.getKey()) {
                paramsToRecord.set("id", id);
                paramsToRecord.set("persist_version", old_version + 1);
                paramsToRecord.set("update_by", userInfo.getUsr_id());
                paramsToRecord.set("update_on", new Date());
                paramsToRecord.set("service_status", WebConstant.BillStatus.CANCEL.getKey());
                paramsToRecord.set("feed_back", TypeUtils.castToString(paramsToRecord.get("feed_back")));

                // 要更新的列
                paramsToRecord.remove("id");
                paramsToRecord.remove("ids");

                boolean flag = chgDbPaymentByIdAndVersion(paramsToRecord,
                        new Record().set("id", id).set("persist_version", old_version));
                if (!flag) {
                    throw new DbProcessException("单据作废失败!");
                }
            } else {
                throw new ReqDataException("单据状态不正确!");
            }
        }
    }

    /**
     * 更新收款人账户信息
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
     * 支付通资金核对列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> checkbillList(int pageNum, int pageSize, Record record) {
        record.set("delete_flag", 0);
        SqlPara sqlPara = Db.getSqlPara("zjzf.findMoreInfo", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 根据单据id,查询交易信息列表
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> checkTradeList(Record record) throws ReqDataException {
        Long id = TypeUtils.castToLong(record.get("id"));
        Record billList = Db.findById("outer_zf_payment", "id", id);
        record.remove("id");
        record.set("pay_account_no", billList.get("pay_account_no"));
        record.set("recv_account_no", billList.get("recv_account_no"));
        record.set("payment_amount", billList.get("payment_amount"));
        Date create = TypeUtils.castToDate(billList.get("create_on"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String create_on = sdf.format(create);
        record.set("create_on", create_on);
        SqlPara sqlPara = Db.getSqlPara("zjzf.tradingList", Kv.by("map", record.getColumns()));
        List<Record> find = Db.find(sqlPara);
        setBankName(find);
        return find;
    }

    private void setBankName(List<Record> find) throws ReqDataException {
        if (null != find && find.size() > 0) {
            for (Record rec : find) {
                Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
                Record queryPayInfo = this.queryPayInfo(acc_id);
                String bank_name = TypeUtils.castToString(queryPayInfo.get("bank_name"));
                rec.set("bank_name", bank_name);
            }
        }
    }

    /**
     * 查询已核对交易根据单据id 需要关联中间表
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> checkAlreadyTradeList(Record record) throws ReqDataException {
        List<Record> find = Db.find(Db.getSql("zjzf.alreaadyTradingList"), TypeUtils.castToLong(record.get("id")));
        setBankName(find);
        return find;
    }

    /**
     * 交易通_确认核对
     *
     * @param record
     * @param userInfo
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public Page<Record> confirm(Record record, UserInfo userInfo) throws DbProcessException, ReqDataException {
        final Long billId = TypeUtils.castToLong((record.get("bill_id")));
        Record innerRec = Db.findById("outer_zf_payment", "id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }

        final List<Integer> tradingId = record.get("trade_id");
        if (tradingId.size() != 1) {
            throw new ReqDataException("只可以选择一条付款交易记录进行核对!");
        }
        final List<Record> records = CommonService.genConfirmRecords(tradingId, billId, userInfo);

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));

        final Map<Integer, Date> tradMap = CommonService.getPeriod(tradingId);//key= transid , value=所属结账日的年月

        // 进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                set.set("persist_version", old_version + 1);
                set.set("is_checked", 1);
                Record where = new Record();
                where.set("id", billId);
                where.set("persist_version", old_version);
                if (CommonService.update("outer_zf_payment", set, where)) {
                    for (Record r : records) {
                        boolean i = Db.save("outer_pay_trans_checked", r);
                        boolean t = Db.update("acc_his_transaction", "id",
                                new Record().set("id", r.getInt("trans_id")).set("is_checked", 1)
                                        .set("ref_bill_id", billId).set("checked_ref", "outer_zf_payment"));
                        if (!(i && t)) {
                            return false;
                        }
                    }

                    try {
                        //生成凭证信息
                        CheckVoucherService.sunVoucherData(tradingId, billId, WebConstant.MajorBizType.ZFT.getKey(), tradMap);
                    } catch (BusinessException e) {
                        e.printStackTrace();
                        return false;
                    }

                    return true;
                } else {
                    return false;
                }
            }
        });
        if (!flag) {
            throw new DbProcessException("交易核对失败！");
        } else {
            // 返回未核对的单据列表
            Record rd = new Record();
            rd.set("is_checked", 0);
            rd.set("org_id", userInfo.getCurUodp().getOrg_id());
            AccCommonService.setInnerTradStatus(rd, "service_status");
            SqlPara sqlPara = Db.getSqlPara("zjzf.findMoreInfo", Kv.by("map", rd.getColumns()));
            return Db.paginate(1, 10, sqlPara);
        }
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

                final Record innerRec = Db.findFirst(Db.getSql("zjzf.getBillById"), idStr);
                final Integer status = innerRec.getInt("service_status");
                final int persist_version = TypeUtils.castToInt(innerRec.get("persist_version"));
                final String feedBack = errMsg;
                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {

                        if( WebConstant.BillStatus.PASS.getKey() != status && WebConstant.BillStatus.FAILED.getKey() != status ){
                            log.error("单据状态有误!");
                            return false;
                        }
                        Record setRecord = new Record();
                        Record whereRecord  = new Record();

                        setRecord.set("service_status",WebConstant.BillStatus.FAILED.getKey()).set("feed_back",feedBack)
                                .set("persist_version",persist_version+1);
                        whereRecord.set("id",idStr).set("service_status",status).set("persist_version",persist_version);
                        return CommonService.updateRows("outer_zf_payment",setRecord,whereRecord) == 1;
                    }
                });
                if(!flag){
                    log.error("数据过期！");
                }
                continue;
            }
        }

    }

    private void sendPayDetail(final Long id) throws Exception {
        Record innerRec = Db.findFirst(Db.getSql("zjzf.getBillById"), id);
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
        innerRec.set("source_ref", "outer_zf_payment");
        final int old_repeat_count = TypeUtils.castToInt(innerRec.get("repeat_count"));
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
                    return Db.update(Db.getSql("zjzf.updBillById"), instr.getStr("bank_serial_number"),
                            instr.getInt("repeat_count"), WebConstant.BillStatus.PROCESSING.getKey(), instr.getStr("instruct_code"), id,
                            status,old_repeat_count) == 1;
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

   
    /**
     * 支付通退票封装record
     *
     * @param record
     * @return
     */
    public Record getRecord(Record record) {
        String query_key = record.get("query_key");
        record.remove("query_key");
        // 是否包含中文
        boolean flag = StringKit.isContainChina(query_key);
        if (flag) {
            // 名称
            record.set("acc_name", query_key);
        } else {
            // 帐号
            record.set("acc_no", query_key);
        }
        return record;
    }


    /**
     * 支付确认
     */

    public void payok(final Record record, final long usr_id) throws BusinessException {
        final List<Record> ids = record.get("ids");
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean f = false;
                for (Record _r : ids) {
                    f = CommonService.update("outer_zf_payment",
                            new Record().set("persist_version", TypeUtils.castToInt(_r.get("persist_version")) + 1)
                                    .set("update_on", new Date()).set("update_by", usr_id)
                                    .set("service_status", WebConstant.BillStatus.SUCCESS.getKey()),
                            new Record().set("id", _r.get("id")).set("persist_version", _r.get("persist_version")));
                    if (!f) {
                        break;
                    }
                }
                return f;
            }
        });

        if (!flag) {
            throw new DbProcessException("单据支付确认失败，请刷新重试！");
        }
    }
}
