package com.houkunlin.dict.jackson2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.dict.DictJsonCodec;
import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.DictValueSerializerFactory;
import com.houkunlin.dict.bean.DictType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Jackson2 字典序列化自动配置。
 * <p>
 * 该配置类负责：
 * <ul>
 * <li>向 Spring 注册 {@link DictJsonMapperBuilderCustomizer}，启用 Jackson2 的字典值序列化功能；</li>
 * <li>向 {@link DictUtil} 注册当前版本（Jackson2）的 {@link DictValueSerializerFactory} 序列化器工厂；</li>
 * <li>注册当前版本（Jackson2）的 {@link DictJsonCodec} 与 DictType 的 Redis 值序列化器。</li>
 * </ul>
 * </p>
 *
 * @author HouKunLin
 */
@Configuration(proxyBeanMethods = false)
public class DictJacksonAutoConfiguration implements InitializingBean {
    /**
     * 向 {@link DictUtil} 注册 Jackson2 版本的序列化器工厂。
     */
    @Override
    public void afterPropertiesSet() {
        DictUtil.setSerializerFactory(DictValueSerializerUtil::getDictTextValueSerializer);
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
     * 注册 Jackson2 版本的 JSON 序列化工具。
     *
     * @param objectMapper Jackson2 ObjectMapper
     * @return JSON 序列化工具
     */
    @Bean
    public DictJsonCodec dictJsonCodec(final ObjectMapper objectMapper) {
        return new DictJsonCodecImpl(objectMapper);
    }

    /**
     * 注册 DictType 的 Redis 值序列化器。
     *
     * @return Redis 值序列化器
     */
    @ConditionalOnMissingBean
    @Bean
    public RedisSerializer<DictType> dictTypeRedisSerializer() {
        return new Jackson2JsonRedisSerializer<>(DictType.class);
    }
}
