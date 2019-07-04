package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp;

/**
 * 保单查询的响应对象
 * 
 * 返回字段：1、 投保人2、 投保人证件号3、 保单机构4、 核心系统5、 保费标准6、 暂记余额7、 保单状态8、 是否APL垫交中9、 是否银行转账中
 * 
 * @author CHT
 *
 */
public class PersonBillQryRespBean {
	/**
	 * 投保人
	 */
	private String policyHolder;
	/**
	 * 投保人客户号
	 */
	private String policyHolderClientNo;
	/**
	 * 投保人证件号
	 */
	private String policyHolderCert;

	/**
	 * 保单机构编码
	 */
	private String insureOrgCode;
	/**
	 * 保单机构名称
	 */
//	private String insureOrgName;
	/**
	 * 核心系统：LA(0, "LA"), EBS(1, "EBS")，NB(2, "NB")
	 */
	private String sourceSys;
	/**
	 * 保费标准
	 */
	private String premiumStandard;
	/**
	 * 暂记余额
	 */
	private String suspenseBalance;
	/**
	 * 保单状态
	 */
	private String insureStatus;
	/**
	 * 是否APL垫交中
	 */
	private String isPadPayment;
	/**
	 * 是否银行转账中:0否，1是
	 */
	private String isTransAccount;
	private String bankcode;
	
	/**
	 * 
	 * @param policyHolder 投保人
	 * @param policyHolderClientNo 投保人客户号
	 * @param policyHolderCert 投保人证件号
	 * @param insureOrgCode 保单机构编码
	 * @param insureOrgName 保单机构名称
	 */
	public PersonBillQryRespBean(String policyHolder, String policyHolderClientNo,String policyHolderCert, String insureOrgCode/*, String insureOrgName*/){
		this.policyHolder = policyHolder;
		this.policyHolderClientNo = policyHolderClientNo;
		this.policyHolderCert = policyHolderCert;
		this.insureOrgCode = insureOrgCode;
//		this.insureOrgName = insureOrgName;
	}
	
	public String getPolicyHolder() {
		return policyHolder;
	}
	public String getPolicyHolderClientNo() {
		return policyHolderClientNo;
	}

	public void setPolicyHolderClientNo(String policyHolderClientNo) {
		this.policyHolderClientNo = policyHolderClientNo;
	}

	public String getPolicyHolderCert() {
		return policyHolderCert;
	}
	public String getInsureOrgCode() {
		return insureOrgCode;
	}
	/*public String getInsureOrgName() {
		return insureOrgName;
	}*/
	public String getSourceSys() {
		return sourceSys;
	}
	public void setSourceSys(String sourceSys) {
		this.sourceSys = sourceSys;
	}
	public String getPremiumStandard() {
		return premiumStandard;
	}
	public void setPremiumStandard(String premiumStandard) {
		this.premiumStandard = premiumStandard;
	}
	public String getSuspenseBalance() {
		return suspenseBalance;
	}
	public void setSuspenseBalance(String suspenseBalance) {
		this.suspenseBalance = suspenseBalance;
	}
	public String getInsureStatus() {
		return insureStatus;
	}
	public void setInsureStatus(String insureStatus) {
		this.insureStatus = insureStatus;
	}
	public String getIsPadPayment() {
		return isPadPayment;
	}
	public void setIsPadPayment(String isPadPayment) {
		this.isPadPayment = isPadPayment;
	}
	public String getIsTransAccount() {
		return isTransAccount;
	}
	public void setIsTransAccount(String isTransAccount) {
		this.isTransAccount = isTransAccount;
	}
	
	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	@Override
	public String toString() {
		return String.format("{投保人=[%s],投保人客户号=[%s],投保人证件号=[%s],保单机构编码=[%s],"
				+ "核心系统=[%s],保费标准=[%s],暂记余额=[%s],保单状态=[%s],是否APL垫交中=[%s],是否银行转账中=[%s]}", policyHolder,
				policyHolderClientNo, policyHolderCert, insureOrgCode, sourceSys, premiumStandard, suspenseBalance,
				insureStatus, isPadPayment, isTransAccount);
	}
}
