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
 * DictText 注解的序列化器，处理 DictText 注解，把字段的数据字典值转换成为数据字典文本信息
 *
 * @author HouKunLin
 */
@Getter
public class DictValueSerializerUtil {
    /**
     * bean 字段对于的序列化对象缓存
     */
    protected static final ConcurrentHashMap<String, DictValueSerializer> CACHE = new ConcurrentHashMap<>();
    public static final DictArray DEFAULT_DICT_ARRAY = AnnotationUtils.synthesizeAnnotation(Map.of(
        "split", "",
        "toText", true,
        "delimiter", "、",
        "nullStrategy", NullStrategy.IGNORE
    ), DictArray.class, null);

    public static DictValueSerializer getDictTextValueSerializer(final Class<?> beanClazz, final Class<?> javaTypeRawClass, final String fieldName, final DictText annotation, DictArray dictArray, DictTree dictTree) {
        final String cacheKey = cacheKey(javaTypeRawClass, fieldName, annotation, dictArray == null ? DEFAULT_DICT_ARRAY : dictArray, dictTree);

        if (dictArray == null) {
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictValueSerializerArrayTextTrueImpl(fieldName, javaTypeRawClass, annotation, DEFAULT_DICT_ARRAY, dictTree)
            );
        } else if (dictArray.toText()) {
            return CACHE.computeIfAbsent(cacheKey, key ->
                new DictValueSerializerArrayTextTrueImpl(fieldName, javaTypeRawClass, annotation, dictArray, dictTree)
            );
        }

        return CACHE.computeIfAbsent(cacheKey, key ->
            new DictValueSerializerArrayTextFalseImpl(fieldName, javaTypeRawClass, annotation, dictArray, dictTree)
        );
    }

    private static String cacheKey(final Class<?> javaTypeRawClass, final String fieldName, final DictText annotation, DictArray dictArray, DictTree dictTree) {
        return javaTypeRawClass.getName() + ":" + fieldName + annotation.hashCode() + (dictArray == null ? "" : dictArray.hashCode()) + (dictTree == null ? "" : dictTree.hashCode());
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
        DictTree dictTree = field.getAnnotation(DictTree.class);
        return getDictTextValueSerializer(beanClazz, field.getType(), field.getName(), annotation, dictArray, dictTree);
    }
}
