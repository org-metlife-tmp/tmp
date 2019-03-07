package com.qhjf.cfm.web.webservice.ebs.queue;

import com.qhjf.cfm.web.webservice.ebs.EbsCallbackBean;

import java.util.List;

public class EbsQueueBean {

	private List<EbsCallbackBean> beans;

	private String params;

	public EbsQueueBean(List<EbsCallbackBean> beans, String params) {
		this.beans = beans;
		this.params = params;
	}

	public List<EbsCallbackBean> getBeans() {
		return beans;
	}

	public void setBeans(List<EbsCallbackBean> beans) {
		this.beans = beans;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

}
