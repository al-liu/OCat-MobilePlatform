package com.lhc.ocat.mobileplatform.domain.vo;

import java.time.Instant;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lhc.ocat.mobileplatform.exception.BaseException;
import com.lhc.ocat.mobileplatform.exception.ErrorType;
import com.lhc.ocat.mobileplatform.exception.SystemErrorType;

/**
 * 
 * @author lhc
 * @date 2019年8月27日
 * @param <T>
 */
public class Result<T> {

	private static final String SUCCESSFUL_CODE = "000000";
	private static final String SUCCESSFUL_MESSAGE = "成功";
	
	private String code;
	
	private String message;
	
	private Instant timestamp;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;
	
	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public T getData() {
		return data;
	}

	/**
	 * 默认构造方法
	 */
	public Result() {
		this.timestamp = ZonedDateTime.now().toInstant();
	}
	
	public Result(ErrorType errorType) {
		this.code = errorType.getCode();
		this.message = errorType.getMessage();
		this.timestamp = ZonedDateTime.now().toInstant();
	}
	
	public Result(ErrorType errorType, T data) {
		this(errorType);
		this.data = data;
	}
	
	public Result(String code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.timestamp = ZonedDateTime.now().toInstant();
	}
	
	/**
	 * 快速构建的成功返回结果
	 * @param data 处理结果数据
	 * @return Result
	 */
	public static Result<Object> success(Object data) {
		return new Result<>(SUCCESSFUL_CODE, SUCCESSFUL_MESSAGE, data);
	}
	
	/**
	 * 快速构建成功返回结果，不带处理结果数据
	 * @return Result
	 */
	public static Result<?> success() {
		return Result.success(null);
	}
	
	/**
	 * 快速构建 “系统异常” 的返回结果
	 * @return Result
	 */
	public static Result<?> fail() {
		return new Result<>(SystemErrorType.SYSTEM_ERROR);
	}
	
	/**
	 * 构建带异常的返回结果，不带处理结果信息
	 * @param baseException 基础异常
	 * @return Result
	 */
	public static Result<?> fail(BaseException baseException) {
		return Result.fail(baseException, null);
	}
	
	/**
	 * 构建带异常的返回结果，带处理结果信息
	 * @param baseException 基础异常
	 * @param data 返回数据
	 * @return Result
	 */
	public static Result<?> fail(BaseException baseException, Object data) {
		return new Result<>(baseException.getErrorType(), data);
	}

	/**
	 * 构建带 ErrorType 的返回结果，带处理结果信息
	 * @param errorType 错误类型接口
	 * @param data 基础异常
	 * @return Result
	 */
	public static Result<?> fail(ErrorType errorType, Object data) {
		return new Result<>(errorType, data);
	}
	
	public static Result<?> fail(ErrorType errorType) {
		return Result.fail(errorType, null);
	}

	public static Result<?> fail(Object data) {
		return new Result<>(SystemErrorType.SYSTEM_ERROR, data);
	}
	
	/**
	 * 判断返回结果是否成功
	 * @return code 是 SUCCESSFUL_CODE（000000）时是 true，否则是 false
	 */
	public boolean isSuccess() {
		return SUCCESSFUL_CODE.equals(this.code);
	}
	
	/**
	 * 判断返回结果是否成功
	 * @return @return code 不是 SUCCESSFUL_CODE（000000）时是 true，否则是 false
	 */
	public boolean isFail() {
		return !isSuccess();
	}

	@Override
	public String toString() {
		return "Result [code=" + code + ", message=" + message + ", timestamp=" + timestamp + ", data=" + data + "]";
	}
	
}
