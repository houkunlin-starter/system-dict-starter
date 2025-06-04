package com.houkunlin.system.dict.starter.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.houkunlin.system.dict.starter.json.DictTextJsonSerializer;

/**
 * 自定义 SerializerProvider ，主要是为了处理数据字典的 null 值处理问题
 *
 * @author HouKunLin
 * @since 1.4.3
 */
public class DictDefaultSerializerProviderImpl extends DefaultSerializerProvider {
    /**
     * 构造方法
     */
    public DictDefaultSerializerProviderImpl() {
    }

    /**
     * 构造方法
     *
     * @param src    序列化提供商
     * @param config 序列化配置
     * @param f      序列化工厂
     */
    public DictDefaultSerializerProviderImpl(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
        super(src, config, f);
    }

    /**
     * 构造方法
     *
     * @param src 序列化提供商
     */
    public DictDefaultSerializerProviderImpl(final DefaultSerializerProvider src) {
        super(src);
    }

    /**
     * 创建序列化提供商实例对象
     *
     * @param config 序列化配置
     * @param jsf    序列化工厂
     * @return 默认序列化提供商
     */
    @Override
    public DefaultSerializerProvider createInstance(final SerializationConfig config, final SerializerFactory jsf) {
        return new DictDefaultSerializerProviderImpl(this, config, jsf);
    }

    /**
     * 重写此方法，为了给使用了 {@link com.houkunlin.system.dict.starter.json.DictText} 注解的字段自定义的 null 值序列化器
     *
     * @param property BeanProperty
     * @return JsonSerializer
     * @throws JsonMappingException 异常
     */
    @Override
    public JsonSerializer<Object> findNullValueSerializer(final BeanProperty property) throws JsonMappingException {
        final JsonSerializer<Object> serializer = DictTextJsonSerializer.getJsonSerializer(property);
        if (serializer != null) {
            return serializer;
        }
        return super.findNullValueSerializer(property);
    }
}
