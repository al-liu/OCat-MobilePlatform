package com.lhc.ocat.android_demo.manager;

/**
 * Created by lhc on 2019/12/16.
 */

public class PackageError extends Error {
    private int code;
    private String description;
    private String reason;
    private String suggestion;

    static final String LAUNCH_FAIL_DESCRIPTION = "包管理器启动失败";
    static final String UPDATE_FAIL_DESCRIPTION = "包管理器更新补丁失败";

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public PackageError(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public PackageError(int code, String description, Throwable cause) {
        super(cause);
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return "PackageError{" +
                "code=" + code +
                ", description='" + description + '\'' +
                ", reason='" + reason + '\'' +
                ", suggestion='" + suggestion + '\'' +
                '}';
    }
}
