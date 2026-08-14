package com.houkunlin.dict;

/**
 * 字典 JSON 写入器抽象接口。
 * <p>
 * 该接口抽象了 Jackson2（{@code com.fasterxml.jackson.core.JsonGenerator}）与
 * Jackson3（{@code tools.jackson.core.JsonGenerator}）的 JSON 写入操作，
 * 使字典序列化逻辑与具体 Jackson 版本解耦。
 * 各版本 Starter 提供对应的 {@link DictJsonWriter} 适配器实现。
 * </p>
 *
 * @author HouKunLin
 */
public interface DictJsonWriter {
    /**
     * 获取当前正在序列化的对象。
     *
     * @return 当前值
     */
    Object currentValue();

    /**
     * 写入字符串值。
     *
     * @param s 字符串
     */
    void writeString(String s);

    /**
     * 写入 null 值。
     */
    void writeNull();

    /**
     * 开始写入数组。
     */
    void writeStartArray();

    /**
     * 开始写入数组。
     *
     * @param forValue 数组对应的对象
     */
    void writeStartArray(Object forValue);

    /**
     * 结束写入数组。
     */
    void writeEndArray();

    /**
     * 开始写入对象。
     */
    void writeStartObject();

    /**
     * 开始写入对象。
     *
     * @param forValue 对象对应的值
     */
    void writeStartObject(Object forValue);

    /**
     * 结束写入对象。
     */
    void writeEndObject();

    /**
     * 写入字段名称。
     *
     * @param name 字段名称
     */
    void writeName(String name);

    /**
     * 写入对象值。
     *
     * @param value 对象值
     */
    void writeObject(Object value);
}
