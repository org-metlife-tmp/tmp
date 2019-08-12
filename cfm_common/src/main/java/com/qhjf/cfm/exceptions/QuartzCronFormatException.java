package com.qhjf.cfm.exceptions;

public class QuartzCronFormatException extends BusinessException {

    public QuartzCronFormatException() {
        super();
        setError_code("CRONFORAMTERROR");
    }


    public QuartzCronFormatException(String message) {
        super(message);
        setError_code("CRONFORAMTERROR");
    }


    public QuartzCronFormatException(String message, Throwable cause) {
        super(message, cause);
        setError_code("CRONFORAMTERROR");
    }

    public QuartzCronFormatException(Throwable cause) {
        super(cause);
        setError_code("CRONFORAMTERROR");
    }

}
