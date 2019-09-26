package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.*;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.queue.RecvGroupCounterConfirmQueue;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.*;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.*;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 柜面收 团单
 *
 * @author pc_liweibing
 */
public class RecvGroupCounterService {

    private final static Log logger = LogbackLog.getLog(RecvGroupCounterService.class);
    private RecvCounterRemoteCall recvCounter = new RecvCounterRemoteCall();

    public void add(final Record record, final UserInfo userInfo, final UodpInfo curUodp) throws ReqDataException {
        int thirdPayment = TypeUtils.castToInt(record.get("third_payment"));
        if (thirdPayment == 1) {
            if (StringUtils.isEmpty(record.getStr("payer_relation_insured")) || StringUtils.isEmpty(record.getStr("pay_reason"))) {
                throw new ReqDataException("请补充完整与投保人关系和代缴费原因!");
            }
        }
        // 是否包含中文
        String currency = TypeUtils.castToString(record.get("currency"));
        boolean flagcur = StringKit.isContainChina(currency);
        if(!flagcur) {
            Record currencyRec = Db.findById("currency", "id", TypeUtils.castToString(record.get("currency")));
            currency = currencyRec.getStr("name");
            record.set("currency", currency);
        }
        int useFunds = TypeUtils.castToInt(record.get("use_funds"));
        useFunds(useFunds);
        //对所有字段求md5做校验
        String bill_org_id = TypeUtils.castToString(record.get("bill_org_id"));
        String recv_org_id = String.valueOf(curUodp.getOrg_id());
        String recv_date = TypeUtils.castToString(record.get("recv_date"));
        String recv_mode = TypeUtils.castToString(record.get("recv_mode"));
        String bill_status = TypeUtils.castToString(record.get("bill_status"));
        String bill_number = TypeUtils.castToString(record.get("bill_number"));
        String bill_date = TypeUtils.castToString(record.get("bill_date"));
        String recv_bank_name = TypeUtils.castToString(record.get("recv_bank_name"));
        String recv_acc_no = TypeUtils.castToString(record.get("recv_acc_no"));
        String use_funds = TypeUtils.castToString(record.get("use_funds"));
        String consumer_bank_name = TypeUtils.castToString(record.get("consumer_bank_name"));
        String consumer_acc_no = TypeUtils.castToString(record.get("consumer_acc_no"));
        String consumer_accname = TypeUtils.castToString(record.get("consumer_accname"));
        String consumer_no = TypeUtils.castToString(record.get("consumer_no"));
        String consumer_acc_name = TypeUtils.castToString(record.get("consumer_acc_name"));
        String preinsure_bill_no = TypeUtils.castToString(record.get("preinsure_bill_no"));
        String insure_bill_no = TypeUtils.castToString(record.get("insure_bill_no"));
        String batch_no = TypeUtils.castToString(record.get("batch_no"));
        String insure_name = TypeUtils.castToString(record.get("insure_name"));
        String insure_acc_no = TypeUtils.castToString(record.get("insure_acc_no"));
        String business_acc = TypeUtils.castToString(record.get("business_acc"));
        String business_acc_no = TypeUtils.castToString(record.get("business_acc_no"));
        String amount = TypeUtils.castToString(record.get("amount"));
        String third_payment = TypeUtils.castToString(record.get("third_payment"));
        String payer = TypeUtils.castToString(record.get("payer"));
        String pay_code = TypeUtils.castToString(record.get("pay_code"));
        String payer_relation_insured = TypeUtils.castToString(record.get("payer_relation_insured"));
        String pay_reason = TypeUtils.castToString(record.get("pay_reason"));
        String agent_com = TypeUtils.castToString(record.get("agent_com"));

        //判断是否有重复票据编号
        if(bill_number != null){
            Record findBypjbh = Db.findFirst(Db.getSql("recv_counter.selbillnum"),bill_number);
            if(findBypjbh != null){
                throw new ReqDataException("待确认的保单票据票号已存在，请重新输入!") ;
            }

        }

        StringBuilder sb = new StringBuilder();
        sb.append(recv_date).append(bill_org_id).append(recv_org_id).append(recv_date).append(currency).append(recv_mode).
                append(bill_status).append(bill_number).append(bill_date).append(recv_bank_name).append(recv_acc_no).append(use_funds).append(consumer_bank_name).
                append(consumer_acc_no).append(consumer_accname).append(consumer_no).append(consumer_acc_name).append(preinsure_bill_no).append(insure_bill_no).append(batch_no).
                append(insure_name).append(insure_acc_no).append(business_acc).append(business_acc_no).append(amount).append(third_payment).append(payer).
                append(pay_code).append(payer_relation_insured).append(pay_reason).append(agent_com);
        //防重校验
        final String md5String = MD5Kit.string2MD5(sb.toString());
        Record findById = Db.findById("recv_counter_bill", "md5", md5String);
        logger.info(record.getColumnValues().toString());
        if(findById != null) {
            logger.error("====录入的团单与库中数据重复了===="+insure_bill_no);
            throw new ReqDataException("团单保单重复");
        }
        final Record insertRecord = new Record();
        //资金流水号和资金反冲流水号 TMP+年份+12位流水号。如：2019000000201033 
        String zj_flow_number = RedisSericalnoGenTool.gmszjflownum("TMP"+DateKit.toStr(new Date(), "yyyy"));
        String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.GMSTD);
        String agentCom = TypeUtils.castToString(record.get("agent_com"));
        if(StringUtils.isNotBlank(agentCom)){
            if("Y".equals(agentCom)){
                agentCom = "1";
            }else if("N".equals(agentCom)){
                agentCom = "0";
            }else{
                agentCom = null;
            }
        }
        String bill_status2 = TypeUtils.castToString(record.get("bill_status"));
        if(StringUtils.isNotBlank(bill_status2)){
            if( "已到账".equals(bill_status2)){
                bill_status2 = "0";
            }else if("已退票".equals(bill_status2)){
                bill_status2 = "1";
            }
        }

