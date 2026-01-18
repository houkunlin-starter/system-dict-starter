package com.houkunlin.dict;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 系统字典转换器 WebMvc 配置器
 * <p>使用 WebMvcConfigurer 来处理枚举字典转换器，防止在 debug 日志级别下 SpringBoot Context 打印：ConfigurationClassUtils: Could not find class file for introspecting configuration annotations: 异常信息</p>
 * <p>主要用于注册系统字典相关的类型转换器，确保枚举类型等能够正确转换</p>
 *
 * @author HouKunLin
 */
public class SystemDictConverterWebMvcConfigurer implements WebMvcConfigurer {
    /**
     * 转换器类集合，存储需要注册的转换器类
     */
    private final Set<Class<?>> classes = new LinkedHashSet<>();

    /**
     * 添加转换器类
     *
     * @param clazz 转换器类
     */
    public void addConverterClass(Class<?> clazz) {
        this.classes.add(clazz);
    }

    /**
     * 添加格式化器和转换器到注册表
     *
     * @param registry 格式化器注册表
     */
    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        for (Class<?> aClass : this.classes) {
            try {
                // 创建转换器实例
                Object instance = ClassUtil.newInstance(aClass);
                // 检查是否为转换器实例
                if (instance instanceof Converter converter) {
                    // 注册转换器
                    registry.addConverter(converter);
                }
            } catch (NoSuchMethodException ignore) {
                // 忽略无参构造函数异常
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                // 其他异常直接抛出
                throw new RuntimeException(e);
            }
        }
    }
}
