package com.houkunlin.dict.json;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictText;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 * 字典值JSON序列化器
 * <p>
 * 该类负责将字典值对象序列化为JSON格式，支持原始值和文本值的不同处理方式。
 * 根据系统配置决定是否使用原始值模式，在原始值模式下会保留数值类型的原始格式，
 * 否则统一转换为字符串格式。
 * </p>
 *
 * @author HouKunLin
 * @since 1.7.0
 */
public class DictWriter {

    /**
     * 写入字典值到JSON生成器
     * <p>
     * 根据系统配置的原始值模式，对不同类型的值进行相应的序列化处理：
     * - 原始值模式：保留数值类型的原始格式，其他类型转换为字符串
     * - 非原始值模式：所有类型都转换为字符串格式
     * </p>
     *
     * @param gen      JSON生成器
     * @param value    要序列化的字典值，支持null、数组、集合、DictEnum、字符串、数值等类型
     * @param dictText 字典文本配置信息
     * @param useRawValueType 是否使用原始值模式，true表示保留数值类型的原始格式，false表示转换为字符串格式
     * @throws JacksonException JSON序列化异常
     */
    public void writeDictValue(JsonGenerator gen, Object value, DictText dictText, boolean useRawValueType) throws JacksonException {
        if (value == null) {
            if (useRawValueType) {
                gen.writeNull();
            } else {
                gen.writeString("");
            }
            return;
        }
        if (useRawValueType) {
            if (value.getClass().isArray()) {
                writeDictValue(gen, (Object[]) value, dictText, true);
            } else if (value instanceof Collection<?> v) {
                writeDictValue(gen, v, dictText, true);
            } else if (value instanceof Iterable<?> v) {
                writeDictValue(gen, v, dictText, true);
            } else if (value instanceof DictEnum<?> v) {
                writeDictValue(gen, v.getValue(), dictText, true);
            } else if (value.getClass().isEnum()) {
                gen.writeString(value.toString());
            } else if (value instanceof String v) {
                gen.writeString(v);
            } else if (value instanceof BigDecimal v) {
                gen.writeNumber(v);
            } else if (value instanceof BigInteger v) {
                gen.writeNumber(v);
            } else if (value instanceof Number v) {
                gen.writeNumber(v.toString());
            } else {
                gen.writeString(value.toString());
            }
        } else {
            if (value.getClass().isArray()) {
                writeDictValue(gen, (Object[]) value, dictText, false);
            } else if (value instanceof Collection<?> v) {
                writeDictValue(gen, v, dictText, false);
            } else if (value instanceof Iterable<?> v) {
                writeDictValue(gen, v, dictText, false);
            } else if (value instanceof DictEnum<?> v) {
                gen.writeString(v.getValue().toString());
            } else if (value.getClass().isEnum()) {
                gen.writeString(value.toString());
            } else if (value instanceof String v) {
                gen.writeString(v);
            } else if (value instanceof BigDecimal v) {
                gen.writeString(v.toPlainString());
            } else {
                gen.writeString(value.toString());
            }
        }
    }

    /**
     * 处理对象数组的字典值序列化
     * <p>
     * 将对象数组序列化为JSON数组格式，递归处理数组中的每个元素
     * </p>
     *
     * @param gen      JSON生成器
     * @param value    对象数组
     * @param dictText 字典文本配置信息
     * @param useRawValueType 是否使用原始值模式
     * @throws JacksonException JSON序列化异常
     */
    private void writeDictValue(JsonGenerator gen, Object[] value, DictText dictText, boolean useRawValueType) throws JacksonException {
        gen.writeStartArray(value);
        for (Object o : value) {
            writeDictValue(gen, o, dictText, useRawValueType);
        }
        gen.writeEndArray();
    }

    /**
     * 处理集合的字典值序列化
     * <p>
     * 将集合序列化为JSON数组格式，递归处理集合中的每个元素
     * </p>
     *
     * @param gen      JSON生成器
     * @param value    集合对象
     * @param dictText 字典文本配置信息
     * @param useRawValueType 是否使用原始值模式
     * @throws JacksonException JSON序列化异常
     */
    private void writeDictValue(JsonGenerator gen, Collection<?> value, DictText dictText, boolean useRawValueType) throws JacksonException {
        gen.writeStartArray(value);
        for (Object o : value) {
            writeDictValue(gen, o, dictText, useRawValueType);
        }
        gen.writeEndArray();
    }

    /**
     * 处理集合的字典值序列化
     * <p>
     * 将集合序列化为JSON数组格式，递归处理集合中的每个元素
     * </p>
     *
     * @param gen      JSON生成器
     * @param value    集合对象
     * @param dictText 字典文本配置信息
     * @param useRawValueType 是否使用原始值模式
     * @throws JacksonException JSON序列化异常
     */
    private void writeDictValue(JsonGenerator gen, Iterable<?> value, DictText dictText, boolean useRawValueType) throws JacksonException {
        gen.writeStartArray(value);
        for (Object o : value) {
            writeDictValue(gen, o, dictText, useRawValueType);
        }
        gen.writeEndArray();
    }
}
