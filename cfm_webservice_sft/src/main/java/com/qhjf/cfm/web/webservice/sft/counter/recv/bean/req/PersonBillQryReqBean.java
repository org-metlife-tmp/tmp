package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req;

/**
 * 单据查询的请求参数
 * @author CHT
 *
 */
public class PersonBillQryReqBean {

	/**
	 * 保单号
	 */
	private String insureBillNo;
	
	public PersonBillQryReqBean(String insureBillNo){
		this.insureBillNo = insureBillNo;
	}

	public String getInsureBillNo() {
		return insureBillNo;
	}
	
}
