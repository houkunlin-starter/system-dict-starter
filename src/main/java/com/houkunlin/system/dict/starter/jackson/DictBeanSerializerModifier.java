package com.houkunlin.system.dict.starter.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.houkunlin.system.dict.starter.json.DictText;
import com.houkunlin.system.dict.starter.json.DictTextJsonSerializerDefault;

import java.util.List;

import static com.houkunlin.system.dict.starter.json.DictTextJsonSerializer.buildJsonSerializerInstance;

/**
 * 数据字典值序列化对象编辑
 *
 * @author HouKunLin
 * @since 1.6.3
 */
public class DictBeanSerializerModifier extends BeanSerializerModifier {
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            final JavaType javaType = beanProperty.getType();
            final String fieldName = beanProperty.getName();
            final Class<?> javaTypeRawClass = javaType.getRawClass();
            final Class<?> beanClazz = beanDesc.getBeanClass();

            final DictText annotation = beanProperty.getAnnotation(DictText.class);
            if (annotation != null) {
                DictTextJsonSerializerDefault dictTextJsonSerializerDefault = buildJsonSerializerInstance(beanClazz, javaTypeRawClass, fieldName, annotation);
                beanProperty.assignSerializer(dictTextJsonSerializerDefault);
                beanProperty.assignNullSerializer(dictTextJsonSerializerDefault);
            }
        }
        return super.changeProperties(config, beanDesc, beanProperties);
    }
}
