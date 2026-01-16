package com.houkunlin.dict.json;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictText;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.io.Serializable;

/**
 * 字段是枚举的情况，或者注解使用了枚举的情况
 *
 * @author HouKunLin
 * @since 1.4.3
 */
public class DictTextJsonSerializerEnums extends DictTextJsonSerializerDefault {
    /**
     * 日志
     */
    protected static final Logger logger = LoggerFactory.getLogger(DictTextJsonSerializerEnums.class);
    /**
     * 缓存了直接使用系统字典枚举来渲染数据字典文本的所有数据
     */
    private static final Table<Class<? extends DictEnum<?>>, Serializable, String> CACHE_ENUMS = HashBasedTable.create();
    /**
     * 直接使用系统字典枚举的枚举对象列表
     */
    protected final Class<? extends DictEnum<?>>[] enumsClass;
    /**
     * {@link #beanFieldClass} 是否是一个枚举类型
     */
    protected final boolean isDictEnum;

    /**
     * 字段是特定枚举对象类型的场景
     *
     * @param beanClass     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param dictText      实体类字段上的 {@link DictText} 注解对象
     * @param enumsClass    实体类字段是一个特定枚举对象
     */
    public DictTextJsonSerializerEnums(Class<?> beanClass, Class<?> beanFieldClass, String beanFieldName, DictText dictText, Class<? extends DictEnum<?>>[] enumsClass) {
        super(beanClass, beanFieldClass, beanFieldName, dictText);
        this.enumsClass = enumsClass;
        this.isDictEnum = DictEnum.class.isAssignableFrom(beanFieldClass);
        if (this.enumsClass.length == 0) {
            logger.error("无法解析 {}#{} 字段的字典信息。请在该对象上使用 @DictText 注解标记", beanClass, beanFieldName);
        }
        initEnumsClass();
    }

    /**
     * 初始化枚举对象数据，主要初始化枚举对象的字典值信息存储到Map对象中
     */
    private void initEnumsClass() {
        // 解析系统字典枚举列表
        for (final Class<? extends DictEnum<?>> enumClass : enumsClass) {
            if (!enumClass.isEnum()) {
                continue;
            }
            final DictEnum<?>[] enumConstants = enumClass.getEnumConstants();
            // 解析枚举对象枚举列表
            for (DictEnum<?> enums : enumConstants) {
                // 缓存系统字典枚举对象的解析数据
                CACHE_ENUMS.put(enumClass, String.valueOf(enums.getValue()), enums.getTitle());
            }
        }
    }

    @Override
    public void serialize(@Nullable final Object fieldValue, final JsonGenerator gen, final SerializationContext ctxt) throws JacksonException {
        if (fieldValue == null) {
            writeFieldValue(gen, null, defaultNullableValue(defaultDictTextResult));
            return;
        }
        final Object outFieldValue;
        final Object dictValueText;
        if (isDictEnum) {
            // 字段是系统字典枚举对象
            final DictEnum<?> enums = (DictEnum<?>) fieldValue;
            outFieldValue = enums.getValue();
            dictValueText = enums.getTitle();
        } else {
            // 字段是普通类型，但是使用注解标记了来自枚举对象取值
            outFieldValue = fieldValue;
            dictValueText = obtainDictValueText(gen.currentValue(), outFieldValue);
        }
        writeFieldValue(gen, outFieldValue, defaultNullableValue(dictValueText));
    }

    @Override
    public Object serialize(final Object bean, final Object fieldValue) {
        if (fieldValue == null) {
            return defaultNullableValue(defaultDictTextResult);
        }
        final Object dictValueText;
        if (isDictEnum) {
            // 字段是系统字典枚举对象
            final DictEnum<?> enums = (DictEnum<?>) fieldValue;
            dictValueText = enums.getTitle();
        } else {
            // 字段是普通类型，但是使用注解标记了来自枚举对象取值
            dictValueText = obtainDictValueText(bean, fieldValue);
        }
        return defaultNullableValue(dictValueText);
    }

    @Override
    public String obtainDictValueText(final Object bean, final String dictValue) {
        String cacheTitle;
        for (final Class<? extends DictEnum<?>> aClass : enumsClass) {
            cacheTitle = CACHE_ENUMS.get(aClass, String.valueOf(dictValue));
            if (cacheTitle != null) {
                return cacheTitle;
            }
        }
        return null;
    }
}
