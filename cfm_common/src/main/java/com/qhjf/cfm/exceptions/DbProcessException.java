package com.qhjf.cfm.exceptions;

public class DbProcessException extends BusinessException {

    DbProcessException(){
        setError_code("DB_PROCESS_ERROR");
    }

    public DbProcessException(String message){
        super(message);
        setError_code("DB_PROCESS_ERROR");
    }

    public DbProcessException(String message,Throwable cause) {
        super(message,cause);
        setError_code("DB_PROCESS_ERROR");
    }

    public DbProcessException(Throwable cause) {
        super(cause);
        setError_code("DB_PROCESS_ERROR");
    }
}
