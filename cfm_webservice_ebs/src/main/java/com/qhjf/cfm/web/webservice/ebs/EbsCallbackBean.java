package com.qhjf.cfm.web.webservice.ebs;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;

import java.util.HashMap;
import java.util.Map;

public class EbsCallbackBean {
	
	private String PayNo;
	private String PayDate;
	private String PayTime;
	private String PayResult;
	private String ErrCol;
	private String ErrCode;
	private String ErrMsg;
	private String PayMoney;
	private String PayBankCode ;
	private String PayBankAccNo ;
	private String PayBankName ;  //EBS新接口,接收大都会支付银行名称
	private String GetAccName ;  //EBS新接口,接收实际收款人账户名
	private String GetBankName ;  //EBS新接口,接收实际收款人银行名称
	private String GetAccNo ;  //EBS新接口,接收实际收款人账户名
	
	public EbsCallbackBean(Record origin) throws Exception{
				
		this.PayNo = origin.getStr("pay_code");
		this.PayResult = origin.getStr("pay_code");
		if(origin.getInt("tmp_status") == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
			this.PayResult = "SC";
		}else if(origin.getInt("tmp_status") == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey()){
			this.PayResult = "FL";
			this.ErrMsg = origin.getStr("tmp_err_message");
		}else{
			throw new Exception(origin.getLong("id")+"EBS原始数据状态有误,无法回写,状态为"+origin.getInt("tmp_status"));
		}
		this.PayMoney = origin.getStr("amount");
		this.PayDate = origin.getStr("paydate");
		this.PayTime = origin.getStr("paytime");
		this.PayBankAccNo = origin.getStr("paybankaccno");
		this.PayBankCode = origin.getStr("paybankcode");
		Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccno"), PayBankAccNo);
		this.PayBankName = payRec.getStr("bank_name");
		this.GetAccName = origin.getStr("recv_acc_name");
		this.GetAccNo = origin.getStr("recv_acc_no");
	}
	
	public Map<String,Object> toMap(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("PayNo", this.PayNo);
		map.put("PayDate", this.PayDate);
		map.put("PayTime", this.PayTime);
		map.put("PayResult", this.PayResult);
		map.put("ErrCol", this.ErrCol);
		map.put("ErrCode", this.ErrCode);
		map.put("ErrMsg", this.ErrMsg);
		map.put("PayMoney", this.PayMoney);
		map.put("PayBankCode", this.PayBankCode);
		map.put("PayBankAccNo", this.PayBankAccNo);
		map.put("PayBankName", this.PayBankName);
		map.put("GetAccName", this.GetAccName);
		map.put("GetAccNo", this.GetAccNo);
		map.put("GetBankName", this.GetBankName);
		return map;
	}

	public String getPayNo() {
		return PayNo;
	}

	public void setPayNo(String payNo) {
		PayNo = payNo;
	}

	public String getPayDate() {
		return PayDate;
	}

	public void setPayDate(String payDate) {
		PayDate = payDate;
	}

	public String getPayTime() {
		return PayTime;
	}

	public void setPayTime(String payTime) {
		PayTime = payTime;
	}

	public String getPayResult() {
		return PayResult;
	}

	public void setPayResult(String payResult) {
		PayResult = payResult;
	}

	public String getErrCol() {
		return ErrCol;
	}

	public void setErrCol(String errCol) {
		ErrCol = errCol;
	}

	public String getErrCode() {
		return ErrCode;
	}

	public void setErrCode(String errCode) {
		ErrCode = errCode;
	}

	public String getErrMsg() {
		return ErrMsg;
	}

	public void setErrMsg(String errMsg) {
		ErrMsg = errMsg;
	}

	public String getPayMoney() {
		return PayMoney;
	}

	public void setPayMoney(String payMoney) {
		PayMoney = payMoney;
	}

	public String getPayBankCode() {
		return PayBankCode;
	}

	public void setPayBankCode(String payBankCode) {
		PayBankCode = payBankCode;
	}

	public String getPayBankAccNo() {
		return PayBankAccNo;
	}

	public void setPayBankAccNo(String payBankAccNo) {
		PayBankAccNo = payBankAccNo;
	}

	public String getPayBankName() {
		return PayBankName;
	}

	public void setPayBankName(String payBankName) {
		PayBankName = payBankName;
	}

	public String getGetAccName() {
		return GetAccName;
	}

	public void setGetAccName(String getAccName) {
		GetAccName = getAccName;
	}

	public String getGetBankName() {
		return GetBankName;
	}

	public void setGetBankName(String getBankName) {
		GetBankName = getBankName;
	}

	public String getGetAccNo() {
		return GetAccNo;
	}

	public void setGetAccNo(String getAccNo) {
		GetAccNo = getAccNo;
	}
	
	
	
	
}



