package test.application.server.local;

import com.houkunlin.system.dic.starter.SystemDicScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import test.application.common.CommandRunnerTests;

/**
 * 应用启动器
 *
 * @author HouKunLin
 */
@SpringBootApplication(scanBasePackageClasses = {ApplicationLocalStore.class, CommandRunnerTests.class})
@SystemDicScan
public class ApplicationLocalStore {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationLocalStore.class);
    }
}
