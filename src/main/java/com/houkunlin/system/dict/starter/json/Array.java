package com.houkunlin.system.dict.starter.json;

import java.lang.annotation.*;

/**
 * 字典字段的字符串分隔
 *
 * @author HouKunLin
 * @since 1.4.3
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Array {
    /**
     * 是否分隔字段值来转换字典。当此值为 空字符串 的时候配置失效，表示不启用数据字典数组转换
     *
     * @return 分隔符
     */
    String split() default ",";

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
    String joinSeparator() default "、";

    /**
     * 是否忽略字典文本为 null 的数据。true：忽略，跳过；false：不忽略，输出 'null' 字符串
     *
     * @return 是否忽略字典文本为 null 的数据
     */
    boolean ignoreNull() default true;
}
