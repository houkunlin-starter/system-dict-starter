package com.houkunlin.system.dict.starter.json;

import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.annotation.DictText;
import lombok.Getter;
import tools.jackson.databind.BeanProperty;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DictText 注解的序列化器，处理 DictText 注解，把字段的数据字典值转换成为数据字典文本信息
 *
 * @author HouKunLin
 */
@Getter
public class DictTextJsonSerializer {
    /**
     * bean 字段对于的序列化对象缓存
     */
    protected static final ConcurrentHashMap<String, DictTextJsonSerializerDefault> CACHE = new ConcurrentHashMap<>();

    public static DictTextJsonSerializerDefault buildJsonSerializerInstance(final Class<?> beanClazz, final Class<?> fieldTypeRawClass, final String fieldName, final DictText annotation) {
        final String cacheKey = cacheKey(fieldTypeRawClass, fieldName, annotation);

        // 直接使用系统字典对象作为字段类型，需要进行一个特殊的处理
        if (DictEnum.class.isAssignableFrom(fieldTypeRawClass)) {
            final Class<? extends DictEnum<?>> aClass = (Class<? extends DictEnum<?>>) fieldTypeRawClass;
            // @DictText 注解目前仅对 字段、方法 起作用，因此这个条件判断的内容一定是会执行的
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictTextJsonSerializerEnums(beanClazz, fieldTypeRawClass, fieldName, annotation, new Class[]{aClass})
            );
        }

        final Class<? extends DictEnum<?>>[] enums = (Class<? extends DictEnum<?>>[]) annotation.enums();
        if (enums.length > 0) {
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictTextJsonSerializerEnums(beanClazz, fieldTypeRawClass, fieldName, annotation, enums)
            );
        }

        return CACHE.computeIfAbsent(cacheKey, key ->
            new DictTextJsonSerializerDefault(beanClazz, fieldTypeRawClass, fieldName, annotation)
        );
    }

    private static String cacheKey(final Class<?> javaTypeRawClass, final String fieldName, final DictText annotation) {
        return javaTypeRawClass.getName() + ":" + fieldName + annotation.hashCode();
    }

    /**
     * 获取数据字典缓存的序列化器
     *
     * @param property BeanProperty
     * @return JsonSerializer
     * @since 1.4.3
     */
    public static DictTextJsonSerializerBasic getJsonSerializer(BeanProperty property) {
        if (property == null) {
            return null;
        }
        final DictText annotation = property.getAnnotation(DictText.class);
        if (annotation == null) {
            return null;
        }
        return CACHE.get(cacheKey(property.getType().getRawClass(), property.getName(), annotation));
    }

    /**
     * 获取数据字典缓存的序列化器
     *
     * @param beanClazz bean 对象
     * @param field     字段
     * @return JsonSerializer
     */
    public static DictTextJsonSerializerBasic getJsonSerializer(final Class<?> beanClazz, final Field field) {
        if (field == null) {
            return null;
        }
        final DictText annotation = field.getDeclaredAnnotation(DictText.class);
        if (annotation == null) {
            return null;
        }
        return buildJsonSerializerInstance(beanClazz, field.getType(), field.getName(), annotation);
    }
}
