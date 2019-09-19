package com.qhjf.cfm.web.queue;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.ExcleDiskSendingService;
import com.qhjf.cfm.web.service.TxtDiskSendingService;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillComfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillConfirmRespBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 柜面收_确认缴费按钮
 * 
 * @author pc_liweibing
 *
 */
public class RecvCounterConfirmQueue implements Runnable {

	private static Logger log = LoggerFactory.getLogger(RecvCounterConfirmQueue.class);

	private Record record;

	private UserInfo userInfo;

	private UodpInfo curUodp;

	public static final String policySuspense = "6";

	public static final String additionalInvestmentSuspension = "7";

	public static final String SACSTYPEZJ = "S";

	public static final String SACSTYPEIV = "IV";

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
		log.info("====开启异步线程确认支付开始====");
		final List<Record> policys = record.get("policy_infos");
		log.info("====确认支付的保单号个数===="+policys.size());
		// 多个保单号共用这几个附件
		List<PersonBillComfirmReqBean> PersonBillComfirmReqBeans = new ArrayList<PersonBillComfirmReqBean>();
		
		//目前确认个单只有LA,去LA映射表
		record.set("bank_code","31");
		for (Record policy : policys) {
			String paytype = WebConstant.Sft_RecvPersonalCounter_Recvmode.getByKey(TypeUtils.castToInt(record.get("recv_mode")))
					.getPrefix();
			PersonBillComfirmReqBean personBillComfirmReqBean = new PersonBillComfirmReqBean(
					TypeUtils.castToString(policy.get("amount")), record.getStr("consumer_acc_no"), null,
					policy.getStr("insure_bill_no"), paytype,"31",getSacsTypeByusefund(record));
			PersonBillComfirmReqBeans.add(personBillComfirmReqBean);			
		}
		log.info("=====调用外部接口开始进行确认====");
		List<PersonBillConfirmRespBean> personConfirmBill = null;
		try {
			RecvCounterRemoteCall recvCounterRemoteCall = new RecvCounterRemoteCall();
			personConfirmBill = recvCounterRemoteCall.personConfirmBill(PersonBillComfirmReqBeans);
		} catch (Exception e) {
			log.error("====调用外部系统异常====");
			e.printStackTrace();
			//根据批次号,更新所有的数据为确认失败
			int updateRows = CommonService.updateRows("recv_counter_bill", 
					               new Record().set("confirm_status", 0).set("confirm_msg", "调用外部系统异常")
					               .set("update_on", new Date()).set("update_by", userInfo.getUsr_id()), 
					               new Record().set("batch_process_no", record.get("batch_process_no")));
		    log.info("====根据批次号===="+record.get("batch_process_no")+"====更新保单数量===="+updateRows);
			return ;
		}
		if (null == personConfirmBill || personConfirmBill.size() == 0) {
			log.error("====确认按钮调用外部系统获取到返回信息为空====");
			//根据批次号,更新所有的数据为确认失败
			int updateRows = CommonService.updateRows("recv_counter_bill", 
		               new Record().set("confirm_status", 0).set("confirm_msg", "调用外部系统返回结果为空"), 
		               new Record().set("batch_process_no", record.get("batch_process_no")));
            log.info("====根据批次号===="+record.get("batch_process_no")+"====更新保单数量===="+updateRows);
            return ;
		}
		List<Record> returnRecords = new ArrayList<>();
		// 个单,批处理号,保单号可以作为条件更新
		for (int i = 0; i < personConfirmBill.size(); i++) {
			PersonBillConfirmRespBean personBillConfirmRespBean = personConfirmBill.get(i);
			Record returnReocrd = new Record();
			returnReocrd.set("batch_process_no", record.get("batch_process_no")); // 批处理号
			returnReocrd.set("receipt", personBillConfirmRespBean.getReceipt()); // 收据号
			returnReocrd.set("confirm_msg", personBillConfirmRespBean.getErrmess()); // 错误信息
			returnReocrd.set("insure_bill_no", personBillConfirmRespBean.getCownsel()); // 保单号
			returnReocrd.set("update_on", new Date()); // 更新时间
			returnReocrd.set("update_by", userInfo.getUsr_id()); // 更新人
			if ("SC".equalsIgnoreCase(personBillConfirmRespBean.getStatus())) {
				returnReocrd.set("confirm_status", 1);
				returnReocrd.set("back_on", new Date());
				returnReocrd.set("is_sunvouder", 1);
			} else {
				returnReocrd.set("confirm_status", 0);
			}
			returnRecords.add(returnReocrd);
		}
        int[] batchUpdate = Db.batchUpdate("recv_counter_bill", "insure_bill_no,batch_process_no", returnRecords, returnRecords.size());
        boolean checkDbResult = ArrayUtil.checkDbResult(batchUpdate);
        if(!checkDbResult) {
        	log.error("====根据外部系统返回更新保单确认状态失败,批次号===="+record.get("batch_process_no"));
        	return ;
        }   
        log.info("====开启异步线程确认支付结束,总耗时====" + (System.currentTimeMillis() - startMillis));
	}
	//通过usefoud增加回传LA报文SACSCODE和SACSTYPE字段
	public String getSacsTypeByusefund(Record record)
	{
		String usefoud = TypeUtils.castToString(record.get("use_funds"));
		try {
			if(usefoud.equals(policySuspense))
			{
				return SACSTYPEZJ;
			}else if(usefoud.equals(additionalInvestmentSuspension))
			{
				return SACSTYPEIV;
			}
			else {
				return null;
			}
		}catch (Exception e)
		{
			log.error("====通过usefoud获取SACSTYPE失败=");
		}
		return null;
	}

}
