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
    /**
     * Jackson2 JSON 生成器，负责实际的 JSON 写入操作
     */
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

    /**
     * 写入字符串值
     *
     * @param s 字符串
     */
    @SneakyThrows
    @Override
    public void writeString(String s) {
        gen.writeString(s);
    }

    /**
     * 写入 null 值
     */
    @SneakyThrows
    @Override
    public void writeNull() {
        gen.writeNull();
    }

    /**
     * 开始写入数组
     */
    @SneakyThrows
    @Override
    public void writeStartArray() {
        gen.writeStartArray();
    }

    /**
     * 开始写入数组
     *
     * @param forValue 数组对应的对象
     */
    @SneakyThrows
    @Override
    public void writeStartArray(Object forValue) {
        gen.writeStartArray(forValue);
    }

    /**
     * 结束写入数组
     */
    @SneakyThrows
    @Override
    public void writeEndArray() {
        gen.writeEndArray();
    }

    /**
     * 开始写入对象
     */
    @SneakyThrows
    @Override
    public void writeStartObject() {
        gen.writeStartObject();
    }

    /**
     * 开始写入对象
     *
     * @param forValue 对象对应的值
     */
    @SneakyThrows
    @Override
    public void writeStartObject(Object forValue) {
        gen.writeStartObject(forValue);
    }

    /**
     * 结束写入对象
     */
    @SneakyThrows
    @Override
    public void writeEndObject() {
        gen.writeEndObject();
    }

    /**
     * 写入字段名称
     *
     * @param name 字段名称
     */
    @SneakyThrows
    @Override
    public void writeName(String name) {
        gen.writeFieldName(name);
    }

    /**
     * 写入对象值
     *
     * @param value 对象值
     */
    @SneakyThrows
    @Override
    public void writeObject(Object value) {
        gen.writeObject(value);
    }
}
