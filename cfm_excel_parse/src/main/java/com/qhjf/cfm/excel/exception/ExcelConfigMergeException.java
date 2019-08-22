package com.qhjf.cfm.excel.exception;

/**
 * Excel配置文件解析异常
 * @author CHT
 *
 */
public class ExcelConfigMergeException extends Exception {
	private static final long serialVersionUID = -1067375792545201090L;

	private String errorCode;
	private String errorMessage;

    public ExcelConfigMergeException(){
        super();
    }

    public ExcelConfigMergeException(String errorMessage){
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public ExcelConfigMergeException(String errorMessage,Throwable cause) {
        super(errorMessage,cause);
        this.errorMessage = errorMessage;
    }

    public ExcelConfigMergeException(Throwable cause) {
        super(cause);
    }

	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
