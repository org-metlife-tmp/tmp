package com.qhjf.cfm.web.webservice.oa.server.request;

import com.qhjf.cfm.web.webservice.ann.FieldValidate;
import com.qhjf.cfm.web.webservice.oa.server.request.parent.ParentReq;

public class OAQueryStatusReq extends ParentReq{
	
	public OAQueryStatusReq(String xml) throws Exception {
		super(xml);
	}
	
	@FieldValidate(description="流程id")
	private String flow_id;
	@FieldValidate(description="发送次数")
	private String send_count;
	
	public String getFlow_id() {
		return flow_id;
	}
	public void setFlow_id(String flow_id) {
		this.flow_id = flow_id;
	}
	public String getSend_count() {
		return send_count;
	}
	public void setSend_count(String send_count) {
		this.send_count = send_count;
	}
	@Override
	public String toString() {
		return "OAQueryStatusReq [flow_id=" + flow_id + ", send_count=" + send_count + "]";
	}
	
	

}
