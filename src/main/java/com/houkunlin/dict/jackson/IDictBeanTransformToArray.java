package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import tools.jackson.core.JacksonException;

import java.util.*;

/**
 * 字典转换数组文本为 false 接口，用于处理字典数组文本为 false 时的转换逻辑。
 *
 * @author HouKunLin
 * @since 2.0.0
 */
public interface IDictBeanTransformToArray extends IDictValueSerializerTree {
    /**
     * 日志对象
     */
    Logger logger = LoggerFactory.getLogger(IDictBeanTransformToArray.class);

    /**
     * 转换数组文本为 false 的字典值。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArray(final Object bean, final Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree) throws JacksonException {
        String dictType = getDictType(bean, fieldName, dictText);
        if (value.getClass().isArray()) {
            return transformBeanFieldValueToArray(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformBeanFieldValueToArray(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformBeanFieldValueToArray(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?> v) {
            return Collections.singleton(v.getTitle());
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return Collections.singleton("");
        } else if (value instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            return Collections.singleton("");
        } else if (value instanceof CharSequence v) {
            if (dictArray.split().isEmpty()) {
                return transformBeanFieldValueToArray(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformBeanFieldValueToArray(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformBeanFieldValueToArray(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    /**
     * 转换数组文本为 false 的字典值（内部方法）。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArrayForFunc(Object bean, Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            return transformBeanFieldValueToArray(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformBeanFieldValueToArray(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformBeanFieldValueToArray(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
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
                return transformBeanFieldValueToArrayForFunc(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformBeanFieldValueToArray(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformBeanFieldValueToArrayForFunc(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    /**
     * 转换数组文本为 false 的字典值（对象数组）。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArray(Object bean, Object[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (Object o : value) {
            Object result = transformBeanFieldValueToArrayForFunc(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendObjectToList(list, result, dictArray);
        }
        return list;
    }

    /**
     * 转换数组文本为 false 的字典值（集合）。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArray(Object bean, Collection<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (Object o : value) {
            Object result = transformBeanFieldValueToArrayForFunc(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendObjectToList(list, result, dictArray);
        }
        return list;
    }

    /**
     * 转换数组文本为 false 的字典值（可迭代对象）。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArray(Object bean, Iterable<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (Object o : value) {
            Object result = transformBeanFieldValueToArrayForFunc(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendObjectToList(list, result, dictArray);
        }
        return list;
    }

    /**
     * 转换数组文本为 false 的字典值（字典枚举）。
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
    default void transformBeanFieldValueToArray(Object bean, DictEnum<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    /**
     * 转换数组文本为 false 的字典值（Map）。
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
    default void transformBeanFieldValueToArray(Object bean, Map<?, ?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    /**
     * 转换数组文本为 false 的字典值（字符序列数组）。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArray(Object bean, CharSequence[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (CharSequence charSequence : value) {
            Object result = transformBeanFieldValueToArrayForFunc(bean, charSequence, fieldName, dictText, dictArray, dictTree, dictType);
            appendObjectToList(list, result, dictArray);
        }
        return list;
    }

    /**
     * 转换数组文本为 false 的字典值（字符序列）。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArray(Object bean, CharSequence value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        if (dictTree == null) {
            String text = getDictText(bean, fieldName, value, dictText, dictType, value.toString());
            appendObjectToList(list, text, dictArray);
        } else if (dictTree.toText()) {
            String text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            appendObjectToList(list, text, dictArray);
        } else {
            Collection<String> treeDictTextList = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            list.add(treeDictTextList);
        }
        return list;
    }

    /**
     * 转换数组文本为 false 的字典值（字符序列，内部方法）。
     *
     * @param bean      Bean 对象
     * @param value     字段值
     * @param fieldName 字段名称
     * @param dictText  字典文本注解配置
     * @param dictArray 字典数组注解配置
     * @param dictTree  字典树注解配置
     * @param dictType  字典类型
     * @return 转换后的字典值
     * @throws JacksonException Jackson 异常
     */
    default Object transformBeanFieldValueToArrayForFunc(Object bean, CharSequence value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (dictTree == null) {
            return getDictText(bean, fieldName, value, dictText, dictType, value.toString());
        } else if (dictTree.toText()) {
            return getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
        } else {
            return getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
        }
    }
}
