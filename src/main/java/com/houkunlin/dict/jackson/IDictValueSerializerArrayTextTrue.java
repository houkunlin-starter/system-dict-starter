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
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IDictValueSerializerArrayTextTrue extends IDictValueSerializerTree {
    Logger logger = LoggerFactory.getLogger(IDictValueSerializerArrayTextTrue.class);

    default void serializeArrayTextTrue(Object value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree) throws JacksonException {
        Object bean = gen.currentValue();
        String dictType = getDictType(bean, fieldName, dictText);
        String text;
        if (value.getClass().isArray()) {
            text = serializeArrayTextTrue(bean, (Object[]) value, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            text = serializeArrayTextTrue(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            text = serializeArrayTextTrue(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?> v) {
            text = v.getTitle();
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            text = "";
        } else if (value instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            text = "";
        } else if (value instanceof CharSequence v) {
            if (dictArray.split().isEmpty()) {
                text = serializeArrayTextTrue(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                text = serializeArrayTextTrue(bean, split, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            text = serializeArrayTextTrue(bean, value.toString(), gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
        if (text != null) {
            gen.writeString(text);
        } else if (isTextNullable()) {
            gen.writeNull();
        } else {
            gen.writeString("");
        }
    }

    default String serializeArrayTextTrueFor(Object bean, Object value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            return serializeArrayTextTrue(bean, (Object[]) value, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            return serializeArrayTextTrue(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            return serializeArrayTextTrue(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
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
                return serializeArrayTextTrue(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                return serializeArrayTextTrue(bean, split, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            return serializeArrayTextTrue(bean, value.toString(), gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    default String serializeArrayTextTrue(Object bean, Object[] value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = serializeArrayTextTrueFor(bean, o, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    default String serializeArrayTextTrue(Object bean, Collection<?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = serializeArrayTextTrueFor(bean, o, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    default String serializeArrayTextTrue(Object bean, Iterable<?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        List<String> textList = new ArrayList<>();
        for (Object o : value) {
            String text = serializeArrayTextTrueFor(bean, o, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            appendTextToList(textList, text, dictArray);
        }
        return String.join(dictArray.delimiter(), textList);
    }

    default void serializeArrayTextTrue(Object bean, DictEnum<?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default void serializeArrayTextTrue(Object bean, Map<?, ?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default String serializeArrayTextTrue(Object bean, CharSequence[] value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
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

    default String serializeArrayTextTrue(Object bean, CharSequence value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (dictTree == null) {
            return getDictText(bean, fieldName, value, dictText, dictType, value.toString());
        } else {
            return getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
        }
    }
}
