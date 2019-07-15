package com.qhjf.cfm.web.quartzs.jobs;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.*;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.*;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecvCounterJob implements Job {

    private static Logger log = LoggerFactory.getLogger(RecvCounterJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("【柜面收个单确认自动核对任务---begin】");
        
        //个单确认定时任务
        confirmAutoTry();

		log.info("【柜面收个单确认自动核对任务---end】");

		log.info("【柜面收团单确认自动核对任务---begin】");
		//团单确认定时任务
		confirmGroupAutoTry();
		log.info("【柜面收团单确认自动核对任务---end】");

    }
	/**
      * 个单确认重试
      */
    private void confirmAutoTry() {
		List<Record> find = Db.find(Db.getSql("auto_recvcounter_cfm.confirm_list"));
		if(null == find || find.size()==0) {
			log.info("====不存在需要重试的确认支付数据====");
			return ;
		}
		List<PersonBillComfirmReqBean> personBillComfirmReqBeans = new ArrayList<PersonBillComfirmReqBean>();
		List<Record>  updateRecords = new ArrayList<>();

		for (int i = 0; i < find.size(); i++) {
			Record rec = find.get(i);
			String paytype = WebConstant.Sft_RecvPersonalCounter_Recvmode.getByKey(rec.getInt("recv_mode"))
					.getPrefix();
			PersonBillComfirmReqBean PersonBillComfirmReqBean = 
					new PersonBillComfirmReqBean(TypeUtils.castToString(rec.get("amount")), rec.getStr("consumer_acc_no"), null,
							rec.getStr("insure_cer_no"),paytype ,rec.getStr("bank_code"));
			//无论调用是否成功,此处都将确认次数+1
			Record updateRecord = new Record();
			updateRecord.set("id", rec.get("id"))
			            .set("confrim_try_times", rec.getInt("confrim_try_times")+1)
			            .set("persist_version", rec.getInt("persist_version")+1);
			updateRecords.add(updateRecord);
			personBillComfirmReqBeans.add(PersonBillComfirmReqBean);
		}
		Db.batchUpdate("recv_counter_bill", "id", updateRecords,updateRecords.size());
		try {
			log.info("====开始调用外部系统进行支付确认====");
			RecvCounterRemoteCall recvCounterRemoteCall = new RecvCounterRemoteCall();
			List<PersonBillConfirmRespBean> personConfirmBill = recvCounterRemoteCall.personConfirmBill(personBillComfirmReqBeans);
			if(null == personConfirmBill || personConfirmBill.size() == 0) {
				log.error("====调用外部系统支付确认结果为null====");
				return ;				
			}			
			//for循环,保单号作为主键去更新
			List<Record> returnRecords = new ArrayList<>();
			for (int i = 0; i < personConfirmBill.size(); i++) {
				PersonBillConfirmRespBean personBillConfirmRespBean = personConfirmBill.get(i);
				Record returnReocrd = new Record();
				returnReocrd.set("receipt", personBillConfirmRespBean.getReceipt()); // 收据号
				returnReocrd.set("confirm_msg", personBillConfirmRespBean.getErrmess()); // 错误信息
				returnReocrd.set("insure_bill_no", personBillConfirmRespBean.getCownsel()); // 保单号
				returnReocrd.set("update_on", new Date()); // 更新时间
				if ("SC".equalsIgnoreCase(personBillConfirmRespBean.getStatus())) {
					returnReocrd.set("confirm_status", 1);
					returnReocrd.set("back_on", new Date());
					returnReocrd.set("is_sunvouder", 1);
				} else {
					returnReocrd.set("confirm_status", 0);
				}
				returnRecords.add(returnReocrd);
			}
			int[] batchUpdate = Db.batchUpdate("recv_counter_bill", "insure_bill_no", returnRecords, returnRecords.size());
		    boolean checkDbResult = ArrayUtil.checkDbResult(batchUpdate);
		    if(!checkDbResult) {
		        log.error("====根据外部系统返回更新保单确认状态失败====");
		        return ;
		    }   
		} catch (Exception e) {
			log.error("====调用外部系统支付确认异常====");			
			return ;
		}
	}

	/**
	 * 团单确认重试
	 */
	private void confirmGroupAutoTry() {
		List<Record> find = Db.find(Db.getSql("auto_recvcounter_cfm.confirm_group_list"));
		if (null == find || find.size() == 0) {
			log.info("====不存在需要重试的团单确认支付数据====");
			return;
		}
		for (int i = 0; i < find.size(); i++) {
			Record record = find.get(i);
			int persist_version = TypeUtils.castToInt(record.get("persist_version"));
			int confrim_try_times = TypeUtils.castToInt(record.get("confrim_try_times"));
			for (int j = 0; j < 5; j++) {
				confrim_try_times = confrim_try_times + 1;
				log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询接口开始====");
				int useFunds = TypeUtils.castToInt(record.get("use_funds"));
				RecvCounterRemoteCall recvCounter = new RecvCounterRemoteCall();
				//1，调用查询接口
				try {
					GroupAdvanceReceiptStatusQryReqBean groupAdvanceReceiptStatusQryReqBean = new GroupAdvanceReceiptStatusQryReqBean(
							TypeUtils.castToString(record.get("zj_flow_number")));
					GroupAdvanceReceiptStatusQryRespBean groupAdvanceReceiptStatusQryRespBean = recvCounter.ebsConfirmStatusQry(groupAdvanceReceiptStatusQryReqBean);
					if ("SUCCESS".equals(groupAdvanceReceiptStatusQryRespBean.getResultCode())) {
						//返回成功,更新数据
						log.error("===团单新增查询接口返回成功===");
						if (CommonService.updateRows("recv_counter_bill",
								new Record().set("confirm_status", 1).set("persist_version", persist_version + 1).set("confrim_try_times", confrim_try_times),
								new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
							persist_version++;
							return;
						}
					}
				} catch (ReqDataException e) {
					log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询接口异常====");
					continue;
				}
				log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询接口结束====");

				//2，调用确认接口
				try {
					log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次查询确认开始====");
					if (useFunds == WebConstant.SftRecvGroupCounterUseFunds.KHZH.getKey()) {
						//客户账号
						GroupCustomerAccConfirmReqBean groupCustomerAccConfirmReqBean = new GroupCustomerAccConfirmReqBean();
						groupCustomerAccConfirmReqBean.setPayNo(TypeUtils.castToString(record.get("zj_flow_number")));   //资金流水号
						groupCustomerAccConfirmReqBean.setPayCustomerNo(TypeUtils.castToString(record.get("consumer_acc_no")));  //缴费人客户号。 对应缴费公司的客户号码， ebs查询不到该客户号会报错
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
						groupCustomerAccConfirmReqBean.setBankCode(TypeUtils.castToString(febsBank.get("ebs_bank_code")));  //客户付款银行 需要做Mapping,缴费方式 2和3时 必传
						groupCustomerAccConfirmReqBean.setBankAccNo("consumer_acc_no");  //客户付款银行账号;缴费方式 2和3时 必传
						groupCustomerAccConfirmReqBean.setBankAccName(TypeUtils.castToString(faccount.get("acc_name")));  //客户付款银行户名;缴费方式 2和3时 必传
						//根据bankcode做映射
						Record stmpBank = Db.findById("const_bank_type", "code", TypeUtils.castToString(record.get("consumer_bank_name")));
						Record sebsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(stmpBank.getStr("code")));
						groupCustomerAccConfirmReqBean.setInBankCode(TypeUtils.castToString(sebsBank.get("ebs_bank_code")));   //大都会收款银行编码 需要做Mapping
						groupCustomerAccConfirmReqBean.setInBankAccNo("consumer_acc_no");  //大都会收款银行账号

						GroupCustomerAccConfirmRespBean groupCustomerAccConfirmRespBean = recvCounter.ebsCustomerAccConfirm(groupCustomerAccConfirmReqBean);
						if ("SUCCESS".equals(groupCustomerAccConfirmRespBean.getResultCode())) {
							//返回成功
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 1).set("persist_version", persist_version + 1).set("confrim_try_times", confrim_try_times)
											.set("back_on", new Date()).set("is_sunvouder", 1),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								return;
							}
						} else {
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 0).set("persist_version", persist_version + 1)
											.set("confirm_msg", groupCustomerAccConfirmRespBean.getResultMsg()).set("confrim_try_times", confrim_try_times),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								continue;
							}
						}
					} else {
						//XDQF(1, "新单签发"), BQSF(2, "保全收费"), DQJSSF(3, "定期结算收费"), XQSF(4, "续期收费"), BDQSF(5, "不定期收费")
						GroupBizPayConfirmReqBean groupBizPayConfirmReqBean = new GroupBizPayConfirmReqBean();
						groupBizPayConfirmReqBean.setBussinessNo("bussiness_no");
						groupBizPayConfirmReqBean.setBussinessType(WebConstant.SftRecvGroupCounterUseFunds.getSftRecvGroupCounterUseFundsByKey(useFunds).getKeyc());
						groupBizPayConfirmReqBean.setPayNo(TypeUtils.castToString(record.get("zj_flow_number")));   //资金流水号
						groupBizPayConfirmReqBean.setPayCustomerNo(TypeUtils.castToString(record.get("consumer_acc_no")));  //缴费人客户号。 对应缴费公司的客户号码， ebs查询不到该客户号会报错
						groupBizPayConfirmReqBean.setPayWay(WebConstant.Sft_RecvGroupCounter_Recvmode.
								getSft_RecvGroupCounter_RecvmodeByKey(Integer.valueOf(Integer.valueOf(TypeUtils.castToString(record.get("recv_mode"))))).getKeyc());  //缴费方式 2-现金缴款单 3-支票 4-转账汇款
						groupBizPayConfirmReqBean.setPayMoney(TypeUtils.castToBigDecimal(record.get("amount")).setScale(2).
								stripTrailingZeros().toPlainString());  //交费金额 单位元，2位小数
						groupBizPayConfirmReqBean.setChequeNo(TypeUtils.castToString(record.get("bill_number")));  //票据号码 3-支票时必传
						groupBizPayConfirmReqBean.setChequeDate(DateFormatThreadLocal.format("yyyy-MM-dd",
								TypeUtils.castToDate(record.get("bill_date"))));  //支票日期,YYYY-MM-DD 3-支票时必传
						Record faccount = Db.findById("account", "bankcode", TypeUtils.castToString(record.get("bankcode")));
						Record ftmpBank = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(faccount.get("bank_cnaps_code")));
						Record febsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(ftmpBank.get("bank_type")));
						groupBizPayConfirmReqBean.setBankCode(TypeUtils.castToString(febsBank.get("ebs_bank_code")));  //客户付款银行 需要做Mapping,缴费方式 2和3时 必传
						groupBizPayConfirmReqBean.setBankAccNo("consumer_acc_no");  //客户付款银行账号;缴费方式 2和3时 必传
						groupBizPayConfirmReqBean.setBankAccName(TypeUtils.castToString(faccount.get("acc_name")));  //客户付款银行户名;缴费方式 2和3时 必传
						//根据bankcode做映射
						Record stmpBank = Db.findById("const_bank_type", "code", TypeUtils.castToString(record.get("consumer_bank_name")));
						Record sebsBank = Db.findById("ebs_bank_mapping", "tmp_bank_code", TypeUtils.castToString(stmpBank.getStr("code")));
						groupBizPayConfirmReqBean.setInBankCode(TypeUtils.castToString(sebsBank.get("ebs_bank_code")));   //大都会收款银行编码 需要做Mapping
						groupBizPayConfirmReqBean.setInBankAccNo("consumer_acc_no");  //大都会收款银行账号

						GroupBizPayConfirmRespBean groupBizPayConfirmRespBean = recvCounter.ebsBizPayConfirm(groupBizPayConfirmReqBean);
						if ("SUCCESS".equals(groupBizPayConfirmRespBean.getResultCode())) {
							//返回成功
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 1).set("persist_version", persist_version + 1).set("confrim_try_times", confrim_try_times)
											.set("back_on", new Date()).set("is_sunvouder", 1),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								return;
							}
						} else {
							if (CommonService.updateRows("recv_counter_bill",
									new Record().set("confirm_status", 0).set("persist_version", persist_version + 1)
											.set("confirm_msg", groupBizPayConfirmRespBean.getResultMsg()).set("confrim_try_times", confrim_try_times),
									new Record().set("id", record.get("id")).set("persist_version", persist_version)) == 1) {
								persist_version++;
								continue;
							}
						}
					}
				} catch (ReqDataException e) {
					log.error("===团单新增确认接口异常===");
					e.printStackTrace();
					log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次确认接口异常====");
					continue;
				}
				log.info("====团单id为【" + record.get("id") + "】的数据开始第【" + j + "】次确认接口结束====");
			}
		}

	}

}
