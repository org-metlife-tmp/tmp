package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysNcSinglePayInter;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.webservice.nc.callback.NcCallback;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class HeadOrgNcService {

    private static final Log log = LogbackLog.getLog(HeadOrgNcService.class);

    public Page<Record> getTodoPage(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("head_org_nc.getTodoPage", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }




    public boolean pass(final Record record) {
        Record headRecord = null;
        try {
            headRecord = Db.findFirst(Db.getSql("head_org_nc.findHeadPayById"), record.get("id"));
            String payCnaps = headRecord.getStr("pay_bank_cnaps");
            String payBankCode = payCnaps.substring(0, 3);
            IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
            headRecord.set("source_ref", "nc_head_payment");
            final int old_repeat_count = TypeUtils.castToInt(headRecord.get("repeat_count"));
            headRecord.set("repeat_count", old_repeat_count+ 1);
            headRecord.set("bank_serial_number", ChannelManager.getSerianlNo(payBankCode));
            SysNcSinglePayInter sysInter = new SysNcSinglePayInter();
            sysInter.setChannelInter(channelInter);
            final Record instr = sysInter.genInstr(headRecord);

            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    boolean save = Db.save("single_pay_instr_queue", instr);
                    if (save) {
                        return Db.update(Db.getSql("head_org_nc.updBillById"), instr.getStr("bank_serial_number"),
                                instr.getInt("repeat_count"), WebConstant.BillStatus.PROCESSING.getKey(), instr.getStr("instruct_code")
                                ,new Date(), record.get("id"),old_repeat_count) == 1;
                    }
                    return save;
                }
            });
            if (flag) {
                QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
                ProductQueue productQueue = new ProductQueue(bean);
                new Thread(productQueue).start();
            } else {
                throw new DbProcessException("发送银行失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (headRecord != null) {
                String errMsg = null;
                if (e.getMessage() == null || e.getMessage().length() > 1000) {
                    errMsg = "发送银行失败！";
                } else {
                    errMsg = e.getMessage();
                }
                Long originDateId = headRecord.getLong("ref_id");
                Record billRecordCopy = new Record();
                billRecordCopy.set("id", record.get("id"));
                billRecordCopy.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                billRecordCopy.set("feed_back", errMsg);
                Db.update("nc_head_payment", billRecordCopy);
                Db.update(Db.getSql("origin_data_nc.updProcessStatus"), WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(),
                        errMsg, originDateId);
            }

        }

        return true;
    }




}

