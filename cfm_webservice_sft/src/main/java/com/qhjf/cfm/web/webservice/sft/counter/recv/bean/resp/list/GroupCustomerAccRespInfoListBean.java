package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.list;

/**
 * 客户账户查询子列表
 * @author CHT
 *
 */
public class GroupCustomerAccRespInfoListBean {

	/**
	 * 客户号
	 */
	private String customerNo;
	/**
	 * 客户姓名
	 */
	private String customerName;
	/**
	 * 账户金额
	 */
	private String money;
	/**
	 * 
	 * @param customerNo 客户号
	 * @param customerName 客户姓名
	 * @param money 账户金额
	 */
	public GroupCustomerAccRespInfoListBean(String customerNo, String customerName, String money){
		this.customerNo = customerNo;
		this.customerName = customerName;
		this.money = money;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getMoney() {
		return money;
	}
	
	@Override
	public String toString() {
		return String.format("{客户号=[%s],客户姓名=[%s],账户金额=[%s]}", customerNo, customerName, money);
	}
}
