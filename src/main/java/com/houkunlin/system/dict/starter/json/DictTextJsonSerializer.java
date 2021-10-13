package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.houkunlin.system.dict.starter.DictEnum;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.SystemDictStarter;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DictText 注解的序列化器，处理 DictText 注解，把字段的数据字典值转换成为数据字典文本信息
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Getter
public class DictTextJsonSerializer extends JsonSerializer<Object> implements ContextualSerializer {
    private static final Logger logger = LoggerFactory.getLogger(DictTextJsonSerializer.class);
    private static final ConcurrentHashMap<String, JsonSerializer<Object>> CACHE = new ConcurrentHashMap<>();
    /**
     * 缓存了直接使用系统字典枚举来渲染数据字典文本的所有数据
     */
    private static final Table<Class<? extends DictEnum>, Serializable, String> CACHE_ENUMS = HashBasedTable.create();
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
    private final DictText dictText;
    /**
     * 字典类型代码
     */
    private final String dictType;
    /**
     * 直接使用系统字典枚举的枚举对象列表
     */
    private final Class<? extends DictEnum>[] enumsClass;

    /**
     * 默认的构造方法， Jackson 会先调用该构造方法实例化对象，然后再调用 {@link #createContextual(SerializerProvider, BeanProperty)} 方法获取序列化对象
     */
    public DictTextJsonSerializer() {
        this.beanClazz = null;
        this.beanFieldName = null;
        this.destinationFieldName = null;
        this.dictText = null;
        this.dictType = null;
        this.enumsClass = null;
    }

    /**
     * 一般情况下的场景，{@link DictText} 的普通用法
     *
     * @param beanClazz     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param dictText      实体类字段上的 {@link DictText} 注解对象
     */
    public DictTextJsonSerializer(Class<?> beanClazz, String beanFieldName, DictText dictText) {
        this.beanClazz = beanClazz;
        this.beanFieldName = beanFieldName;
        this.dictText = dictText;
        this.dictType = dictText.value();
        this.destinationFieldName = getFieldName(dictText);
        this.enumsClass = dictText.enums();
        initEnumsClass();
    }

    /**
     * 字段是特定枚举对象类型的场景，但是字段并未使用 {@link DictText} 注解
     *
     * @param beanClazz     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param enumsClass    实体类字段是一个特定枚举对象
     */
    public DictTextJsonSerializer(Class<?> beanClazz, String beanFieldName, Class<? extends DictEnum<?>>[] enumsClass) {
        this.beanClazz = beanClazz;
        this.beanFieldName = beanFieldName;
        this.dictText = null;
        this.dictType = null;
        this.destinationFieldName = beanFieldName + "Text";
        this.enumsClass = enumsClass;
        initEnumsClass();
    }

    /**
     * 字段是特定枚举对象类型的场景
     *
     * @param beanClazz     实体类 class
     * @param beanFieldName 实体类字段名称
     * @param dictText      实体类字段上的 {@link DictText} 注解对象
     * @param enumsClass    实体类字段是一个特定枚举对象
     */
    public DictTextJsonSerializer(Class<?> beanClazz, String beanFieldName, DictText dictText, Class<? extends DictEnum<?>>[] enumsClass) {
        this.beanClazz = beanClazz;
        this.beanFieldName = beanFieldName;
        this.dictText = dictText;
        this.dictType = dictText.value();
        this.destinationFieldName = getFieldName(dictText);
        this.enumsClass = enumsClass;
        initEnumsClass();
    }

    /**
     * 获取字典文本的字段名称
     *
     * @param dictText 注解对象
     * @return
     */
    private String getFieldName(DictText dictText) {
        final String fieldName = dictText.fieldName();
        if (StringUtils.hasText(fieldName)) {
            return fieldName;
        } else {
            return beanFieldName + "Text";
        }
    }

