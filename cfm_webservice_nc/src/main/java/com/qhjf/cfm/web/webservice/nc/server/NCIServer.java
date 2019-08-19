package com.qhjf.cfm.web.webservice.nc.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface NCIServer {
	
	@WebMethod
	public String reciveDate(String xml);


}
