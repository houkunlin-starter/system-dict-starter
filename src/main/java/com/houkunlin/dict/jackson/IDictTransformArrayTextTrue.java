package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.enums.NullStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import tools.jackson.core.JacksonException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IDictTransformArrayTextTrue extends IDictValueSerializerTree {
    Logger logger = LoggerFactory.getLogger(IDictTransformArrayTextTrue.class);

    default String transformArrayTextTrue(final Object bean, final Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree) throws JacksonException {
        String dictType = getDictType(bean, fieldName, dictText);
        if (value.getClass().isArray()) {
            return transformArrayTextTrue(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformArrayTextTrue(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformArrayTextTrue(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
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
                return transformArrayTextTrue(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformArrayTextTrue(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformArrayTextTrue(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    default String transformArrayTextTrueFor(Object bean, Object value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            return transformArrayTextTrue(bean, (Object[]) value, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return transformArrayTextTrue(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return transformArrayTextTrue(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
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
                return transformArrayTextTrue(bean, v, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return transformArrayTextTrue(bean, split, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return transformArrayTextTrue(bean, value.toString(), fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    default String transformArrayTextTrue(Object bean, Object[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = transformArrayTextTrueFor(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    default String transformArrayTextTrue(Object bean, Collection<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = transformArrayTextTrueFor(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    default String transformArrayTextTrue(Object bean, Iterable<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = transformArrayTextTrueFor(bean, o, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    default void transformArrayTextTrue(Object bean, DictEnum<?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default void transformArrayTextTrue(Object bean, Map<?, ?> value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default String transformArrayTextTrue(Object bean, CharSequence[] value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (CharSequence charSequence : value) {
            String text;
            if (dictTree == null) {
                text = getDictText(bean, fieldName, value, dictText, dictType, charSequence.toString());
            } else {
                text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, charSequence.toString());
            }
            if (text != null) {
                textList.add(text);
            } else if (dictArray.nullStrategy() != NullStrategy.IGNORE) {
                if (dictArray.nullStrategy() == NullStrategy.NULL) {
                    textList.add(null);
                } else {
                    textList.add("");
                }
            }
        }
        return String.join(dictArray.delimiter(), textList);
    }

    default String transformArrayTextTrue(Object bean, CharSequence value, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (dictTree == null) {
            return getDictText(bean, fieldName, value, dictText, dictType, value.toString());
        } else {
            return getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
        }
    }
}
