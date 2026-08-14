package com.houkunlin.dict.jackson2;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * 数据字典 Jackson2ObjectMapperBuilder 初始化处理器，用于向 ObjectMapper 构建器添加字典 Jackson 模块。
 * <p>
 * 该类实现了 Jackson2ObjectMapperBuilderCustomizer 接口，在 Spring Boot 应用启动时自动执行，
 * 向 Jackson 的 ObjectMapper 构建器添加 DictJacksonModule 模块，从而启用字典值序列化功能。
 * </p>
 *
 * @author HouKunLin
 * @since 1.7.0
 */
public class DictJsonMapperBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {
    /**
     * 自定义 Jackson2ObjectMapperBuilder 构建器，添加字典 Jackson 模块。
     * <p>
     * 在 ObjectMapper 构建过程中，向构建器添加 DictJacksonModule 模块，
     * 使得带有 DictText 注解的字段能够自动进行字典值序列化。
     * </p>
     *
     * @param jacksonObjectMapperBuilder ObjectMapper 构建器
     */
    @Override
    public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        jacksonObjectMapperBuilder.modules(new DictJacksonModule());
    }
}
