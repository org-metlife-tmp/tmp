package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp;

public class PersonBillConfirmRespBean {
	/**
	 * 状态:SC成功，FL失败
	 */
	private String status;
	/**
	 * 保单号
	 */
	private String cownsel;
	/**
	 * 收据号
	 */
	private String receipt;
	/**
	 * 错误字段
	 */
	private String fldnam;
	/**
	 * 错误码
	 */
	private String errcode;
	/**
	 * 错误信息
	 */
	private String errmess;

	/**
	 * 
	 * @param status
	 * @param cownsel 保单号
	 * @param receipt
	 *            收据号
	 * @param fldnam
	 *            错误字段
	 * @param errcode
	 * @param errmess
	 */
	public PersonBillConfirmRespBean(String status, String cownsel, String receipt, String fldnam, String errcode, String errmess) {
		this.status = status;
		this.cownsel = cownsel;
		this.receipt = receipt;
		this.fldnam = fldnam;
		this.errcode = errcode;
		this.errmess = errmess;
	}

	public String getStatus() {
		return status;
	}

	public String getCownsel() {
		return cownsel;
	}

	public String getReceipt() {
		return receipt;
	}

	public String getFldnam() {
		return fldnam;
	}

	public String getErrcode() {
		return errcode;
	}

	public String getErrmess() {
		return errmess;
	}

	@Override
	public String toString() {
		return String.format("{状态[%s],保单号[%s],收据号[%s],错误字段[%s],错误码[%s],错误信息[%s]}", status, cownsel, receipt, fldnam, errcode, errmess);
	}
}
