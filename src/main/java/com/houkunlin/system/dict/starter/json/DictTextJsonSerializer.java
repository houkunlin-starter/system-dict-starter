package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.houkunlin.system.dict.starter.DictEnum;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DictText 注解的序列化器，处理 DictText 注解，把字段的数据字典值转换成为数据字典文本信息
 *
 * @author HouKunLin
 */
@Getter
public class DictTextJsonSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private static final Logger logger = LoggerFactory.getLogger(DictTextJsonSerializer.class);
    protected static final ConcurrentHashMap<String, DictTextJsonSerializerDefault> CACHE = new ConcurrentHashMap<>();

    /**
     * 默认的构造方法， Jackson 会先调用该构造方法实例化对象，然后再调用 {@link #createContextual(SerializerProvider, BeanProperty)} 方法获取序列化对象
     */
    public DictTextJsonSerializer() {
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    }

    /**
     * property.getMember().getDeclaringClass() 是这个实体类的对象class
     * property.member 是一个 AnnotationMethod 对象，字段名一致的时候 property.member.hashCode() 是相同的
     * 当字段名相同的时候 property.hashCode() 是相同的
     * 当 字段名 相同的时候：property.member.hashCode() 是相同的
     * 当 {@link DictText} 参数全部相同的时候：annotation.hashCode() 是相同的
     * 因此 property.member 和 annotation 都不能作为 CacheMap 的 Key
     * 否则导致不同 field 的 {@link DictText} 注解可能只有一个 JsonSerializer 对象，从而导致 {@link DictTextJsonSerializer} 参数出现错误、冲突，因此Json数据错误
     * property.member.member 是这个字段对应的 Getter 方法，因此能够保证唯一
     * 但是想要更优化的缓存，需要把 Key 设置为：字段名 + 注解对象 两个合并使用，才能保证唯一缓存
     * 这样可以由相同的 field 加相同的 {@link DictText} 共用同一个 JsonSerializer 对象，当任何一个参数不同时都会重新创建一个 JsonSerializer 对象
     * 方案一： property.getMember().getMember() 对象是字段的 Getter 方法对象
     * 当 {@link DictText} 参数一致的时候， {@link DictText#hashCode()} 是相同的
     * 因此导致不同 field 的 {@link DictText} 注解可能只有一个 JsonSerializer 对象，从而导致数据字典 JSON 字段名称出现错误、冲突
     * 方案二：此时需要把 field 加入到 cache 的 key 中
     * 这样可以由相同的 field 加相同的 {@link DictText} 共用同一个 JsonSerializer 对象，当任何一个参数不同时都会重新创建一个 JsonSerializer 对象
     *
     * @param prov     SerializerProvider
     * @param property BeanProperty
     * @return 序列化对象
     * @throws JsonMappingException 异常
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        // 为空直接跳过
        if (property == null) {
            return prov.findNullValueSerializer(null);
        }
        final JavaType javaType = property.getType();
        final String fieldName = property.getName();
        final Class<?> javaTypeRawClass = javaType.getRawClass();
        final Class<?> beanClazz = property.getMember().getDeclaringClass();

        // @DictText 注解目前仅对 字段、方法 起作用，因此这里拿到的注解一定是不为null的
        final DictText annotation = property.getAnnotation(DictText.class);
        if (annotation == null) {
            // 这里的代码实际已经过时，由于在之前的一次次提交中引入了 @DictType 注解来对系统字典进行自定义配置，因此实际上不会执行到这里。执行到这里是表示 @DictText 对整个类起了作用，这是一种错误的情况
            throw new JsonMappingException(null, "无法解析 " + beanClazz.getName() + "#" + fieldName + " 字段的字典信息。请在该对象上使用 @DictText 注解标记");
        }
        return buildJsonSerializerInstance(beanClazz, javaTypeRawClass, fieldName, annotation);
    }

    private static DictTextJsonSerializerDefault buildJsonSerializerInstance(final Class<?> beanClazz, final Class<?> fieldTypeRawClass, final String fieldName, final DictText annotation) {
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
