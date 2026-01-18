package com.houkunlin.dict.annotation;

import com.houkunlin.dict.enums.NullStrategy;

import java.lang.annotation.*;

/**
 * 配置处理字典值数组的处理方式注解。
 * <p>
 * 适用场景：
 * <ul>
 *   <li>情景一：处理多个字典值通过分隔符拼接成一个字符串的情况</li>
 *   <li>情景二：处理字典值数组，配置数组转换为字符串显示的方式</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <ul>
 *   <li>字符串分隔：userType = "1,2,3,4" 可配置 {@link DictArray#split()} = "," 进行分割</li>
 *   <li>数组输出：userType301 = Arrays.asList("0", "1", "3", "0", "0", "2") 可配置 {@link DictArray#toText()} = false 字典文本输出成数组</li>
 * </ul>
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictArray {
    /**
     * 分隔字段值来转换字典的分隔符。
     * 用于配置字段为字符串类型时，使用特定分隔符存储多个字典值的情况。
     * 此值只对字段为字符串类型的生效，当此值为 空字符串 时不生效（即字符串不分割）。
     * 请注意，使用 | 竖线分隔符时，必须要加上转义字符 \ ，例如："\\|"
     *
     * @return 分隔符
     */
    String split() default "";

    /**
     * 是否将字典文本转换为字符串显示。
     * <ul>
     *   <li>true：将字典文本转换为字符串显示，使用 {@link #delimiter()} 作为分隔符</li>
     *   <li>false：将字典文本输出为数组显示</li>
     * </ul>
     *
     * @return 是否转换为字符串显示
     */
    boolean toText() default true;

    /**
     * 字典文本转换为字符串时的分隔符。此参数仅当 {@link #toText()} 设置为 true 时有效。
     *
     * @return 字符串分隔符
     */
    String delimiter() default "、";

    /**
     * 空值处理策略，用于指定当字段值为空时的处理方式。
     *
     * @return 空值处理策略
     */
    NullStrategy nullStrategy() default NullStrategy.IGNORE;
}
