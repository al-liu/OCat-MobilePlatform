package com.lhc.ocat.mobileplatform.exception;

/**
 * @author lhc
 * @date 2019年7月26日
 */
public class ApiException extends BaseException {

	private static final long serialVersionUID = 1L;

	public ApiException(ErrorType errorType) {
		super(errorType);
	}
	
	public ApiException(ErrorType errorType, String message) {
		super(errorType, message);
	}
}
