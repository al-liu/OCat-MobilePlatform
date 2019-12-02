package com.lhc.ocat.mobileplatform.exception;

/**
 * 
 * @author lhc
 * @date 2019年7月26日
 */
public interface ErrorType {
	
	/**
	 * 返回码
	 * @return String
	 */
	String getCode();
	
	/**
	 * 返回信息
	 * @return String
	 */
	String getMessage();
}
