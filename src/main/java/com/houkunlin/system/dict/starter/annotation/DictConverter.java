package com.houkunlin.system.dict.starter.annotation;

import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.*;

/**
 * 系统字典枚举 {@link Converter Converter 转换器} 生成配置，生成的 {@link Converter Converter 转换器} 只对 GET 方法参数有效；
 * 使用该注解将自动向SpringBoot提供一个对应的 {@link Converter Converter&lt;String, DictEnumObject&gt;} 对象；
 * 该功能需要使用到字节码技术，在运行时动态生成 {@link Converter Converter 转换器} 实现类并注入到 SpringBoot Bean 中。
 * 目前已经内置了 ASM/javassist 两种字节码实现方式，可通过 system.dict.bytecode 配置文件来配置。
 *
 * <p>转换器实现代码参考1：字典值类型不是 String 类型且 onlyDictValue = false，测试包中 test.converter.PeopleType1Converter</p>
 * <p>转换器实现代码参考2：字典值类型不是 String 类型且 onlyDictValue = true，测试包中 test.converter.PeopleType2Converter</p>
 * <p>转换器实现代码参考3：字典值类型是 String 类型且 onlyDictValue = false，测试包中 test.converter.Switch1Converter</p>
 * <p>转换器实现代码参考4：字典值类型是 String 类型且 onlyDictValue = true，测试包中 test.converter.Switch2Converter</p>
 *
 * @author HouKunLin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictConverter {
    /**
     * 是否只支持字典值转换。SpringBoot默认支持枚举名称转换；
     * <p>是：只支持字典值转换 DictEnum.valueOf(DictEnumObject.values(), VALUE)；</p>
     * <p>否：优先使用枚举名称转换（DictEnumObject.valueOf(VALUE)），枚举名称转换失败时使用字典值转换（DictEnum.valueOf(DictEnumObject.values(), VALUE)）；</p>
     *
     * @return 是否
     */
    boolean onlyDictValue() default false;
}
