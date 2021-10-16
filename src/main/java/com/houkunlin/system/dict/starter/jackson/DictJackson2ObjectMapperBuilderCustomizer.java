package com.houkunlin.system.dict.starter.jackson;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

/**
 * 自定义 ObjectMapper 处理器，主要是为了处理数据字典的 null 值处理问题
 *
 * @author HouKunLin
 * @since 1.4.3
 */
@Component
public class DictJackson2ObjectMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
    @Override
    public void customize(final Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.postConfigurer(objectMapper -> {
            // 复用原来的一些配置参数
            final DictDefaultSerializerProviderImpl impl = new DictDefaultSerializerProviderImpl(objectMapper.getSerializerProvider(), objectMapper.getSerializationConfig(), objectMapper.getSerializerFactory());
            objectMapper.setSerializerProvider(impl);
        });
    }
}
