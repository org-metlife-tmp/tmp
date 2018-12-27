package com.qhjf.cfm.web.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
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
import com.qhjf.cfm.web.inter.impl.SysOaSinglePayInter;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;

public class BranchOrgOaService {

    OaCallback oaCallback = new OaCallback();
    private static final Log log = LogbackLog.getLog(BranchOrgOaService.class);

    /**
     * 获取分公司及下级分公司未处理付款列表
     *
     * @param record
     * @param pageSize
     * @param pageNum
     * @return
     */
    public Page<Record> getTodoList(Record record, int pageNum, int pageSize) {
        record.set("delete_flag", 0);
        this.setInnerTradStatus(record, "service_status");
        CommonService.processQueryKey(record, "recv_query_key", "recv_account_name", "recv_account_no");
        SqlPara sqlPara = Db.getSqlPara("branch_org_oa.findOaList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }


    /**
     * 获取分公司及下级分公司已处理付款列表
     *
     * @param record
     * @param pageSize
     * @param pageNum
     * @return
     */
    public Page<Record> getDoneList(Record record, int pageNum, int pageSize) {
        record.set("delete_flag", 0);
        this.setDoneTradStatus(record, "service_status");
        CommonService.processQueryKey(record, "recv_query_key", "recv_account_name", "recv_account_no");
        SqlPara sqlPara = Db.getSqlPara("branch_org_oa.findOaList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }


    /**
     * 根据org_id查询账户列表
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> normallist(Record record) throws ReqDataException {
        Record findById = Db.findById("oa_branch_payment", "id", record.get("id"));
        Integer org_id = TypeUtils.castToInt(findById.get("org_id"));
        Record organization = Db.findById("organization", "org_id", findById.get("org_id"));
        if (null == organization) {
            throw new ReqDataException("未根据单据找到相应的机构信息!");
        }
        if (3 == TypeUtils.castToInt(organization.get("level_num"))) {
            //得到的是支公司,需要先得到上级的org_id
            String sql = Db.getSql("org.getParentOrgs");
            List<Record> find = Db.find(sql, organization.get("level_code"));
            if (null == find || find.size() < 1) {
                throw new ReqDataException("未找到支公司匹配的相应分公司!");
            }
            org_id = TypeUtils.castToInt(find.get(0).get("org_id"));
        }
        record.set("org_id", org_id);
        SqlPara sqlPara = Db.getSqlPara("acc_comm.normallist", record.getColumns());
        List<Record> list = Db.find(sqlPara);
        return list;
    }

    /**
     * 根据中间账户查找资金池内的转账账户
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public Record poolAccList(Record record) throws ReqDataException {
        SqlPara sqlPara = Db.getSqlPara("poolAcc.findPoolByCodeAccid", Kv.by("map", record.getColumns()));
        List<Record> list = Db.find(sqlPara);
        if (null == list || list.size() == 0) {
            record.remove("bank_type");
            record.set("default_flag", 1);
            sqlPara = Db.getSqlPara("poolAcc.findPoolByCodeAccid", Kv.by("map", record.getColumns()));
            list = Db.find(sqlPara);
            if (null == list || list.size() == 0) {
                throw new ReqDataException("未在资金池内找到匹配的账号");
            }
        }
        Record rec = list.get(0);
        Long id = TypeUtils.castToLong(rec.get("acc_id"));
        Record queryPayInfo = this.queryPayInfo(id);
        return queryPayInfo;
    }

    /**
     * 保存 ,入库oa_branch_payment_item 2条数据
     *
     * @param record
     * @param record
     * @param userInfo
     * @throws ReqDataException
     * @throws DbProcessException
     */
    public Record chgBranch(Record record, UserInfo userInfo) throws ReqDataException, DbProcessException {
        Record returnRecord = new Record();
        final List<Object> list = record.get("files");
        //版本号
        int old_version = TypeUtils.castToInt(record.get("persist_version"));
        //分公司转账记录id
        Long id = TypeUtils.castToLong(record.get("id"));
        //资金池选择的账户id
        Long poll_acc_id = TypeUtils.castToLong(record.get("pool_account_id"));
        //中间账户id
        Long inner_acc_id = TypeUtils.castToLong(record.get("pay_account_id"));
        final Record pool_record = new Record();  //下拨记录
        final Record inner_record = new Record();  //对外转账记录
        Record queryPayInfo = this.queryPayInfo(poll_acc_id);
        Record innerqueryPayInfo = this.queryPayInfo(inner_acc_id);


        //对分公司付款信息进行 oa_branch_payment  更新
        Record set = new Record(); //分公司付款总信息
        set.set("persist_version", old_version + 1);
        set.set("attachment_count", list != null ? list.size() : 0); //附件数量
        set.set("pay_mode", record.get("pay_mode")); //付款方式
        set.set("payment_summary", record.get("payment_summary")); //摘要
        set.set("service_status", WebConstant.BillStatus.SAVED.getKey()); //状态
        set.set("update_on", new Date()); //更新时间
        set.set("update_by", userInfo.getUsr_id()); //更新人
        Record where = new Record();
        where.set("id", id);
        where.set("persist_version", old_version);
        if (!CommonService.update("oa_branch_payment", set, where)) {
            throw new ReqDataException("此条记录分公司付款记录更新失败");
        }
        Record findById = Db.findById("oa_branch_payment", "id", id);


        //删除附件
        CommonService.delFileRef(WebConstant.MajorBizType.OA_BRANCH_PAY.getKey(), id);
        if (list != null && list.size() > 0) {
            // 保存附件
            CommonService.saveFileRef(WebConstant.MajorBizType.OA_BRANCH_PAY.getKey(), id, list);
        }
        //开始处理分公司付款详情表       
        //逻辑删除
        List<Record> find = Db.find(Db.getSql("branch_org_oa.findDetail"), id);
        if (null != find && find.size() > 0) {
            Record set1 = new Record();
            set1.set("delete_flag", 1);
            Record where1 = new Record();
            where1.set("base_id", id);
            if (!CommonService.update("oa_branch_payment_item", set1, where1)) {
                throw new ReqDataException("分公司付款详情记录更新失败");
            }
        }
        //下拨
        pool_record.set("base_id", id);
        pool_record.set("org_id", findById.get("org_id"));
        pool_record.set("dept_id", findById.get("dept_id"));
        pool_record.set("pay_account_id", poll_acc_id);
        pool_record.set("pay_account_no", queryPayInfo.get("acc_no"));
        pool_record.set("pay_account_name", queryPayInfo.get("acc_name"));
        pool_record.set("pay_account_cur", queryPayInfo.get("curr_code"));
        pool_record.set("pay_account_bank", queryPayInfo.get("bank_name"));
        pool_record.set("pay_bank_cnaps", queryPayInfo.get("bank_cnaps_code"));
        pool_record.set("pay_bank_prov", queryPayInfo.get("province"));
        pool_record.set("pay_bank_city", queryPayInfo.get("city"));
        pool_record.set("process_bank_type", queryPayInfo.get("bank_type"));
        pool_record.set("recv_account_id", inner_acc_id);
        pool_record.set("recv_account_no", innerqueryPayInfo.get("acc_no"));
        pool_record.set("recv_account_name", innerqueryPayInfo.get("acc_name"));
        pool_record.set("recv_account_bank", innerqueryPayInfo.get("bank_name"));
        pool_record.set("recv_account_cur", innerqueryPayInfo.get("curr_code"));
        pool_record.set("recv_bank_cnaps", innerqueryPayInfo.get("bank_cnaps_code"));
        pool_record.set("recv_bank_prov", innerqueryPayInfo.get("province"));
        pool_record.set("recv_bank_city", innerqueryPayInfo.get("city"));
        pool_record.set("payment_amount", findById.get("payment_amount"));
        pool_record.set("pay_mode", findById.get("pay_mode"));
        pool_record.set("payment_summary", findById.get("payment_summary"));
        pool_record.set("service_status", WebConstant.PayStatus.INIT.getKey());
        String poll_Number = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.OA_BRANCH_PAY);
        pool_record.set("service_serial_number", poll_Number);
        pool_record.set("repeat_count", 0);
        pool_record.set("create_by", userInfo.getUsr_id());
        pool_record.set("create_on", new Date());
        pool_record.set("delete_flag", 0);
        pool_record.set("item_type", 1);
        pool_record.set("attachment_count", findById.get("attachment_count"));
        //对外转账
        inner_record.set("base_id", id);
        inner_record.set("org_id", findById.get("org_id"));
        inner_record.set("dept_id", findById.get("dept_id"));
        inner_record.set("pay_account_id", inner_acc_id);
        inner_record.set("pay_account_no", innerqueryPayInfo.get("acc_no"));
        inner_record.set("pay_account_name", innerqueryPayInfo.get("acc_name"));
        inner_record.set("pay_account_cur", innerqueryPayInfo.get("curr_code"));
        inner_record.set("pay_account_bank", innerqueryPayInfo.get("bank_name"));
        inner_record.set("pay_bank_cnaps", innerqueryPayInfo.get("bank_cnaps_code"));
        inner_record.set("pay_bank_prov", innerqueryPayInfo.get("province"));
        inner_record.set("pay_bank_city", innerqueryPayInfo.get("city"));
        inner_record.set("process_bank_type", innerqueryPayInfo.get("bank_type"));
        inner_record.set("recv_account_id", findById.get("recv_account_id"));
        inner_record.set("recv_account_no", findById.get("recv_account_no"));
        inner_record.set("recv_account_name", findById.get("recv_account_name"));
        inner_record.set("recv_account_bank", findById.get("recv_account_bank"));
        inner_record.set("recv_account_cur", findById.get("recv_account_cur"));
        inner_record.set("recv_bank_cnaps", findById.get("recv_bank_cnaps"));
        inner_record.set("recv_bank_prov", findById.get("recv_bank_prov"));
        inner_record.set("recv_bank_city", findById.get("recv_bank_city"));
        inner_record.set("payment_amount", findById.get("payment_amount"));
        inner_record.set("pay_mode", findById.get("pay_mode"));
        inner_record.set("payment_summary", findById.get("payment_summary"));
        inner_record.set("service_status", WebConstant.PayStatus.INIT.getKey());
        String inner_Number = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.OA_BRANCH_PAY);
        inner_record.set("service_serial_number", inner_Number);
        inner_record.set("repeat_count", 0);
        inner_record.set("create_by", userInfo.getUsr_id());
        inner_record.set("create_on", new Date());
        inner_record.set("delete_flag", 0);
        inner_record.set("item_type", 2);
        inner_record.set("attachment_count", findById.get("attachment_count"));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean innerResult = Db.save("oa_branch_payment_item", inner_record);
                boolean poolResult = Db.save("oa_branch_payment_item", pool_record);
                if (innerResult && poolResult) {
                    return true;
                }
                return false;
            }
        });
        if (!flag) {
            throw new DbProcessException("分公司付款详情表入库错误");
        }
        //修改成功,返回此单据详情
        findById = Db.findById("oa_branch_payment", "id", id);
        findById.set("poll_acc_id", poll_acc_id);
        findById.set("inner_acc_id", inner_acc_id);
        returnRecord.set("brand_payment", findById);
        List<Record> recordDetail = new ArrayList<Record>();
        recordDetail.add(pool_record);
        recordDetail.add(inner_record);
        returnRecord.set("brand_datail_payment", recordDetail);
        return returnRecord;
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
     * 分公司收款详情 ,预留方法
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public Record detailItem(Record record) throws ReqDataException {
        Record returnRecord = new Record();
        Long id = TypeUtils.castToLong(record.get("id"));
        Record brand_record = Db.findById("oa_branch_payment", "id", id);
        if (null == brand_record) {
            throw new ReqDataException("未找到此条分公司付款记录,请刷新!");
        }
        List<Record> brand_datail = Db.find(Db.getSql("branch_org_oa.findDetail"), id);
        returnRecord.set("brand_payment", brand_record);
        returnRecord.set("brand_datail_payment", brand_datail);
        return returnRecord;
    }

