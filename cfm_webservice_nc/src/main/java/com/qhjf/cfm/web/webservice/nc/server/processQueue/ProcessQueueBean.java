package com.qhjf.cfm.web.webservice.nc.server.processQueue;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.nc.server.request.NCReciveDateReq;

public class ProcessQueueBean extends ParentQueueBean{
	
	private int type;
	private NCReciveDateReq req;
	private Record originData;
	
	
	public ProcessQueueBean(NCReciveDateReq req,Record originData){
		this.type  =1;
		this.req = req;
		this.originData = originData;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public NCReciveDateReq getReq() {
		return req;
	}
	public void setReq(NCReciveDateReq req) {
		this.req = req;
	}
	public Record getOriginData() {
		return originData;
	}
	public void setOriginData(Record originData) {
		this.originData = originData;
	}
	
	

}
