package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictText;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * 字典值JSON序列化器
 * <p>
 * 该类负责将字典值对象序列化为JSON格式，支持多种类型的字典值处理：
 * <ul>
 * <li>null 值处理</li>
 * <li>数组类型处理</li>
 * <li>集合类型处理</li>
 * <li>可迭代对象处理</li>
 * <li>DictEnum 类型处理</li>
 * <li>枚举类型处理</li>
 * <li>字符串类型处理</li>
 * <li>数值类型处理</li>
 * </ul>
 * </p>
 * <p>
 * 该类在字典值序列化过程中扮演重要角色，被 DictValueSerializer 及其实现类调用，
 * 用于将字典值转换为适合JSON输出的格式。
 * </p>
 *
 * @author HouKunLin
 * @since 1.7.0
 */
public class DictValueWriter {

    /**
     * 私有构造方法，防止实例化
     */
    private DictValueWriter() {
    }

    /**
     * 写入字典值到JSON生成器
     * <p>
     * 根据值的类型进行相应的序列化处理：
     * <ul>
     * <li>null 值：写入空字符串</li>
     * <li>数组：调用数组处理方法</li>
     * <li>集合：调用集合处理方法</li>
     * <li>可迭代对象：调用可迭代对象处理方法</li>
     * <li>DictEnum：写入其值的字符串表示</li>
     * <li>枚举：写入枚举的字符串表示</li>
     * <li>字符串：直接写入</li>
     * <li>BigDecimal：写入其plain字符串表示</li>
     * <li>其他类型：写入其toString()结果</li>
     * </ul>
     * </p>
     *
     * @param gen      JSON生成器，用于写入JSON数据
     * @param value    要序列化的字典值，支持多种类型
     * @param dictText 字典文本配置信息，提供字典处理相关的配置
     * @throws JacksonException JSON序列化异常
     */
    public static void writeDictValueToText(JsonGenerator gen, Object value, DictText dictText) throws JacksonException {
        if (value == null) {
            gen.writeString("");
        } else if (value.getClass().isArray()) {
            writeDictValueToText(gen, (Object[]) value, dictText);
        } else if (value instanceof Collection<?> v) {
            writeDictValueToText(gen, v, dictText);
        } else if (value instanceof Iterable<?> v) {
            writeDictValueToText(gen, v, dictText);
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

    /**
     * 处理对象数组的字典值序列化
     * <p>
     * 将对象数组序列化为JSON数组格式，递归处理数组中的每个元素，
     * 确保数组中的每个元素都能正确序列化为字典文本值。
     * </p>
     *
     * @param gen      JSON生成器，用于写入JSON数据
     * @param value    对象数组，需要序列化的数组值
     * @param dictText 字典文本配置信息，提供字典处理相关的配置
     * @throws JacksonException JSON序列化异常
     */
    private static void writeDictValueToText(JsonGenerator gen, Object[] value, DictText dictText) throws JacksonException {
        gen.writeStartArray(value);
        for (Object o : value) {
            writeDictValueToText(gen, o, dictText);
        }
        gen.writeEndArray();
    }

    /**
     * 处理集合的字典值序列化
     * <p>
     * 将集合序列化为JSON数组格式，递归处理集合中的每个元素，
     * 确保集合中的每个元素都能正确序列化为字典文本值。
     * </p>
     *
     * @param gen      JSON生成器，用于写入JSON数据
     * @param value    集合对象，需要序列化的集合值
     * @param dictText 字典文本配置信息，提供字典处理相关的配置
     * @throws JacksonException JSON序列化异常
     */
    private static void writeDictValueToText(JsonGenerator gen, Collection<?> value, DictText dictText) throws JacksonException {
        gen.writeStartArray(value);
        for (Object o : value) {
            writeDictValueToText(gen, o, dictText);
        }
        gen.writeEndArray();
    }

    /**
     * 处理可迭代对象的字典值序列化
     * <p>
     * 将可迭代对象序列化为JSON数组格式，递归处理迭代中的每个元素，
     * 确保可迭代对象中的每个元素都能正确序列化为字典文本值。
     * </p>
     *
     * @param gen      JSON生成器，用于写入JSON数据
     * @param value    可迭代对象，需要序列化的可迭代值
     * @param dictText 字典文本配置信息，提供字典处理相关的配置
     * @throws JacksonException JSON序列化异常
     */
    private static void writeDictValueToText(JsonGenerator gen, Iterable<?> value, DictText dictText) throws JacksonException {
        gen.writeStartArray(value);
        for (Object o : value) {
            writeDictValueToText(gen, o, dictText);
        }
        gen.writeEndArray();
    }
}
