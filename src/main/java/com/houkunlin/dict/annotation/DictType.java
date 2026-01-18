package com.houkunlin.dict.annotation;

import java.lang.annotation.*;

/**
 * 用在系统枚举对象上的字典类型注解，用于标记该枚举的内容信息。
 * 可重复使用，通过 {@link DictTypes} 注解支持在同一个枚举上使用多个 {@link DictType} 注解。
 *
 * @author HouKunLin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DictTypes.class)
@Documented
public @interface DictType {
    /**
     * 数据字典的代码。
     * 当此注解在系统字典枚举上时，该字段表示字典类型代码。
     *
     * @return 数据字典代码
     */
    String value() default "";

    /**
     * 字典注释说明。
     * 当此注解在系统字典枚举上时，该字段表示字典类型名称，仅在扫描系统字典时有效。
     *
     * @return 说明内容
     */
    String comment() default "";
}
