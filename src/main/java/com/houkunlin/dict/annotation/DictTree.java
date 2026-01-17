package com.houkunlin.dict.annotation;

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
}
