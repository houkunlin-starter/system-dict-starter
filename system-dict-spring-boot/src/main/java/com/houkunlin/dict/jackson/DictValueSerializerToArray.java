package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.DictJsonWriter;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 字典值序列化为文本数组的接口，用于处理字典值序列化为文本数组的逻辑。
 * <p>
 * 该接口提供了将字典值（包括数组、集合、可迭代对象等）序列化为文本数组的方法，
 * 每个字典值对应一个文本元素。
 * 例如：当值为 ["1", "2"] 时，会转换为 ["字典1", "字典2"] 这样的格式。
 * </p>
 * <p>
 * 支持处理字典树结构，可根据配置将字典树转换为文本数组。
 * 通过 {@link DictJsonWriter} 抽象与具体 Jackson 版本解耦。
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
public interface DictValueSerializerToArray extends DictValueSerializerTree {
    /**
     * 日志对象
     */
    Logger logger = LoggerFactory.getLogger(DictValueSerializerToArray.class);

    /**
     * 序列化字典数组值为文本数组。
     * <p>
     * 根据字段值类型，将字典数组值转换为文本数组，每个字典值对应一个文本元素。
     * </p>
     *
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     */
    default void serializeValueToArray(Object value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree) {
        Object bean = writer.currentValue();
        String dictType = getDictType(bean, fieldName, dictText);
        if (value.getClass().isArray()) {
            serializeValueToArray(bean, (Object[]) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?>) {
            serializeValueToArray(bean, (Collection<?>) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?>) {
            serializeValueToArray(bean, (Iterable<?>) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?>) {
            writer.writeStartArray();
            writer.writeString(((DictEnum<?>) value).getTitle());
            writer.writeEndArray();
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            writer.writeStartArray();
            writer.writeString("");
            writer.writeEndArray();
        } else if (value instanceof Map<?, ?>) {
            serializeValueToArray(bean, (Map<?, ?>) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof CharSequence) {
            if (dictArray.split().isEmpty()) {
                serializeValueToArray(bean, (CharSequence) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(value).split(dictArray.split());
                serializeValueToArray(bean, split, writer, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            serializeValueToArray(bean, value.toString(), writer, fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    /**
     * 序列化字典数组值为文本数组（内部方法）。
     * <p>
     * 处理单个字典值的序列化，根据值类型将其转换为对应的文本并写入数组。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArrayForFunc(Object bean, Object value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        if (value == null) {
            writeArrayText(writer, null, dictArray);
            return;
        }
        if (value.getClass().isArray()) {
            serializeValueToArray(bean, (Object[]) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?>) {
            serializeValueToArray(bean, (Collection<?>) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?>) {
            serializeValueToArray(bean, (Iterable<?>) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?>) {
            writer.writeString(((DictEnum<?>) value).getTitle());
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            writer.writeString("");
        } else if (value instanceof Map<?, ?>) {
            serializeValueToArray(bean, (Map<?, ?>) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof CharSequence) {
            if (dictArray.split().isEmpty()) {
                serializeValueToArrayForFunc(bean, (CharSequence) value, writer, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(value).split(dictArray.split());
                serializeValueToArray(bean, split, writer, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            serializeValueToArrayForFunc(bean, value.toString(), writer, fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    /**
     * 序列化对象数组为文本数组。
     * <p>
     * 将对象数组中的每个元素转换为字典文本，然后写入到一个数组中。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArray(Object bean, Object[] value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        writer.writeStartArray();
        for (Object o : value) {
            serializeValueToArrayForFunc(bean, o, writer, fieldName, dictText, dictArray, dictTree, dictType);
        }
        writer.writeEndArray();
    }

    /**
     * 序列化集合为文本数组。
     * <p>
     * 将集合中的每个元素转换为字典文本，然后写入到一个数组中。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArray(Object bean, Collection<?> value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        writer.writeStartArray();
        for (Object o : value) {
            serializeValueToArrayForFunc(bean, o, writer, fieldName, dictText, dictArray, dictTree, dictType);
        }
        writer.writeEndArray();
    }

    /**
     * 序列化可迭代对象为文本数组。
     * <p>
     * 将可迭代对象中的每个元素转换为字典文本，然后写入到一个数组中。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArray(Object bean, Iterable<?> value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        writer.writeStartArray();
        for (Object o : value) {
            serializeValueToArrayForFunc(bean, o, writer, fieldName, dictText, dictArray, dictTree, dictType);
        }
        writer.writeEndArray();
    }

    /**
     * 序列化字典枚举为文本数组。
     * <p>
     * 处理字典枚举类型的序列化，将枚举的标题写入数组。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArray(Object bean, DictEnum<?> value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {

    }

    /**
     * 序列化Map为文本数组。
     * <p>
     * 处理Map类型的序列化，将 Map 的每个键转换为对应的字典文本，并以 JSON 对象形式输出。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArray(Object bean, Map<?, ?> value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        writer.writeStartObject(value);
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            String v = entry.getKey().toString();
            writer.writeName(v);
            if (dictArray.split().isEmpty()) {
                if (dictTree == null) {
                    writer.writeStartArray();
                    serializeValueToArrayForFunc(bean, v, writer, fieldName, dictText, dictArray, dictTree, dictType);
                    writer.writeEndArray();
                } else {
                    serializeValueToArrayForFunc(bean, v, writer, fieldName, dictText, dictArray, dictTree, dictType);
                }
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                serializeValueToArray(bean, split, writer, fieldName, dictText, dictArray, dictTree, dictType);
            }
        }
        writer.writeEndObject();
    }

    /**
     * 序列化字符序列数组为文本数组。
     * <p>
     * 将字符序列数组中的每个元素转换为字典文本，然后写入到一个数组中。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArray(Object bean, CharSequence[] value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        writer.writeStartArray();
        for (CharSequence charSequence : value) {
            serializeValueToArrayForFunc(bean, charSequence, writer, fieldName, dictText, dictArray, dictTree, dictType);
        }
        writer.writeEndArray();
    }

    /**
     * 序列化字符序列为文本数组。
     * <p>
     * 将字符序列转换为字典文本，然后写入到一个数组中。支持字典树结构的处理。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArray(Object bean, CharSequence value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        writer.writeStartArray();
        if (dictTree == null) {
            String text = getDictText(bean, fieldName, value, dictText, dictType, value.toString());
            writeArrayText(writer, text, dictArray);
        } else if (dictTree.toText()) {
            String text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            writeArrayText(writer, text, dictArray);
        } else {
            Collection<String> treeDictTextList = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            writer.writeStartArray();
            for (String text : treeDictTextList) {
                writeArrayText(writer, text, dictArray);
            }
            writer.writeEndArray();
        }
        writer.writeEndArray();
    }

    /**
     * 序列化字符序列为文本数组（内部方法）。
     * <p>
     * 处理单个字符序列的序列化，根据字典树配置将其转换为对应的文本并写入数组。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param writer    JSON写入器
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     */
    default void serializeValueToArrayForFunc(Object bean, CharSequence value, DictJsonWriter writer, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) {
        if (dictTree == null) {
            String text = getDictText(bean, fieldName, value, dictText, dictType, value.toString());
            writeArrayText(writer, text, dictArray);
        } else if (dictTree.toText()) {
            String text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            writeArrayText(writer, text, dictArray);
        } else {
            Collection<String> treeDictTextList = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            writer.writeStartArray();
            for (String text : treeDictTextList) {
                writeArrayText(writer, text, dictArray);
            }
            writer.writeEndArray();
        }
    }
}
