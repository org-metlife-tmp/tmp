package com.qhjf.cfm.exceptions;

public class WorkflowException extends BusinessException {

    public WorkflowException(){
        setError_code("WORKFLOW_ERROR");
    }

    public WorkflowException(String message){
        super(message);
        setError_code("WORKFLOW_ERROR");
    }

    public WorkflowException(String message,Throwable cause) {
        super(message,cause);
        setError_code("WORKFLOW_ERROR");
    }

    public WorkflowException(Throwable cause) {
        super(cause);
        setError_code("WORKFLOW_ERROR");
    }
}
