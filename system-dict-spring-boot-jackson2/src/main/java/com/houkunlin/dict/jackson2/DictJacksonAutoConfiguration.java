package com.houkunlin.dict.jackson2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.dict.DictJsonCodec;
import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.DictValueSerializerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson2 字典序列化自动配置。
 * <p>
 * 该配置类负责：
 * <ul>
 * <li>向 Spring 注册 {@link DictJacksonModule}，启用 Jackson2 的字典值序列化功能；</li>
 * <li>向 {@link DictUtil} 注册当前版本（Jackson2）的 {@link DictValueSerializerFactory} 序列化器工厂；</li>
 * <li>注册当前版本（Jackson2）的 {@link DictJsonCodec}。</li>
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
     * 注册 Jackson2 版本的字典值序列化模块。
     *
     * @return 字典 Jackson 模块
     */
    @Bean
    public DictJacksonModule dictJacksonModule() {
        return new DictJacksonModule();
    }

}
