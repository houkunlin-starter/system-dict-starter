package com.houkunlin.dict.jackson;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.json.DictTypeKeyHandler;
import org.jspecify.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 字典值序列化器（数组文本为 false 实现），用于处理字典数组文本为 false 时的序列化逻辑。
 * <p>
 * 该实现类负责将字典数组值转换为文本数组，每个字典值对应一个文本元素。
 * 例如：当数组值为 ["1", "2"] 时，会转换为 ["字典1", "字典2"] 这样的格式。
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
public class DictValueSerializerArrayTextFalseImpl extends DictValueSerializer implements IDictValueSerializerArrayTextFalse, IDictTransformArrayTextFalse {
    /**
     * 构造方法
     *
     * @param fieldName        字段名称
     * @param javaTypeRawClass Java类型信息
     * @param dictText         字典文本注解配置
     * @param dictArray        字典数组注解配置
     * @param dictTree         字典树注解配置
     */
    public DictValueSerializerArrayTextFalseImpl(String fieldName, Class<?> javaTypeRawClass, DictText dictText, DictArray dictArray, DictTree dictTree) {
        super(fieldName, javaTypeRawClass, dictText, dictArray, dictTree);
    }

    /**
     * 获取字典类型键处理器。
     * <p>
     * 字典类型键处理器用于处理字典类型的获取和转换逻辑。
     * </p>
     *
     * @return 字典类型键处理器
     */
    @Override
    public DictTypeKeyHandler<Object> getDictTypeKeyHandler() {
        return dictTypeKeyHandler;
    }

    /**
     * 序列化字典值。
     * <p>
     * 根据字段值类型和配置，将字典值序列化为JSON格式。支持数组、集合、可迭代对象等类型的字典值序列化。
     * 当值为null时，根据配置返回null或空数组。
     *</p>
     *
     * @param value 字段值
     * @param gen   JSON生成器
     * @param ctxt  序列化上下文
     * @throws JacksonException Jackson 异常
     */
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        if (!useReplaceFieldValue) {
            DICT_WRITER.writeDictValue(gen, value, dictText, useRawValueType);
            gen.writeName(outputFieldName);
        }
        if (useMap) {
            gen.writeStartObject();
            gen.writeName("value");
            DICT_WRITER.writeDictValue(gen, value, dictText, useRawValueType);
            gen.writeName("text");
        }
        if (value != null) {
            serializeArrayTextFalse(value, gen, ctxt, fieldName, dictText, dictArray, dictTree);
        } else {
            if (textNullable) {
                gen.writeNull();
            } else if (javaTypeRawClass.isArray() ||
                Collection.class.isAssignableFrom(javaTypeRawClass) ||
                Iterable.class.isAssignableFrom(javaTypeRawClass)) {
                gen.writeStartArray();
                gen.writeEndArray();
            } else if (Map.class.isAssignableFrom(javaTypeRawClass)) {
                gen.writeStartObject();
                gen.writeEndObject();
            } else {
                gen.writeStartArray();
                gen.writeEndArray();
            }
        }
        if (useMap) {
            gen.writeEndObject();
        }
    }

    /**
     * 转换字典值。
     * <p>
     * 将字段值转换为字典文本数组，支持处理null值和各种类型的字段值。
     * 当值为null时，根据配置返回null或空列表。
     *</p>
     *
     * @param bean       Bean 对象
     * @param fieldValue 字段值
     * @return 转换后的字典值
     */
    @Override
    public Object transform(final Object bean, @Nullable final Object fieldValue) {
        if (fieldValue == null) {
            if (textNullable) {
                return null;
            }
            return Collections.emptyList();
        }
        return transformArrayTextFalse(bean, fieldValue, fieldName, dictText, dictArray, dictTree);
    }
}
