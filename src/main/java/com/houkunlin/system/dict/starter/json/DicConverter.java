package com.houkunlin.system.dict.starter.json;

import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.*;

/**
 * 系统字典枚举 {@link Converter Converter 转换器} 生成配置，生成的 {@link Converter Converter 转换器} 只对 GET 方法参数有效；
 * 使用该注解将自动向SpringBoot提供一个对应的 {@link Converter Converter&lt;String, DicEnumObject&gt;} 对象；
 * 该功能需要使用到 javassist 动态字节码技术，在运行时动态生成 {@link Converter Converter 转换器} 实现类并注入到 SpringBoot Bean 中
 *
 * @author HouKunLin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DicConverter {
    /**
     * 是否只支持字典值转换。SpringBoot默认支持枚举名称转换；
     * 是：只支持字典值转换 DicEnum.valueOf(DicEnumObject.values(), VALUE)；
     * 否：优先使用枚举名称转换（DicEnumObject.valueOf(VALUE)），枚举名称转换失败时使用字典值转换（DicEnum.valueOf(DicEnumObject.values(), VALUE)）；
     *
     * @return 是否
     */
    boolean onlyDicValue() default false;
}
