package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.houkunlin.system.dict.starter.DictEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.Serializable;

/**
 * 字段是枚举的情况，或者注解使用了枚举的情况
 *
 * @author HouKunLin
 * @since 1.4.3
 */
public class DictTextJsonSerializerEnums extends DictTextJsonSerializerDefault {
    protected static final Logger logger = LoggerFactory.getLogger(DictTextJsonSerializerEnums.class);
    /**
     * 缓存了直接使用系统字典枚举来渲染数据字典文本的所有数据
     */
    private static final Table<Class<? extends DictEnum>, Serializable, String> CACHE_ENUMS = HashBasedTable.create();
    /**
     * 直接使用系统字典枚举的枚举对象列表
     */
    protected final Class<? extends DictEnum>[] enumsClass;

    /**
     * 字段是特定枚举对象类型的场景
     *
     * @param beanClazz     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param dictText      实体类字段上的 {@link DictText} 注解对象
     * @param enumsClass    实体类字段是一个特定枚举对象
     */
    public DictTextJsonSerializerEnums(Class<?> beanClazz, Class<?> fieldClazz, String beanFieldName, DictText dictText, Class<? extends DictEnum<?>>[] enumsClass) {
        super(beanClazz, fieldClazz, beanFieldName, dictText);
        this.enumsClass = enumsClass;
        if (this.enumsClass.length == 0) {
            logger.error("无法解析 {}#{} 字段的字典信息。请在该对象上使用 @DictText 注解标记", beanClazz, beanFieldName);
        }
        initEnumsClass();
    }

    /**
     * 初始化枚举对象数据，主要初始化枚举对象的字典值信息存储到Map对象中
     */
    private void initEnumsClass() {
        // 解析系统字典枚举列表
        for (final Class<? extends DictEnum> enumClass : enumsClass) {
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
    public void serialize(@Nullable final Object value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (fromFieldEnumsClass(value, gen)) {
            return;
        }
        if (fromDictTextEnumsClass(value, gen)) {
            return;
        }
        writeFieldValue(gen, value, defaultNullableValue(null));
    }

    /**
     * 字段是系统字典枚举对象
     *
     * @param value 实体类字段值，此时该值可能是一个系统字典枚举对象
     * @param gen   JsonGenerator
     * @return 是否处理成功
     */
    private boolean fromFieldEnumsClass(@Nullable Object value, JsonGenerator gen) throws IOException {
        if (!DictEnum.class.isAssignableFrom(fieldClazz)) {
            return false;
        }
        if (value != null) {
            final DictEnum enums = (DictEnum) value;
            final Object title = obtainDictValueText(enums.getValue());
            writeFieldValue(gen, enums.getValue(), defaultNullableValue(title));
        } else {
            writeFieldValue(gen, null, defaultNullableValue(null));
        }
        return true;
    }

    /**
     * 字段是普通类型，但是使用 {@link DictText} 标记了来自枚举对象取值
     *
     * @param value 实体类字段值
     * @param gen   JsonGenerator
     * @return 是否处理成功
     */
    private boolean fromDictTextEnumsClass(@Nullable Object value, JsonGenerator gen) throws IOException {
        final Object title = obtainDictValueText(value);
        if (title == null) {
            logger.debug("{}#{} = {} 指定了从多个字典枚举中取值，但是由于未找到其值因而会进行进一步的信息获取。实际上这里不应该发生的", beanClazz, beanFieldName, value);
            return false;
        }
        writeFieldValue(gen, value, defaultNullableValue(title));
        return true;
    }

    @Override
    protected String obtainDictValueText(final String dictValue) {
        String cacheTitle;
        for (final Class<? extends DictEnum> aClass : enumsClass) {
            cacheTitle = CACHE_ENUMS.get(aClass, String.valueOf(dictValue));
            if (cacheTitle != null) {
                return cacheTitle;
            }
        }
        return null;
    }
}
