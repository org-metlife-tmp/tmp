package com.qhjf.cfm.web.queue;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupAdvanceReceiptStatusQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupBizPayConfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccConfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillComfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupAdvanceReceiptStatusQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupBizPayConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillConfirmRespBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 柜面收_团单确认缴费按钮
 *
 * @author pc_liweibing
 */
public class RecvGroupCounterConfirmQueue implements Runnable {

    private static Logger log = LoggerFactory.getLogger(RecvGroupCounterConfirmQueue.class);

    private Record record;

    private UserInfo userInfo;

    private UodpInfo curUodp;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UodpInfo getCurUodp() {
        return curUodp;
    }

    public void setCurUodp(UodpInfo curUodp) {
        this.curUodp = curUodp;
    }


    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    @Override
    public void run() {
        long startMillis = System.currentTimeMillis();
        log.info("===团单新增开启异步线程查询接口===");
        int useFunds = TypeUtils.castToInt(record.get("use_funds"));
        int persist_version = TypeUtils.castToInt(record.get("persist_version"));
        RecvCounterRemoteCall recvCounter = new RecvCounterRemoteCall();
        //1，调用查询接口
        try {
            GroupAdvanceReceiptStatusQryReqBean groupAdvanceReceiptStatusQryReqBean = new GroupAdvanceReceiptStatusQryReqBean(
                    TypeUtils.castToString(record.get("zj_flow_number")));
            GroupAdvanceReceiptStatusQryRespBean groupAdvanceReceiptStatusQryRespBean = recvCounter.ebsConfirmStatusQry(groupAdvanceReceiptStatusQryReqBean);
            if ("SUCCESS".equals(groupAdvanceReceiptStatusQryRespBean.getResultCode())) {
                //返回成功,更新数据
                log.error("===团单新增查询接口返回成功===");
                if(CommonService.updateRows("recv_counter_bill",
                        new Record().set("confirm_status", 1).set("update_on", new Date()).set("update_by", userInfo.getUsr_id())
                        .set("persist_version", persist_version+1),
                        new Record().set("id", record.get("id"))) == 1){
                    return;
                }
            }
        } catch (ReqDataException e) {
            log.error("===团单新增查询接口异常===");
            if(CommonService.updateRows("recv_counter_bill",
                    new Record().set("confirm_status", 0).set("update_on", new Date())
                            .set("update_by", userInfo.getUsr_id()).set("confirm_status", 0).set("persist_version", persist_version+1),
                    new Record().set("id", record.get("id"))) == 1){
                return;
            }
            return;
        }
        log.info("===查询接口结束，进入团单确认接口===");

        //2，调用确认接口
        try{
            if (useFunds == WebConstant.SftRecvGroupCounterUseFunds.KHZH.getKey()) {
                //客户账号
                GroupCustomerAccConfirmReqBean groupCustomerAccConfirmReqBean = new GroupCustomerAccConfirmReqBean();
                groupCustomerAccConfirmReqBean.setPayNo(TypeUtils.castToString(record.get("zj_flow_number")));   //资金流水号
                groupCustomerAccConfirmReqBean.setPayCustomerNo(TypeUtils.castToString(record.get("consumer_no")));  //缴费人客户号。 对应缴费公司的客户号码， ebs查询不到该客户号会报错
                groupCustomerAccConfirmReqBean.setPayWay(WebConstant.Sft_RecvGroupCounter_Recvmode.
                        getSft_RecvGroupCounter_RecvmodeByKey(Integer.valueOf(TypeUtils.castToString(record.get("recv_mode")))).getKeyc());  //缴费方式 2-现金缴款单 3-支票 4-转账汇款
                groupCustomerAccConfirmReqBean.setPayMoney(TypeUtils.castToBigDecimal(record.get("amount")).setScale(2).
                        stripTrailingZeros().toPlainString());  //交费金额 单位元，2位小数
                groupCustomerAccConfirmReqBean.setChequeNo(TypeUtils.castToString(record.get("bill_number")));  //票据号码 3-支票时必传
                groupCustomerAccConfirmReqBean.setChequeDate(DateFormatThreadLocal.format("yyyy-MM-dd",
                        TypeUtils.castToDate(record.get("bill_date"))));  //支票日期,YYYY-MM-DD 3-支票时必传
                Record faccount = Db.findById("account", "bankcode", TypeUtils.castToString(record.get("recv_bank_name")));
                Record ftmpBank = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(faccount.get("bank_cnaps_code")));
                Record febsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(ftmpBank.get("bank_type")));
                groupCustomerAccConfirmReqBean.setInBankCode(TypeUtils.castToString(febsBank.get("ebs_bank_code")));   //大都会收款银行编码 需要做Mapping
                groupCustomerAccConfirmReqBean.setInBankAccNo(TypeUtils.castToString(record.get("recv_acc_no")));//大都会收款银行账号

                //根据bankcode做映射
                Record stmpBank = Db.findById("const_bank_type", "code", TypeUtils.castToString(record.get("consumer_bank_name")));
                Record sebsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(stmpBank.getStr("code")));
                groupCustomerAccConfirmReqBean.setBankCode(TypeUtils.castToString(sebsBank.get("ebs_bank_code")));  //客户付款银行 需要做Mapping,缴费方式 2和3时 必传
                groupCustomerAccConfirmReqBean.setBankAccNo(TypeUtils.castToString(record.get("consumer_acc_no")));  //客户付款银行账号;缴费方式 2和3时 必传
                groupCustomerAccConfirmReqBean.setBankAccName(TypeUtils.castToString(record.get("consumer_accname")));  //客户付款银行户名;缴费方式 2和3时 必传
                GroupCustomerAccConfirmRespBean groupCustomerAccConfirmRespBean = recvCounter.ebsCustomerAccConfirm(groupCustomerAccConfirmReqBean);
                if ("SUCCESS".equals(groupCustomerAccConfirmRespBean.getResultCode())) {
                    //返回成功
                    if(CommonService.updateRows("recv_counter_bill",
                            new Record().set("confirm_status", 1).set("update_on", new Date()).set("update_by", userInfo.getUsr_id())
                                    .set("back_on", new Date()).set("is_sunvouder", 1)
                                    .set("persist_version", persist_version+1),
                            new Record().set("id", record.get("id"))) == 1){
                        return;
                    }
                }else {
                    if(CommonService.updateRows("recv_counter_bill",
                            new Record().set("confirm_status", 0).set("update_on", new Date())
                                    .set("update_by", userInfo.getUsr_id()).set("confirm_msg", groupCustomerAccConfirmRespBean.getResultMsg())
                                    .set("persist_version", persist_version+1),
                            new Record().set("id", record.get("id"))) == 1){
                        return;
                    }
                }
            } else {
                //XDQF(1, "新单签发"), BQSF(2, "保全收费"), DQJSSF(3, "定期结算收费"), XQSF(4, "续期收费"), BDQSF(5, "不定期收费")
                GroupBizPayConfirmReqBean groupBizPayConfirmReqBean = new GroupBizPayConfirmReqBean();
                groupBizPayConfirmReqBean.setBussinessNo(TypeUtils.castToString(record.get("bussiness_no")));        //业务号码
                groupBizPayConfirmReqBean.setBussinessType(WebConstant.SftRecvGroupCounterUseFunds.getSftRecvGroupCounterUseFundsByKey(useFunds).getKeyc());
                groupBizPayConfirmReqBean.setPayNo(TypeUtils.castToString(record.get("zj_flow_number")));   //资金流水号
                //groupBizPayConfirmReqBean.setPayCustomerNo(TypeUtils.castToString(record.get("consumer_acc_no")));  //不传 缴费人客户号。对应缴费公司的客户号码， ebs查询不到该客户号会报错
                groupBizPayConfirmReqBean.setPayWay(WebConstant.Sft_RecvGroupCounter_Recvmode.
                        getSft_RecvGroupCounter_RecvmodeByKey(Integer.valueOf(Integer.valueOf(TypeUtils.castToString(record.get("recv_mode"))))).getKeyc());  //缴费方式 2-现金缴款单 3-支票 4-转账汇款
                groupBizPayConfirmReqBean.setPayMoney(TypeUtils.castToBigDecimal(record.get("amount")).setScale(2).
                        stripTrailingZeros().toPlainString());  //交费金额 单位元，2位小数
                groupBizPayConfirmReqBean.setChequeNo(TypeUtils.castToString(record.get("bill_number")));  //票据号码 3-支票时必传
                groupBizPayConfirmReqBean.setChequeDate(DateFormatThreadLocal.format("yyyy-MM-dd",
                        TypeUtils.castToDate(record.get("bill_date"))));  //支票日期,YYYY-MM-DD 3-支票时必传
                Record faccount = Db.findById("account", "bankcode", TypeUtils.castToString(record.get("recv_bank_name")));
                Record ftmpBank = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(faccount.get("bank_cnaps_code")));
                Record febsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(ftmpBank.get("bank_type")));
                groupBizPayConfirmReqBean.setInBankCode(TypeUtils.castToString(febsBank.get("ebs_bank_code")));   //大都会收款银行编码 需要做Mapping
                groupBizPayConfirmReqBean.setInBankAccNo(TypeUtils.castToString(record.get("recv_acc_no")));  //大都会收款银行账号
                //根据bankcode做映射
                Record stmpBank = Db.findById("const_bank_type", "code", TypeUtils.castToString(record.get("consumer_bank_name")));
                Record sebsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(stmpBank.getStr("code")));
                groupBizPayConfirmReqBean.setBankCode(TypeUtils.castToString(sebsBank.get("ebs_bank_code")));  //客户付款银行 需要做Mapping,缴费方式 2和3时 必传
                groupBizPayConfirmReqBean.setBankAccNo(TypeUtils.castToString(record.get("consumer_acc_no")));  //客户付款银行账号;缴费方式 2和3时 必传
                groupBizPayConfirmReqBean.setBankAccName(TypeUtils.castToString(record.get("consumer_accname")));  //客户付款银行户名;缴费方式 2和3时 必传
                GroupBizPayConfirmRespBean groupBizPayConfirmRespBean = recvCounter.ebsBizPayConfirm(groupBizPayConfirmReqBean);
                if ("SUCCESS".equals(groupBizPayConfirmRespBean.getResultCode())) {
                    //返回成功
                    if(CommonService.updateRows("recv_counter_bill",
                            new Record().set("confirm_status", 1).set("update_on", new Date()).set("update_by", userInfo.getUsr_id())
                                    .set("back_on", new Date()).set("is_sunvouder", 1)
                                    .set("persist_version", persist_version+1),
                            new Record().set("id", record.get("id"))) == 1){
                        return;
                    }
                }else {
                    if(CommonService.updateRows("recv_counter_bill",
                            new Record().set("confirm_status", 0).set("update_on", new Date())
                                    .set("update_by", userInfo.getUsr_id()).set("confirm_msg", groupBizPayConfirmRespBean.getResultMsg())
                                    .set("persist_version", persist_version+1),
                            new Record().set("id", record.get("id"))) == 1){
                        return;
                    }
                }
            }
        } catch (ReqDataException e) {
            log.error("===团单新增确认接口异常===");
            e.printStackTrace();
            if(CommonService.updateRows("recv_counter_bill",
                    new Record().set("confirm_status", 0).set("update_on", new Date())
                            .set("update_by", userInfo.getUsr_id()).set("confirm_status", 0)
                            .set("confirm_msg", "确认异常").set("persist_version", persist_version+1),
                    new Record().set("id", record.get("id"))) == 1){
                return;
            }
            return;
        }
        log.info("===团单新增开启异步线程确认结束,总耗时===" + (System.currentTimeMillis() - startMillis));
    }
}
