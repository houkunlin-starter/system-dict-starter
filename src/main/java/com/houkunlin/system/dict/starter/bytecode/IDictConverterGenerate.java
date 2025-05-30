package com.houkunlin.system.dict.starter.bytecode;

import com.houkunlin.system.dict.starter.ClassUtil;
import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.json.DictConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * 动态生成字典转换器
 *
 * @author HouKunLin
 * @since 1.4.8
 */
public interface IDictConverterGenerate {
    Logger logger = LoggerFactory.getLogger(IDictConverterGenerate.class);
    String CONVERTER_CLASS_NAME = Converter.class.getName().replace(".", "/");

    /**
     * 动态生成字典转换器，并将其注册到Spring容器中
     *
     * @param factory       容器
     * @param dictEnumClass 字典枚举类
     * @param dictConverter 字典转换器注解
     */
    default <T extends DictEnum<V>, V extends Serializable> void registerBean(final DefaultListableBeanFactory factory, final Class<DictEnum<V>> dictEnumClass, final DictConverter dictConverter) {
        try {
            final String beanName = dictEnumClass.getName() + "$$SystemDictSpringConverter";
            if (factory.containsBean(beanName)) {
                return;
            }
            final Converter<String, DictEnum<V>> converter = getConverter(dictEnumClass, dictConverter);
            if (converter != null) {
                factory.registerSingleton(beanName, converter);
            }
        } catch (Throwable e) {
            logger.error("自动创建系统字典枚举 {} 的 Converter 转换器失败，不影响系统启动，但是会影响此枚举转换器功能", dictEnumClass.getName(), e);
        }
    }

    /**
     * 获得转换器类（实际是通过字节码技术动态生成的转换器类）
     *
     * @param dictEnumClass 字典枚举类
     * @param dictConverter 字典转换器注解信息
     * @return 转换器类
     * @throws Exception 通过字节码技术动态生成的转换器类异常
     */
    <T extends DictEnum<V>, V extends Serializable> Class<T> getConverterClass(Class<T> dictEnumClass, DictConverter dictConverter) throws Exception;

    /**
     * 获得转换器类（实际是通过字节码技术动态生成的转换器类）
     *
     * @param dictEnumClass 字典枚举类
     * @param dictConverter 字典转换器注解信息
     * @return 转换器类
     * @throws Exception 通过字节码技术动态生成的转换器类异常
     */
    @SuppressWarnings({"unchecked"})
    default <T extends DictEnum<V>, V extends Serializable> Converter<String, T> getConverter(Class<T> dictEnumClass, DictConverter dictConverter) throws Exception {
        final Class<T> converterClass = getConverterClass(dictEnumClass, dictConverter);
        if (converterClass == null) {
            return null;
        }
        Constructor<T> defaultConstructor = ClassUtil.getDefaultConstructor(converterClass);
        if (defaultConstructor == null) {
            logger.error("自动创建系统字典枚举 {} 的 Converter 没有默认构造方法，请联系开发者修复bug", dictEnumClass.getName());
            return null;
        }
        return (Converter<String, T>) defaultConstructor.newInstance();
    }

    /**
     * 获取枚举接口的泛型参数对象
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
