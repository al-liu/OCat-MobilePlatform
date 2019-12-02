package com.lhc.ocat.mobileplatform.systemmanage;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author lhc
 * @date 2019-11-25 11:58
 */
@Configuration
public class ShiroConfig {

    public static final String HASH_ALGORITHM_NAME = "md5";
    public static final int HASH_ITERATION_TIME = 3;

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/needLogin");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 权限控制
        filterChainDefinitionMap.put("/application/**", "rest[application]");
        filterChainDefinitionMap.put("/package/publish/**", "perms[package:publish]");
        filterChainDefinitionMap.put("/package/release/**", "perms[package:release]");
        filterChainDefinitionMap.put("/system/user/**", "rest[system:user]");
        filterChainDefinitionMap.put("/system/role/**", "rest[system:role]");
        filterChainDefinitionMap.put("/system/permission/**", "rest[system:permission]");
        filterChainDefinitionMap.put("/system/menu/**", "rest[system:menu]");
        // 认证控制
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/needLogin", "anon");
        filterChainDefinitionMap.put("/unauthorized", "anon");
        filterChainDefinitionMap.put("/package/fetch/**", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName(HASH_ALGORITHM_NAME);
        hashedCredentialsMatcher.setHashIterations(HASH_ITERATION_TIME);
        return hashedCredentialsMatcher;
    }

    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return userRealm;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        return securityManager;
    }

}
