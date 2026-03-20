package com.houkunlin.dict.enums;

/**
 * 字节码技术类型枚举，用于指定生成字典转换器时使用的字节码技术。
 *
 * @author HouKunLin
 * @since 1.4.8
 */
@Deprecated(since = "1.7.3 and 2.0.3", forRemoval = true)
public enum BytecodeType {
    /**
     * 不设定字节码技术。
     */
    NONE,
    /**
     * Spring ASM 字节码技术
     */
    ASM,
    /**
     * javassist 字节码技术
     */
    JAVASSIST;
}
