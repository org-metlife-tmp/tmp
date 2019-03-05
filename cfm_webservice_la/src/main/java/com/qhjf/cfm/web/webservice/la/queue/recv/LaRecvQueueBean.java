package com.qhjf.cfm.web.webservice.la.queue.recv;

import java.util.List;
import org.apache.axiom.om.OMElement;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;


public class LaRecvQueueBean {
	
	private List<LaRecvCallbackBean> beans;

	private OMElement oMElement;
	
	public LaRecvQueueBean(List<LaRecvCallbackBean> beans,OMElement oMElement){
		this.beans = beans;
		this.oMElement = oMElement;
	}

	public List<LaRecvCallbackBean> getBeans() {
		return beans;
	}

	public void setBeans(List<LaRecvCallbackBean> beans) {
		this.beans = beans;
	}

	public OMElement getoMElement() {
		return oMElement;
	}

	public void setoMElement(OMElement oMElement) {
		this.oMElement = oMElement;
	}
	
	

	

	
}
