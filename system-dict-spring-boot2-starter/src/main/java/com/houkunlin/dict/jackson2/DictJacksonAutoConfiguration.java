package com.houkunlin.dict.jackson2;

import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.IDictValueSerializerFactory;
import com.houkunlin.dict.jackson.DictValueSerializerUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson2 字典序列化自动配置。
 * <p>
 * 该配置类负责：
 * <ul>
 * <li>向 Spring 注册 {@link DictJsonMapperBuilderCustomizer}，启用 Jackson2 的字典值序列化功能；</li>
 * <li>向 {@link DictUtil} 注册当前版本（Jackson2）的 {@link IDictValueSerializerFactory} 序列化器工厂。</li>
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
}
