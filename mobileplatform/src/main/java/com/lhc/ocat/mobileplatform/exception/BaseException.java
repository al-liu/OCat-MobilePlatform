package com.lhc.ocat.mobileplatform.exception;

/**
 * 自定义基础异常，包括 ErrorType（code and mesage）
 * @author lhc
 * @date 2019年7月26日
 */
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final ErrorType errorType;
	
	public BaseException() {
		this.errorType = SystemErrorType.SYSTEM_ERROR;
	}
	
	public BaseException(ErrorType errorType) {
		this.errorType = errorType;
	}
	
	public BaseException(ErrorType errorType, String message) {
		super(message);
		this.errorType = errorType;
	}
	
	public BaseException(ErrorType errorType, String message, Throwable cause) {
		super(message, cause);
		this.errorType = errorType;
	}

	public ErrorType getErrorType() {
		return errorType;
	}
	
}
