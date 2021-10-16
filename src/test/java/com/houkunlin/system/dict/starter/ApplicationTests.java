package com.houkunlin.system.dict.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import test.application.common.CommandRunnerTests;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
@SpringBootApplication(scanBasePackageClasses = {ApplicationTests.class, CommandRunnerTests.class})
@SystemDictScan(basePackages = "test.application.common.bean")
public class ApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTests.class);
    }
}
