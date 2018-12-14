package com.qhjf.cfm.excel.exception;

public class CellValueUndesirableException extends Exception {
	private static final long serialVersionUID = -4609897548724577716L;
	private String errorMessage;
	
	public CellValueUndesirableException() {
		super();
	}

	public CellValueUndesirableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.errorMessage = message;
	}

	public CellValueUndesirableException(String message, Throwable cause) {
		super(message, cause);
		this.errorMessage = message;
	}

	public CellValueUndesirableException(String message) {
		super(message);
		this.errorMessage = message;
	}

	public CellValueUndesirableException(Throwable cause) {
		super(cause);
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
