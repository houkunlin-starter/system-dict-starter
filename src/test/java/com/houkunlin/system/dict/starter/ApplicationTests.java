package com.houkunlin.system.dict.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import test.application.common.CommandRunnerTests1;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
// @EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackageClasses = {ApplicationTests.class, CommandRunnerTests1.class})
@SystemDictScan(basePackageClasses = {CommandRunnerTests1.class})
public class ApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTests.class);
    }
}
