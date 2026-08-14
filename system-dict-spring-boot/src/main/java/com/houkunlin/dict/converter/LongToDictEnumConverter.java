package com.houkunlin.dict.converter;

import com.houkunlin.dict.ClassUtil;
import com.houkunlin.dict.DictEnum;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Method;

/**
 * 将Long类型转换为实现DictEnum接口的枚举类型的转换器
 * 此转换器通过反射调用目标枚举类的静态of方法来创建枚举实例
 *
 * @param <T> 实现了DictEnum&lt;Long&gt;接口的枚举类型
 */
public class LongToDictEnumConverter<T extends DictEnum<Long>> implements Converter<Long, T> {
    /**
     * 目标枚举类型
     */
    private final Class<T> enumType;

    /**
     * 用于创建枚举实例的静态方法
     */
    private final Method ofMethod;

    /**
     * 构造函数，初始化转换器
     *
     * @param enumType 目标枚举类型
     * @throws ConverterNotFoundException 当找不到对应的of方法时抛出异常
     */
    public LongToDictEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
        this.ofMethod = ClassUtil.findJsonCreatorMethod(enumType, Long.class);
        if (this.ofMethod == null) {
            throw new ConverterNotFoundException(TypeDescriptor.valueOf(Long.class), TypeDescriptor.valueOf(enumType));
        }
    }

    /**
     * 执行转换操作，将Long值转换为对应的枚举实例
     *
     * @param source 原始Long值
     * @return 转换后的枚举实例，如果输入为null则返回null
     * @throws ConversionFailedException 当转换过程失败时抛出异常
     */
    @Override
    public T convert(Long source) {
        if (source == null) {
            return null;
        }

        try {
            // 调用静态的of方法创建枚举实例
            @SuppressWarnings("unchecked")
            T result = (T) ofMethod.invoke(null, source);

            return result;
        } catch (Exception e) {
            throw new ConversionFailedException(TypeDescriptor.valueOf(Long.class), TypeDescriptor.valueOf(enumType), source, e);
        }
    }
}
