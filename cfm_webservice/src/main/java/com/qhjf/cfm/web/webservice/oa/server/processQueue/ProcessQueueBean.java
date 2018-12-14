package com.qhjf.cfm.web.webservice.oa.server.processQueue;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.oa.server.request.OAReciveDateReq;

public class ProcessQueueBean extends ParentQueueBean{
	
	private int type;
	private OAReciveDateReq req;
	private Record originData;
	
	
	public ProcessQueueBean(OAReciveDateReq req,Record originData){
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

	public OAReciveDateReq getReq() {
		return req;
	}
	public void setReq(OAReciveDateReq req) {
		this.req = req;
	}
	public Record getOriginData() {
		return originData;
	}
	public void setOriginData(Record originData) {
		this.originData = originData;
	}
	
	

}
