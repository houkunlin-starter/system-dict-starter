package com.houkunlin.dict.annotation;

import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.*;

/**
 * 系统字典枚举 {@link Converter Converter 转换器} 生成配置注解。
 * 用于配置系统字典枚举的转换器生成方式，生成的 {@link Converter Converter 转换器} 只对 GET 方法参数有效。
 * <p>
 * 使用该注解将自动向SpringBoot提供一个对应的 {@link Converter Converter&lt;String, DictEnumObject&gt;} 对象；
 * 该功能需要使用到字节码技术，在运行时动态生成 {@link Converter Converter 转换器} 实现类并注入到 SpringBoot Bean 中。
 * 目前已经内置了 ASM/javassist 两种字节码实现方式，可通过 system.dict.bytecode 配置文件来配置。
 * </p>
 * <p>
 * 转换器实现代码参考：
 * <ul>
 *   <li>参考1：字典值类型不是 String 类型且 onlyDictValue = false，测试包中 test.converter.PeopleType1Converter</li>
 *   <li>参考2：字典值类型不是 String 类型且 onlyDictValue = true，测试包中 test.converter.PeopleType2Converter</li>
 *   <li>参考3：字典值类型是 String 类型且 onlyDictValue = false，测试包中 test.converter.Switch1Converter</li>
 *   <li>参考4：字典值类型是 String 类型且 onlyDictValue = true，测试包中 test.converter.Switch2Converter</li>
 * </ul>
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictConverter {
    /**
     * 是否只支持字典值转换。SpringBoot默认支持枚举名称转换。
     * <ul>
     *   <li>true：只支持字典值转换 DictEnum.valueOf(DictEnumObject.values(), VALUE)</li>
     *   <li>false：优先使用枚举名称转换（DictEnumObject.valueOf(VALUE)），枚举名称转换失败时使用字典值转换（DictEnum.valueOf(DictEnumObject.values(), VALUE)）</li>
     * </ul>
     *
     * @return 是否只支持字典值转换
     */
    boolean onlyDictValue() default false;
}
