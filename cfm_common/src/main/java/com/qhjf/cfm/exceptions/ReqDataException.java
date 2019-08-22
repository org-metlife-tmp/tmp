package com.qhjf.cfm.exceptions;

public class ReqDataException extends BusinessException {

    ReqDataException(){
        setError_code("REQ_DATA_ERROR");
    }

    public ReqDataException(String message){
        super(message);
        setError_code("REQ_DATA_ERROR");
    }

    public ReqDataException(String message,Throwable cause) {
        super(message,cause);
        setError_code("REQ_DATA_ERROR");
    }

    public ReqDataException(Throwable cause) {
        super(cause);
        setError_code("REQ_DATA_ERROR");
    }
}
