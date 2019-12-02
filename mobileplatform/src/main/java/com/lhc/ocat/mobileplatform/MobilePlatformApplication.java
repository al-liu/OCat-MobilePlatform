package com.lhc.ocat.mobileplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * @author lhc
 * @date 2019-10-18 17:07
 */
@SpringBootApplication
@Configuration
@MapperScan("com.lhc.ocat.mobileplatform.mapper")
public class MobilePlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(MobilePlatformApplication.class, args);
    }
}
