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
import com.qhjf.cfm.web.webservice.nc.callback.NcCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;

public class SysNcSinglePayInter extends SysSinglePayInter {

    private static Logger log = LoggerFactory.getLogger(SysNcSinglePayInter.class);

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
                       if ("nc_head_payment".equals(instrRecord.getStr("source_ref"))) {
                            bill_setRecord.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
                            bill_setRecord.set("feed_back", "success");
                            bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());
                            if (CommonService.updateRows("nc_head_payment", bill_setRecord, bill_whereRecord) == 1) {
                                /**
                                 * 修改原始表状态，并加入oa回调队列
                                 */
                                Long originDataId = billRecord.getLong("ref_id");
                                Db.update(Db.getSql("nc_interface.updOriginDataInterfaceStatus"),
                                        WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_S.getKey(), null, null,
                                        WebConstant.OaProcessStatus.OA_TRADE_SUCCESS.getKey(), originDataId);
                                new NcCallback().callback(Db.findById("nc_origin_data", originDataId), parseRecord);
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
                        if ("nc_head_payment".equals(instrRecord.getStr("source_ref"))) {
                            bill_setRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                            bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());

                            if (CommonService.updateRows("nc_head_payment", bill_setRecord, bill_whereRecord) == 1) {

                                Long originDataId = billRecord.getLong("ref_id");
                                /**
                                 * 修改原始表状态，并加入nc回调队列
                                 */
                                Db.update(Db.getSql("nc_interface.updOriginDataProcessStatus"),
                                        WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), parseRecord.getStr("message"),
                                        parseRecord.getStr("message"), originDataId);
                                Db.update(Db.getSql("nc_interface.updOriginDataInterfaceStatus"),
                                        WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_F.getKey(), "P00098", parseRecord.getStr("message"),
                                        WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), originDataId);
                                new NcCallback().callback(Db.findById("nc_origin_data", originDataId),null);
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

                    if ("nc_head_payment".equals(source_ref)) {
                        if (WebConstant.BillStatus.PROCESSING.getKey() != billRecord.getInt("service_status")
                                && WebConstant.BillStatus.PASS.getKey() == billRecord.getInt("service_status")) {
                            log.info("单据状态有误！");
                            return false;
                        }
                        bill_whereRecord.set(statusField, billRecord.getInt("service_status"));
                        if (CommonService.updateRows("nc_head_payment", bill_setRecord, bill_whereRecord) == 1) {

                            Long originDataId = billRecord.getLong("ref_id");
                            Db.update(Db.getSql("nc_interface.updOriginDataProcessStatus"),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), e.getMessage(),
                                    e.getMessage(), originDataId);
                            Db.update(Db.getSql("nc_interface.updOriginDataInterfaceStatus"),
                                    WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_F.getKey(), "P00098", e.getMessage(),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), originDataId);
                            final Record  ncRecord = Db.findById("nc_origin_data", "id",
                                    billRecord.getInt("ref_id"));
                            if(ncRecord!=null){
                                new NcCallback().callback(ncRecord,null);
                            }
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

}
