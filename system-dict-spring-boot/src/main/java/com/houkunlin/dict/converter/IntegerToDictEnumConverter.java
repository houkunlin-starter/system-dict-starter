package com.houkunlin.dict.converter;

import com.houkunlin.dict.ClassUtil;
import com.houkunlin.dict.DictEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.Method;

/**
 * 将Integer类型转换为实现DictEnum接口的枚举类型的转换器
 * 此转换器通过反射调用目标枚举类的静态of方法来创建枚举实例
 * 如果未找到@JsonCreator方法，则使用默认的枚举常量数组索引方式获取枚举常量
 *
 * @param <T> 实现了DictEnum&lt;Integer&gt;接口的枚举类型
 */
public class IntegerToDictEnumConverter<T extends DictEnum<Integer>> implements Converter<Integer, T> {
    private static final Logger logger = LoggerFactory.getLogger(IntegerToDictEnumConverter.class);
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
     */
    public IntegerToDictEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
        this.ofMethod = ClassUtil.findJsonCreatorMethod(enumType, Integer.class);
        if (this.ofMethod == null) {
            logger.warn("Cannot find @JsonCreator method for {}, using default enum constructor", enumType);
        }
    }

    /**
     * 执行转换操作，将Integer值转换为对应的枚举实例
     * 如果找到了@JsonCreator方法，则调用该方法；否则使用默认的枚举常量索引方式
     *
     * @param source 原始Integer值
     * @return 转换后的枚举实例，如果输入为null则返回null
     * @throws ConversionFailedException 当转换过程失败时抛出异常
     */
    @Override
    public T convert(Integer source) {
        if (source == null) {
            return null;
        }

        try {
            if (ofMethod == null) {
                // 当没有找到@JsonCreator方法时，使用默认的枚举常量数组索引方式获取枚举值
                return enumType.getEnumConstants()[source];
            }
            // 调用静态的of方法创建枚举实例
            @SuppressWarnings("unchecked")
            T result = (T) ofMethod.invoke(null, source);

            return result;
        } catch (Exception e) {
            throw new ConversionFailedException(TypeDescriptor.valueOf(Integer.class), TypeDescriptor.valueOf(enumType), source, e);
        }
    }
}
