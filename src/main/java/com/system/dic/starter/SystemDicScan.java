package com.system.dic.starter;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 扫描系统数据字典。系统数据字典需要为枚举类型，然后枚举需要实现 {@link IDicEnums} 接口才能够自动扫描到。
 *
 * @author HouKunLin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SystemDicScanRegistrar.class)
public @interface SystemDicScan {
    /**
     * 系统字典扫描包路径
     *
     * @return 包路径
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * 系统字典扫描包路径
     *
     * @return 包路径
     */
    String[] basePackages() default {};

    /**
     * 系统字典扫描包路径
     *
     * @return 包路径
     */
    Class<?>[] basePackageClasses() default {};
}
