package com.houkunlin.dict.enums;

/**
 * 空值处理策略
 *
 * @author HouKunLin
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
