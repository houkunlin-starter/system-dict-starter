package com.houkunlin.dict.annotation;

import com.houkunlin.dict.enums.NullStrategy;

import java.lang.annotation.*;

/**
 * 以树形结构加载字典数据，为防止陷入死循环，请设置最大访问深度。
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictTree {
    /**
     * 为防止陷入死循环，请设置树形结构数据的向访问的最大访问深度，超过最大访问深度则直接返回。
     *
     * @return int &lt;= 0 视为不限制深度
     * @since 2.0.0
     */
    int maxDepth() default -1;

    /**
     * 是否转换为字符串显示。true：字符串显示。false：数组显示
     *
     * @return 是否转换为数组显示
     */
    boolean toText() default false;

    /**
     * 此参数仅当 {@link #toText()} 设置为 true 时有效（使用字符串显示），此参数将用作每个字典文本值之间的分隔符
     *
     * @return 字符串分隔符
     */
    String delimiter() default "/";

    /**
     * 空值处理策略
     *
     * @return 空值处理策略
     */
    NullStrategy nullStrategy() default NullStrategy.IGNORE;
}
