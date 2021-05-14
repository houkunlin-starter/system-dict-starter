package test.application.server.auto;

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
@SpringBootApplication(scanBasePackageClasses = {ApplicationAutoStore.class, CommandRunnerTests.class})
@SystemDicScan(basePackageClasses = {ApplicationLocalStore.class, CommandRunnerTests.class})
public class ApplicationAutoStore {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationAutoStore.class);
    }
}
