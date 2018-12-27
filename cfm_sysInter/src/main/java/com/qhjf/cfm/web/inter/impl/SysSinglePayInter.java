package com.qhjf.cfm.web.inter.impl;

import java.sql.SQLException;
import java.util.Date;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.IAtom;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.cmbc.CmbcChannel;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;

public class SysSinglePayInter implements ISysAtomicInterface {

    private static Logger log = LoggerFactory.getLogger(SysSinglePayInter.class);
    private ISingleResultChannelInter channelInter;
    private Record instr;


    @Override
    public Record genInstr(Record record) {
        this.instr = new Record();
        String payCnaps = record.getStr("pay_bank_cnaps");
        String recvCnaps = record.getStr("recv_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        String recvBankCode = recvCnaps.substring(0, 3);
        int isCrossBank = 0;
        if (!payBankCode.equals(recvBankCode)) {
            isCrossBank = 1;
        }
		/*String bankSerialNumber = record.getStr("bank_serial_number");
		if(bankSerialNumber == null || bankSerialNumber.length() == 0){
			bankSerialNumber = CmbcChannel.getInstance().getSerialnoGenTool().next();
		}*/
        String instructCode = record.getStr("instruct_code");
        if (instructCode == null || instructCode.length() == 0) {
            instructCode = RedisSericalnoGenTool.genShortSerial();
        }
        //String bankSerialNumber = CmbcChannel.getInstance().getSerialnoGenTool().next();
        instr.set("bank_serial_number", record.getStr("bank_serial_number"));
        instr.set("repeat_count", record.getInt("repeat_count"));
        instr.set("source_ref", record.get("source_ref"));
        instr.set("bill_id", record.getLong("id"));
        instr.set("pay_account_no", record.getStr("pay_account_no"));
        instr.set("pay_account_name", record.getStr("pay_account_name"));
        instr.set("pay_account_cur", record.getStr("pay_account_cur"));
        instr.set("pay_account_bank", record.getStr("pay_account_bank"));
        instr.set("pay_bank_cnaps", record.getStr("pay_bank_cnaps"));
        instr.set("pay_bank_prov", record.getStr("pay_bank_prov"));
        instr.set("pay_bank_city", record.getStr("pay_bank_city"));
        instr.set("recv_account_no", record.getStr("recv_account_no"));
        instr.set("recv_account_name", record.getStr("recv_account_name"));
        instr.set("recv_account_cur", record.getStr("recv_account_cur"));
        instr.set("recv_account_bank", record.getStr("recv_account_bank"));
        instr.set("recv_bank_cnaps", record.getStr("recv_bank_cnaps"));
        instr.set("recv_bank_prov", record.getStr("recv_bank_prov"));
        instr.set("recv_bank_city", record.getStr("recv_bank_city"));
        instr.set("payment_amount", record.getBigDecimal("payment_amount"));
        instr.set("process_bank_type", record.getStr("process_bank_type"));
        instr.set("is_cross_bank", isCrossBank);
        instr.set("status", 3);
        instr.set("instruct_code", instructCode);
        instr.set("summary", record.getStr("payment_summary"));
        instr.set("trade_date", new Date());
        instr.set("init_send_time", new Date());
        return this.instr;
    }

    @Override
    public Record getInstr() {
        return this.instr;
    }


    public void setInnerInstr(Record record) {
        this.instr = record;
    }

    @Override
    public void callBack(String jsonStr) throws Exception {
        log.debug("交易回写开始");
        final Record parseRecord = channelInter.parseResult(jsonStr);
        final int status = parseRecord.getInt("status");
        if (status == WebConstant.PayStatus.SUCCESS.getKey() || status == WebConstant.PayStatus.FAILD.getKey()) {
            final Record instrRecord = Db.findById("single_pay_instr_queue", "id", instr.getLong("id"));
            if (instrRecord == null) {
                throw new Exception("发送指令不存在");
            }
            final String source_ref = instrRecord.getStr("source_ref");
            final String primaryKey = SysInterManager.getSourceRefPrimaryKey(source_ref);
            final Record billRecord = Db.findById(source_ref, primaryKey, instrRecord.getLong("bill_id"));
            if (billRecord == null) {
                throw new Exception("单据不存在");
            }

            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    int persist_version = TypeUtils.castToInt(billRecord.get("persist_version"));
                    Record bill_setRecord = new Record().set("persist_version", persist_version + 1);
                    Record bill_whereRecord = new Record().set(primaryKey, billRecord.get(primaryKey))
                            .set("persist_version", persist_version);

                    Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date()); //添加初始反馈时间;;
                    Record instr_whereRecord = new Record().set("id", instr.getLong("id"))
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
                log.error("回写更新数据库失败！");
            }
        } else {

            Record setRecord = new Record();
            setRecord.set("init_resp_time", new Date());

            if (CommonService.updateRows("single_pay_instr_queue",
                    new Record().set("init_resp_time", new Date()),
                    new Record().set("id", instr.getLong("id"))
                            .set("status", WebConstant.PayStatus.HANDLE.getKey())) != 1) {
                log.error("数据过期，修改指令反馈时间失败！");
            }
        }
        log.debug("交易回写结束");
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
    public void callBack(final Exception e) throws Exception {
        /**
         * 如果是底层抛出的BankInterfaceException ，明确为失败
         * 如果是其他的异常，打印日志，不进行处理
         */
        if (e instanceof BankInterfaceException) {
            final String source_ref = instr.getStr("source_ref");
            final String primaryKey = SysInterManager.getSourceRefPrimaryKey(source_ref);

            final Record billRecord = Db.findById(source_ref, primaryKey, instr.getLong("bill_id"));
            if (billRecord == null) {
                throw new Exception("单据不存在");
            }

            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    int persist_version = TypeUtils.castToInt(billRecord.get("persist_version"));
                    Record bill_setRecord = new Record().set("persist_version", persist_version + 1);
                    Record bill_whereRecord = new Record().set(primaryKey, billRecord.get(primaryKey))
                            .set("persist_version", persist_version);

                    Record instr_setRecord = new Record().set("status", WebConstant.PayStatus.FAILD.getKey())
                            .set("init_resp_time", new Date()); //添加初始反馈时间;;
                    Record instr_whereRecord = new Record().set("id", instr.getLong("id"))
                            .set("status", WebConstant.PayStatus.HANDLE.getKey());

                    String statusField = SysInterManager.getStatusFiled(source_ref);
                    Integer statusEnum = SysInterManager.getFailStatusEnum(source_ref);

                    String errMsg = e.getMessage();
                    if (errMsg == null || errMsg.length() > 500) {
                        errMsg = "发送银行失败";
                    }

                    bill_setRecord.set(statusField, statusEnum);
                    bill_setRecord.set("feed_back", errMsg);

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
        } else {
            log.error(e.getMessage());
        }


    }


}
