package com.qhjf.cfm.web.webservice.oa.server.request;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WebConstant.OaInterfaceStatus;
import com.qhjf.cfm.web.webservice.ann.FieldValidate;
import com.qhjf.cfm.web.webservice.oa.constant.ErrorCode;
import com.qhjf.cfm.web.webservice.oa.exception.WebServiceException;
import com.qhjf.cfm.web.webservice.oa.server.request.parent.ParentReq;

public class OAReciveDateReq extends ParentReq{
	
	public OAReciveDateReq(String xml) throws Exception{
		super(xml);
		/*if(this.applyUser == null){
			throw new WebServiceException(ErrorCode.P0003);
		}*/
		
		Record originRecord = Db.findFirst(Db.getSql("oa_interface.getSameBill"),this.getFlow_id(),OaInterfaceStatus.OA_INTER_PROCESS_F.getKey() );
		if(originRecord != null){
			throw new WebServiceException(ErrorCode.P0008,"该笔单据正在处理中或已成功,不允许重复发送");
		}
		originRecord = Db.findFirst(Db.getSql("oa_interface.getSameAndSendCountBill"),this.getFlow_id(),this.getSend_count() );
		if(originRecord != null){
			throw new WebServiceException(ErrorCode.P0008,"单据号重复,不允许发送");
		}
		this.applyOrg = Db.findFirst(Db.getSql("oa_interface.getOrgByCode"),this.getApply_org());
		if(this.applyOrg == null){
			throw new WebServiceException(ErrorCode.P0004);
		}

		List<Record> banks = Db.find(Db.getSql("oa_interface.getBankJQ"),this.getRecv_bank());
		if(banks == null || banks.size() ==0){
			banks = Db.find(Db.getSql("oa_interface.getBank"),"%"+this.getRecv_bank()+"%");
			if(banks == null || banks.size() ==0){
				throw new WebServiceException(ErrorCode.P0005);
			}
			if(banks.size() > 1){
				throw new WebServiceException(ErrorCode.P0006);
			}
		}
		this.recvBank = banks.get(0);

		BigDecimal amount = new BigDecimal(this.amount);
		if(amount.compareTo(BigDecimal.ZERO) <= 0){
			throw new WebServiceException(ErrorCode.P0009,"金额小于等于0,非法!");
		}
		
		this.payOrgType = Integer.parseInt(this.pay_org_type);
		if(payOrgType !=WebConstant.OaPayOrgType.OA_HEAD_PAY.getKey() && payOrgType !=WebConstant.OaPayOrgType.OA_BRANCH_PAY.getKey()){
			throw new Exception("pay_org_type值有误,只能为1或2");
		}
	}
	
	@FieldValidate(description="流程id")
	private String flow_id;
	@FieldValidate(description="报销申请编号")
	private String bill_no;
	@FieldValidate(description="业务类型")
	private String biz_type;
	@FieldValidate(description="申请人",nullable=true)
	private String apply_user;
	@FieldValidate(description="预算使用人",nullable=true)
	private String budget_user;
	@FieldValidate(description="申请日期")
	private String apply_date;
	@FieldValidate(description="收款人账号")
	private String recv_accno;
	@FieldValidate(description="收款人账户名称")
	private String recv_accname;
	@FieldValidate(description="收款人开户行")
	private String recv_bank;
	@FieldValidate(description="收款人开户行所在省",nullable=true)
	private String recv_province;
	@FieldValidate(description="收款人开户行所在市",nullable=true)
	private String recv_city;
	@FieldValidate(description="收款人开户行地址",nullable=true)
	private String recv_address;
	@FieldValidate(description="银行大类",nullable=true)
	private String recv_banktype;
	@FieldValidate(description="收款人cnaps号",nullable=true)
	private String recv_cnaps;
	@FieldValidate(description="金额")
	private String amount;
	@FieldValidate(description="发送次数")
	private String send_count;
	@FieldValidate(description="出纳处理",nullable=true)
	private String cashier_process;
	@FieldValidate(description="出纳处理日期",nullable=true)
	private String cashier_process_date;
	@FieldValidate(description="备注")
	private String memo;
	@FieldValidate(description="申请人所在单位")
	private String apply_org;
	@FieldValidate(description="申请人所在部门",nullable=true)
	private String apply_dept;
	@FieldValidate(description="付款机构类型")
	private String pay_org_type;
	
	private Record applyOrg;
	private Record recvBank;
	private Record originData;
	private int payOrgType;

