package com.houkunlin.dict.enums;

/**
 * 空值处理策略枚举，用于指定字典值为空时的处理方式。
 *
 * @author HouKunLin
 * @since 2.0.0
 */
public enum NullStrategy {
    /**
     * 忽略空值，不进行处理
     */
    IGNORE,
    /**
     * 空值处理为 null
     */
    NULL,
    /**
     * 空值处理为空字符串
     */
    EMPTY;
}
