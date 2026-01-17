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

public class DictValueSerializerArrayTextFalseImpl extends DictValueSerializer implements IDictValueSerializerArrayTextFalse, IDictTransformArrayTextFalse {
    public DictValueSerializerArrayTextFalseImpl(String fieldName, Class<?> javaTypeRawClass, DictText dictText, DictArray dictArray, DictTree dictTree) {
        super(fieldName, javaTypeRawClass, dictText, dictArray, dictTree);
    }

    @Override
    public DictTypeKeyHandler<Object> getDictTypeKeyHandler() {
        return dictTypeKeyHandler;
    }

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
