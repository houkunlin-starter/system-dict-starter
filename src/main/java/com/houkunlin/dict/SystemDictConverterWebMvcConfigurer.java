package com.houkunlin.dict;

import org.jspecify.annotations.NonNull;
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
    public void addFormatters(@NonNull FormatterRegistry registry) {
        for (Class<?> aClass : this.classes) {
            try {
                Object instance = ClassUtil.newInstance(aClass);
                if (instance instanceof Converter converter) {
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
