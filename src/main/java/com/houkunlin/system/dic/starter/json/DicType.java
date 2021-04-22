package com.houkunlin.system.dic.starter.json;

import java.lang.annotation.*;

/**
 * 把数据字典值转换成数据字典文本。
 * 使用自定义注解好像发现一个无解的问题，自定义注解会使 @JsonIgnore 注解失效。
 * 本身 @JsonIgnore 会忽略字段，再使用自定义注解会出现：字段没有被忽略，但是自定义注解的功能被忽略了，也就是字段值照样输出，但是数据字典值无法生成。
 * 该注解可用在系统字典枚举对象上，也可用在实体类的字段上。
 * 用在系统枚举对象上：标记该枚举的内容。
 * 用在实体类字段上：自动转换该字段的字典文本信息
 *
 * @author HouKunLin
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DicType {
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