        insertRecord.set("bill_type", WebConstant.SftRecvType.TDSK.getKey())
                .set("md5", md5String)
                .set("bill_org_id", record.get("bill_org_id"))    //保单机构id
                .set("recv_org_id", curUodp.getOrg_id())        //收款机构id 当前登录人所属机构
                .set("source_sys", "1")        //核心系统0:LA 1:EBS
                .set("recv_date", record.get("recv_date"))        //收款日期
                .set("batch_process_no", record.get("batch_process_no"))    //批处理号
                .set("currency", record.get("currency"))
                .set("recv_mode", record.get("recv_mode"))        //收款方式 1:现金解款单、2:支票、3:网银汇款
                .set("bill_status", bill_status2)            //票据状态 0:已到账、1:已退票
                .set("bill_number", record.get("bill_number"))        //票据编号
                .set("bill_date", record.get("bill_date"))    //票据日期
                .set("recv_bank_name", record.get("recv_bank_name"))    //收款银行
                .set("recv_acc_no", record.get("recv_acc_no"))        //收款账号
                .set("use_funds", TypeUtils.castToInt(record.get("use_funds")))        //资金用途 0:客户账户、1:新单签发、2:保全收费、3:定期结算收费、4:续期收费、5:不定期收费
                .set("consumer_bank_name", record.get("consumer_bank_name"))        //客户银行
                .set("consumer_acc_no", record.get("consumer_acc_no"))        //客户账号
                .set("consumer_accname", record.get("consumer_accname"))        //客户账号户名
                .set("consumer_no", record.get("consumer_no"))        //客户号
                .set("consumer_acc_name", record.get("consumer_acc_name"))    //客户名称
                .set("preinsure_bill_no", record.get("preinsure_bill_no"))        //投保单号
                .set("insure_bill_no", record.get("insure_bill_no"))        //保单号/团体保单号
                .set("batch_no", record.get("batch_no"))                //批单号
                .set("insure_name", record.get("insure_name"))        //投保人/投保单位名称
                .set("insure_acc_no", record.get("insure_acc_no"))        //投保人客户号
                .set("business_acc", record.get("business_acc"))                //业务所属客户
                .set("business_acc_no", record.get("business_acc_no"))        //业务所属客户号
                .set("amount", TypeUtils.castToBigDecimal(record.get("amount")))            //金额
                .set("third_payment", record.get("third_payment"))        //第三方缴费
                .set("payer", record.get("payer"))                        //缴费人
                .set("pay_code", record.get("pay_code"))                //缴费编码
                .set("payer_relation_insured", record.get("payer_relation_insured"))                //与投保人关系
                .set("pay_reason", record.get("pay_reason"))                //代缴费原因
                .set("attachment_count", record.get("attachment_count"))        //附件数量
                .set("wait_match_flag", record.get("wait_match_flag"))
                .set("wait_match_id", record.get("wait_match_id"))
                .set("bussiness_no", record.get("bussiness_no"))
                .set("zj_flow_number", zj_flow_number)            //资金流水号
                .set("agent_com", agentCom)            //是否为中介业务 0: 否， 1:是
                .set("pay_status", WebConstant.SftRecvCounterPayStatus.QR.getKey())
                .set("service_serial_number", serviceSerialNumber)
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("create_on", new Date())
                .set("create_by", userInfo.getUsr_id())
                .set("create_user_name", userInfo.getName())
                .set("update_on", new Date())
                .set("update_by", userInfo.getUsr_id())
                .set("delete_flag", 0)
                .set("persist_version", 0);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                List<Object> list = record.get("files");
                boolean save = Db.save("recv_counter_bill", insertRecord);
                if (save) {
                    // 保存附件
                    if (list != null && list.size() > 0) {
                        CommonService.saveFileRef(WebConstant.MajorBizType.GMSTD.getKey(), TypeUtils.castToLong(insertRecord.get("id")), list);
                    }
                } else {
                    logger.error("===此条团单保存失败===" + record.get("insure_bill_no"));
                    return false;
                }

