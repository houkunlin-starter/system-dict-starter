package com.houkunlin.dict;

import com.houkunlin.dict.jackson.DictValueSerializer;

import java.lang.reflect.Field;

/**
 * 字典值序列化器工厂接口。
 * <p>
 * 由于不同 SpringBoot 版本使用的 Jackson 版本不同（Jackson2 / Jackson3），
 * 字典值序列化器（{@link DictValueSerializer}）的具体实现类由各版本对应的 Starter 提供。
 * 该接口用于解耦 {@link DictUtil} 与具体版本实现，各版本 Starter 在启动时向 {@link DictUtil} 注册自己的实现。
 * </p>
 *
 * @author HouKunLin
 */
public interface DictValueSerializerFactory {
    /**
     * 获取指定 Bean 字段的字典值序列化器。
     *
     * @param beanClazz Bean 类
     * @param field     字段
     * @return 字典值序列化器，如果字段没有 DictText 注解则返回 null
     */
    DictValueSerializer getDictTextValueSerializer(Class<?> beanClazz, Field field);
}
