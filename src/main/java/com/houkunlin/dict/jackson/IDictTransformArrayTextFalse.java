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

public interface IDictTransformArrayTextFalse extends IDictValueSerializerTree {
    Logger logger = LoggerFactory.getLogger(IDictTransformArrayTextFalse.class);

    default Object transformArrayTextFalse(final Object bean, final Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree) throws JacksonException {
        String dictType = getDictType(bean, fieldName, dictText);
        if (value.getClass().isArray()) {
            return transformArrayTextFalse(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformArrayTextFalse(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformArrayTextFalse(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
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
                return transformArrayTextFalse(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformArrayTextFalse(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformArrayTextFalse(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    default Object transformArrayTextFalseFor(Object bean, Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            return transformArrayTextFalse(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformArrayTextFalse(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformArrayTextFalse(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
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
                return transformArrayTextFalseFor(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformArrayTextFalse(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformArrayTextFalseFor(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    default Object transformArrayTextFalse(Object bean, Object[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (Object o : value) {
            list.add(transformArrayTextFalseFor(bean, o, fieldName, dictText, dictArray, dictTree, dictType));
        }
        return list;
    }

    default Object transformArrayTextFalse(Object bean, Collection<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (Object o : value) {
            list.add(transformArrayTextFalseFor(bean, o, fieldName, dictText, dictArray, dictTree, dictType));
        }
        return list;
    }

    default Object transformArrayTextFalse(Object bean, Iterable<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (Object o : value) {
            list.add(transformArrayTextFalseFor(bean, o, fieldName, dictText, dictArray, dictTree, dictType));
        }
        return list;
    }

    default void transformArrayTextFalse(Object bean, DictEnum<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default void transformArrayTextFalse(Object bean, Map<?, ?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default Object transformArrayTextFalse(Object bean, CharSequence[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        for (CharSequence charSequence : value) {
            list.add(transformArrayTextFalseFor(bean, charSequence, fieldName, dictText, dictArray, dictTree, dictType));
        }
        return list;
    }

    default Object transformArrayTextFalse(Object bean, CharSequence value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<Object> list = new ArrayList<>();
        if (dictTree == null) {
            String text = getDictText(bean, fieldName, value, dictText, dictType, value.toString());
            list.add(text);
        } else if (dictTree.toText()) {
            String text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            list.add(text);
        } else {
            Collection<String> treeDictTextList = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            list.add(treeDictTextList);
        }
        return list;
    }

    default Object transformArrayTextFalseFor(Object bean, CharSequence value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (dictTree == null) {
            return getDictText(bean, fieldName, value, dictText, dictType, value.toString());
        } else if (dictTree.toText()) {
            return getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
        } else {
            return getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
        }
    }
}