                if(null !=  record.get("wait_match_flag")&& org.apache.commons.lang3.StringUtils.isNotBlank(record.getStr("wait_match_flag"))
                        && 1 == record.getInt("wait_match_flag")) {
                    logger.info("====来源未匹配数据,更新匹配主表====");
                    Integer wait_match_id = record.getInt("wait_match_id");
                    boolean updateMatch = CommonService.update("recv_counter_match",
                            new Record().set("match_on", new Date())
                                    .set("match_status", WebConstant.SftRecvCounterMatchStatus.YPP.getKey())
                                    .set("match_by",userInfo.getUsr_id())
                                    .set("match_user_name", userInfo.getName()),
                            new Record().set("id", wait_match_id));
                    logger.info("====更新recv_counter_match数据===="+wait_match_id);
                    return updateMatch ;
                }

                return true;
            }
        });
        if (!flag) {
            throw new ReqDataException("团单新增失败!");
        }

        //已退款的新增不调用核心，只入库
        if (bill_status.equals("1")){
            return;
        }
        logger.info("====团单新增开启异步线程请求外部系统====");
        RecvGroupCounterConfirmQueue recvGroupCounterConfirmQueue = new RecvGroupCounterConfirmQueue();
        recvGroupCounterConfirmQueue.setUserInfo(userInfo);
        recvGroupCounterConfirmQueue.setCurUodp(curUodp);
        recvGroupCounterConfirmQueue.setRecord(insertRecord);
        Thread thread =  new Thread(recvGroupCounterConfirmQueue);
        thread.start();
        return ;

    }

    public List<Record> getBankcode(final Record record, final UodpInfo uodpInfo) throws ReqDataException {
        Long orgId = uodpInfo.getOrg_id();
        Record orgRecord = Db.findById("organization", "org_id", orgId);
        if (orgRecord == null) {
            throw new ReqDataException("当前登录人的机构信息未维护");
        }
        record.set("level_code", TypeUtils.castToString(orgRecord.get("level_code")));
        return Db.find(Db.getSqlPara("recv_group_counter.getBankcode", Kv.by("map", record.getColumns())));
    }

    /**
     * 撤销，只有当天且状态为已确认才可撤销
     *
     * @param record
     * @param userInfo
     * @throws ReqDataException
     */
    public void revoke(final Record record, final UserInfo userInfo, UodpInfo uodpInfo) throws ReqDataException {
        final Integer id = TypeUtils.castToInt(record.get("id"));
        final Record rec = Db.findById("recv_counter_bill", "id", id);
        if (null == rec) {
            throw new ReqDataException("此条数据已过期,请刷新页面");
        }
        long userId = userInfo.getUsr_id();
        if(rec.getLong("create_by") != userId){
            throw new ReqDataException("撤销人必须是当前数据录入人！");
        }

        //判断是否为当天且状态为已确认
        String date = DateKit.toStr(rec.getDate("create_on"), "yyyyMMdd");
        if (!DateKit.toStr(new Date(), "yyyyMMdd").equals(date)) {
            throw new ReqDataException("只有当天的数据才可撤销");
        }

        int status = rec.getInt("pay_status");
        if (status != WebConstant.SftRecvCounterPayStatus.QR.getKey()) {
            throw new ReqDataException("只能撤销已确认状态数据");
        }

        int serviceStatus = rec.getInt("service_status");
        if (serviceStatus!=WebConstant.BillStatus.SAVED.getKey() && serviceStatus!=WebConstant.BillStatus.REJECT.getKey()) {
            throw new ReqDataException("该数据可能已在审批中，不允许重复审批");
        }

        final int useFunds = TypeUtils.castToInt(rec.get("use_funds"));

        if(useFunds != WebConstant.SftRecvGroupCounterUseFunds.KHZH.getKey()){
            throw new ReqDataException("非客户账号类型不允许撤销");
        }

        // 走审批流
        List<Record> flows = null;
        try {
            flows = CommonService.displayPossibleWf(WebConstant.MajorBizType.GMS.getKey(),
                    uodpInfo.getOrg_id(), null);
        } catch (BusinessException e) {
            e.printStackTrace();
            logger.error("============获取柜面收团单审批流异常");
            throw new ReqDataException("柜面收团单撤销获取审批流异常,请联系管理员");
        }
        if (flows == null || flows.size() == 0) {
            logger.error("============未查询到柜面收团单审批流");
            throw new ReqDataException("柜面收团单撤销未查询到审批流,请联系管理员进行配置");
        }
        Record flow = flows.get(0);
        rec.set("define_id", flow.getLong("define_id"));
        WfRequestObj wfRequestObj = new WfRequestObj(WebConstant.MajorBizType.GMS, "recv_counter_bill",
                rec) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) {
                return null;
            }

            @Override
            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                return null;
            }

        };
        WorkflowProcessService workflowProcessService = new WorkflowProcessService();
        boolean submitFlowFlg;
        try {
            submitFlowFlg = workflowProcessService.startWorkflow(wfRequestObj, userInfo);
        } catch (WorkflowException e) {
            e.printStackTrace();
            logger.error("=========柜面收团单审批流失败");
            throw new ReqDataException("柜面收团单撤销提交审批流异常");
        }
    }

    /**
     * @param record
     * @param curUodp
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<Record> list(Record record, UodpInfo curUodp, int pageSize, int pageNum) {
            List<Integer> org_ids = new ArrayList<>();
            List<Record> find = Db.find(Db.getSql("pay_counter.getSonOrg"), curUodp.getOrg_id());
            for (int i = 0; i < find.size(); i++) {
                org_ids.add(find.get(i).getInt("org_id"));
            }
            record.set("org_ids", org_ids);

        SqlPara sqlPara = Db.getSqlPara("recv_group_counter.grouplist", Kv.by("map", record.getColumns()));
        Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
        return paginate;

    }


    /**
     * 查看详情
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public Record detail(Record record) throws ReqDataException {
        List<Object> list = record.get("files");
        Integer id = TypeUtils.castToInt(record.get("id"));
        Record rec = Db.findById("recv_counter_bill", "id", id);
        if (null == rec) {
            throw new ReqDataException("此条数据已过期,请刷新页面");
        }
        Record bankRec = Db.findById("const_bank_type", "code", TypeUtils.castToString(rec.get("consumer_bank_name")));
        if(bankRec != null){
            rec.set("display_name", TypeUtils.castToString(bankRec.get("name")));
            rec.set("name", TypeUtils.castToString(bankRec.get("name")));
        }
        return rec;
    }

    /**
     * 详情确认
     * @param record
     * @throws ReqDataException
     */
    public void detailconfirm(Record record) throws ReqDataException {
        Integer id = TypeUtils.castToInt(record.get("id"));
        Record rec = Db.findById("recv_counter_bill", "id", id);
        if (null == rec) {
            throw new ReqDataException("此条数据已过期,请刷新页面");
        }
        List<Object> list = record.get("files");
        // 删除附件
        CommonService.delFileRef(WebConstant.MajorBizType.GMSTD.getKey(), id);
        if (list != null && list.size() > 0) {
            // 保存附件
            CommonService.saveFileRef(WebConstant.MajorBizType.GMSTD.getKey(), id, list);
        }
    }

    /**
     * 非客户账号类型的搜索
     *
     * @param record
     * @throws ReqDataException
     */
    public Object customOtherList(Record record) throws ReqDataException {
        //KHZH(0, "0", "客户账户"), XDQF(1, "1", "新单签发"), BQSF(2, "2", "保全收费"),DQJSSF(3, "3", "定期结算收费"), XQSF(4, "4", "续期收费"), BDQSF(5, "5", "不定期收费");
        //"新单签发、保全收费、定期结算收费、续期收费、不定期收费"
        GroupBizPayQryReqBean groupBizPayQryReqBean = new GroupBizPayQryReqBean(
                TypeUtils.castToString(record.get("customerNo")),
                TypeUtils.castToString(record.get("customerName")),
                TypeUtils.castToString(record.get("preinsureBillNo")),
                TypeUtils.castToString(record.get("insureBillNo")),
                TypeUtils.castToString(record.get("bussinessNo")),
                WebConstant.SftRecvGroupCounterUseFunds.getSftRecvGroupCounterUseFundsByKey(TypeUtils.castToInt(record.get("use_funds"))).getKeyc()
        );
        GroupBizPayQryRespBean groupBizPayQryRespBean = recvCounter.ebsBizPayQry(groupBizPayQryReqBean);
        if ("SUCCESS".equals(groupBizPayQryRespBean.getResultCode())) {
            //返回成功
            return groupBizPayQryRespBean.getList();
        }else if("FAIL".equals(groupBizPayQryRespBean.getResultCode())){
            throw new ReqDataException(groupBizPayQryRespBean.getResultMsg());
        }else {
            throw new ReqDataException("未找到想要的数据");
        }
    }

    /**
     * 客户账号的搜索
     *
     * @param record
     * @throws ReqDataException
     */
    public Object customList(Record record) throws ReqDataException {
        //KHZH(0, "0", "客户账户"), XDQF(1, "1", "新单签发"), BQSF(2, "2", "保全收费"),DQJSSF(3, "3", "定期结算收费"), XQSF(4, "4", "续期收费"), BDQSF(5, "5", "不定期收费");
        //客户账号的查询
        GroupCustomerAccQryReqBean groupCustomerAccQryReqBean = new GroupCustomerAccQryReqBean
                (TypeUtils.castToString(record.get("customerNo")), TypeUtils.castToString(record.getStr("customerName")));
        GroupCustomerAccQryRespBean groupCustomerAccQryRespBean = recvCounter.ebsCustomerAccQry(groupCustomerAccQryReqBean);
        if ("SUCCESS".equals(groupCustomerAccQryRespBean.getResultCode())) {
            //返回成功
            return groupCustomerAccQryRespBean.getList();
        }else if("FAIL".equals(groupCustomerAccQryRespBean.getResultCode())){
            throw new ReqDataException(groupCustomerAccQryRespBean.getResultMsg());
        }else {
            throw new ReqDataException("未找到想要的数据");
        }
    }

    /**
     * 判断资金用途
     */
    public void useFunds(int useFunds) throws ReqDataException {
        WebConstant.SftRecvGroupCounterUseFunds sftRecvGroupCounterUseFunds = WebConstant.SftRecvGroupCounterUseFunds
                .getSftRecvGroupCounterUseFundsByKey(useFunds);
        if (sftRecvGroupCounterUseFunds == null) {
            throw new ReqDataException("未找到对应的资金用途!");
        }
    }

    /**
     * 开始调用 外部系统,进行撤销
     * @param record
     * @param userInfo
     * @return
     */
    public boolean hookPass(final Record record, final UserInfo userInfo) {

        final Integer id = TypeUtils.castToInt(record.get("id"));
        logger.info("====调用外部系统,id===="+id);
        final Record recv_counter_data = Db.findById("recv_counter_bill", "id", id);
        if(TypeUtils.castToInt(recv_counter_data.get("bill_type")) == WebConstant.SftRecvType.TDSK.getKey()) {
            logger.info("====团单收款====");
            int useFunds = TypeUtils.castToInt(recv_counter_data.get("use_funds"));
            if(useFunds != WebConstant.SftRecvGroupCounterUseFunds.KHZH.getKey()){
                return false;
            }
            final String zj_fc_flow_number = RedisSericalnoGenTool.gmszjflownum("TMP"+DateKit.toStr(new Date(), "yyyy"));
            //调用确认接口
            try{
                //客户账号
                GroupCustomerAccCancelReqBean groupCustomerAccCancelReqBean = new GroupCustomerAccCancelReqBean(
                        TypeUtils.castToString(recv_counter_data.get("zj_flow_number")),
                        zj_fc_flow_number
                );

                GroupCustomerAccCancelRespBean groupCustomerAccCancelRespBean = recvCounter.ebsCustomerAccCancel(groupCustomerAccCancelReqBean);
                if ("SUCCESS".equals(groupCustomerAccCancelRespBean.getResultCode())) {
                    //返回成功
                    boolean tx = Db.tx(new IAtom() {
                        @Override
                        public boolean run() throws SQLException {
                            boolean updateFlag = CommonService.update("recv_counter_bill",
                                    new Record().set("pay_status", WebConstant.SftRecvCounterPayStatus.CX.getKey()).set("update_on", new Date())
                                            .set("update_by", userInfo.getUsr_id()).set("cancel_status", 1),
                                    new Record().set("id", id));
                            if(!updateFlag) {
                                logger.error("====根据外部系统返回结果更新保单状态失败,更新数据id===="+id);
                                return false ;
                            }
                            if(1 == recv_counter_data.getInt("wait_match_flag")) {
                                logger.info("====来源未匹配数据,更新匹配主表====");
                                Integer wait_match_id = record.getInt("wait_match_id");
                                return CommonService.update("recv_counter_match",
                                        new Record().set("match_on", new Date())
                                                .set("match_status", WebConstant.SftRecvCounterMatchStatus.YCX.getKey())
                                                .set("match_by",userInfo.getUsr_id())
                                                .set("match_user_name", userInfo.getName()),
                                        new Record().set("id", wait_match_id));
                            }
                            return true;
                        }
                    });
                    if (!tx) {
                        logger.error("====撤销此条数据更新失败====");
                    }
                    return true;
                }else {
                    return CommonService.update("recv_counter_bill",
                            new Record().set("update_on", new Date()).set("update_by", userInfo.getUsr_id()).set("cancel_status", WebConstant.YesOrNo.NO.getKey())
                                    .set("cancel_msg", groupCustomerAccCancelRespBean.getResultMsg()),
                            new Record().set("id", id));
                }
            } catch (ReqDataException e) {
                e.printStackTrace();
                logger.error("====撤销此条团单数据更新失败====");
                CommonService.update("recv_counter_bill",
                        new Record().set("cancel_status", WebConstant.YesOrNo.NO.getKey()).set("cancel_msg", "调用外部系统撤销异常")
                                .set("update_on", new Date()).set("update_by", userInfo.getUsr_id()),
                        new Record().set("id", id));
                return false;
            }
        }else {
            logger.info("====个单收款====");
            //同步去请求外部系统,查看是否可以撤销
            RecvCounterRemoteCall recvCounterRemoteCall = new RecvCounterRemoteCall();
            PersonBillCancelReqBean personBillCancelReqBean = new PersonBillCancelReqBean(recv_counter_data.getStr("receipt"));
            final PersonBillCancelRespBean returnBean;
            try {
                returnBean = recvCounterRemoteCall.personCancelBill(personBillCancelReqBean);
            } catch (ReqDataException e) {
                e.printStackTrace();
                logger.error("====调用外部系统异常了====");
                CommonService.update("recv_counter_bill",
                        new Record().set("cancel_status", WebConstant.YesOrNo.NO.getKey()).set("cancel_msg", "调用外部系统撤销异常")
                                .set("update_on", new Date()).set("update_by", userInfo.getUsr_id()),
                        new Record().set("id", id));
                return true;
            }
            if (null == returnBean) {
                logger.error("====调用外部系统返回对象为空====");
                CommonService.update("recv_counter_bill",
                        new Record().set("cancel_status", WebConstant.YesOrNo.NO.getKey()).set("cancel_msg", "调用外部系统撤销返回结果为空")
                                .set("update_on", new Date()).set("update_by", userInfo.getUsr_id()),
                        new Record().set("id", id));
                return true;
            }
            final String errmess = returnBean.getErrmess();
            String status = returnBean.getStatus();
            logger.info("====接口返回状态===="+status+"====接口返回信息===="+errmess);

            //开始更新状态为 pay_status = 撤销

            boolean tx = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    Integer cancel_status = WebConstant.YesOrNo.NO.getKey();
                    Integer pay_status = WebConstant.SftRecvCounterPayStatus.QR.getKey();
                    if("SC".equalsIgnoreCase(returnBean.getStatus())) {
                        cancel_status = WebConstant.YesOrNo.YES.getKey() ;
                        pay_status = WebConstant.SftRecvCounterPayStatus.CX.getKey();
                    }
                    boolean update = CommonService.update("recv_counter_bill",
                            new Record().set("update_on", new Date()).set("update_by", userInfo.getUsr_id()).set("cancel_status", cancel_status)
                                    .set("cancel_msg", errmess).set("pay_status", pay_status),
                            new Record().set("id", id));
                    if(!update) {
                        logger.error("====根据外部系统返回结果更新保单状态失败,更新数据id===="+id);
                        return false ;
                    }
                    if(1 == recv_counter_data.getInt("wait_match_flag")) {
                        logger.info("====来源未匹配数据,更新匹配主表====");
                        Integer wait_match_id = record.getInt("wait_match_id");
                        return CommonService.update("recv_counter_match",
                                new Record().set("match_on", new Date())
                                        .set("match_status", WebConstant.SftRecvCounterMatchStatus.YCX.getKey())
                                        .set("match_by",userInfo.getUsr_id())
                                        .set("match_user_name", userInfo.getName()),
                                new Record().set("id", wait_match_id));
                    }
                    return true;
                }
            });
            if (!tx) {
                logger.error("====撤销此条数据更新失败====");
            }
            return true ;
        }
    }

    public boolean hookReject(Record record, UserInfo userInfo) {

        return true;
    }

}

