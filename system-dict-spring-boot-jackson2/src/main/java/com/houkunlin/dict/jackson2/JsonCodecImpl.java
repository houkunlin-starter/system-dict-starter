package com.houkunlin.dict.jackson2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.dict.JsonCodec;

/**
 * Jackson2 ObjectMapper 的 {@link JsonCodec} 实现。
 *
 * @author HouKunLin
 */
public class JsonCodecImpl implements JsonCodec {
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     *
     * @param objectMapper Jackson2 ObjectMapper
     */
    public JsonCodecImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String writeValueAsString(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    @Override
    public <T> T readValue(byte[] src, Class<T> valueType) throws Exception {
        return objectMapper.readValue(src, valueType);
    }
}
