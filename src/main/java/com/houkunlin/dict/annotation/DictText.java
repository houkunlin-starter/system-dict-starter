package com.houkunlin.dict.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.enums.DictBoolType;
import com.houkunlin.dict.json.DictTypeKeyHandler;
import com.houkunlin.dict.json.VoidDictTypeKeyHandler;

import java.lang.annotation.*;

/**
 * 数据字典转换注解，用于把数据字典值转换成数据字典文本。
 * <p>
 * 适用场景：
 * <ul>
 *   <li>用在实体类字段上：自动转换该字段的字典文本信息</li>
 * </ul>
 * </p>
 * <p>
 * 支持的字段类型：
 * <ul>
 *   <li>基本类型：{@code byte, short, int, long, float, double, boolean, char}</li>
 *   <li>包装类型：{@code Byte, Short, Integer, Long, Float, Double, Boolean, Character}</li>
 *   <li>字符串类型：{@code java.lang.String}</li>
 *   <li>系统枚举类型：{@code com.houkunlin.dict.DictEnum}</li>
 *   <li>数组类型：{@code java.lang.Object[]} 基本类型数组、包装类型数组、字符串数组、系统枚举数组</li>
 *   <li>集合类型：{@code java.util.Collection, java.util.List, java.util.Set} 基本类型集合、包装类型集合、字符串集合、系统枚举集合</li>
 * </ul>
 * 注意事项：
 * 使用自定义注解可能会使 @JsonIgnore 注解失效。
 * 本身 @JsonIgnore 会忽略字段，再使用自定义注解会出现：字段没有被忽略，但是自定义注解的功能被忽略了，
 * 也就是字段值照样输出，但是数据字典值无法生成。
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@JacksonAnnotationsInside
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictText {
    /**
     * 数据字典的代码。
     *
     * @return 数据字典代码
     */
    String value() default "";

    /**
     * 数据字典的代码处理器。
     * 此配置优先于 {@link #value()} 配置
     *
     * @return 数据字典代码处理器
     * @since 1.4.7 beta 不稳定，此选项后期有可能会被删除
     */
    Class<? extends DictTypeKeyHandler> dictTypeHandler() default VoidDictTypeKeyHandler.class;

    /**
     * 显示字典文本的字段名称。
     * 假如为空字符串则默认为 字典字段名称 + "Text" 作为显示字典文本的字段名称
     *
     * @return 字典文本字段名称
     */
    String fieldName() default "";

    /**
     * 直接从系统字典枚举解析，不走Redis缓存。
     *
     * @return 与当前字典有关的系统字典枚举列表
     */
    Class<? extends DictEnum>[] enums() default {};

    /**
     * 设置当没有获取到数据时是否为 null。
     *
     * @return 空值处理类型
     */
    DictBoolType nullable() default DictBoolType.GLOBAL;

    /**
     * 标记是否使用 Map 对象返回字典值信息。
     * <pre>
     * {
     *   "peopleTypeText" : {
     *     "value" : 0,
     *     "text" : "系统管理"
     *   }
     * }
     * </pre>
     *
     * @return 是否使用 Map 对象返回字典值信息
     */
    DictBoolType mapValue() default DictBoolType.GLOBAL;

    /**
     * 标记是否替换原始值。
     * 不使用 {@link #fieldName} 字段输出，直接用字典文本替换原来的字典值输出。
     * 默认使用全局配置参数
     *
     * @return 是否替换原始值
     */
    DictBoolType replace() default DictBoolType.GLOBAL;

}
