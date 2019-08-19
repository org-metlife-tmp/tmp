package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp;

/**
 * 保单撤销bean
 * @author CHT
 *
 */
public class PersonBillCancelRespBean {

	/**
	 * 状态:SC成功，FL失败
	 */
	private String status;
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
	 * @param fldnam
	 * @param errcode
	 * @param errmess
	 */
	public PersonBillCancelRespBean(String status, String fldnam, String errcode, String errmess){
		this.status = status;
		this.fldnam = fldnam;
		this.errcode = errcode;
		this.errmess = errmess;
	}
	public String getStatus() {
		return status;
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
		return String.format("{状态[%s],错误字段[%s],错误码[%s],错误信息[%s]}", status, fldnam, errcode, errmess);
	}
}
