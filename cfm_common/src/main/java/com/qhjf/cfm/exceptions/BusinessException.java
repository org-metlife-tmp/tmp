package com.qhjf.cfm.exceptions;

public abstract class BusinessException extends  Exception {

    private String error_code;

    public BusinessException(){
        super();
    }

    public BusinessException(String message){
        super(message);
    }

    public BusinessException(String message,Throwable cause) {
        super(message,cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }
}
