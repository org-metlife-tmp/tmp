package com.qhjf.cfm.web.inter.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.ProcessEntrance;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;

public class SysTradeResultQueryInter implements ISysAtomicInterface {

    private static Logger log = LoggerFactory.getLogger(SysTradeResultQueryInter.class);
    private ISingleResultChannelInter channelInter;
    private Record instr;

    @Override
    public Record genInstr(Record record) {
        this.instr = new Record();
        Date date = new Date();
        instr.set("bank_serial_number", record.getStr("bank_serial_number"));
        instr.set("source_ref", record.getStr("source_ref"));
        instr.set("bill_id", record.getLong("id"));
        instr.set("process_bank_type", record.getStr("process_bank_type"));
        instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date, DateKit.timeStampPattern)));
        return this.instr;
    }

    @Override
    public Record getInstr() {
        return this.instr;
    }

    public Record updateBillStatus(int status, Record billRecord) {
        if (status == 1) {
            billRecord.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
        } else {
            billRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey());
        }
        return billRecord;
    }

    @Override
    public void callBack(String jsonStr) throws Exception {
        log.debug("查询交易状态指令回写开始");
        Db.delete("trade_result_query_instr_queue_lock", instr);
        JSONObject json = JSONObject.parseObject(jsonStr);
        json.put("bank_serial_number", this.getInstr().getStr("bank_serial_number"));
        final Record parseRecord = channelInter.parseResult(json.toJSONString());
        final int status = parseRecord.getInt("status");
        if (status == WebConstant.PayStatus.SUCCESS.getKey() || status == WebConstant.PayStatus.FAILD.getKey()) {
            final Record instrRecord = Db.findById("single_pay_instr_queue", "id", instr.getLong("bill_id"));
            if (instrRecord == null) {
                throw new Exception("发送指令不存在");
            }
            final String source_ref = instrRecord.getStr("source_ref");
            final String primaryKey = SysInterManager.getSourceRefPrimaryKey(source_ref);
            final Record billRecord = Db.findById(instrRecord.getStr("source_ref"), primaryKey, instrRecord.getLong("bill_id"));
            if (billRecord == null) {
                throw new Exception("单据不存在");
            }

            if ("oa_head_payment".equals(instrRecord.getStr("source_ref")) ||
                    "oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))) {
                oaCallBack(billRecord, instrRecord, status, parseRecord);
            } else {

                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {

                        int persist_version = TypeUtils.castToInt(billRecord.get("persist_version"));
                        Record bill_setRecord = new Record().set("persist_version", persist_version + 1);
                        Record bill_whereRecord = new Record().set(primaryKey, billRecord.get(primaryKey))
                                .set("persist_version", persist_version);

                        Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date()); //添加初始反馈时间;

                        Record instr_whereRecord = new Record().set("id", instrRecord.getLong("id"))
                                .set("status", WebConstant.PayStatus.HANDLE.getKey());

                        String statusField = SysInterManager.getStatusFiled(source_ref);
                        Integer statusEnum = null;
                        if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                            statusEnum = SysInterManager.getSuccessStatusEnum(source_ref);
                            bill_setRecord.set(statusField, statusEnum);
                            bill_setRecord.set("feed_back", "success");
                        } else {
                            statusEnum = SysInterManager.getFailStatusEnum(source_ref);
                            bill_setRecord.set(statusField, statusEnum);
                            bill_setRecord.set("feed_back", parseRecord.getStr("message"));
                        }

                        if (CommonService.updateRows(source_ref, bill_setRecord, bill_whereRecord) == 1) { //修改单据状态
                            return CommonService.updateRows("single_pay_instr_queue", instr_setRecord, instr_whereRecord) == 1;
                        } else {
                            log.error("数据过期！");
                            return false;
                        }
                    }
                });
                if (!flag) {
                    log.error("修改单据状态失败！");
                }

            }

        }
        log.debug("查询交易状态指令回写结束");
    }

    @Override
    public void setChannelInter(IChannelInter channelInter) {
        this.channelInter = (ISingleResultChannelInter) channelInter;
    }

    @Override
    public IChannelInter getChannelInter() {
        return this.channelInter;
    }

    @Override
    public void callBack(Exception e) throws Exception {
        Db.delete("trade_result_query_instr_queue_lock", instr);
    }

    private void oaCallBack(final Record billRecord, final Record instrRecord, final int status, final Record parseRecord) throws Exception {

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date()); //添加初始反馈时间;;
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
                                        new Record().set("id", baseId).set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
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

    }

    private void sendBanchPayment(final Record record)  {
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
                                    .set("repeat_count", oldRepearCount + 1),
                            new Record().set("id", branchRecord.get("id"))
                                    .set("service_status", WebConstant.PayStatus.INIT.getKey()).set("repeat_count", oldRepearCount));
                    if (changeCount == 1) {
                        Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                        Long originDataId = oaBranchPayment.getLong("ref_id");
                        if (CommonService.updateRows("oa_branch_payment",
                                new Record().set("service_status", WebConstant.BillStatus.FAILED.getKey()),
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
            log.debug("sysInter :"+sysInter);
            log.debug("instr :"+instr);
            log.debug("instr'id is:"+instr.get("id"));
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
            log.error("发送失败！");
        }
    }
}
