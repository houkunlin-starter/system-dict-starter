package com.houkunlin.dict.jackson3;

import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.IDictValueSerializerFactory;
import com.houkunlin.dict.JsonCodec;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.jackson.DictValueSerializerUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.ObjectMapper;

/**
 * Jackson3 字典序列化自动配置。
 * <p>
 * 该配置类负责：
 * <ul>
 * <li>向 Spring 注册 {@link DictJsonMapperBuilderCustomizer}，启用 Jackson3 的字典值序列化功能；</li>
 * <li>向 {@link DictUtil} 注册当前版本（Jackson3）的 {@link IDictValueSerializerFactory} 序列化器工厂；</li>
 * <li>注册当前版本（Jackson3）的 {@link JsonCodec} 与 DictType 的 Redis 值序列化器。</li>
 * </ul>
 * </p>
 *
 * @author HouKunLin
 */
@Configuration(proxyBeanMethods = false)
public class DictJacksonAutoConfiguration implements InitializingBean {
    /**
     * 向 {@link DictUtil} 注册 Jackson3 版本的序列化器工厂。
     */
    @Override
    public void afterPropertiesSet() {
        DictUtil.setSerializerFactory((beanClazz, field) -> DictValueSerializerUtil.getDictTextValueSerializer(beanClazz, field));
    }

    /**
     * 数据字典 JSONMapper 初始化处理器。
     *
     * @return JSON 映射构建器自定义器
     */
    @Bean
    public DictJsonMapperBuilderCustomizer dictJsonMapperBuilderCustomizer() {
        return new DictJsonMapperBuilderCustomizer();
    }

    /**
     * 注册 Jackson3 版本的 JSON 序列化工具。
     *
     * @param objectMapper Jackson3 ObjectMapper
     * @return JSON 序列化工具
     */
    @Bean
    public JsonCodec jsonCodec(final ObjectMapper objectMapper) {
        return new JsonCodecImpl(objectMapper);
    }

    /**
     * 注册 DictType 的 Redis 值序列化器。
     *
     * @return Redis 值序列化器
     */
    @ConditionalOnMissingBean
    @Bean
    public RedisSerializer<DictType> dictTypeRedisSerializer() {
        return new JacksonJsonRedisSerializer<>(DictType.class);
    }
}
