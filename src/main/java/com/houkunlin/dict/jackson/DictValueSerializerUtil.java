package com.houkunlin.dict.jackson;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DictText 注解的序列化器，处理 DictText 注解，把字段的数据字典值转换成为数据字典文本信息
 *
 * @author HouKunLin
 */
@Getter
public class DictValueSerializerUtil {
    /**
     * bean 字段对于的序列化对象缓存
     */
    protected static final ConcurrentHashMap<String, DictValueSerializerDefaultImpl> CACHE = new ConcurrentHashMap<>();

    public static DictValueSerializerDefaultImpl getDictTextValueSerializer(final Class<?> beanClazz, final Class<?> fieldTypeRawClass, final String fieldName, final DictText annotation, DictArray dictArray) {
        final String cacheKey = cacheKey(fieldTypeRawClass, fieldName, annotation, dictArray);

        // 直接使用系统字典对象作为字段类型，需要进行一个特殊的处理
        if (DictEnum.class.isAssignableFrom(fieldTypeRawClass)) {
            final Class<? extends DictEnum<?>> aClass = (Class<? extends DictEnum<?>>) fieldTypeRawClass;
            // @DictText 注解目前仅对 字段、方法 起作用，因此这个条件判断的内容一定是会执行的
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictValueSerializerEnumsImpl(beanClazz, fieldTypeRawClass, fieldName, annotation, dictArray, new Class[]{aClass})
            );
        }

        final Class<? extends DictEnum<?>>[] enums = (Class<? extends DictEnum<?>>[]) annotation.enums();
        if (enums.length > 0) {
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictValueSerializerEnumsImpl(beanClazz, fieldTypeRawClass, fieldName, annotation, dictArray, enums)
            );
        }

        return CACHE.computeIfAbsent(cacheKey, key ->
            new DictValueSerializerDefaultImpl(beanClazz, fieldTypeRawClass, fieldName, annotation, dictArray)
        );
    }

    private static String cacheKey(final Class<?> javaTypeRawClass, final String fieldName, final DictText annotation, DictArray dictArray) {
        return javaTypeRawClass.getName() + ":" + fieldName + annotation.hashCode() + (dictArray == null ? "" : dictArray.hashCode());
    }

    /**
     * 获取数据字典缓存的序列化器
     *
     * @param beanClazz bean 对象
     * @param field     字段
     * @return JsonSerializer
     */
    public static DictValueSerializer getDictTextValueSerializer(final Class<?> beanClazz, final Field field) {
        if (field == null) {
            return null;
        }
        final DictText annotation = field.getDeclaredAnnotation(DictText.class);
        if (annotation == null) {
            return null;
        }
        DictArray dictArray = field.getAnnotation(DictArray.class);
        if (dictArray != null && dictArray.split().isEmpty()) {
            dictArray = null;
        }
        return getDictTextValueSerializer(beanClazz, field.getType(), field.getName(), annotation, dictArray);
    }
}
