package com.houkunlin.dict.annotation;

import java.lang.annotation.*;

/**
 * 用在系统枚举对象上的字典类型注解容器，用于支持在同一个枚举上重复使用 {@link DictType} 注解。
 * 通过此注解，可以在同一个枚举上定义多个字典类型信息，实现多个系统枚举合并到一个字典中的功能。
 *
 * @author HouKunLin
 * @since 1.4.7
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictTypes {
    /**
     * 数据字典类型注解数组，包含多个 {@link DictType} 注解。
     *
     * @return 数据字典类型注解数组
     */
    DictType[] value() default {};
}
