package com.houkunlin.system.dict.starter.bytecode;

import com.houkunlin.system.dict.starter.DictException;
import com.houkunlin.system.dict.starter.json.DictConverter;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * 动态生成字典转换器
 *
 * @author HouKunLin
 */
public interface IDictConverterGenerate {
    /**
     * 动态生成字典转换器，并将其注册到Spring容器中
     *
     * @param factory       容器
     * @param clazz         字典枚举类
     * @param dictConverter 字典转换器注解
     * @throws DictException 字典处理异常
     */
    void registerBean(final DefaultListableBeanFactory factory, final Class<?> clazz, final DictConverter dictConverter) throws DictException;
}
