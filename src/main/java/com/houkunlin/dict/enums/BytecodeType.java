package com.houkunlin.dict.enums;

/**
 * 字节码技术类型枚举，用于指定生成字典转换器时使用的字节码技术。
 *
 * @author HouKunLin
 * @since 1.4.8
 * @deprecated 2.0.3 版本已废弃，已经改为使用 ConverterFactory 实现转换枚举值转换成枚举，默认支持枚举名称转换+字典值转换，兼容旧版，对旧版一些无法兼容的情况进行了处理。
 */
@Deprecated(since = "2.0.3", forRemoval = true)
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
