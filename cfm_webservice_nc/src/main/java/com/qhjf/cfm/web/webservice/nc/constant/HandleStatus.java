package com.qhjf.cfm.web.webservice.nc.constant;

public enum HandleStatus {
	
	NC_INTER_RECV_SUCCESS(1, "资金平台接收成功"),NC_INTER_RECV_FAIL(2, "资金平台接收失败");
	
	Integer key;
	String desc;
	HandleStatus(int key,String desc){
		this.key = key;
		this.desc = desc;
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	

}
