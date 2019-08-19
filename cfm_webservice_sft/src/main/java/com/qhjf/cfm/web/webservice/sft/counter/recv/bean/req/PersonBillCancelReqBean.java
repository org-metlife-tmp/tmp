package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req;

/**
 * 保单撤销bean
 * @author CHT
 *
 */
public class PersonBillCancelReqBean {

	/**
	 * 收据号
	 */
	private String recept;
	
	/**
	 * 
	 * @param recept 保单号
	 */
	public PersonBillCancelReqBean(String recept){
		this.recept = recept;
	}

	public String getRecept() {
		return recept;
	}
	
}