    /**
     * 初始化枚举对象数据，主要初始化枚举对象的字典值信息存储到Map对象中
     */
    private void initEnumsClass() {
        if (this.enumsClass.length == 0) {
            return;
        }
        // 解析系统字典枚举列表
        for (final Class<? extends DictEnum> enumClass : this.enumsClass) {
            if (!enumClass.isEnum()) {
                continue;
            }
            final DictEnum<?>[] enumConstants = enumClass.getEnumConstants();
            // 解析枚举对象枚举列表
            for (DictEnum<?> enums : enumConstants) {
                // 缓存系统字典枚举对象的解析数据
                CACHE_ENUMS.put(enumClass, String.valueOf(enums.getValue()), enums.getTitle());
            }
        }
    }

    /**
     * 序列化字段
     *
     * @param value       字段值
     * @param gen         JsonGenerator 对象
     * @param serializers SerializerProvider 对象
     * @throws IOException
     * @see JsonSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
     */
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (fromFieldEnumsClass(value, gen)) {
            return;
        }
        if (fromDictTextEnumsClass(value, gen)) {
            return;
        }
        fromDictCache(value, gen);
    }

    /**
     * 字段是系统字典枚举对象
     *
     * @param value 实体类字段值，此时该值可能是一个系统字典枚举对象
     * @param gen
     * @return 是否设置成功
     */
    private boolean fromFieldEnumsClass(Object value, JsonGenerator gen) throws IOException {
        if (value instanceof DictEnum) {
            final DictEnum enums = (DictEnum) value;
            final String title = getTitleFormClass(enums.getValue());
            if (title == null) {
                logger.warn("{}#{} = {} 本身是一个 系统字典枚举对象，但是由于未找到其值因而会进行进一步的信息获取。实际上这里不应该发生的", beanClazz, beanFieldName, value);
                return false;
            }
            writeFieldValue(gen, enums.getValue(), defaultValue(title));
            return true;
        }
        return false;
    }

    /**
     * 字段是普通类型，但是使用 {@link DictText} 标记了来自枚举对象取值
     *
     * @param value 实体类字段值
     * @param gen
     * @return
     */
    private boolean fromDictTextEnumsClass(Object value, JsonGenerator gen) throws IOException {
        if (enumsClass != null && enumsClass.length > 0) {
            final String title = getTitleFormClass(value);
            if (title == null) {
                logger.warn("{}#{} = {} 指定了从多个字典枚举中取值，但是由于未找到其值因而会进行进一步的信息获取。实际上这里不应该发生的", beanClazz, beanFieldName, value);
                return false;
            }
            writeFieldValue(gen, value, defaultValue(title));
            return true;
        }
        return false;
    }

    /**
     * 从缓存中获取字典文本
     *
     * @param value 字典值对象
     * @param gen
     * @throws IOException
     */
    private void fromDictCache(Object value, JsonGenerator gen) throws IOException {
        if (dictType != null && StringUtils.hasText(dictType)) {
            writeFieldValue(gen, value, DictUtil.getDictText(dictType, String.valueOf(value)));
        } else {
            writeFieldValue(gen, value, defaultValue(null));
            logger.warn("{}#{} @DictText annotation not set dictType value", beanClazz, beanFieldName);
        }
    }

    /**
     * 把字段字典值写入到JSON数据中
     *
     * @param fieldValue 字段值
     * @param gen
     * @throws IOException
     */
    private void writeFieldValue(Object fieldValue, JsonGenerator gen) throws IOException {
        if (SystemDictStarter.isRawValue()) {
            gen.writeObject(fieldValue);
        } else {
            gen.writeString(String.valueOf(fieldValue));
        }
    }

    /**
     * 把数据字典原始值和转换后的字典文本值写入到 Json 中
     *
     * @param gen            JsonGenerator 对象
     * @param rawValueObject 实体类字典值
     * @param dictValueText  字典文本值
     * @throws IOException
     */
    private void writeFieldValue(JsonGenerator gen, Object rawValueObject, Object dictValueText) throws IOException {
        if (dictText == null) {
            writeFieldValue(rawValueObject, gen);
            return;
        }
        if (dictText.mapValue() == DictText.Type.YES || (SystemDictStarter.isMapValue() && dictText.mapValue() == DictText.Type.GLOBAL)) {
            final Map<String, Object> map = new HashMap<>();
            map.put("value", rawValueObject);
            map.put("text", dictValueText);
            if (StringUtils.hasText(dictText.fieldName())) {
                writeFieldValue(rawValueObject, gen);
                gen.writeFieldName(dictText.fieldName());
                gen.writeObject(map);
            } else {
                gen.writeObject(map);
            }
        } else {
            writeFieldValue(rawValueObject, gen);
            gen.writeFieldName(destinationFieldName);
            gen.writeObject(dictValueText);
        }
    }

    /**
     * 从系统字典枚举对象中获取字典文本信息
     *
     * @param value 字典值
     * @return 字典文本
     * @throws IOException
     */
    private String getTitleFormClass(Object value) throws IOException {
        assert enumsClass != null;

        String cacheTitle = null;
        for (final Class<? extends DictEnum> aClass : enumsClass) {
            cacheTitle = CACHE_ENUMS.get(aClass, String.valueOf(value));
            if (cacheTitle != null) {
                break;
            }
        }
        return cacheTitle;
    }

    private Object defaultValue(Object value) {
        if (dictText != null) {
            if (dictText.nullable() == DictText.Type.GLOBAL) {
                return defaultValue(value, SystemDictStarter.isTextValueDefaultNull());
            }
            return defaultValue(value, dictText.nullable() == DictText.Type.YES);
        }
        return defaultValue(value, SystemDictStarter.isTextValueDefaultNull());
    }

    /**
     * 获取默认值
     *
     * @param value    原始值
     * @param nullable 是否为null
     * @return 处理结果
     */
    private Object defaultValue(Object value, boolean nullable) {
        if (nullable) {
            return value;
        }
        if (value == null) {
            return "";
        }
        return value;
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
     * @param prov
     * @param property
     * @return 序列化对象
     * @throws JsonMappingException
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        // 为空直接跳过
        if (property != null) {
            final JavaType javaType = property.getType();
            final String fieldName = property.getName();
            final Class<?> javaTypeRawClass = javaType.getRawClass();

            // @DictText 注解目前仅对 字段、方法 起作用，因此这里拿到的注解一定是不为null的
            final DictText annotation = property.getAnnotation(DictText.class);

            // 直接使用系统字典对象作为字段类型，需要进行一个特殊的处理
            if (DictEnum.class.isAssignableFrom(javaTypeRawClass)) {
                final Class<? extends DictEnum<?>> aClass = (Class<? extends DictEnum<?>>) javaTypeRawClass;
                if (annotation != null) {
                    // @DictText 注解目前仅对 字段、方法 起作用，因此这个条件判断的内容一定是会执行的
                    return CACHE.computeIfAbsent(javaTypeRawClass.getName() + ":" + fieldName + annotation.hashCode(), key ->
                        new DictTextJsonSerializer(
                            property.getMember().getDeclaringClass(),
                            fieldName,
                            annotation,
                            new Class[]{aClass})
                    );
                }
                // 这里的代码实际已经过时，由于在本次提交，引入了 @DictType 注解来对系统字典进行自定义配置，因此实际上不会执行到这里。执行到这里是表示 @DictText 对整个类起了作用
                return CACHE.computeIfAbsent(javaTypeRawClass.getName() + ":" + fieldName, key ->
                    new DictTextJsonSerializer(
                        property.getMember().getDeclaringClass(),
                        fieldName,
                        new Class[]{aClass}));
            }
            if (annotation != null) {
                // @DictText 注解目前仅对 字段、方法 起作用，因此这个条件判断的内容一定是会执行的
                return CACHE.computeIfAbsent(fieldName + annotation.hashCode(), key ->
                    new DictTextJsonSerializer(
                        property.getMember().getDeclaringClass(),
                        fieldName,
                        annotation)
                );
            }
            try {
                // 这里的代码实际已经过时，由于在本次提交，引入了 @DictType 注解来对系统字典进行自定义配置，因此实际上不会执行到这里。执行到这里是表示 @DictText 对整个类起了作用
                return prov.findValueSerializer(javaType, property);
            } catch (JsonMappingException e) {
                throw new JsonMappingException(null, "无法解析 " + javaTypeRawClass + " 类型的字典序列化对象。由于在该对象上使用了 @DictText 注解，但其未实现 DictEnum 接口可能就会出现这个异常", e);
            }
        }
        return prov.findNullValueSerializer(null);
    }
}
