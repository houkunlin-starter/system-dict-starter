package com.houkunlin.dict.jackson2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.dict.DictJsonCodec;

/**
 * Jackson2 ObjectMapper 的 {@link DictJsonCodec} 实现。
 *
 * @author HouKunLin
 */
public class DictJsonCodecImpl implements DictJsonCodec {
    /**
     * Jackson2 ObjectMapper 对象，用于 JSON 序列化与反序列化
     */
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     *
     * @param objectMapper Jackson2 ObjectMapper
     */
    public DictJsonCodecImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param value 对象
     * @return JSON 字符串
     * @throws Exception 序列化异常
     */
    @Override
    public String writeValueAsString(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    /**
     * 将字节数组反序列化为指定类型对象
     *
     * @param src       字节数组
     * @param valueType 目标类型
     * @param <T>       目标类型
     * @return 反序列化对象
     * @throws Exception 反序列化异常
     */
    @Override
    public <T> T readValue(byte[] src, Class<T> valueType) throws Exception {
        return objectMapper.readValue(src, valueType);
    }
}
