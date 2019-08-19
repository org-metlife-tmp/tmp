package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp;

public class GroupBizPayConfirmRespBean {
	/**
	 * 错误码:SUCCESS/FAIL
	 */
	private String resultCode;
	/**
	 * 错误信息
	 */
	private String resultMsg;
	/**
	 * 资金流水号
	 */
	private String payNo;

	/**
	 * 
	 * @param resultCode
	 *            错误码:SUCCESS/FAIL
	 * @param resultMsg
	 *            错误信息
	 * @param payNo
	 *            资金流水号
	 */
	public GroupBizPayConfirmRespBean(String resultCode, String resultMsg, String payNo) {
		this.resultCode = resultCode;
		this.resultMsg = resultMsg;
		this.payNo = payNo;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public String getPayNo() {
		return payNo;
	}
	
	@Override
	public String toString() {
		return String.format("{错误码=[%s],错误信息=[%s],资金流水号=[%s]}", resultCode, resultMsg, payNo);
	}
}
