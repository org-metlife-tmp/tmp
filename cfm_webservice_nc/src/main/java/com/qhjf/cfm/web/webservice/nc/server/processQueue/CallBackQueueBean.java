package com.qhjf.cfm.web.webservice.nc.server.processQueue;

import com.jfinal.plugin.activerecord.Record;

public class CallBackQueueBean extends ParentQueueBean{
	
	private int type;
	private Record originData;
	private Record tranRecord;
	
	public CallBackQueueBean(Record originData,Record tranRecord){
		this.type = 2;
		this.originData = originData;
		this.tranRecord = tranRecord;
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

	public Record getTranRecord() {
		return tranRecord;
	}

	public void setTranRecord(Record tranRecord) {
		this.tranRecord = tranRecord;
	}
}
