package com.houkunlin.dict.annotation;

import java.lang.annotation.*;

/**
 * 用在系统枚举对象上：标记该枚举的内容信息。
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
     * 字典注释说明。这个字段实际并没有多大用处。只有在扫描系统字典时，把这个注解写到系统字典枚举上时才有用。
     * 当此注解在系统字典枚举上时，该字段表示字典类型名称
     *
     * @return 说明内容
     */
    String comment() default "";
}
