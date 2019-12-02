package com.lhc.ocat.mobileplatform.exception;

public enum SqlErrorType implements ErrorType {
	
	// SQLIntegrityConstraintViolationException
	INTEGRITY_CONSTRAIN_VIOLATION_ERROR("100001", "参数错误或操作异常,约束校验失败"),
	DUPLICATE_PRIMARY_KEY("100002", "唯一主键冲突");
	
	private String code;
	
	private String message;
	
	SqlErrorType(String code, String message) {
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
}
