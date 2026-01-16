package com.houkunlin.dict.jackson;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import tools.jackson.databind.json.JsonMapper;

/**
 * 数据字典JSONMapper初始化处理
 *
 * @author HouKunLin
 */
public class DictJsonMapperBuilderCustomizer implements JsonMapperBuilderCustomizer {
    @Override
    public void customize(JsonMapper.@NonNull Builder jsonMapperBuilder) {
        jsonMapperBuilder.addModule(new DictJacksonModule());
    }
}
