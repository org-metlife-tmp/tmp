package com.qhjf.cfm.web.webservice.ebs.queue;

import com.qhjf.cfm.web.webservice.ebs.EbsCallbackBean;

public class EbsQueueBean {

	private EbsCallbackBean beans;

	private String params;

	public EbsQueueBean(EbsCallbackBean beans, String params) {
		this.beans = beans;
		this.params = params;
	}

	public EbsCallbackBean getBeans() {
		return beans;
	}

	public void setBeans(EbsCallbackBean beans) {
		this.beans = beans;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

}
