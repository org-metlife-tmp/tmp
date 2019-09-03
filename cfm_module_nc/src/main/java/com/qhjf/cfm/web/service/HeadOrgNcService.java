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
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class HeadOrgNcService {

    private static final Log log = LogbackLog.getLog(HeadOrgNcService.class);
    NcCallback ncCallback = new NcCallback();
    public Page<Record> getTodoPage(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("head_org_nc.getTodoPage", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }




    public boolean pass(final Record record) {
        Record headRecord = null;
        try {
            headRecord = Db.findFirst(Db.getSql("head_org_nc.findHeadPayById"), record.get("id"),record.get("persist_version"));
            if (headRecord==null){
                throw new ReqDataException("此条单据信息已过期!");
            }
            String payCnaps = headRecord.getStr("pay_bank_cnaps");
            String payBankCode = payCnaps.substring(0, 3);
            IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
            headRecord.set("source_ref", "nc_head_payment");
            final int old_repeat_count = TypeUtils.castToInt(headRecord.get("repeat_count"));
            final int old_persist_version = TypeUtils.castToInt(record.get("persist_version"));
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
                                ,new Date(),  old_persist_version+ 1,record.get("id"),old_repeat_count,old_persist_version) == 1;
                    }
                    return save;
                }
            });
            if (flag) {
                QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
                ProductQueue productQueue = new ProductQueue(bean);
                new Thread(productQueue).start();
                return true;
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

        return false;
    }

    /**
     * @param paramsToRecord
     * @param userInfo
     * @param uodpInfo
     * @ 支付作废
     */
    public void payOff(Record paramsToRecord, UserInfo userInfo, UodpInfo uodpInfo) throws Exception {
        //总公司支付作废按钮 nc_head_payment  , nc_origin_data 表
        final List<Long> ids = paramsToRecord.get("ids");
        final List<Integer> persist_versions = paramsToRecord.get("persist_version");
        for (int i = 0; i < ids.size(); i++) {
            long id = TypeUtils.castToLong(ids.get(i));
            Integer old_version = TypeUtils.castToInt(persist_versions.get(i));
            Record innerRec = Db.findById("nc_head_payment", "id", id);
            if (innerRec == null) {
                throw new ReqDataException("未找到有效的单据!");
            }
            Long originId = TypeUtils.castToLong(innerRec.get("ref_id"));
            int service_status = TypeUtils.castToInt(innerRec.get("service_status"));
            String feed_back = TypeUtils.castToString(paramsToRecord.get("feed_back"));
            // 判断单据状态为“审批通过”或“已失败”时可以发送，其他状态需抛出异常！
            if (service_status == 0) {
                String errorMessage = this.payOff(id,old_version,feed_back,userInfo,uodpInfo,originId);
                if(errorMessage != null){
                    throw new ReqDataException(errorMessage);
                }
            }else{
                throw new ReqDataException("此条单据信息已过期!");
            }
        }

    }
    /**
     * 作废,添加事物
     * @param id
     * @param old_version
     * @param feed_back
     * @param userInfo
     * @param uodpInfo
     * @param originId
     */
    private String payOff(final long id, final Integer old_version, final String feed_back, final UserInfo userInfo, UodpInfo uodpInfo, final Long originId) {
        // TODO Auto-generated method stub
        String errmsg = null;
        //添加事物
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                Record where = new Record();
                set.set("persist_version", old_version + 1);
                set.set("service_status", WebConstant.BillStatus.CANCEL.getKey());
                set.set("update_by", userInfo.getUsr_id());
                set.set("update_on", new Date());
                where.set("id", id);
                where.set("persist_version", old_version);
                int updateRows = CommonService.updateRows("nc_head_payment", set, where);
                if(updateRows == 1){
                    log.debug("==================更新nc_head_payment条数==="+updateRows);
                    set.clear();
                    where.clear();
                    Record originRecord = Db.findById("nc_origin_data", "id", originId);
                    if (null == originRecord) {
                        log.error("=======nc_origin_data此条单据原始数据信息已过期========");
                        return false ;
                    }
                    set.set("process_status", WebConstant.OaProcessStatus.OA_TRADE_CANCEL.getKey());
                    set.set("lock_id", originId);
                    set.set("interface_status", WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_F.getKey());
                    set.set("interface_fb_code", "P0098");
                    set.set("interface_fb_msg", feed_back);
                    where.set("id", originId);
                    int rows = CommonService.updateRows("nc_origin_data", set, where);
                    if(rows == 1){
                        return true ;
                    }else{
                        log.error("=======此条单据原始数据信息更新条数==="+rows);
                        return false ;
                    }
                }else{
                    log.error("=====nc_head_payment此条单据信息已过期,请刷新页面=====") ;
                    return false ;
                }
            }
        });
        if(flag){
            log.debug("===========作废单据,更新数据库成功,开始回调接口===");
            try {
                Record originRecord = Db.findById("nc_origin_data", "id", originId);
                ncCallback.callback(originRecord,null);
            } catch (Exception e) {
                log.error("============回调接口异常了!========");
                e.printStackTrace();
                errmsg = "回调NC接口异常";
            }
        }else{
            log.error("============事物状态flag========"+flag);
            errmsg = "单据信息状态过期";
        }
        return errmsg ;
    }

}

