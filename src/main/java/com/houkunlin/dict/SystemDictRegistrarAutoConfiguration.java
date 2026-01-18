package com.houkunlin.dict;

import com.houkunlin.dict.bytecode.IDictConverterGenerate;
import com.houkunlin.dict.bytecode.IDictConverterGenerateAsmImpl;
import com.houkunlin.dict.bytecode.IDictConverterGenerateJavassistImpl;
import com.houkunlin.dict.provider.SystemDictProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
     * ASM 字节码编码实现
     *
     * @return IDictConverterGenerate
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "bytecode", havingValue = "ASM", matchIfMissing = true)
    @ConditionalOnMissingBean
    @Bean
    public IDictConverterGenerate dictConverterGenerateAsm() {
        return new IDictConverterGenerateAsmImpl();
    }

    /**
     * JAVASSIST 字节码编码实现
     *
     * @return IDictConverterGenerate
     */
    @ConditionalOnProperty(prefix = "system.dict", name = "bytecode", havingValue = "JAVASSIST")
    @ConditionalOnClass(name = "javassist.ClassPool")
    @ConditionalOnMissingBean
    @Bean
    public IDictConverterGenerate dictConverterGenerateJavassist() {
        return new IDictConverterGenerateJavassistImpl();
    }

    /**
     * 使用 MvcConfigurer 来处理枚举字典转换器，防止在 debug 日志级别下 SpringBoot Context 打印：ConfigurationClassUtils: Could not find class file for introspecting configuration annotations:  异常信息
     *
     * @return MvcConfigurer
     */
    @Bean("systemDictConverterWebMvcConfigurer")
    public SystemDictConverterWebMvcConfigurer systemDictConverterWebMvcConfigurer() {
        return new SystemDictConverterWebMvcConfigurer();
    }
}
