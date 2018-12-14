package com.qhjf.cfm.web.webservice.oa.server.processQueue;

import com.jfinal.plugin.activerecord.Record;

public class CallBackQueueBean extends ParentQueueBean{
	
	private int type;
	private Record originData;
	
	public CallBackQueueBean(Record originData){
		this.type = 2;
		this.originData = originData;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Record getOriginData() {
		return originData;
	}
	public void setOriginData(Record originData) {
		this.originData = originData;
	}
	
	

}
