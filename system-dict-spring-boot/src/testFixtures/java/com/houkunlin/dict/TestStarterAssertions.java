package com.houkunlin.dict;

import org.junit.jupiter.api.Assertions;

import java.io.File;

/**
 * 测试断言工具类，提供公共的测试断言方法，避免各个 Starter 测试类中重复编写。
 *
 * @author HouKunLin
 */
public final class TestStarterAssertions {
    private TestStarterAssertions() {
    }

    /**
     * 断言当前测试运行在指定版本的 Starter 模块目录下。
     *
     * @param version Starter 版本号，如 2、3、4
     */
    public static void assertCurrentStarterModule(final String version) {
        final String absolutePath = new File(".").getAbsolutePath();
        System.out.println(absolutePath);
        Assertions.assertTrue(absolutePath.contains("system-dict-spring-boot" + version + "-starter"));
    }
}
