package com.houkunlin.dict.jackson2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.houkunlin.dict.DictJsonWriter;
import lombok.SneakyThrows;

/**
 * Jackson2 JsonGenerator 的 {@link DictJsonWriter} 适配器实现。
 * <p>
 * 用于将 Jackson2 的 {@link JsonGenerator} 适配为版本无关的 {@link DictJsonWriter}。
 * </p>
 *
 * @author HouKunLin
 */
public class DictJsonWriterImpl implements DictJsonWriter {
    private final JsonGenerator gen;

    /**
     * 构造方法
     *
     * @param gen Jackson2 JSON生成器
     */
    public DictJsonWriterImpl(JsonGenerator gen) {
        this.gen = gen;
    }

    @SneakyThrows
    @Override
    public Object currentValue() {
        return gen.getCurrentValue();
    }

    @SneakyThrows
    @Override
    public void writeString(String s) {
        gen.writeString(s);
    }

    @SneakyThrows
    @Override
    public void writeNull() {
        gen.writeNull();
    }

    @SneakyThrows
    @Override
    public void writeStartArray() {
        gen.writeStartArray();
    }

    @SneakyThrows
    @Override
    public void writeStartArray(Object forValue) {
        gen.writeStartArray(forValue);
    }

    @SneakyThrows
    @Override
    public void writeEndArray() {
        gen.writeEndArray();
    }

    @SneakyThrows
    @Override
    public void writeStartObject() {
        gen.writeStartObject();
    }

    @SneakyThrows
    @Override
    public void writeStartObject(Object forValue) {
        gen.writeStartObject(forValue);
    }

    @SneakyThrows
    @Override
    public void writeEndObject() {
        gen.writeEndObject();
    }

    @SneakyThrows
    @Override
    public void writeName(String name) {
        gen.writeFieldName(name);
    }

    @SneakyThrows
    @Override
    public void writeObject(Object value) {
        gen.writeObject(value);
    }
}
