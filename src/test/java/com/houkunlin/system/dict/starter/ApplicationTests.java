package com.houkunlin.system.dict.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
// @EnableAsync
@EnableScheduling
@SpringBootApplication
@SystemDictScan
public class ApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTests.class);
    }
}
