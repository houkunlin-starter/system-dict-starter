package com.houkunlin.dict.jackson;

import com.houkunlin.dict.annotation.DictText;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;

import java.util.List;

import static com.houkunlin.dict.jackson.DictValueSerializerUtil.getDictTextValueSerializer;

/**
 * 数据字典值序列化对象编辑
 *
 * @author HouKunLin
 */
public class DictValueSerializerModifier extends ValueSerializerModifier {
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription.Supplier beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            final JavaType javaType = beanProperty.getType();
            final String fieldName = beanProperty.getName();
            final Class<?> javaTypeRawClass = javaType.getRawClass();
            final Class<?> beanClazz = beanDesc.getBeanClass();

            final DictText annotation = beanProperty.getAnnotation(DictText.class);
            if (annotation != null) {
                DictValueSerializerDefaultImpl valueSerializer = getDictTextValueSerializer(beanClazz, javaTypeRawClass, fieldName, annotation);
                beanProperty.assignSerializer(valueSerializer);
                beanProperty.assignNullSerializer(valueSerializer);
            }
        }
        return super.changeProperties(config, beanDesc, beanProperties);
    }
}
