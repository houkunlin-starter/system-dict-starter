package com.system.dic.starter.dic;

import com.system.dic.starter.SystemDicScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HouKunLin
 */
@SpringBootApplication
@SystemDicScan
public class ApplicationTests {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationTests.class);
    }
}
