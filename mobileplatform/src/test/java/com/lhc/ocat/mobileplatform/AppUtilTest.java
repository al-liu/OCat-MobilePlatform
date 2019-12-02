package com.lhc.ocat.mobileplatform;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

/**
 * @author lhc
 * @date 2019-11-15 11:02
 */
public class AppUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(AppUtilTest.class);

    @Test
    public void test10Random() {
        logger.info("10位随机数:"+getRandom(10));
    }

    @Test
    public void testUUID32() {
        logger.info("uuid:"+getUUID32());
    }

    private String getRandom(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(10));
        }
        return rs.toString();
    }

    private static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
