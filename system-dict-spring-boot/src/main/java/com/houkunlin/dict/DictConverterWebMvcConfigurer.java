package com.houkunlin.dict;

import com.houkunlin.dict.converter.*;
import org.jspecify.annotations.NonNull;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 系统字典转换器Web MVC配置类
 * 该类实现了WebMvcConfigurer接口，用于注册各种数据类型到字典枚举类型的转换器工厂
 */
public class DictConverterWebMvcConfigurer implements WebMvcConfigurer {

    /**
     * 添加格式化转换器
     * 注册多个工厂类，用于将不同数据类型自动转换为对应的字典枚举类型
     *
     * @param registry 格式化转换器注册表
     */
    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        // 注册布尔类型到字典枚举的转换器工厂
        registry.addConverterFactory(new BooleanToDictEnumConverterFactory());
        // 注册字节类型到字典枚举的转换器工厂
        registry.addConverterFactory(new ByteToDictEnumConverterFactory());
        // 注册双精度浮点型到字典枚举的转换器工厂
        registry.addConverterFactory(new DoubleToDictEnumConverterFactory());
        // 注册单精度浮点型到字典枚举的转换器工厂
        registry.addConverterFactory(new FloatToDictEnumConverterFactory());
        // 注册整型到字典枚举的转换器工厂
        registry.addConverterFactory(new IntegerToDictEnumConverterFactory());
        // 注册长整型到字典枚举的转换器工厂
        registry.addConverterFactory(new LongToDictEnumConverterFactory());
        // 注册短整型到字典枚举的转换器工厂
        registry.addConverterFactory(new ShortToDictEnumConverterFactory());
        // 注册字符串到字典枚举的转换器工厂
        registry.addConverterFactory(new StringToDictEnumConverterFactory());
    }
}
