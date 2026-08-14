package com.houkunlin.dict;


import com.houkunlin.dict.properties.DictProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 系统字典属性自动配置
 * <p>
 * 负责注册 {@link DictProperties} 配置信息对象，并绑定 {@code system.dict} 前缀的配置项。
 * </p>
 *
 * @author HouKunLin
 */
@Configuration(proxyBeanMethods = false)
public class SystemDictPropertiesAutoConfiguration {

    /**
     * 注册数据字典配置信息对象
     * <p>
     * 该对象绑定 {@code system.dict} 前缀的配置属性，包含字典系统的各种配置参数。
     * </p>
     *
     * @return 数据字典配置信息对象
     */
    @ConfigurationProperties("system.dict")
    @Bean
    public DictProperties dictProperties() {
        return new DictProperties();
    }
}
