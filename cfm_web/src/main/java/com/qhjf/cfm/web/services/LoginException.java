package com.qhjf.cfm.web.services;

import com.qhjf.cfm.exceptions.BusinessException;

public class LoginException extends BusinessException {
    LoginException(){
        setError_code("LOGIN_ERROR");
    }

    public LoginException(String message){
        super(message);
        setError_code("LOGIN_ERROR");
    }

    public LoginException(String message,Throwable cause) {
        super(message,cause);
        setError_code("LOGIN_ERROR");
    }

    public LoginException(Throwable cause) {
        super(cause);
        setError_code("LOGIN_ERROR");
    }
}
