package com.houkunlin.dict.converter;

import com.houkunlin.dict.DictEnum;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * Float到DictEnum的转换器工厂类
 * 该工厂用于创建将Float类型转换为实现DictEnum接口的枚举类型的转换器
 */
public class FloatToDictEnumConverterFactory implements ConverterFactory<Float, DictEnum<Float>> {

    /**
     * 获取指定目标类型的转换器
     *
     * @param targetType 目标类型，必须是实现DictEnum&lt;Float&gt;接口的枚举类型
     * @param <T>        实现DictEnum&lt;Float&gt;接口的枚举类型
     * @return 返回对应的Float到枚举类型的转换器
     */
    @Override
    public <T extends DictEnum<Float>> @NonNull Converter<Float, T> getConverter(@NonNull Class<T> targetType) {
        return new FloatToDictEnumConverter<>(targetType);
    }
}
