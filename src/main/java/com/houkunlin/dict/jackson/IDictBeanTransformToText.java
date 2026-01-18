package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import tools.jackson.core.JacksonException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 字典值转换为文本字符串的接口，用于处理字典值转换为单个文本字符串的逻辑。
 * <p>
 * 该接口提供了将字典值（包括数组、集合、可迭代对象等）转换为单个文本字符串的方法，
 * 支持将多个字典文本使用配置的分隔符连接成一个字符串。
 * 例如：当值为 ["1", "2"] 时，会转换为 "字典1、字典2" 这样的格式。
 * </p>
 * <p>
 * 支持处理字典树结构，可根据配置将字典树转换为单个文本字符串。
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
public interface IDictBeanTransformToText extends IDictValueSerializerTree {
    /**
     * 日志对象
     */
    Logger logger = LoggerFactory.getLogger(IDictBeanTransformToText.class);

    /**
     * 转换字典数组值为文本字符串。
     * <p>
     * 根据字段值类型，将字典数组值转换为文本字符串，使用配置的分隔符连接多个字典文本。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @return 转换后的文本字符串
     * @throws JacksonException Jackson 异常
     */
    default String transformBeanFieldValueToText(final Object bean, final Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree) throws JacksonException {
        String dictType = getDictType(bean, fieldName, dictText);
        if (value.getClass().isArray()) {
            return transformBeanFieldValueToText(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformBeanFieldValueToText(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformBeanFieldValueToText(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?> v) {
            return v.getTitle();
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return "";
        } else if (value instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return "";
        } else if (value instanceof CharSequence v) {
            if (dictArray.split().isEmpty()) {
                return transformBeanFieldValueToText(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformBeanFieldValueToText(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformBeanFieldValueToText(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    /**
     * 转换字典数组值为文本字符串（内部方法）。
     * <p>
     * 处理单个字典值的转换，根据值类型返回对应的文本字符串。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的文本字符串
     * @throws JacksonException Jackson 异常
     */
    default String transformBeanFieldValueToTextForFunc(Object bean, Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            return transformBeanFieldValueToText(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformBeanFieldValueToText(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformBeanFieldValueToText(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?> v) {
            return v.getTitle();
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return "";
        } else if (value instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return "";
        } else if (value instanceof CharSequence v) {
            if (dictArray.split().isEmpty()) {
                return transformBeanFieldValueToText(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformBeanFieldValueToText(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformBeanFieldValueToText(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    /**
     * 转换对象数组为文本字符串。
     * <p>
     * 将对象数组中的每个元素转换为字典文本，然后使用配置的分隔符连接成一个字符串。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的文本字符串
     * @throws JacksonException Jackson 异常
     */
    default String transformBeanFieldValueToText(Object bean, Object[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = transformBeanFieldValueToTextForFunc(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    /**
     * 转换集合为文本字符串。
     * <p>
     * 将集合中的每个元素转换为字典文本，然后使用配置的分隔符连接成一个字符串。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的文本字符串
     * @throws JacksonException Jackson 异常
     */
    default String transformBeanFieldValueToText(Object bean, Collection<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = transformBeanFieldValueToTextForFunc(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    /**
     * 转换可迭代对象为文本字符串。
     * <p>
     * 将可迭代对象中的每个元素转换为字典文本，然后使用配置的分隔符连接成一个字符串。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的文本字符串
     * @throws JacksonException Jackson 异常
     */
    default String transformBeanFieldValueToText(Object bean, Iterable<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = transformBeanFieldValueToTextForFunc(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    /**
     * 转换字典枚举为文本字符串。
     * <p>
     * 处理字典枚举类型的转换，返回枚举的标题。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @throws JacksonException Jackson 异常
     */
    default void transformBeanFieldValueToText(Object bean, DictEnum<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    /**
     * 转换Map为文本字符串。
     * <p>
     * 处理Map类型的转换，目前不支持Map类型的字典数组转换。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @throws JacksonException Jackson 异常
     */
    default void transformBeanFieldValueToText(Object bean, Map<?, ?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    /**
     * 转换字符序列数组为文本字符串。
     * <p>
     * 将字符序列数组中的每个元素转换为字典文本，然后使用配置的分隔符连接成一个字符串。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的文本字符串
     * @throws JacksonException Jackson 异常
     */
    default String transformBeanFieldValueToText(Object bean, CharSequence[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (CharSequence charSequence : value) {
            String text;
            if (dictTree == null) {
                text = getDictText(bean, fieldName, value, dictText, dictType, charSequence.toString());
            } else {
                text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, charSequence.toString());
            }
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    /**
     * 转换字符序列为文本字符串。
     * <p>
     * 将字符序列转换为字典文本，支持字典树结构的处理。
     * </p>
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的文本字符串
     * @throws JacksonException Jackson 异常
     */
    default String transformBeanFieldValueToText(Object bean, CharSequence value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (dictTree == null) {
            return getDictText(bean, fieldName, value, dictText, dictType, value.toString());
        } else {
            return getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
        }
    }
}
