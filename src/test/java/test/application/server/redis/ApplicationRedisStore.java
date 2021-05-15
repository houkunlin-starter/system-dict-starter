package test.application.server.redis;

import com.houkunlin.system.dic.starter.SystemDicScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import test.application.common.CommandRunnerTests;
import test.application.server.local.ApplicationLocalStore;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
@EnableScheduling
@SpringBootApplication(scanBasePackageClasses = {ApplicationRedisStore.class, CommandRunnerTests.class})
@SystemDicScan(basePackageClasses = {ApplicationLocalStore.class, CommandRunnerTests.class})
public class ApplicationRedisStore {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationRedisStore.class);
    }
}