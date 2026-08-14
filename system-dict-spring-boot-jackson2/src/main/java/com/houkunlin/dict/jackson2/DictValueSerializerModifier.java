package com.houkunlin.dict.jackson2;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;

import java.util.List;

import static com.houkunlin.dict.jackson2.DictValueSerializerUtil.getDictTextValueSerializer;

/**
 * 数据字典值序列化对象编辑器，用于修改 Bean 属性的序列化器，为带有 DictText 注解的字段添加字典值序列化器。
 * <p>
 * 该类继承自 Jackson 的 BeanSerializerModifier，用于在序列化过程中动态修改 Bean 属性的序列化器，
 * 为带有 DictText 注解的字段添加字典值转换功能。
 * </p>
 *
 * @author HouKunLin
 * @since 1.7.0
 */
public class DictValueSerializerModifier extends BeanSerializerModifier {
    /**
     * 修改 Bean 属性的序列化器，为带有 DictText 注解的字段添加字典值序列化器。
     * <p>
     * 遍历所有 Bean 属性，检查是否带有 DictText 注解，如果有则为其添加字典值序列化器。
     * </p>
     *
     * @param config         序列化配置
     * @param beanDesc       Bean 描述信息
     * @param beanProperties Bean 属性列表
     * @return 修改后的 Bean 属性列表
     */
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter beanProperty : beanProperties) {
            final JavaType javaType = beanProperty.getType();
            final String fieldName = beanProperty.getName();
            final Class<?> javaTypeRawClass = javaType.getRawClass();
            final Class<?> beanClazz = beanDesc.getBeanClass();

            final DictText annotation = beanProperty.getAnnotation(DictText.class);
            if (annotation != null) {
                DictArray dictArray = beanProperty.getAnnotation(DictArray.class);
                DictTree dictTree = beanProperty.getAnnotation(DictTree.class);
                JsonSerializer<Object> valueSerializer = getDictTextValueSerializer(beanClazz, javaTypeRawClass, fieldName, annotation, dictArray, dictTree);
                beanProperty.assignSerializer(valueSerializer);
                beanProperty.assignNullSerializer(valueSerializer);
            }
        }
        return super.changeProperties(config, beanDesc, beanProperties);
    }
}
