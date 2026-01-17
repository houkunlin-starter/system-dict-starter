package com.houkunlin.dict.annotation;

import com.houkunlin.dict.enums.NullStrategy;

import java.lang.annotation.*;

/**
 * 字典字段的字符串分隔，数据字典分割成数组配置。
 * 用在字段是字符串时，并且字段使用了特定的分隔符来存储多个字典值。
 * 例如：
 * <p>userType = "1,2,3,4" 可配置 {@link DictArray#split()} = "," 进行分割</p>
 * <p>userType301 = Arrays.asList("0", "1", "3", "0", "0", "2") 可配置 {@link DictArray#toText()} = false 字典文本输出成数组</p>
 *
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictArray {
    /**
     * 是否分隔字段值来转换字典。此值只对字段为字符串类型的生效，当此值为 空字符串 的时候不生效（即字符串不分割）。
     * 请注意，使用 | 竖线分隔符时，必须要加上转义字符 \ ，例如："\\|"
     *
     * @return 分隔符
     */
    String split() default "";

    /**
     * 是否转换为字符串显示。true：字符串显示。false：数组显示
     *
     * @return 是否转换为数组显示
     */
    boolean toText() default true;

    /**
     * 此参数仅当 {@link #toText()} 设置为 true 时有效（使用字符串显示），此参数将用作每个字典文本值之间的分隔符
     *
     * @return 字符串分隔符
     */
    String delimiter() default "、";

    /**
     * 空值处理策略
     *
     * @return 空值处理策略
     */
    NullStrategy nullStrategy() default NullStrategy.IGNORE;
}
