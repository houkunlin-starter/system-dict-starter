package com.houkunlin.dict.jackson3;

import com.houkunlin.dict.DictJsonCodec;
import tools.jackson.databind.ObjectMapper;

/**
 * Jackson3 ObjectMapper 的 {@link DictJsonCodec} 实现。
 *
 * @author HouKunLin
 */
public class DictJsonCodecImpl implements DictJsonCodec {
    private final ObjectMapper objectMapper;

    /**
     * 构造方法
     *
     * @param objectMapper Jackson3 ObjectMapper
     */
    public DictJsonCodecImpl(ObjectMapper objectMapper) {
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
