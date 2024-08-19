package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bytecode.IDictConverterGenerate;
import com.houkunlin.system.dict.starter.bytecode.IDictConverterGenerateAsmImpl;
import com.houkunlin.system.dict.starter.bytecode.IDictConverterGenerateJavassistImpl;
import com.houkunlin.system.dict.starter.provider.SystemDictProvider;
import javassist.ClassPool;
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
    @ConditionalOnProperty(prefix = "system.dict", name = "bytecode", havingValue = "ASM")
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
    @ConditionalOnProperty(prefix = "system.dict", name = "bytecode", havingValue = "JAVASSIST", matchIfMissing = true)
    @ConditionalOnClass(ClassPool.class)
    @ConditionalOnMissingBean
    @Bean
    public IDictConverterGenerate dictConverterGenerateJavassist() {
        return new IDictConverterGenerateJavassistImpl();
    }
}
