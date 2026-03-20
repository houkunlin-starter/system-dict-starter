package com.houkunlin.dict.converter;

import com.houkunlin.dict.ClassUtil;
import com.houkunlin.dict.DictEnum;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 将String类型转换为实现DictEnum接口的枚举类型的转换器
 * 此转换器通过反射调用目标枚举类的静态of方法来创建枚举实例
 * 支持将字符串转换为目标枚举所关联的值类型，然后使用该值创建枚举实例
 *
 * @param <T> 实现了DictEnum<?>接口的枚举类型
 */
public class StringToDictEnumConverter<T extends DictEnum<?>> implements Converter<String, T> {
    /**
     * 目标枚举类型
     */
    private final Class<T> enumType;

    /**
     * 用于创建枚举实例的静态方法
     */
    private final Method ofMethod;

    /**
     * 字符串解析函数，用于将字符串转换为目标值类型
     */
    private final Function<@NonNull String, Object> valueFunction;

    /**
     * 构造函数，初始化转换器
     *
     * @param enumType 目标枚举类型
     * @throws ConverterNotFoundException 当找不到对应的of方法时抛出异常
     */
    public StringToDictEnumConverter(Class<T> enumType) {
        this.enumType = enumType;

        // 获取ClassEnum的泛型参数类型
        Class<?> parameterFirst = ClassUtil.getInterfaceParameterFirst(enumType);
        // 枚举值的参数类型
        Class<?> valueType = parameterFirst != null ? parameterFirst : String.class;
        this.valueFunction = ClassUtil.getParseValueFunction(valueType);
        this.ofMethod = ClassUtil.findJsonCreatorMethod(enumType, valueType);
        if (this.ofMethod == null) {
            throw new ConverterNotFoundException(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(enumType));
        }
    }

    /**
     * 执行转换操作，将String值转换为对应的枚举实例
     * 首先将字符串转换为枚举对应的值类型，然后调用of方法创建枚举实例
     * 如果结果为空且目标类型是枚举，则尝试按枚举名称匹配
     *
     * @param source 原始String值
     * @return 转换后的枚举实例，如果输入为null则返回null
     * @throws ConversionFailedException 当转换过程失败时抛出异常
     */
    @Override
    public T convert(String source) {
        if (source == null) {
            return null;
        }

        try {
            if (enumType.isEnum()) {
                // 如果通过of方法未能成功创建实例，尝试通过枚举名称进行匹配
                for (T enumConstant : enumType.getEnumConstants()) {
                    if (source.equals(((Enum<?>) enumConstant).name())) {
                        return enumConstant;
                    }
                }
            }

            // 根据值类型进行类型转换
            Object convertedValue = valueFunction.apply(source);

            // 调用静态的of方法创建枚举实例
            @SuppressWarnings("unchecked")
            T result = (T) ofMethod.invoke(null, convertedValue);

            return result;
        } catch (Exception e) {
            throw new ConversionFailedException(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(enumType), source, e);
        }
    }

}
