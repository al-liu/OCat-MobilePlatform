package com.lhc.ocat.mobileplatform;

import com.lhc.ocat.mobileplatform.systemmanage.ShiroConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.Test;

/**
 * @author lhc
 * @date 2019-11-29 10:51
 */
@Log4j2
public class ShiroTest {

    @Test
    public void testShiroSalt() {
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        log.info("盐:"+salt);
    }

    @Test
    public void testShiroEncodePassword() {
        String encodePassword1 = new SimpleHash(ShiroConfig.HASH_ALGORITHM_NAME, "a123", "mmbSTHo0ONEeauusz7B52A==", ShiroConfig.HASH_ITERATION_TIME).toString();
        log.info("密码a123:"+encodePassword1);
        String encodePassword2 = new SimpleHash(ShiroConfig.HASH_ALGORITHM_NAME, "a321", "9Qlv4I5KB6Tx1bIAV0EzOw==", ShiroConfig.HASH_ITERATION_TIME).toString();
        log.info("密码a321:"+encodePassword2);
    }
}
