package com.lhc.ocat.mobileplatform.controller;

import com.lhc.ocat.mobileplatform.domain.dos.UserDO;
import com.lhc.ocat.mobileplatform.domain.dto.User;
import com.lhc.ocat.mobileplatform.domain.vo.Result;
import com.lhc.ocat.mobileplatform.exception.ApiErrorType;
import com.lhc.ocat.mobileplatform.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-26 09:35
 */
@Validated
@RestController
@Log4j2
public class SystemAuthenticationController {

    @GetMapping("/needLogin")
    public Result needLogin() {
        return Result.fail(ApiErrorType.NEED_LOGIN_ERROR);
    }

    @GetMapping("/unauthorized")
    public Result unauthorized() {
        return Result.fail(ApiErrorType.UNAUTHORIZED_ERROR);
    }

    @PostMapping(value = "/login")
    public Result login(@RequestBody Map<String, String> params)  {
        String username = params.get("username");
        String password = params.get("password");
        if (Objects.isNull(username) || username.isEmpty()) {
            throw new ApiException(ApiErrorType.LOGIN_USERNAME_NULL_ERROR);
        }
        if (Objects.isNull(password) || password.isEmpty()) {
            throw new ApiException(ApiErrorType.LOGIN_PASSWORD_NULL_ERROR);
        }
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject currentSubject = SecurityUtils.getSubject();

        ApiErrorType errorType = ApiErrorType.LOGIN_USERNAME_OR_PASSWORD_ERROR;
        try {
            log.info("开始对用户[" + username + "]进行登录验证");
            currentSubject.login(token);
            if (currentSubject.isAuthenticated()) {
                log.info("用户[" + username + "]登录验证通过");
                UserDO userDO = (UserDO) currentSubject.getPrincipal();
                User user = new User();
                BeanUtils.copyProperties(userDO, user);
                return Result.success(user);
            }
        } catch (UnknownAccountException uae) {
            log.info("用户[" + username + "]登录验证未通过,未知账户");
            errorType = ApiErrorType.LOGIN_USERNAME_ERROR;
        } catch (IncorrectCredentialsException ice) {
            log.info("用户[" + username + "]登录验证未通过,错误凭证");
            errorType = ApiErrorType.LOGIN_PASSWORD_ERROR;
        } catch (LockedAccountException lae) {
            log.info("用户[" + username + "]登录验证未通过,账户已锁定");
            errorType = ApiErrorType.LOGIN_LOCKED_ACCOUNT_ERROR;
        } catch (ExcessiveAttemptsException eae) {
            log.info("用户[" + username + "]登录验证未通过,尝试次数过多");
            errorType = ApiErrorType.LOGIN_EXCESSIVE_ATTEMPTS_ERROR;
        } catch (AuthenticationException ae) {
            log.info("用户[" + username + "]登录验证未通过,堆栈如下:");
            ae.printStackTrace();
        }
        token.clear();
        return Result.fail(errorType);
    }

    @GetMapping(value = "/logout")
    public Result logout() {
        Subject currentSubject = SecurityUtils.getSubject();
        currentSubject.logout();
        if (currentSubject.isAuthenticated()) {
            return Result.fail(ApiErrorType.LOGOUT_ERROR);
        }
        return Result.success();
    }
}
