package com.qhjf.cfm.web.webservice.nc.server.response.parent;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant.OaInterfaceStatus;
import com.qhjf.cfm.web.webservice.ann.FieldValidate;
import com.qhjf.cfm.web.webservice.nc.constant.ErrorCode;
import com.qhjf.cfm.web.webservice.nc.exception.WebServiceException;
import com.qhjf.cfm.web.webservice.nc.server.request.parent.ParentReq;

import java.math.BigDecimal;
import java.util.List;

public class NCCallBackResp extends ParentReq {

	public NCCallBackResp(String xml) throws Exception{
		super(xml);


	}

	@FieldValidate(description="流程id")
	private String flow_id;
	@FieldValidate(description="发送次数")
	private String send_count;
	@FieldValidate(description="回调状态")
	private String status ;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "";
	}
	
	
	

}
