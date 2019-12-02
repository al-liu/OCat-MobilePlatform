package com.lhc.ocat.mobileplatform.exception;

/**
 * @author lhc
 * @date 2019-10-31 17:42
 */
public enum BusinessErrorType implements ErrorType{

    /**
     *
     */
    ARGUMENT_DUPLICATION_ERROR("300001", "参数重复");

    private String code;

    private String message;

    BusinessErrorType(String code, String message) {
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
