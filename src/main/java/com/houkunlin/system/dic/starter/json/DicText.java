package com.houkunlin.system.dic.starter.json;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.houkunlin.system.dic.starter.DicEnum;

import java.lang.annotation.*;

/**
 * 把数据字典值转换成数据字典文本。
 * 使用自定义注解好像发现一个无解的问题，自定义注解会使 @JsonIgnore 注解失效。
 * 本身 @JsonIgnore 会忽略字段，再使用自定义注解会出现：字段没有被忽略，但是自定义注解的功能被忽略了，也就是字段值照样输出，但是数据字典值无法生成。
 * 用在系统枚举对象上：标记该枚举的内容。
 * 用在实体类字段上：自动转换该字段的字典文本信息
 *
 * @author HouKunLin
 */
@JacksonAnnotationsInside
@JsonSerialize(using = DicTextJsonSerializer.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DicText {
    /**
     * 数据字典的代码。
     * 当此注解在系统字典枚举上时，该字段表示字典类型代码。
     *
     * @return 数据字典代码
     */
    String value() default "";

    /**
     * 显示字典文本的字段名称，假如为空字符串则默认为 字典字段名称 + "Text" 作为显示字典文本的字段名称
     *
     * @return 字典文本字段
     */
    String fieldName() default "";

    /**
     * 直接从系统字典枚举解析，不走Redis缓存
     *
     * @return 与当前字典有关的系统字典枚举列表
     */
    Class<? extends DicEnum<?>>[] enums() default {};

    /**
     * 设置当没有获取到数据时是否为 null。
     *
     * @return Type
     */
    Type nullable() default Type.GLOBAL;

    /**
     * 此配置将会使 #fieldName 配置失效；用来标记是否使用 Map 对象返回字典值信息
     * <code>
     * {
     * "peopleType" : {
     * "value" : 0,
     * "text" : "系统管理"
     * }
     * }
     * </code>
     *
     * @return mapValue
     */
    Type mapValue() default Type.GLOBAL;

    enum Type {
        /**
         * 根据全局参数决定配置
         */
        GLOBAL,
        /**
         * 强制为 YES
         */
        YES,
        /**
         * 强制为 NO
         */
        NO,
    }
}
