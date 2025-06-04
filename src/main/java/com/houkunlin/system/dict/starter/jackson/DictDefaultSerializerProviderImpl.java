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
    public DictDefaultSerializerProviderImpl() {
    }

    public DictDefaultSerializerProviderImpl(final SerializerProvider src, final SerializationConfig config, final SerializerFactory f) {
        super(src, config, f);
    }

    public DictDefaultSerializerProviderImpl(final DefaultSerializerProvider src) {
        super(src);
    }

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
