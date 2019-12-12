package com.lhc.ocat.mobileplatform.exception;

/**
 * @author lhc
 * @date 2019-11-15 10:42
 */
public enum ApiErrorType implements ErrorType  {
    // 注册应用时应用名称重复
    APP_NAME_DUPLICATION_ERROR("300001", "应用名称不能重复"),
    // 未找到指定应用
    APP_NOT_FOUND_ERROR("300002", "未找到应用"),
    // 认证客户端失败
    APP_SECRET_ERROR("300003", "应用的 App Secret 错误"),
    APP_PRE_RELEASE_ERROR("300004", "应用存在未发行版本，不能再上传新版本，请先处理未发行版本"),
    RESOURCE_NOT_FOUND_ERROR("300005", "未找到版本资源"),
    RESOURCE_NOT_RELEASE_ERROR("300006", "版本资源还未正式发布"),
    RESOURCE_ENABLE_NOT_FOUND_ERROR("300007", "未找到可用的版本资源"),
    RESOURCE_DISENABLE_ERROR("300008", "该版本资源还未发行"),
    RESOURCE_RELEASE_ERROR("300009", "版本资源已经正式发布"),
    RESOURCE_NOTHING_ERROR("300010", "该应用还没有可用的版本资源"),
    // 登录，认证
    NEED_LOGIN_ERROR("300020", "您还没有登录，请先登录！"),
    UNAUTHORIZED_ERROR("300021", "您没有权限使用该功能！"),
    LOGIN_USERNAME_NULL_ERROR("300022", "登录用户名不能为空"),
    LOGIN_PASSWORD_NULL_ERROR("300023", "登录密码不能为空"),
    LOGIN_USERNAME_ERROR("300024", "登录用户名不正确"),
    LOGIN_PASSWORD_ERROR("300025", "登录密码不正确"),
    LOGIN_USERNAME_OR_PASSWORD_ERROR("300026", "登录用户名或密码不正确"),
    LOGIN_LOCKED_ACCOUNT_ERROR("300027", "登录用户被锁定"),
    LOGIN_EXCESSIVE_ATTEMPTS_ERROR("300028", "登录尝试次数过多"),
    LOGOUT_ERROR("300029", "退出登录失败"),
    // 系统管理
    SYSTEM_USER_NOT_FOUND_ERROR("300101", "系统用户未找到"),
    SYSTEM_USER_ENABLE_ILLEGAL_ERROR("300102", "系统用户是否可用值不合法"),
    SYSTEM_ROLE_NOT_FOUND_ERROR("300111", "系统角色未找到"),
    SYSTEM_PERMISSION_NOT_FOUND_ERROR("300111", "系统权限未找到"),
    SYSTEM_MENU_NOT_FOUND_ERROR("300111", "系统菜单未找到"),
    ;

    private String code;

    private String message;

    ApiErrorType(String code, String message) {
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
