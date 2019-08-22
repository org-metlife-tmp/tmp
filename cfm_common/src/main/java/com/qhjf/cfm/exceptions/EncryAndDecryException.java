package com.qhjf.cfm.exceptions;

/**
 * 加解密异常
 */
public class EncryAndDecryException extends BusinessException {

    EncryAndDecryException(){
        setError_code("ENCRY_DECRY_ERROR");
    }

    public EncryAndDecryException(String message){
        super(message);
        setError_code("ENCRY_DECRY_ERROR");
    }

    public EncryAndDecryException(String message,Throwable cause) {
        super(message,cause);
        setError_code("ENCRY_DECRY_ERROR");
    }

    public EncryAndDecryException(Throwable cause) {
        super(cause);
        setError_code("ENCRY_DECRY_ERROR");
    }
}
