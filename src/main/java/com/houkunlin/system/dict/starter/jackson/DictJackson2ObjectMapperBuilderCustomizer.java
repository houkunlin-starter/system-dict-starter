package com.houkunlin.system.dict.starter.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 更改默认 ObjectMapper 的 SerializerProvider 对象，主要是为了处理数据字典的 null 值处理问题
 *
 * @author HouKunLin
 * @since 1.4.3
 */
@Component
@RequiredArgsConstructor
public class DictJackson2ObjectMapperBuilderCustomizer implements InitializingBean {
    private final ObjectMapper objectMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        final DictDefaultSerializerProviderImpl impl = new DictDefaultSerializerProviderImpl(objectMapper.getSerializerProvider(), objectMapper.getSerializationConfig(), objectMapper.getSerializerFactory());
        objectMapper.setSerializerProvider(impl);
    }
}
