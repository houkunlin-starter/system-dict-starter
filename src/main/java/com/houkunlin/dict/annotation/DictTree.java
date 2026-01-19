package com.houkunlin.dict.annotation;

import com.houkunlin.dict.enums.NullStrategy;

import java.lang.annotation.*;

/**
 * 以树形结构加载字典数据的注解。
 * 用于配置字段以树形结构加载字典数据，并可设置最大访问深度以防止陷入死循环。
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictTree {
    /**
     * 树形结构数据的向下访问的最大访问深度，超过最大访问深度则直接返回。
     * 为防止陷入死循环，建议根据实际业务场景设置合适的最大深度。
     *
     * @return 最大访问深度，&lt;= 0 视为不限制深度
     * @since 2.0.0
     */
    int maxDepth() default -1;

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
    String delimiter() default "/";

    /**
     * 空值处理策略，用于指定当字段值为空时的处理方式。
     *
     * @return 空值处理策略
     */
    NullStrategy nullStrategy() default NullStrategy.IGNORE;
}
