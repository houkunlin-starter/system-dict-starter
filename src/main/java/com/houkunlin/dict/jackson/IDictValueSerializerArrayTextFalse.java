package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.util.Collection;
import java.util.Map;

public interface IDictValueSerializerArrayTextFalse extends IDictValueSerializerTree {
    Logger logger = LoggerFactory.getLogger(IDictValueSerializerArrayTextFalse.class);

    default void serializeArrayTextFalse(Object value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree) throws JacksonException {
        Object bean = gen.currentValue();
        String dictType = getDictType(bean, fieldName, dictText);
        if (value.getClass().isArray()) {
            serializeArrayTextFalse(bean, (Object[]) value, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            serializeArrayTextFalse(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            serializeArrayTextFalse(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?> v) {
            gen.writeStartArray();
            gen.writeString(v.getTitle());
            gen.writeEndArray();
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            gen.writeStartArray();
            gen.writeString("");
            gen.writeEndArray();
        } else if (value instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            gen.writeStartArray();
            gen.writeString("");
            gen.writeEndArray();
        } else if (value instanceof CharSequence v) {
            if (dictArray.split().isEmpty()) {
                serializeArrayTextFalse(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                serializeArrayTextFalse(bean, split, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            serializeArrayTextFalse(bean, value.toString(), gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    default void serializeArrayTextFalseFor(Object bean, Object value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (value == null) {
            writeArrayText(gen, null, dictArray);
            return;
        }
        if (value.getClass().isArray()) {
            serializeArrayTextFalse(bean, (Object[]) value, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Collection<?> v) {
            serializeArrayTextFalse(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof Iterable<?> v) {
            serializeArrayTextFalse(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        } else if (value instanceof DictEnum<?> v) {
            gen.writeString(v.getTitle());
        } else if (value.getClass().isEnum()) {
            logger.warn("不支持 Enum 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            gen.writeString("");
        } else if (value instanceof Map<?, ?>) {
            logger.warn("不支持 Map 类型的字典数组序列化，字段名：{}，字段值：{}", fieldName, value);
            gen.writeString("");
        } else if (value instanceof CharSequence v) {
            if (dictArray.split().isEmpty()) {
                serializeArrayTextFalseFor(bean, v, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            } else {
                String[] split = ObjectUtils.getDisplayString(v).split(dictArray.split());
                serializeArrayTextFalse(bean, split, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
            }
        } else {
            serializeArrayTextFalseFor(bean, value.toString(), gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
    }

    default void serializeArrayTextFalse(Object bean, Object[] value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        gen.writeStartArray();
        for (Object o : value) {
            serializeArrayTextFalseFor(bean, o, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
        gen.writeEndArray();
    }

    default void serializeArrayTextFalse(Object bean, Collection<?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        gen.writeStartArray();
        for (Object o : value) {
            serializeArrayTextFalseFor(bean, o, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
        gen.writeEndArray();
    }

    default void serializeArrayTextFalse(Object bean, Iterable<?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        gen.writeStartArray();
        for (Object o : value) {
            serializeArrayTextFalseFor(bean, o, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
        gen.writeEndArray();
    }

    default void serializeArrayTextFalse(Object bean, DictEnum<?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default void serializeArrayTextFalse(Object bean, Map<?, ?> value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {

    }

    default void serializeArrayTextFalse(Object bean, CharSequence[] value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        gen.writeStartArray();
        for (CharSequence charSequence : value) {
            serializeArrayTextFalseFor(bean, charSequence, gen, ctxt, fieldName, dictText, dictArray, dictTree, dictType);
        }
        gen.writeEndArray();
    }

    default void serializeArrayTextFalse(Object bean, CharSequence value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        gen.writeStartArray();
        if (dictTree == null) {
            String text = getDictText(bean, fieldName, value, dictText, dictType, value.toString());
            writeArrayText(gen, text, dictArray);
        } else if (dictTree.toText()) {
            String text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            writeArrayText(gen, text, dictArray);
        } else {
            Collection<String> treeDictTextList = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            gen.writeStartArray();
            for (String text : treeDictTextList) {
                writeArrayText(gen, text, dictArray);
            }
            gen.writeEndArray();
        }
        gen.writeEndArray();
    }

    default void serializeArrayTextFalseFor(Object bean, CharSequence value, JsonGenerator gen, SerializationContext ctxt, String fieldName, DictText dictText, DictArray dictArray, DictTree dictTree, String dictType) throws JacksonException {
        if (dictTree == null) {
            String text = getDictText(bean, fieldName, value, dictText, dictType, value.toString());
            writeArrayText(gen, text, dictArray);
        } else if (dictTree.toText()) {
            String text = getTreeDictTextString(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            writeArrayText(gen, text, dictArray);
        } else {
            Collection<String> treeDictTextList = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, value.toString());
            gen.writeStartArray();
            for (String text : treeDictTextList) {
                writeArrayText(gen, text, dictArray);
            }
            gen.writeEndArray();
        }
    }
}
