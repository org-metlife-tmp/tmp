package com.qhjf.cfm.web.inter.impl;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;

public class SysOaSinglePayInter extends SysSinglePayInter {

    private static Logger log = LoggerFactory.getLogger(SysOaSinglePayInter.class);

    public void callBack(String jsonStr) throws Exception {
        log.debug("交易回写开始");
        if(jsonStr == null || jsonStr.length() == 0){
            Record instr_setRecord = new Record().set("init_resp_time", new Date()); //添加初始反馈时间;;
            Record instr_whereRecord = new Record().set("id", super.getInstr().getLong("id"));
            CommonService.updateRows("single_pay_instr_queue", instr_setRecord, instr_whereRecord);
            log.error("交易返回报文为空,修改响应时间,不做其他处理");
            return;
        }
        final Record parseRecord = ((ISingleResultChannelInter) super.getChannelInter()).parseResult(jsonStr);
        final int status = parseRecord.getInt("status");
        if ((status == WebConstant.PayStatus.SUCCESS.getKey()) || status == WebConstant.PayStatus.FAILD.getKey()) {
            final Record instrRecord = Db.findById("single_pay_instr_queue", "id", super.getInstr().getLong("id"));
            if (instrRecord == null) {
                throw new Exception("发送指令不存在");
            }
            final Record billRecord = Db.findById(instrRecord.getStr("source_ref"), "id",
                    instrRecord.getLong("bill_id"));
            if (billRecord == null) {
                throw new Exception("单据不存在");
            }
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {

                    Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date());
                    ;
                    Record instr_whereRecord = new Record().set("id", instrRecord.getLong("id"));

                    Record bill_setRecord = new Record().set("persist_version", billRecord.getInt("persist_version") + 1);  //乐观锁机制
                    Record bill_whereRecord = new Record().set("id", billRecord.getLong("id"))
                            .set("persist_version", billRecord.getInt("persist_version"));
                    String statusField = SysInterManager.getStatusFiled(instrRecord.getStr("source_ref"));

                    if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                        bill_setRecord.set("feed_back", "success");
                        instr_setRecord.set("bank_err_msg", " ").set("bank_err_code", "");
                        if ("oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))) {
                            bill_setRecord.set("service_status", WebConstant.PayStatus.SUCCESS.getKey());
                            bill_whereRecord.set("service_status", WebConstant.PayStatus.HANDLE.getKey());
                            if (CommonService.updateRows("oa_branch_payment_item", bill_setRecord, bill_whereRecord) == 1) {
                                if (billRecord.getInt("item_type") == 2) { //返回的是分公司付款的指令状态，进行主单的更新
                                    Long baseId = billRecord.getLong("base_id");
                                    Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                                    Long originDataId = oaBranchPayment.getLong("ref_id");
                                    /**
                                     * 修改主单状态，原始表状态，并加入oa回调队列
                                     */
                                    if (CommonService.updateRows("oa_branch_payment",
                                            new Record().set("service_status", WebConstant.BillStatus.SUCCESS.getKey())
                                                    .set("feed_back", "success"),
                                            new Record().set("id", baseId)
                                                    .set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
                                        Db.update(Db.getSql("oa_interface.updOriginDataInterfaceStatus"),
                                                WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_S.getKey(), null, null,
                                                WebConstant.OaProcessStatus.OA_TRADE_SUCCESS.getKey(), originDataId);
                                        new OaCallback().callback(Db.findById("oa_origin_data", originDataId));
                                    }
                                } else {
                                    log.info("返回的是分公司付款，下拨指令的指令状态");
                                }
                            } else {
                                log.error("已进行过状态更新！");
                                return false;
                            }
                        } else if ("oa_head_payment".equals(instrRecord.getStr("source_ref"))) {
                            bill_setRecord.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
                            bill_setRecord.set("feed_back", "success");
                            bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());
                            if (CommonService.updateRows("oa_head_payment", bill_setRecord, bill_whereRecord) == 1) {
                                /**
                                 * 修改原始表状态，并加入oa回调队列
                                 */
                                Long originDataId = billRecord.getLong("ref_id");
                                Db.update(Db.getSql("oa_interface.updOriginDataInterfaceStatus"),
                                        WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_S.getKey(), null, null,
                                        WebConstant.OaProcessStatus.OA_TRADE_SUCCESS.getKey(), originDataId);
                                new OaCallback().callback(Db.findById("oa_origin_data", originDataId));
                            } else {
                                log.error("已进行过状态更新！");
                                return false;
                            }
                        } else {
                            log.error("source_ref is " + instrRecord.getStr("source_ref") + ", is error");
                            return false;
                        }

                    } else if (status == WebConstant.PayStatus.FAILD.getKey()) {
                        bill_setRecord.set("feed_back", parseRecord.getStr("message"));
                        instr_setRecord.set("bank_err_msg", parseRecord.getStr("message"));
                        if ("oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))) {
                            bill_setRecord.set("service_status", WebConstant.PayStatus.FAILD.getKey());
                            bill_whereRecord.set("service_status", WebConstant.PayStatus.HANDLE.getKey());
                            if (CommonService.updateRows("oa_branch_payment_item", bill_setRecord, bill_whereRecord) == 1) {
                                Long baseId = billRecord.getLong("base_id");
                                Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                                Long originDataId = oaBranchPayment.getLong("ref_id");
                                if (CommonService.updateRows("oa_branch_payment",
                                        new Record().set("service_status", WebConstant.BillStatus.FAILED.getKey())
                                                .set("feed_back", parseRecord.getStr("message")),
                                        new Record().set("id", baseId).set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
                                    Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                            WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), parseRecord.getStr("message"),
                                            parseRecord.getStr("message"), originDataId);
                                }
                            } else {
                                log.error("已进行过状态更新！");
                                return false;
                            }
                        } else if ("oa_head_payment".equals(instrRecord.getStr("source_ref"))) {
                            bill_setRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                            bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());

                            if (CommonService.updateRows("oa_head_payment", bill_setRecord, bill_whereRecord) == 1) {

                                Long originDataId = billRecord.getLong("ref_id");
                                /**
                                 * 修改原始表状态，并加入oa回调队列
                                 */
                                Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                        WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), parseRecord.getStr("message"),
                                        parseRecord.getStr("message"), originDataId);

                            } else {
                                log.error("已进行过状态更新！");
                                return false;
                            }
                        } else {
                            log.error("source_ref is " + instrRecord.getStr("source_ref") + ", is error");
                            return false;
                        }

                    }
                    //修改指令信息
                    return CommonService.update("single_pay_instr_queue", instr_setRecord, instr_whereRecord);

                }
            });
            if (flag) {
                if (status == WebConstant.PayStatus.SUCCESS.getKey()
                        && "oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))
                        && billRecord.getInt("item_type") == 1) { //oa_branch_payment_item 的下拨指令成功， 开始发送支付指令
                    sendBanchPayment(billRecord);
                }
            } else {
                log.error("修改单据状态失败！");
            }

        } else {
            Record setRecord = new Record();
            setRecord.set("init_resp_time", new Date());

            if (CommonService.updateRows("single_pay_instr_queue",
                    new Record().set("init_resp_time", new Date()),
                    new Record().set("id", super.getInstr().getLong("id"))
                            .set("status", WebConstant.PayStatus.HANDLE.getKey())) != 1) {
                log.error("数据过期，修改指令反馈时间失败！");
            }
        }
        log.debug("交易回写结束");
    }


    @Override
    public void callBack(final Exception e) throws Exception {
        /**
         * 如果是底层抛出的BankInterfaceException ，明确为失败
         * 如果是其他的异常，打印日志，不进行处理
         */
        if (e instanceof BankInterfaceException) {
            log.debug("super:" + super.getInstr().getStr("source_ref"));
            log.debug("this:" + this.getInstr().getStr("source_ref"));

            log.debug("super:" + super.getInstr().getLong("bill_id"));
            log.debug("this:" + this.getInstr().getLong("bill_id"));

            final String source_ref = this.getInstr().getStr("source_ref");
            final Record billRecord = Db.findById(source_ref, "id",
                    this.getInstr().getLong("bill_id"));
            if (billRecord == null) {
                throw new Exception("单据不存在");
            }

            final Record instr = this.getInstr();
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {


                    Record instr_setRecord = new Record().set("status", WebConstant.PayStatus.FAILD.getKey())
                            .set("init_resp_time", new Date()); // 修改指令的状态和响应时间
                    ;
                    Record instr_whereRecord = new Record().set("id", instr.getLong("id"));

                    Record bill_setRecord = new Record().set("persist_version", billRecord.getInt("persist_version") + 1);  //乐观锁机制
                    Record bill_whereRecord = new Record().set("id", billRecord.getLong("id"))
                            .set("persist_version", billRecord.getInt("persist_version"));

                    String statusField = SysInterManager.getStatusFiled(instr.getStr("source_ref"));
                    Integer statusEnum = SysInterManager.getFailStatusEnum(instr.getStr("source_ref"));

                    String errMsg = e.getMessage();
                    if (errMsg == null || errMsg.length() > 500) {
                        errMsg = "发送银行失败";
                    }
                    bill_setRecord.set(statusField, statusEnum).set("feed_back", errMsg); //修改单据的状态和错误信息
                    instr_setRecord.set("bank_err_msg", errMsg); // 修改指令的错误信息

                    if ("oa_branch_payment_item".equals(source_ref)) {
                        if (WebConstant.PayStatus.HANDLE.getKey() != billRecord.getInt("service_status")
                                && WebConstant.PayStatus.INIT.getKey() == billRecord.getInt("service_status")) {
                            log.info("单据状态有误:" + billRecord.getInt("service_status"));
                            return false;
                        }
                        bill_whereRecord.set(statusField, billRecord.getInt("service_status"));
                        if (CommonService.updateRows("oa_branch_payment_item", bill_setRecord, bill_whereRecord) == 1) {
                            Long baseId = billRecord.getLong("base_id");
                            Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                            Long originDataId = oaBranchPayment.getLong("ref_id");
                            if (CommonService.updateRows("oa_branch_payment",
                                    new Record().set("service_status", WebConstant.BillStatus.FAILED.getKey())
                                            .set("feed_back", errMsg),
                                    new Record().set("id", baseId).set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
                                Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                        WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), e.getMessage(),
                                        e.getMessage(), originDataId);
                            } else {
                                return false;
                            }
                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    } else if ("oa_head_payment".equals(source_ref)) {
                        if (WebConstant.BillStatus.PROCESSING.getKey() != billRecord.getInt("service_status")
                                && WebConstant.BillStatus.PASS.getKey() == billRecord.getInt("service_status")) {
                            log.info("单据状态有误！");
                            return false;
                        }
                        bill_whereRecord.set(statusField, billRecord.getInt("service_status"));
                        if (CommonService.updateRows("oa_head_payment", bill_setRecord, bill_whereRecord) == 1) {

                            Long originDataId = billRecord.getLong("ref_id");
                            /**
                             * 修改原始表状态，并加入oa回调队列
                             */
                            Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), e.getMessage(),
                                    e.getMessage(), originDataId);
                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    }
                    //修改指令信息
                    return CommonService.update("single_pay_instr_queue", instr_setRecord, instr_whereRecord);
                }

            });
            if (!flag) {
                log.error("修改单据状态失败！");
            }
        } else {
            log.error(e.getMessage());
        }
    }


    private void sendBanchPayment(final Record record) {
        final Record branchRecord = Db.findFirst(Db.getSql("branch_org_oa.findDetailByItem"), 2, record.get("base_id"));
        final int oldRepearCount = branchRecord.getInt("repeat_count");

        String payCnaps = branchRecord.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = null;
        String bankSerialNumber = null;
        try {
            channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
            bankSerialNumber = ChannelManager.getSerianlNo(payBankCode);
        } catch (Exception e) {
            e.printStackTrace();
            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    Long baseId = branchRecord.getLong("base_id");
                    int changeCount = CommonService.updateRows("oa_branch_payment_item",
                            new Record().set("service_status", WebConstant.PayStatus.FAILD.getKey())
                                    .set("repeat_count", oldRepearCount + 1).set("feed_back", "银行渠道不可用"),
                            new Record().set("id", branchRecord.get("id"))
                                    .set("service_status", WebConstant.PayStatus.INIT.getKey()).set("repeat_count", oldRepearCount));
                    if (changeCount == 1) {
                        Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                        Long originDataId = oaBranchPayment.getLong("ref_id");
                        if (CommonService.updateRows("oa_branch_payment",
                                new Record().set("service_status", WebConstant.BillStatus.FAILED.getKey())
                                        .set("feed_back", "银行渠道不可用"),
                                new Record().set("id", baseId).set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
                            Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), "银行渠道不可用",
                                    "银行渠道不可用", originDataId);
                        }

                    } else {
                        log.error("数据错误！");
                        return false;
                    }
                    return true;
                }
            });
        }

        branchRecord.set("source_ref", "oa_branch_payment_item");
        branchRecord.set("repeat_count", oldRepearCount + 1);
        branchRecord.set("bank_serial_number", bankSerialNumber);
        SysOaSinglePayInter sysInter = new SysOaSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(branchRecord);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("single_pay_instr_queue", instr);
                if (save) {
                    int result = Db.update(Db.getSql("branch_org_oa.updBillById"),
                            instr.getStr("bank_serial_number"), instr.getInt("repeat_count"),
                            WebConstant.PayStatus.HANDLE.getKey(), instr.getStr("instruct_code"),
                            branchRecord.get("id"), oldRepearCount, branchRecord.getInt("service_status"));
                    return result == 1;
                }
                return save;
            }
        });
        // 存储成功， 添加到队列
        if (flag) {
            log.debug("sysInter :" + sysInter);
            log.debug("instr :" + instr);
            log.debug("instr'id is:" + instr.get("id"));
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
            log.error("发送失败！");
        }
    }

}
