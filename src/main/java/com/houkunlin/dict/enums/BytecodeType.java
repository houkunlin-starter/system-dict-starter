package com.houkunlin.dict.enums;

import com.houkunlin.dict.bytecode.IDictConverterGenerate;

/**
 * 字节码技术类型枚举，用于指定生成字典转换器时使用的字节码技术。
 *
 * @author HouKunLin
 * @since 1.4.8
 */
public enum BytecodeType {
    /**
     * 不设定字节码技术。需要手动向容器中注册 {@link IDictConverterGenerate} 对象。
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
