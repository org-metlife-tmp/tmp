package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysSinglePayInter;

public class CollectManageService {

    public void cancel(Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int oldVersion = TypeUtils.castToInt(record.get("persist_version"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean f = CommonService.update("collect_topic",
                        new Record()
                                .set("service_status", WebConstant.BillStatus.CANCEL.getKey())
                                .set("persist_version", oldVersion + 1),
                        new Record()
                                .set("id", id)
                                .set("persist_version", oldVersion));
                if (f) {
                    return CommonService.updateQuartz(WebConstant.YesOrNo.NO, WebConstant.CronTaskGroup.COLLECT, id);
                }
                return f;
            }
        });
        if (!flag) {
            throw new DbProcessException("归集通单据作废失败！");
        }
    }

    public List<Record> list(Record record) {
        SqlPara sqlPara = Db.getSqlPara("collect_manage.list", Kv.by("map", record.getColumns()));
        List<Record> list = Db.find(sqlPara);

        GjtCommonService.buildTopicList(list);
        return list;
    }

    public Record setstate(Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));

        Record topic = Db.findById("collect_topic", "id", id);

        if (topic == null
                || TypeUtils.castToInt(topic.get("delete_flag")) == WebConstant.YesOrNo.YES.getKey()
                || TypeUtils.castToInt(topic.get("service_status")) != WebConstant.BillStatus.PASS.getKey()) {
            throw new ReqDataException("请求单据不存在！");
        }

        int is_activity = TypeUtils.castToInt(topic.get("is_activity"));

        is_activity = is_activity == WebConstant.YesOrNo.NO.getKey() ? WebConstant.YesOrNo.YES.getKey() : WebConstant.YesOrNo.NO.getKey();

        final int finalIs_activity = is_activity;
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean f = CommonService.update("collect_topic",
                        new Record()
                                .set("is_activity", finalIs_activity)
                                .set("persist_version", version + 1),
                        new Record()
                                .set("id", id)
                                .set("persist_version", version));
                if (f) {
                    if (finalIs_activity == WebConstant.YesOrNo.YES.getKey()) {
                        return CommonService.updateQuartz(WebConstant.YesOrNo.YES, WebConstant.CronTaskGroup.COLLECT, id);
                    } else {
                        return CommonService.updateQuartz(WebConstant.YesOrNo.NO, WebConstant.CronTaskGroup.COLLECT, id);
                    }
                }
                return f;
            }
        });

        if (!flag) {
            throw new DbProcessException("修改单据状态失败！");
        }
        return Db.findById("collect_topic", "id", id);
    }

    public Record detail(Record record) {
        return null;
    }

    public void sendPayList(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        for (Long idStr : ids) {
            try {
                sendPayDetail(idStr);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }

    }

    private void sendPayDetail(final Long id) throws Exception {
        Record record = Db.findById("collect_execute_instruction", "id", id);
        if (record == null) {
            throw new Exception("单据id不存在：" + id);
        }
        final Integer status = record.getInt("collect_status");
        if (status != WebConstant.CollOrPoolRunStatus.FAILED.getKey()) {
            throw new Exception("单据状态有误!:" + id);
        }
        String payCnaps = record.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
        record.set("source_ref", "collect_execute_instruction");
        final int old_repeat_count  = TypeUtils.castToInt(record.get("repeat_count"));  //发送之前的repeat_count
        record.set("repeat_count", old_repeat_count+ 1);
        record.set("bank_cnaps_code", record.getStr("pay_bank_cnaps"));
        record.set("payment_amount", record.getBigDecimal("collect_amount"));
        record.set("process_bank_type", record.getStr("recv_bank_cnaps").subSequence(0, 3));
        record.set("payment_summary", record.getStr("memo"));
        record.set("bank_serial_number", ChannelManager.getSerianlNo(payBankCode));
        SysSinglePayInter sysInter = new SysSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(record);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("single_pay_instr_queue", instr);
                if (save) {
                    return Db.update(Db.getSql("collect_manage.updInstrById"), instr.getStr("bank_serial_number"),
                            WebConstant.CollOrPoolRunStatus.SENDING.getKey(),instr.getInt("repeat_count"), id,old_repeat_count,status) == 1;
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

    public List<Record> instruction(final Record record) {
        List<Record> list = new ArrayList<>();
        //根据归集id查询执行id
        List<Record> collExecuteList = Db.find(Db.getSql("collect_manage.findCollectExecuteByCollectId"), record.get("collect_id"));
        if (collExecuteList != null && collExecuteList.size() > 0) {
            BigDecimal totalAmount = new BigDecimal("0");
            for (Record ce : collExecuteList) {
                //成功笔数
                Integer success_count = TypeUtils.castToInt(ce.get("success_count"));
                //失败笔数
                Integer fail_count = TypeUtils.castToInt(ce.get("fail_count"));
                //主账号数量
                Integer main_account_count = TypeUtils.castToInt(ce.get("main_account_count"));
                //子账户数量
                Integer child_account_count = TypeUtils.castToInt(ce.get("child_account_count"));


                list = new ArrayList<>();
                //根据执行id查询执行信息
                List<Record> instList = Db.find(Db.getSql("collect_manage.findExecuteInstructionListByExecuteId"), ce.get("id"), ce.get("execute_time"));
                //归集总金额

                for (Record inst : instList) {
                    BigDecimal collect_amount = TypeUtils.castToBigDecimal(inst.get("collect_amount"));
                    totalAmount = totalAmount.add(collect_amount);
                    list.add(inst);
                }
                //归集总金额
                ce.set("total_amount", totalAmount == null ? 0 : totalAmount);
                //归集总笔数
                ce.set("total_num", collExecuteList.size());
                ce.set("detail", list);

                //null处理
                ce.set("success_count", success_count == null ? 0 : success_count);
                ce.set("fail_count", fail_count == null ? 0 : fail_count);
                ce.set("main_account_count", main_account_count == null ? 0 : main_account_count);
                ce.set("child_account_count", child_account_count == null ? 0 : child_account_count);
            }
        }

        return collExecuteList;
    }

    /**
     * 指令作废
     */
    public Record cancelinstruction(final Record record) throws DbProcessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int repeatCount = TypeUtils.castToInt(record.get("repeat_count"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                set.set("repeat_count", (repeatCount + 1));
                set.set("collect_status", WebConstant.CollOrPoolRunStatus.CANCEL.getKey());

                Record where = new Record();
                where.set("id", id);
                where.set("repeat_count", repeatCount);

                return CommonService.update("collect_execute_instruction", set, where);
            }
        });

        if (!flag) {
            throw new DbProcessException("指令作废失败!");
        }

        return Db.findById("collect_execute_instruction", "id", id);
    }

    /**
     * 指令发送
     */
    public void sendinstruction(final Record record) throws Exception {
        Long id = TypeUtils.castToLong(record.get("id"));
        sendPayDetail(id);
    }

}
