package com.qhjf.cfm.exceptions;

public class AttachmentException extends BusinessException  {

    public AttachmentException(){
        setError_code("ATTACHMENT_ERROR");
    }

    public AttachmentException(String message){
        super(message);
        setError_code("ATTACHMENT_ERROR");
    }

    public AttachmentException(String message,Throwable cause) {
        super(message,cause);
        setError_code("ATTACHMENT_ERROR");
    }

    public AttachmentException(Throwable cause) {
        super(cause);
        setError_code("ATTACHMENT_ERROR");
    }
}
