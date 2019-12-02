package com.lhc.ocat.mobileplatform.exception;

/**
 * 
 * @author lhc
 * @date 2019年7月26日
 * 自定义系统异常的小分类可以划分到单独的枚举类中
 */
public enum SystemErrorType implements ErrorType {

	/**
	 * 
	 */
	SYSTEM_ERROR("000001", "系统异常"),
	SYSTEM_BUSY("000002", "系统繁忙，请稍后再试"),
	SYSTEM_IO_ERROR("000003", "系统 IO 异常"),
	SYSTEM_NULL_POINT_ERROR("000004", "空指针异常，请稍后再试");

	public static final String IO_ERROR_CODE = "000003";

	private String code;
	
	private String message;
	
	SystemErrorType(String code, String message) {
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
