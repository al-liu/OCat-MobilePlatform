package com.lhc.ocat.mobileplatform.exception;

/**
 * @author lhc
 */
public enum HttpErrorType implements ErrorType {

	// MethodArgumentNotValidException
	ARGUMENT_NOT_VALID_ERROR("200001", "参数错误"),
	// HttpMediaTypeNotSupportedException
	MEDIA_TYPE_NOT_SUPPORTED_ERROR("200002", "不支持的媒体类型"),
	UNAUTHORIZED_ERROR("200003", "认证错误")
	;
	
	private String code;
	
	private String message;
	
	HttpErrorType(String code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
