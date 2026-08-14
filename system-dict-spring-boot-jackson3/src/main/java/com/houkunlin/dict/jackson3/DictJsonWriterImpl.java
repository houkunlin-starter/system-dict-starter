package com.houkunlin.dict.jackson3;

import com.houkunlin.dict.DictJsonWriter;
import tools.jackson.core.JsonGenerator;

/**
 * Jackson3 JsonGenerator 的 {@link DictJsonWriter} 适配器实现。
 * <p>
 * 用于将 Jackson3 的 {@link JsonGenerator} 适配为版本无关的 {@link DictJsonWriter}。
 * </p>
 *
 * @author HouKunLin
 */
public class DictJsonWriterImpl implements DictJsonWriter {
    private final JsonGenerator gen;

    /**
     * 构造方法
     *
     * @param gen Jackson3 JSON生成器
     */
    public DictJsonWriterImpl(JsonGenerator gen) {
        this.gen = gen;
    }

    @Override
    public Object currentValue() {
        return gen.currentValue();
    }

    @Override
    public void writeString(String s) {
        gen.writeString(s);
    }

    @Override
    public void writeNull() {
        gen.writeNull();
    }

    @Override
    public void writeStartArray() {
        gen.writeStartArray();
    }

    @Override
    public void writeStartArray(Object forValue) {
        gen.writeStartArray(forValue);
    }

    @Override
    public void writeEndArray() {
        gen.writeEndArray();
    }

    @Override
    public void writeStartObject() {
        gen.writeStartObject();
    }

    @Override
    public void writeStartObject(Object forValue) {
        gen.writeStartObject(forValue);
    }

    @Override
    public void writeEndObject() {
        gen.writeEndObject();
    }

    @Override
    public void writeName(String name) {
        gen.writeName(name);
    }

    @Override
    public void writeObject(Object value) {
        gen.writePOJO(value);
    }
}
