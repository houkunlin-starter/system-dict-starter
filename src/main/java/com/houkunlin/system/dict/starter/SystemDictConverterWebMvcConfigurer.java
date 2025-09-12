package com.houkunlin.system.dict.starter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 使用 MvcConfigurer 来处理枚举字典转换器，防止在 debug 日志级别下 SpringBoot Context 打印：ConfigurationClassUtils: Could not find class file for introspecting configuration annotations:  异常信息
 *
 * @author HouKunLin
 */
public class SystemDictConverterWebMvcConfigurer implements WebMvcConfigurer {
    private final Set<Class<?>> classes = new LinkedHashSet<>();

    public void addConverterClass(Class<?> clazz) {
        this.classes.add(clazz);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        for (Class<?> aClass : this.classes) {
            for (Constructor<?> constructor : aClass.getConstructors()) {
                if (constructor.getParameterCount() == 0) {
                    try {
                        Object o = constructor.newInstance();
                        if (o instanceof Converter converter) {
                            registry.addConverter(converter);
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