    /**
     * 分公司收款详情
     *
     * @param record
     * @param userInfo
     * @return
     * @throws ReqDataException
     */
    public Record detail(Record record, UserInfo userInfo) throws BusinessException {
        Record returnRecord = new Record();
        Long id = TypeUtils.castToLong(record.get("id"));
        Record brand_record = Db.findById("oa_branch_payment", "id", id);
        if (null == brand_record) {
            throw new ReqDataException("未找到此条分公司付款记录,请刷新!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if(record.get("wf_inst_id") == null || record.get("biz_type") == null){
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(),brand_record.getLong("org_id"));
        }

        List<Record> find = Db.find(Db.getSql("branch_org_oa.findBillDetail"), id);
        if (null != find && find.size() > 0) {
            return find.get(0);
        }
        return new Record();
    }


    /**
     * 设置可以核对单据的状态
     *
     * @param record
     * @param statusName
     */
    public void setInnerTradStatus(Record record, String statusName) {
        List status = record.get(statusName);
        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SAVED.getKey(),
                    WebConstant.BillStatus.REJECT.getKey()
            });
        }

    }


    /**
     * 已处理默认状态
     *
     * @param record
     * @param statusName
     */
    private void setDoneTradStatus(Record record, String statusName) {
        List status = record.get(statusName);
        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.PROCESSING.getKey(),
                    WebConstant.BillStatus.SUCCESS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey(),
                    WebConstant.BillStatus.NOCOMPLETION.getKey(),
                    WebConstant.BillStatus.COMPLETION.getKey(),
                    WebConstant.BillStatus.WAITPROCESS.getKey()
            });
        }
    }

    /**
     * @param paramsToRecord
     * @param userInfo
     * @param uodpInfo
     * @throws ReqDataException
     * @支付作废
     */
    public void payOff(final Record paramsToRecord, final UserInfo userInfo, UodpInfo uodpInfo) throws ReqDataException {

        List<Long> ids = paramsToRecord.get("ids");
        List<Integer> persist_versions = paramsToRecord.get("persist_version");
        String feed_back = TypeUtils.castToString(paramsToRecord.get("feed_back"));

        if(ids != null && ids.size() > 0 && persist_versions != null && persist_versions.size() >0){
            if(ids.size() != persist_versions.size()){
                throw new ReqDataException("请求参数错误！");
            }else{
                for(int i =0 ; i<ids.size() ; i++){
                    long id = TypeUtils.castToLong(ids.get(i));
                    int old_version = TypeUtils.castToInt(persist_versions.get(i));
                    String errorMessage = payOff(id , old_version, userInfo,uodpInfo,feed_back);
                    if(errorMessage != null){
                    	throw new ReqDataException(errorMessage);
                    }
                }
            }
        }
    }


    private String payOff(final long base_id, final int old_version,  final UserInfo userInfo, final UodpInfo uodpInfo,final String feed_back){
        String errmsg = null;
        final Record innerRec = Db.findById("oa_branch_payment", "id", base_id);
        if (innerRec == null) {
            errmsg = "未找到有效的单据";
            return errmsg;
        }
        final Long originId = TypeUtils.castToLong(innerRec.get("ref_id"));
        final Record originRecord = Db.findById("oa_origin_data", "id", originId);
        if(originRecord == null){
            errmsg = "未找到有效的原始单据";
            return errmsg;
        }

        final int service_status = TypeUtils.castToInt(innerRec.get("service_status"));

        // 判断单据状态为“审批通过”或“已失败”时可以发送，其他状态需抛出异常！
        if (service_status == WebConstant.BillStatus.SAVED.getKey() || service_status == WebConstant.BillStatus.REJECT.getKey()
                || service_status == WebConstant.BillStatus.FAILED.getKey()){
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    //分公司支付作废需要更新三张表 oa_branch_payment  oa_branch_payment_item oa_origin_data

                    if(CommonService.updateRows("oa_branch_payment",
                            new Record().set("persist_version", old_version + 1)
                                    .set("service_status", WebConstant.BillStatus.CANCEL.getKey())
                                    .set("update_by", userInfo.getUsr_id())
                                    .set("update_on", new Date()),
                            new Record().set("id",base_id)
                                    .set("persist_version",old_version)) == 1){
                    	int updateRows = CommonService.updateRows("oa_branch_payment_item",
                                new Record().set("service_status", WebConstant.PayStatus.CANCEL.getKey())
                                        .set("update_by", userInfo.getUsr_id())
                                        .set("update_on", new Date()),
                                new Record().set("base_id",base_id)
                                            .set("delete_flag", 0));
                    	//更新oa_branch_payment_item条数为0 或者 为 2 ,都通过
                    	log.debug("新oa_branch_payment_item条数为===="+updateRows);
                        if(updateRows == 0 || updateRows == 2){
                            if( CommonService.updateRows("oa_origin_data",
                                    new Record().set("process_status",WebConstant.OaProcessStatus.OA_TRADE_CANCEL.getKey())
                                            .set("lock_id",originId)
                                            .set("interface_status",WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_F.getKey())
                                            .set("interface_fb_code","P0098")
                                            .set("interface_fb_msg", feed_back),
                                    new Record().set("id",originId)) == 1){
                                return true;
                            }else{
                                log.error("oa_origin_data数据过期！");
                                return false;
                            }

                        }else{
                            log.error("oa_branch_payment_item数据过期！");
                            return false;
                        }

                    }else{
                        log.error("oa_branch_payment数据过期！");
                        return false;
                    }

                }
            });
            if(flag){
            	Record newOrigin = Db.findById("oa_origin_data", "id", originId);
                oaCallback.callback(newOrigin);
            }else{
                errmsg = "数据库执行失败";
                return errmsg;
            }

        }else{
            errmsg = "单据状态不符合作废要求!";
            return errmsg;
        }
        return errmsg;
    }

    public boolean pass(final Record record) {
        Record branchRecord = Db.findFirst(Db.getSql("branch_org_oa.findDetailByItem"), 1, record.get("id"));
        if (branchRecord == null){
            log.error("数据错误：branchRecord is null");
            return false;
        }

        final int status = branchRecord.getInt("service_status");
        if (status != WebConstant.PayStatus.INIT.getKey()) {
            log.error("单据状态有误!");
            return false;
        }

        final Long branchRecordId = branchRecord.getLong("id");
        String payCnaps = branchRecord.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = null;
        String bankSerialNumber = null;
        try {
            channelInter   = ChannelManager.getInter(payBankCode, "SinglePay");
            bankSerialNumber = ChannelManager.getSerianlNo(payBankCode);
        } catch (Exception e) {
            if (branchRecord != null) {
                Long baseId = branchRecord.getLong("base_id");
                Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);
                Record oaBranchPaymentCopy = new Record();
                oaBranchPaymentCopy.set("id", oaBranchPayment.getLong("id"));
                oaBranchPaymentCopy.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                Db.update("oa_branch_payment", oaBranchPaymentCopy);
                String errMsg = "直联银行未开通";
                Long originDateId = oaBranchPayment.getLong("ref_id");
                Record billRecordCopy = new Record();
                billRecordCopy.set("id", record.get("id"));
                billRecordCopy.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                billRecordCopy.set("feed_back", errMsg);

                Record billItemRecordCopy = new Record();
                billItemRecordCopy.set("id", branchRecord.getLong("id"));
                billItemRecordCopy.set("service_status", WebConstant.PayStatus.FAILD.getKey());
                billItemRecordCopy.set("feed_back", errMsg);
                Db.update("oa_branch_payment", billRecordCopy);
                Db.update("oa_branch_payment_item", billItemRecordCopy);
                Db.update(Db.getSql("origin_data_oa.updProcessStatus"), WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(),
                        errMsg, originDateId);
            }
            return true;
        }
        final int old_repeat_count = TypeUtils.castToInt(branchRecord.get("repeat_count"));

        branchRecord.set("source_ref", "oa_branch_payment_item");

        branchRecord.set("repeat_count", old_repeat_count + 1);
        branchRecord.set("bank_serial_number", bankSerialNumber);
        SysOaSinglePayInter sysInter = new SysOaSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(branchRecord);

        /**
         * 保存队列表记录
         */
        boolean save = Db.save("single_pay_instr_queue", instr);

        /**
         * 修改 oa_branch_payment_item 下拨指令的状态
         */
        int update_red = Db.update(Db.getSql("branch_org_oa.updBillById"), instr.getStr("bank_serial_number"),
                instr.getInt("repeat_count"), WebConstant.PayStatus.HANDLE.getKey(),
                instr.getStr("instruct_code"), branchRecordId,old_repeat_count,status);

        /**
         * 修改主单的状态 oa_branch_payment 未处理中
         */
        int update_rows = CommonService.updateRows("oa_branch_payment",
                new Record().set("service_status",WebConstant.BillStatus.PROCESSING.getKey()),
                new Record().set("id",record.getInt("id")));


        if(update_red == 1 && update_rows == 1){
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
            return true;
        }else{
            return false;
        }
    }
}
