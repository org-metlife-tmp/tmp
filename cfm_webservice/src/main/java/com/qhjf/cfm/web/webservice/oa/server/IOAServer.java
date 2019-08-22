package com.qhjf.cfm.web.webservice.oa.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface IOAServer {
	
	@WebMethod
	public String reciveDate(String xml);
	
	@WebMethod
	public String queryStatus(String xml);

}
