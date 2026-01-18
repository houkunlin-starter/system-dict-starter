package com.houkunlin.dict.jackson;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.enums.NullStrategy;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DictText 注解的序列化器工具类，用于处理 DictText 注解，把字段的数据字典值转换成为数据字典文本信息。
 * <p>
 * 该工具类提供了字典值序列化器的获取和缓存功能，根据字段类型和注解配置生成合适的序列化器。
 *</p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@Getter
public class DictValueSerializerUtil {
    /**
     * 默认字典数组注解配置，当字段没有显式配置 DictArray 注解时使用
     */
    public static final DictArray DEFAULT_DICT_ARRAY = AnnotationUtils.synthesizeAnnotation(Map.of(
        "split", "",
        "toText", true,
        "delimiter", "、",
        "nullStrategy", NullStrategy.IGNORE
    ), DictArray.class, null);
    /**
     * bean 字段对应的序列化对象缓存，用于提高序列化性能
     */
    protected static final ConcurrentHashMap<String, DictValueSerializer> CACHE = new ConcurrentHashMap<>();

    /**
     * 获取字典文本值序列化器。
     * <p>
     * 根据字段类型和注解配置生成合适的序列化器，并缓存结果以提高性能。
     * </p>
     *
     * @param beanClazz        Bean 类
     * @param javaTypeRawClass Java 类型信息
     * @param fieldName        字段名称
     * @param annotation       字典文本注解配置
     * @param dictArray        字典数组注解配置
     * @param dictTree         字典树注解配置
     * @return 字典值序列化器
     */
    public static DictValueSerializer getDictTextValueSerializer(final Class<?> beanClazz, final Class<?> javaTypeRawClass, final String fieldName, final DictText annotation, DictArray dictArray, DictTree dictTree) {
        final String cacheKey = cacheKey(javaTypeRawClass, fieldName, annotation, dictArray == null ? DEFAULT_DICT_ARRAY : dictArray, dictTree);

        if (dictArray == null) {
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictValueSerializerToTextImpl(fieldName, javaTypeRawClass, annotation, DEFAULT_DICT_ARRAY, dictTree)
            );
        } else if (dictArray.toText()) {
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictValueSerializerToTextImpl(fieldName, javaTypeRawClass, annotation, dictArray, dictTree)
            );
        }

        return CACHE.computeIfAbsent(cacheKey, key ->
            new DictValueSerializerToArrayImpl(fieldName, javaTypeRawClass, annotation, dictArray, dictTree)
        );
    }

    /**
     * 生成缓存键。
     * <p>
     * 根据字段类型、字段名称和注解配置生成唯一的缓存键，用于缓存序列化器实例。
     * </p>
     *
     * @param javaTypeRawClass Java 类型信息
     * @param fieldName        字段名称
     * @param annotation       字典文本注解配置
     * @param dictArray        字典数组注解配置
     * @param dictTree         字典树注解配置
     * @return 缓存键
     */
    private static String cacheKey(final Class<?> javaTypeRawClass, final String fieldName, final DictText annotation, DictArray dictArray, DictTree dictTree) {
        return javaTypeRawClass.getName() + ":" + fieldName + annotation.hashCode() + (dictArray == null ? "" : dictArray.hashCode()) + (dictTree == null ? "" : dictTree.hashCode());
    }

    /**
     * 获取数据字典缓存的序列化器。
     * <p>
     * 根据字段上的注解配置生成合适的序列化器。
     *</p>
     *
     * @param beanClazz Bean 类
     * @param field     字段
     * @return 字典值序列化器，如果字段没有 DictText 注解则返回 null
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
        DictTree dictTree = field.getAnnotation(DictTree.class);
        return getDictTextValueSerializer(beanClazz, field.getType(), field.getName(), annotation, dictArray, dictTree);
    }
}
