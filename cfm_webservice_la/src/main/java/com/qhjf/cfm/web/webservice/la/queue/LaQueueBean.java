package com.qhjf.cfm.web.webservice.la.queue;

import com.qhjf.cfm.web.webservice.la.LaCallbackBean;
import org.apache.axiom.om.OMElement;

import java.util.List;


public class LaQueueBean {
	
	private List<LaCallbackBean> beans;

	private OMElement oMElement;
	
	public LaQueueBean(List<LaCallbackBean> beans,OMElement oMElement){
		this.beans = beans;
		this.oMElement = oMElement;
	}

	public List<LaCallbackBean> getBeans() {
		return beans;
	}

	public void setBeans(List<LaCallbackBean> beans) {
		this.beans = beans;
	}

	public OMElement getoMElement() {
		return oMElement;
	}

	public void setoMElement(OMElement oMElement) {
		this.oMElement = oMElement;
	}
	
	

	

	
}
