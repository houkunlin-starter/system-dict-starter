package com.houkunlin.dict;


import com.houkunlin.dict.properties.DictProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SystemDictPropertiesConfiguration {

    @ConfigurationProperties("system.dict")
    @Bean
    public DictProperties dictProperties() {
        return new DictProperties();
    }
}