	public String getFlow_id() {
		return flow_id;
	}
	public void setFlow_id(String flow_id) {
		this.flow_id = flow_id;
	}
	public String getBill_no() {
		return bill_no;
	}
	public void setBill_no(String bill_no) {
		this.bill_no = bill_no;
	}
	public String getBiz_type() {
		return biz_type;
	}
	public void setBiz_type(String biz_type) {
		this.biz_type = biz_type;
	}
	public String getApply_user() {
		return apply_user;
	}
	public void setApply_user(String apply_user) {
		this.apply_user = apply_user;
	}
	public String getBudget_user() {
		return budget_user;
	}
	public void setBudget_user(String budget_user) {
		this.budget_user = budget_user;
	}
	public String getApply_date() {
		return apply_date;
	}
	public void setApply_date(String apply_date) {
		this.apply_date = apply_date;
	}
	public String getRecv_accno() {
		return recv_accno;
	}
	public void setRecv_accno(String recv_accno) {
		this.recv_accno = recv_accno;
	}
	public String getRecv_accname() {
		return recv_accname;
	}
	public void setRecv_accname(String recv_accname) {
		this.recv_accname = recv_accname;
	}
	public String getRecv_bank() {
		return recv_bank;
	}
	public void setRecv_bank(String recv_bank) {
		this.recv_bank = recv_bank;
	}
	public String getRecv_province() {
		return recv_province;
	}
	public void setRecv_province(String recv_province) {
		this.recv_province = recv_province;
	}
	public String getRecv_city() {
		return recv_city;
	}
	public void setRecv_city(String recv_city) {
		this.recv_city = recv_city;
	}
	public String getRecv_address() {
		return recv_address;
	}
	public void setRecv_address(String recv_address) {
		this.recv_address = recv_address;
	}
	public String getRecv_banktype() {
		return recv_banktype;
	}
	public void setRecv_banktype(String recv_banktype) {
		this.recv_banktype = recv_banktype;
	}
	public String getRecv_cnaps() {
		return recv_cnaps;
	}
	public void setRecv_cnaps(String recv_cnaps) {
		this.recv_cnaps = recv_cnaps;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSend_count() {
		return send_count;
	}
	public void setSend_count(String send_count) {
		this.send_count = send_count;
	}
	public String getCashier_process() {
		return cashier_process;
	}
	public void setCashier_process(String cashier_process) {
		this.cashier_process = cashier_process;
	}
	public String getCashier_process_date() {
		return cashier_process_date;
	}
	public void setCashier_process_date(String cashier_process_date) {
		this.cashier_process_date = cashier_process_date;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getApply_org() {
		return apply_org;
	}
	public void setApply_org(String apply_org) {
		this.apply_org = apply_org;
	}
	public String getApply_dept() {
		return apply_dept;
	}
	public void setApply_dept(String apply_dept) {
		this.apply_dept = apply_dept;
	}
	public String getPay_org_type() {
		return pay_org_type;
	}
	public void setPay_org_type(String pay_org_type) {
		this.pay_org_type = pay_org_type;
	}
	public Record getApplyOrg() {
		return applyOrg;
	}
	public void setApplyOrg(Record applyOrg) {
		this.applyOrg = applyOrg;
	}
	public Record getRecvBank() {
		return recvBank;
	}
	public void setRecvBank(Record recvBank) {
		this.recvBank = recvBank;
	}
	public Record getOriginData() {
		return originData;
	}
	public void setOriginData(Record originData) {
		this.originData = originData;
	}
	
	public int getPayOrgType() {
		return payOrgType;
	}
	public void setPayOrgType(int payOrgType) {
		this.payOrgType = payOrgType;
	}
	@Override
	public String toString() {
		return "OAReciveDateReq [flow_id=" + flow_id + ", bill_no=" + bill_no + ", biz_type=" + biz_type
				+ ", apply_user=" + apply_user + ", budget_user=" + budget_user + ", apply_date=" + apply_date
				+ ", recv_accno=" + recv_accno + ", recv_accname=" + recv_accname + ", recv_bank=" + recv_bank
				+ ", recv_province=" + recv_province + ", recv_city=" + recv_city + ", recv_address=" + recv_address
				+ ", recv_banktype=" + recv_banktype + ", recv_cnaps=" + recv_cnaps + ", amount=" + amount
				+ ", send_count=" + send_count + ", cashier_process=" + cashier_process + ", cashier_process_date="
				+ cashier_process_date + ", memo=" + memo + ", apply_org=" + apply_org + ", apply_dept=" + apply_dept
				+ ", pay_org_type=" + pay_org_type + "]";
	}
	
	
	

}
