package com.qhjf.cfm.web.webservice.nc.constant;

public enum ErrorCode {
	
	P0001("报文格式错误"),
	P0002("参数错误"),
	P0003("未查到申请人"),
	P0004("未查到申请人所属公司"),
	P0005("未匹配到收款银行"),
	P0006("匹配到多个收款银行"),
	P0007("交易数据不存在"),
	P0008("流水号重复"),
	P0009("金额非法"),
	P0098("资金系统作废"),
	P0099("资金系统异常");
	
	String desc;
	ErrorCode(String desc){
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
