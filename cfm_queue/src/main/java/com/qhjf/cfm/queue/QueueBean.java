package com.qhjf.cfm.queue;

import java.util.Map;

import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;

/**
 * 队列发送bean
 * @author yunxw
 *
 */
public class QueueBean {
	
	private ISysAtomicInterface sysInter; //发送接口
	private Map<String,Object> params; //发送参数
	private String bankCode;
	
	public QueueBean(ISysAtomicInterface sysInter,Map<String,Object> params,String bankCode){
		this.sysInter = sysInter;
		this.params = params;
		this.bankCode = bankCode;
	}

	public ISysAtomicInterface getSysInter() {
		return sysInter;
	}

	public void setSysInter(ISysAtomicInterface sysInter) {
		this.sysInter = sysInter;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	
}
