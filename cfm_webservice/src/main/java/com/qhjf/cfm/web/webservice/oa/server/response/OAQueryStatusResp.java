package com.qhjf.cfm.web.webservice.oa.server.response;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.oa.server.response.parent.ParentResp;

public class OAQueryStatusResp extends ParentResp{
	
	private String flow_id;
	private Integer send_count;
	private Integer handleStatus;
	private String handleErrMessage;
	private Integer billStatus;
	private String bill_err_code;
	private String billErrMessage;
	
	public OAQueryStatusResp(JSONObject json,Integer handleStatus,String handleErrMessage,Integer billStatus,String bill_err_code,String billErrMessage){
		this.flow_id = json==null?null:json.getString("flow_id");
		this.send_count = json==null?null:json.getInteger("send_count");
		this.handleStatus = handleStatus;
		this.billStatus = billStatus;
		this.bill_err_code = bill_err_code;
		this.billErrMessage = billErrMessage;
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

	public Integer getHandleStatus() {
		return handleStatus;
	}

	public void setHandleStatus(Integer handleStatus) {
		this.handleStatus = handleStatus;
	}

	public String getHandleErrMessage() {
		return handleErrMessage;
	}

	public void setHandleErrMessage(String handleErrMessage) {
		this.handleErrMessage = handleErrMessage;
	}

	public Integer getBillStatus() {
		return billStatus;
	}

	public void setBillStatus(Integer billStatus) {
		this.billStatus = billStatus;
	}

	
	public String getBill_err_code() {
		return bill_err_code;
	}

	public void setBill_err_code(String bill_err_code) {
		this.bill_err_code = bill_err_code;
	}

	public String getBillErrMessage() {
		return billErrMessage;
	}

	public void setBillErrMessage(String billErrMessage) {
		this.billErrMessage = billErrMessage;
	}


}
