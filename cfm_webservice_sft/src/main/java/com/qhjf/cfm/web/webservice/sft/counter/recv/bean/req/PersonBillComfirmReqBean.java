package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 个单确认bean
 * @author CHT
 *
 */
public class PersonBillComfirmReqBean {
	/**
	 * 机构代码:固定1
	 */
	private String branch;
	/**
	 * 金额
	 */
	private String docorigamt;
	/**
	 * 付款银行账户
	 */
	private String bankacckey;
	/**
	 * 付款账号名称：可不填
	 */
	private String bankaccdsc;
	/**
	 * 银行编码：bank_key
	 */
	private String bankkey;
	/**
	 * 保单号码
	 */
	private String cownsel;
	/**
	 * 缴费方式：POS机、现金解款单、支票、网银/汇款
	 *  E：现金解款单
	 *	1：POS 机
	 *	I：支票
	 *	D：贷记凭证==网银/汇款
	 */
	private String paytype;
	/**
	 * 分公司:固定SH
	 */
	private String company;
	/**
	 * 缴费日期：当天
	 */
	private String tchqdate;
	/**
	 * 银行编码
	 */
	private String bankcode;
	
	/**
	 * LP（保费悬账）:固定LP
	 */
	private String sacscode;
	/**
	 * S（保费悬账）：固定S
	 */
	private String sacstype;
	/**
	 * 
	 * @param docorigamt 金额
	 * @param bankacckey 付款银行账户
	 * @param bankaccdsc 付款账号名称：可不填(null)
	 * @param cownsel 保单号码
	 * @param paytype 缴费方式（E：现金解款单，1：POS机，I：支票，D：贷记凭证==网银/汇款）
	 * @param bankcode 个单表的recv_bank_name（bankcode）转换成的LA的bankcode
	 */
	public PersonBillComfirmReqBean(String docorigamt, String bankacckey, String bankaccdsc, String cownsel, String paytype, String bankcode,String sacstype){
		this.branch = "SH";
		this.docorigamt = docorigamt;
		this.bankacckey = bankacckey;
		this.bankaccdsc = bankaccdsc;
		this.bankkey = null;
		this.cownsel = cownsel;
		this.paytype = paytype;
		this.company = "1";
		this.tchqdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		this.bankcode = bankcode;
		this.sacscode = "LP";
		this.sacstype = sacstype;
	}

	public String getBankacckey() {
		return bankacckey;
	}

	public String getBankaccdsc() {
		return bankaccdsc;
	}

	public void setBankaccdsc(String bankaccdsc) {
		this.bankaccdsc = bankaccdsc;
	}

	public String getBankkey() {
		return bankkey;
	}

	public String getBranch() {
		return branch;
	}

	public String getDocorigamt() {
		return docorigamt;
	}

	public String getCownsel() {
		return cownsel;
	}

	public String getPaytype() {
		return paytype;
	}

	public String getCompany() {
		return company;
	}

	public String getTchqdate() {
		return tchqdate;
	}

	public String getBankcode() {
		return bankcode;
	}

	public String getSacscode() {
		return sacscode;
	}

	public String getSacstype() {
		return sacstype;
	}
	
}
