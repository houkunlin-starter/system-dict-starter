package com.system.dic.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.system.dic.starter.DicUtil;
import com.system.dic.starter.IDicEnums;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DicText 注解的序列化器，处理 DicText 注解，把字段的数据字典值转换成为数据字典文本信息
 *
 * @author HouKunLin
 */
@Getter
public class DicTextJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {
    private static final Logger logger = LoggerFactory.getLogger(DicTextJsonSerializer.class);
    private static final ConcurrentHashMap<String, JsonSerializer<String>> CACHE = new ConcurrentHashMap<>();
    /**
     * 使用了这个注解的对象
     */
    private final Class<?> beanClazz;
    /**
     * 使用了这个注解的字段名称
     */
    private final String beanFieldName;
    /**
     * 字典输出字段名称
     */
    private final String destinationFieldName;
    /**
     * 字典转换注解对象
     */
    private final DicText dicText;
    /**
     * 字典类型代码
     */
    private final String dicType;
    /**
     * 直接使用系统字典枚举的枚举对象列表
     */
    private final Class<? extends IDicEnums<?>>[] enumsClass;
    /**
     * 缓存了直接使用系统字典枚举来渲染数据字典文本的所有数据
     */
    private static final Table<Class<? extends IDicEnums<?>>, Serializable, String> CACHE_ENUMS = HashBasedTable.create();

    public DicTextJsonSerializer() {
        this.beanClazz = null;
        this.beanFieldName = null;
        this.destinationFieldName = null;
        this.dicText = null;
        this.dicType = null;
        this.enumsClass = null;
    }

    public DicTextJsonSerializer(Class<?> beanClazz, String beanFieldName, DicText dicText) {
        this.beanClazz = beanClazz;
        this.beanFieldName = beanFieldName;
        this.dicText = dicText;
        this.dicType = dicText.value();
        final String fieldName = dicText.fieldName();
        if (StringUtils.hasText(fieldName)) {
            this.destinationFieldName = fieldName;
        } else {
            this.destinationFieldName = beanFieldName + "Text";
        }
        this.enumsClass = dicText.enums();
        if (this.enumsClass.length == 0) {
            return;
        }
        initEnumsClass();
    }

    private void initEnumsClass() {
        // 解析系统字典枚举列表
        for (final Class<? extends IDicEnums<?>> enumClass : this.enumsClass) {
            if (!enumClass.isEnum()) {
                continue;
            }
            final IDicEnums<?>[] enumConstants = enumClass.getEnumConstants();
            // 解析枚举对象枚举列表
            for (IDicEnums<?> enums : enumConstants) {
                // 缓存系统字典枚举对象的解析数据
                CACHE_ENUMS.put(enumClass, String.valueOf(enums.getValue()), enums.getTitle());
            }
        }
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value);
        gen.writeFieldName(destinationFieldName);
        if (enumsClass != null && enumsClass.length > 0) {
            String cacheTitle = null;
            for (final Class<? extends IDicEnums<?>> aClass : enumsClass) {
                cacheTitle = CACHE_ENUMS.get(aClass, String.valueOf(value));
                if (cacheTitle != null) {
                    break;
                }
            }
            gen.writeObject(defaultValue(cacheTitle));
            return;
        }
        if (dicType.isBlank()) {
            gen.writeObject(defaultValue(null));
            logger.warn("{}#{} @DicText annotation not set dicType value", beanClazz, beanFieldName);
        } else {
            gen.writeObject(defaultValue(DicUtil.getDicValueTitle(dicType, value)));
        }
    }

    private Object defaultValue(Object value) {
        if (dicText.defaultNull()) {
            return value;
        }
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        // 为空直接跳过
        if (property != null) {
            final JavaType javaType = property.getType();
            // 非 String 类直接跳过
            if (Objects.equals(javaType.getRawClass(), String.class)) {
                final DicText annotation = property.getAnnotation(DicText.class);
                if (annotation != null) {
                    // property.getMember().getDeclaringClass() 是这个实体类的对象class
                    // property.member 是一个 AnnotationMethod 对象，字段名一致的时候 property.member.hashCode() 是相同的
                    // 当字段名相同的时候 property.hashCode() 是相同的
                    // 当 字段名 相同的时候：property.member.hashCode() 是相同的
                    // 当 DicText 参数全部相同的时候：annotation.hashCode() 是相同的
                    // 因此 property.member 和 annotation 都不能作为 CacheMap 的 Key
                    // 否则导致不同 field 的 DicText 注解可能只有一个 JsonSerializer 对象，从而导致 DicJsonSerializer 参数出现错误、冲突，因此Json数据错误

                    // property.member.member 是这个字段对应的 Getter 方法，因此能够保证唯一
                    // 但是想要更优化的缓存，需要把 Key 设置为：字段名 + 注解对象 两个合并使用，才能保证唯一缓存
                    // 这样可以由相同的 field 加相同的 DicText 共用同一个 JsonSerializer 对象，当任何一个参数不同时都会重新创建一个 JsonSerializer 对象

                    // 方案一： property.getMember().getMember() 对象是字段的 Getter 方法对象

                    // 当 DicText 参数一致的时候， DicText.hashCode() 是相同的
                    // 因此导致不同 field 的 DicText 注解可能只有一个 JsonSerializer 对象，从而导致数据字典 JSON 字段名称出现错误、冲突
                    // 方案二：此时需要把 field 加入到 cache 的 key 中
                    // 这样可以由相同的 field 加相同的 DicText 共用同一个 JsonSerializer 对象，当任何一个参数不同时都会重新创建一个 JsonSerializer 对象
                    final String fieldName = property.getName();
                    final String cacheKey = fieldName + annotation.hashCode();

                    // 缓存，防止重复创建
                    return CACHE.computeIfAbsent(cacheKey, key ->
                            new DicTextJsonSerializer(
                                    property.getMember().getDeclaringClass(),
                                    fieldName,
                                    annotation)
                    );
                }
            }
            // TODO 发现个问题，DicText注解在枚举上时会出现堆栈溢出；在直接使用枚举对象做字段的时候这里出现了一个堆栈溢出，无法获取到具体的序列化对象
            return prov.findValueSerializer(javaType, property);
        }
        return prov.findNullValueSerializer(null);
    }
}
