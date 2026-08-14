package com.houkunlin.dict;

import com.houkunlin.dict.provider.SystemDictProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置 {@link SystemDictScanRegistrar} 所需要的 Bean
 *
 * @author HouKunLin
 * @see SystemDictScanRegistrar
 */
@Getter
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class SystemDictRegistrarAutoConfiguration {
    /**
     * 系统字典存储提供者
     *
     * @return 系统字典提供者
     */
    @ConditionalOnMissingBean
    @Bean
    public SystemDictProvider systemDictProvider() {
        return new SystemDictProvider();
    }

    /**
     * 使用 MvcConfigurer 来处理枚举字典转换器，防止在 debug 日志级别下 SpringBoot Context 打印：ConfigurationClassUtils: Could not find class file for introspecting configuration annotations:  异常信息
     *
     * @return MvcConfigurer
     */
    @Bean
    public DictConverterWebMvcConfigurer dictConverterWebMvcConfigurer() {
        return new DictConverterWebMvcConfigurer();
    }
}
