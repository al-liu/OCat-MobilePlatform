package com.lhc.ocat.mobileplatform.exception;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lhc.ocat.mobileplatform.domain.vo.Result;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 
 * @author lhc
 * @date 2019年8月27日
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final String GLOBAL_EXCEPTION_ERROR_CODE = "999999";
	
	/**
	 * 接口异常处理
	 * @param request 请求
	 * @param response 响应
	 * @param exception 异常
	 * @return Result<?>
	 */
	@ExceptionHandler(ApiException.class)
    public Result<?> processApiException(HttpServletRequest request,
										 HttpServletResponse response,
										 ApiException exception) {
        return Result.fail(exception.getErrorType());
    }

	/**
	 * 统一处理 ShiroException
	 * @param exception Shiro 相关异常
	 * @return Result
	 */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(ShiroException.class)
	public Result handleShiroException(ShiroException exception) {
		HttpErrorType httpErrorType = HttpErrorType.UNAUTHORIZED_ERROR;
		httpErrorType.setMessage(exception.getMessage());
		return Result.fail(httpErrorType);
	}
	
	/**
     * 方法参数校验
	 * domain 实体的 javax.validation.constraints 校验，如 @NotBlank。
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleMethodArgumentNotValidException(BindException e) {
    	HttpErrorType httpErrorType = HttpErrorType.ARGUMENT_NOT_VALID_ERROR;
		FieldError fieldError = e.getBindingResult().getFieldError();
		if (Objects.nonNull(fieldError)) {
			httpErrorType.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
		}
        return Result.fail(httpErrorType);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> processDefaultException(HttpServletRequest request,
											 HttpServletResponse response,
											 Exception exception) {

		ErrorType errorType = new ErrorType() {
			
			@Override
			public String getMessage() {
				return Objects.isNull(exception.getMessage()) ? exception.toString() : exception.getMessage();
			}
			
			@Override
			public String getCode() {
				return GLOBAL_EXCEPTION_ERROR_CODE;
			}
		};
		if (exception instanceof SQLIntegrityConstraintViolationException) {
			errorType = SqlErrorType.INTEGRITY_CONSTRAIN_VIOLATION_ERROR;
		} else if (exception instanceof HttpMediaTypeNotSupportedException) {
			errorType = HttpErrorType.MEDIA_TYPE_NOT_SUPPORTED_ERROR;
		} else if (exception instanceof NullPointerException) {
			errorType = SystemErrorType.SYSTEM_NULL_POINT_ERROR;
		}

		return Result.fail(errorType);
    }
}
