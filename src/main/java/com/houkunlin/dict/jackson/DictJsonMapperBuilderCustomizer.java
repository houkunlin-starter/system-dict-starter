package com.houkunlin.dict.jackson;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import tools.jackson.databind.json.JsonMapper;

/**
 * 数据字典 JSONMapper 初始化处理器，用于向 JSONMapper 构建器添加字典 Jackson 模块。
 * <p>
 * 该类实现了 JsonMapperBuilderCustomizer 接口，在 Spring Boot 应用启动时自动执行，
 * 向 Jackson 的 JSONMapper 构建器添加 DictJacksonModule 模块，从而启用字典值序列化功能。
 *</p>
 *
 * @author HouKunLin
 * @since 1.7.0
 */
public class DictJsonMapperBuilderCustomizer implements JsonMapperBuilderCustomizer {
    /**
     * 自定义 JSONMapper 构建器，添加字典 Jackson 模块。
     * <p>
     * 在 JSONMapper 构建过程中，向构建器添加 DictJacksonModule 模块，
     * 使得带有 DictText 注解的字段能够自动进行字典值序列化。
     *</p>
     *
     * @param jsonMapperBuilder JSONMapper 构建器
     */
    @Override
    public void customize(JsonMapper.@NonNull Builder jsonMapperBuilder) {
        jsonMapperBuilder.addModule(new DictJacksonModule());
    }
}
