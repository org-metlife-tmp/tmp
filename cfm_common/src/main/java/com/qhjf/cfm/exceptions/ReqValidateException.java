package com.qhjf.cfm.exceptions;

public class ReqValidateException extends BusinessException {

    ReqValidateException(){
        setError_code("REQ_VALID_ERROR");
    }

    public ReqValidateException(String message){
        super(message);
        setError_code("REQ_VALID_ERROR");
    }

    public ReqValidateException(String message,Throwable cause) {
        super(message,cause);
        setError_code("REQ_VALID_ERROR");
    }

    public ReqValidateException(Throwable cause) {
        super(cause);
        setError_code("REQ_VALID_ERROR");
    }
}
