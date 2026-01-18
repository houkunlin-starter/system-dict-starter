package com.houkunlin.dict.bytecode;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

import java.io.Serializable;

/**
 * 动态生成字典转换器的接口。
 * 定义了生成字典转换器的方法，具体实现通过字节码技术动态生成转换器类。
 *
 * @author HouKunLin
 * @since 1.4.8
 */
public interface IDictConverterGenerate {
    /**
     * 日志对象
     */
    Logger logger = LoggerFactory.getLogger(IDictConverterGenerate.class);
    /**
     * 转换器类名（内部使用）
     */
    String CONVERTER_CLASS_NAME = Converter.class.getName().replace(".", "/");

    /**
     * 获得转换器类（实际是通过字节码技术动态生成的转换器类）。
     *
     * @param dictEnumClass 字典枚举类
     * @param dictConverter 字典转换器注解信息
     * @return 转换器类
     * @throws Exception 通过字节码技术动态生成的转换器类异常
     */
    <T extends DictEnum<V>, V extends Serializable> Class<T> getConverterClass(Class<T> dictEnumClass, DictConverter dictConverter) throws Exception;

    /**
     * 获取枚举接口的泛型参数对象。
     *
     * @param clazz 字典枚举对象
     * @return 泛型对象
     * @see GenericConversionService#getRequiredTypeInfo(java.lang.Class, java.lang.Class)
     */
    @SuppressWarnings({"unchecked"})
    default <T extends DictEnum<V>, V extends Serializable> Class<V> getDictEnumInterfaceType(final Class<T> clazz) {
        final ResolvableType resolvableType = ResolvableType.forClass(clazz).as(DictEnum.class);
        final ResolvableType[] interfaces = resolvableType.getGenerics();

        // DictEnum 的泛型参数类型
        final Class<V> valueType = (Class<V>) interfaces[0].resolve();
        assert valueType != null;
        return valueType;
    }
}
