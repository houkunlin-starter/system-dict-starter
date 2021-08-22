package com.houkunlin.system.dict.starter.dic;

import com.houkunlin.system.dict.starter.SystemDictScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import test.application.common.CommandRunnerTests;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
@SpringBootApplication(scanBasePackageClasses = {ApplicationTests.class, CommandRunnerTests.class})
@SystemDictScan
public class ApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTests.class);
    }
}
