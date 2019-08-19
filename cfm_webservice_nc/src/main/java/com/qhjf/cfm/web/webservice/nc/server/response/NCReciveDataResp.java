package com.qhjf.cfm.web.webservice.nc.server.response;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.nc.server.response.parent.ParentResp;

public class NCReciveDataResp extends ParentResp {
	
	private String flow_id;
	private Integer send_count;
	private Integer handle_status;
	private String handle_err_code;
	private String handle_err_message;
	
	public NCReciveDataResp(JSONObject json,int handle_status){
		this.flow_id = json==null?null:json.getString("flow_id");
		this.send_count = json==null?-1:json.getInteger("send_count");
		this.handle_status = handle_status;
	}
	
	public NCReciveDataResp(JSONObject json,int handle_status,String handle_err_code,String handle_err_message){
		this.flow_id = json==null?null:json.getString("flow_id");
		this.send_count = json==null?-1:json.getInteger("send_count");
		this.handle_status = handle_status;
		this.handle_err_code = handle_err_code;
		this.handle_err_message = handle_err_message;
	}

	public String getFlow_id() {
		return flow_id;
	}

	public void setFlow_id(String flow_id) {
		this.flow_id = flow_id;
	}

	public Integer getSend_count() {
		return send_count;
	}

	public void setSend_count(Integer send_count) {
		this.send_count = send_count;
	}

	public int getHandle_status() {
		return handle_status;
	}

	public void setHandle_status(int handle_status) {
		this.handle_status = handle_status;
	}

	public String getHandle_err_code() {
		return handle_err_code;
	}

	public void setHandle_err_code(String handle_err_code) {
		this.handle_err_code = handle_err_code;
	}

	public String getHandle_err_message() {
		return handle_err_message;
	}

	public void setHandle_err_message(String handle_err_message) {
		this.handle_err_message = handle_err_message;
	}

}
