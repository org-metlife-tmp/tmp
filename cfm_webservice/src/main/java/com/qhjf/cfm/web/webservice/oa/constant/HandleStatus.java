package com.qhjf.cfm.web.webservice.oa.constant;

public enum HandleStatus {
	
	OA_INTER_RECV_SUCCESS(1, "接收成功"),OA_INTER_RECV_FAIL(2, "接收失败");
	
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
