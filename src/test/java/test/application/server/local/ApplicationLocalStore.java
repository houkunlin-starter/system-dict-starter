package test.application.server.local;

import com.houkunlin.system.dic.starter.SystemDicScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import test.application.common.CommandRunnerTests;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
@EnableScheduling
@SpringBootApplication(scanBasePackageClasses = {ApplicationLocalStore.class, CommandRunnerTests.class})
@SystemDicScan(basePackageClasses = {ApplicationLocalStore.class, CommandRunnerTests.class})
public class ApplicationLocalStore {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationLocalStore.class);
    }
}