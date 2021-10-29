package com.houkunlin.system.dict.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import test.application.common.CommandRunnerTests;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackageClasses = {ApplicationTests.class, CommandRunnerTests.class})
@SystemDictScan(basePackageClasses = {CommandRunnerTests.class})
public class ApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTests.class);
    }
}
