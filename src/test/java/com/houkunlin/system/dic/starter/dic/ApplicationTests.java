package com.houkunlin.system.dic.starter.dic;

import com.houkunlin.system.dic.starter.SystemDicScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
@SpringBootApplication
@SystemDicScan
public class ApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTests.class);
    }
}
