package com.qhjf.cfm.web.webservice.oa.exception;

import com.qhjf.cfm.web.webservice.oa.constant.ErrorCode;

public class WebServiceException extends  Exception {


	private static final long serialVersionUID = -8163462059735655204L;
	
	private ErrorCode errorCode;

    public WebServiceException(String message){
        super(message);
    }
    
    public WebServiceException(ErrorCode errorCode){
    	super(errorCode.getDesc());
    	this.errorCode = errorCode;
    }
    
    public WebServiceException(ErrorCode errorCode,String msg){
    	super(msg);
    	this.errorCode = errorCode;
    }

	public ErrorCode getErrorCode() {
		return errorCode;
	}
    
    
}
